package riptide.stream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import riptide.device.ProvidingSensor;
import riptide.queue.ConveyorBelt;

public class GenericEmissiveDataStream<T> implements EmissiveDataStream<T>
{
	private final ProvidingSensor<T> sensor;
	private final DataOutputStream outputStream;

	public GenericEmissiveDataStream(ProvidingSensor<T> sensor, OutputStream stream)
	{
		this.sensor = sensor;
		this.outputStream = new DataOutputStream(stream);
		ConveyorBelt<T> belt = getSensor().getConveyorBelt().clone();

		while(belt.canPull())
		{
			push(belt.pull());
		}
	}

	@Override
	public ProvidingSensor<T> getSensor()
	{
		return sensor;
	}

	@Override
	public DataOutputStream getOutputStream()
	{
		return outputStream;
	}

	@Override
	public void push(T t)
	{
		try
		{
			getSensor().getDataType().writer().write((T) t, getOutputStream());
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		try
		{
			getOutputStream().close();
		}

		catch(IOException e)
		{

		}
	}
}
