package riptide.queue;

import riptide.device.AcceptingSensor;
import riptide.device.Device;
import riptide.net.Riptide;
import riptide.stream.WitholdingDataStream;

public class DataRoller<T> extends Thread
{
	private String deviceName;
	private String sensorName;
	private Device device;
	private ConveyorBelt<T> conveyor;
	private AcceptingSensor<T> sensor;
	private WitholdingDataStream<T> stream;

	public DataRoller(String deviceName, String sensorName, int size)
	{
		this.sensorName = sensorName;
		this.deviceName = deviceName;
		this.conveyor = new GenericConveyorBelt<T>(size);
		start();
	}

	public void setSize(int newSize)
	{
		conveyor.setSize(newSize);
	}

	public void close()
	{
		try
		{
			stream.close();
		}

		catch(Throwable e)
		{

		}

		stream = null;
	}

	@SuppressWarnings("unchecked")
	public boolean open()
	{
		device = Riptide.findDevice(deviceName);

		if(device != null)
		{
			try
			{
				sensor = (AcceptingSensor<T>) device.getSensor(sensorName);

				if(sensor != null)
				{
					stream = Riptide.stream(device, sensor);
					return true;
				}
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public void run()
	{
		while(!interrupted())
		{
			if(stream != null)
			{
				try
				{
					conveyor.push(stream.pull());
				}

				catch(Throwable e)
				{

				}
			}
		}
	}

	public String getDeviceName()
	{
		return deviceName;
	}

	public String getSensorName()
	{
		return sensorName;
	}

	public Device getDevice()
	{
		return device;
	}

	public ConveyorBelt<T> getConveyor()
	{
		return conveyor;
	}

	public AcceptingSensor<T> getSensor()
	{
		return sensor;
	}

	public WitholdingDataStream<T> getStream()
	{
		return stream;
	}
}
