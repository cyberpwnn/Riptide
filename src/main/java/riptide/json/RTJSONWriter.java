package riptide.json;


import java.io.IOException;
import java.io.Writer;

/*
Copyright (c) 2006 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/**
 * JSONWriter provides a quick and convenient way of producing JSON text. The
 * texts produced strictly conform to JSON syntax rules. No whitespace is added,
 * so the results are ready for transmission or storage. Each instance of
 * JSONWriter can produce one JSON text.
 * <p>
 * A JSONWriter instance provides a <code>value</code> method for appending
 * values to the text, and a <code>key</code> method for adding keys before
 * values in objects. There are <code>array</code> and <code>endArray</code>
 * methods that make and bound array values, and <code>object</code> and
 * <code>endObject</code> methods which make and bound object values. All of
 * these methods return the JSONWriter instance, permitting a cascade style. For
 * example,
 * 
 * <pre>
 * new JSONWriter(myWriter).object().key("JSON").value("Hello, World!").endObject();
 * </pre>
 * 
 * which writes
 * 
 * <pre>
 * {"JSON":"Hello, World!"}
 * </pre>
 * <p>
 * The first method called must be <code>array</code> or <code>object</code>.
 * There are no methods for adding commas or colons. JSONWriter adds them for
 * you. Objects and arrays can be nested up to 20 levels deep.
 * <p>
 * This can sometimes be easier than using a JSONObject to build a string.
 * 
 * @author JSON.org
 * @version 2011-11-24
 */
public class RTJSONWriter
{
	private static final int maxdepth = 200;
	
	/**
	 * The comma flag determines if a comma should be output before the next
	 * value.
	 */
	private boolean comma;
	
	/**
	 * The current mode. Values: 'a' (array), 'd' (done), 'i' (initial), 'k'
	 * (key), 'o' (object).
	 */
	protected char mode;
	
	/**
	 * The object/array stack.
	 */
	private final RTJSONObject stack[];
	
	/**
	 * The stack top index. A value of 0 indicates that the stack is empty.
	 */
	private int top;
	
	/**
	 * The writer that will receive the output.
	 */
	protected Writer writer;
	
	/**
	 * Make a fresh JSONWriter. It can be used to build one JSON text.
	 */
	public RTJSONWriter(Writer w)
	{
		this.comma = false;
		this.mode = 'i';
		this.stack = new RTJSONObject[maxdepth];
		this.top = 0;
		this.writer = w;
	}
	
	/**
	 * Append a value.
	 * 
	 * @param string
	 *            A string value.
	 * @return this
	 * @throws RTJSONException
	 *             If the value is out of sequence.
	 */
	private RTJSONWriter append(String string) throws RTJSONException
	{
		if(string == null)
		{
			throw new RTJSONException("Null pointer");
		}
		if(this.mode == 'o' || this.mode == 'a')
		{
			try
			{
				if(this.comma && this.mode == 'a')
				{
					this.writer.write(',');
				}
				this.writer.write(string);
			} catch(IOException e)
			{
				throw new RTJSONException(e);
			}
			if(this.mode == 'o')
			{
				this.mode = 'k';
			}
			this.comma = true;
			return this;
		}
		throw new RTJSONException("Value out of sequence.");
	}
	
	/**
	 * Begin appending a new array. All values until the balancing
	 * <code>endArray</code> will be appended to this array. The
	 * <code>endArray</code> method must be called to mark the array's end.
	 * 
	 * @return this
	 * @throws RTJSONException
	 *             If the nesting is too deep, or if the object is started in
	 *             the wrong place (for example as a key or after the end of the
	 *             outermost array or object).
	 */
	public RTJSONWriter array() throws RTJSONException
	{
		if(this.mode == 'i' || this.mode == 'o' || this.mode == 'a')
		{
			this.push(null);
			this.append("[");
			this.comma = false;
			return this;
		}
		throw new RTJSONException("Misplaced array.");
	}
	
	/**
	 * End something.
	 * 
	 * @param mode
	 *            Mode
	 * @param c
	 *            Closing character
	 * @return this
	 * @throws RTJSONException
	 *             If unbalanced.
	 */
	private RTJSONWriter end(char mode, char c) throws RTJSONException
	{
		if(this.mode != mode)
		{
			throw new RTJSONException(mode == 'a' ? "Misplaced endArray." : "Misplaced endObject.");
		}
		this.pop(mode);
		try
		{
			this.writer.write(c);
		} catch(IOException e)
		{
			throw new RTJSONException(e);
		}
		this.comma = true;
		return this;
	}
	
