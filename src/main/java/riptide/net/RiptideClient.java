package riptide.net;

import java.util.ArrayList;
import java.util.List;

import riptide.data.DataType;
import riptide.device.Device;
import riptide.device.Sensor;
import riptide.queue.DataRoller;

public class RiptideClient
{
	private final List<DataRoller<?>> rollers;

	public RiptideClient()
	{
		rollers = new ArrayList<>();
	}

	public void scanForDevices()
	{
		scanForDevices(new Runnable()
		{
			@Override
			public void run()
			{

			}
		});
	}

	public void scanForDevices(Runnable updatedList)
	{
		try
		{
			Riptide.refreshDevices(updatedList);
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public List<DataRoller<?>> getRollers()
	{
		return rollers;
	}

	public DataRoller<?> roll(Device device, Sensor sensor, int size)
	{
		return roll(device.getName(), sensor.getName(), sensor.getDataType(), size);
	}

	public DataRoller<?> roll(String deviceName, String sensorName, DataType type, int size)
	{
		for(DataRoller<?> i : rollers)
		{
			if(i.getDeviceName().equals(deviceName) && i.getSensorName().equals(sensorName))
			{
				i.setSize(size);
				return i;
			}
		}

		DataRoller<?> dr = null;

		switch(type)
		{
			case DOUBLE:
				dr = new DataRoller<Double>(deviceName, sensorName, size);
				break;
			case FLOAT:
				dr = new DataRoller<Float>(deviceName, sensorName, size);
				break;
			case UTF:
				dr = new DataRoller<String>(deviceName, sensorName, size);
				break;
			default:
				break;
		}

		rollers.add(dr);

		return dr;
	}
}
