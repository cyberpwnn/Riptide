package riptide.stream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import riptide.device.AcceptingSensor;
import riptide.queue.ConveyorBelt;
import riptide.queue.GenericConveyorBelt;

public class GenericWitholdingDataStream<T> implements WitholdingDataStream<T>
{
	private final Socket socket;
	private final AcceptingSensor<T> sensor;
	private final DataInputStream inputStream;
	private final ConveyorBelt<T> belt;

	public GenericWitholdingDataStream(AcceptingSensor<T> sensor, InputStream stream, int beltSize, Socket socket)
	{
		this.sensor = sensor;
		this.inputStream = new DataInputStream(stream);
		this.belt = new GenericConveyorBelt<>(beltSize);
		this.socket = socket;
	}

	@Override
	public AcceptingSensor<T> getSensor()
	{
		return sensor;
	}

	@Override
	public DataInputStream getInputStream()
	{
		return inputStream;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T pull()
	{
		try
		{
			return (T) getSensor().getDataType().reader().read(getInputStream());
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void close()
	{
		try
		{
			socket.close();
			getInputStream().close();
		}

		catch(IOException e)
		{

		}
	}

	@Override
	public ConveyorBelt<T> getConveyorBelt()
	{
		return belt;
	}

	@Override
	public Socket getSocket()
	{
		return socket;
	}
}
