package riptide.device;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import riptide.data.DataType;
import riptide.queue.ConveyorBelt;
import riptide.queue.GenericConveyorBelt;
import riptide.stream.EmissiveDataStream;
import riptide.stream.GenericEmissiveDataStream;

public class LocalSensor<T> extends GenericSensor implements ProvidingSensor<T>
{
	private ConveyorBelt<T> masterConveyor;
	private Map<String, EmissiveDataStream<T>> streams;

	public LocalSensor(String name, DataType type)
	{
		super(name, type);
		masterConveyor = new GenericConveyorBelt<>(256);
		streams = new HashMap<>();
	}

	@Override
	public void provide(T t)
	{
		masterConveyor.push(t);

		for(EmissiveDataStream<T> i : getStreams().values())
		{
			i.push(t);
		}
	}

	@Override
	public EmissiveDataStream<T> openStream(String name, OutputStream out)
	{
		if(getStreams().containsKey(name))
		{
			closeStream(name);
		}

		EmissiveDataStream<T> m = new GenericEmissiveDataStream<>(this, out);
		getStreams().put(name, m);
		return m;
	}

	@Override
	public Map<String, EmissiveDataStream<T>> getStreams()
	{
		return streams;
	}

	@Override
	public EmissiveDataStream<T> getStream(String name)
	{
		return getStreams().get(name);
	}

	@Override
	public void closeStream(String name)
	{
		try
		{
			getStream(name).close();
		}

		catch(Throwable e)
		{

		}

		streams.remove(name);
	}

	@Override
	public void closeAllStreams()
	{
		for(String i : new ArrayList<String>(getStreams().keySet()))
		{
			closeStream(i);
		}
	}

	@Override
	public ConveyorBelt<T> getConveyorBelt()
	{
		return masterConveyor;
	}
}
