package riptide.device;

import java.io.InputStream;

import riptide.data.DataType;
import riptide.stream.GenericWitholdingDataStream;
import riptide.stream.WitholdingDataStream;

public class RemoteSensor<T> extends GenericSensor implements AcceptingSensor<T>
{
	public RemoteSensor(String name, DataType type)
	{
		super(name, type);
	}

	@Override
	public WitholdingDataStream<T> openStream(InputStream in, int beltSize)
	{
		return new GenericWitholdingDataStream<>(this, in, beltSize);
	}
}
