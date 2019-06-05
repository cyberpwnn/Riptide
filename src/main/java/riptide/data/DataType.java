package riptide.data;

public enum DataType
{
	FLOAT((in) -> in.readFloat(), (d, out) -> out.writeFloat((float) d)),
	DOUBLE((in) -> in.readDouble(), (d, out) -> out.writeDouble((double) d)),
	UTF((in) -> in.readUTF(), (d, out) -> out.writeUTF((String) d));

	private DataReader<?> reader;
	private DataWriter<?> writer;

	private DataType(DataReader<?> reader, DataWriter<?> writer)
	{
		this.reader = reader;
		this.writer = writer;
	}

	public DataReader<?> reader()
	{
		return reader;
	}

	public DataWriter<?> writer()
	{
		return writer;
	}
}