	/**
	 * End an array. This method most be called to balance calls to
	 * <code>array</code>.
	 * 
	 * @return this
	 * @throws RTJSONException
	 *             If incorrectly nested.
	 */
	public RTJSONWriter endArray() throws RTJSONException
	{
		return this.end('a', ']');
	}
	
	/**
	 * End an object. This method most be called to balance calls to
	 * <code>object</code>.
	 * 
	 * @return this
	 * @throws RTJSONException
	 *             If incorrectly nested.
	 */
	public RTJSONWriter endObject() throws RTJSONException
	{
		return this.end('k', '}');
	}
	
	/**
	 * Append a key. The key will be associated with the next value. In an
	 * object, every value must be preceded by a key.
	 * 
	 * @param string
	 *            A key string.
	 * @return this
	 * @throws RTJSONException
	 *             If the key is out of place. For example, keys do not belong
	 *             in arrays or if the key is null.
	 */
	public RTJSONWriter key(String string) throws RTJSONException
	{
		if(string == null)
		{
			throw new RTJSONException("Null key.");
		}
		if(this.mode == 'k')
		{
			try
			{
				this.stack[this.top - 1].putOnce(string, Boolean.TRUE);
				if(this.comma)
				{
					this.writer.write(',');
				}
				this.writer.write(RTJSONObject.quote(string));
				this.writer.write(':');
				this.comma = false;
				this.mode = 'o';
				return this;
			} catch(IOException e)
			{
				throw new RTJSONException(e);
			}
		}
		throw new RTJSONException("Misplaced key.");
	}
	
	/**
	 * Begin appending a new object. All keys and values until the balancing
	 * <code>endObject</code> will be appended to this object. The
	 * <code>endObject</code> method must be called to mark the object's end.
	 * 
	 * @return this
	 * @throws RTJSONException
	 *             If the nesting is too deep, or if the object is started in
	 *             the wrong place (for example as a key or after the end of the
	 *             outermost array or object).
	 */
	public RTJSONWriter object() throws RTJSONException
	{
		if(this.mode == 'i')
		{
			this.mode = 'o';
		}
		if(this.mode == 'o' || this.mode == 'a')
		{
			this.append("{");
			this.push(new RTJSONObject());
			this.comma = false;
			return this;
		}
		throw new RTJSONException("Misplaced object.");
		
	}
	
	/**
	 * Pop an array or object scope.
	 * 
	 * @param c
	 *            The scope to close.
	 * @throws RTJSONException
	 *             If nesting is wrong.
	 */
	private void pop(char c) throws RTJSONException
	{
		if(this.top <= 0)
		{
			throw new RTJSONException("Nesting error.");
		}
		char m = this.stack[this.top - 1] == null ? 'a' : 'k';
		if(m != c)
		{
			throw new RTJSONException("Nesting error.");
		}
		this.top -= 1;
		this.mode = this.top == 0 ? 'd' : this.stack[this.top - 1] == null ? 'a' : 'k';
	}
	
	/**
	 * Push an array or object scope.
	 * 
	 * @param jo
	 *            The scope to open.
	 * @throws RTJSONException
	 *             If nesting is too deep.
	 */
	private void push(RTJSONObject jo) throws RTJSONException
	{
		if(this.top >= maxdepth)
		{
			throw new RTJSONException("Nesting too deep.");
		}
		this.stack[this.top] = jo;
		this.mode = jo == null ? 'a' : 'k';
		this.top += 1;
	}
	
	/**
	 * Append either the value <code>true</code> or the value <code>false</code>
	 * .
	 * 
	 * @param b
	 *            A boolean.
	 * @return this
	 * @throws RTJSONException
	 */
	public RTJSONWriter value(boolean b) throws RTJSONException
	{
		return this.append(b ? "true" : "false");
	}
	
	/**
	 * Append a double value.
	 * 
	 * @param d
	 *            A double.
	 * @return this
	 * @throws RTJSONException
	 *             If the number is not finite.
	 */
	public RTJSONWriter value(double d) throws RTJSONException
	{
		return this.value(new Double(d));
	}
	
	/**
	 * Append a long value.
	 * 
	 * @param l
	 *            A long.
	 * @return this
	 * @throws RTJSONException
	 */
	public RTJSONWriter value(long l) throws RTJSONException
	{
		return this.append(Long.toString(l));
	}
	
	/**
	 * Append an object value.
	 * 
	 * @param object
	 *            The object to append. It can be null, or a Boolean, Number,
	 *            String, JSONObject, or JSONArray, or an object that implements
	 *            JSONString.
	 * @return this
	 * @throws RTJSONException
	 *             If the value is out of sequence.
	 */
	public RTJSONWriter value(Object object) throws RTJSONException
	{
		return this.append(RTJSONObject.valueToString(object));
	}
}
