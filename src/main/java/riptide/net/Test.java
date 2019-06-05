package riptide.net;

import java.io.IOException;

import riptide.data.DataType;
import riptide.device.Device;
import riptide.device.LocalDevice;
import riptide.device.LocalSensor;
import riptide.device.ProvidingSensor;
import riptide.queue.DataRoller;

public class Test
{
	// Demonstrates LOW LEVEL setup with riptide
	public static void main(String[] a) throws IOException, InterruptedException
	{
		setupHost();
		setupClient();
	}

	private static void setupHost()
	{
		// SERVER
		// New device "java" to host sensors
		Device d = new LocalDevice("Java");

		// Add a new sensor called memory
		ProvidingSensor<Float> s = new LocalSensor<Float>("Memory", DataType.FLOAT);
		d.getSensors().add(s);

		// Host the device java
		Riptide.getHost().hostDevice(d);

		// Fill the memory sensor 4 times per second with current memory in mb (float)
		Runtime rt = Runtime.getRuntime();
		sched(() -> s.provide((float) (rt.totalMemory() - rt.freeMemory()) / 1024F / 1024F), 50);
	}

	private static void setupClient()
	{
		// CLIENT
		RiptideClient c = new RiptideClient();

		// Scan for devices on LAN
		c.scanForDevices();

		// Look for our device
		for(Device i : Riptide.getDevices())
		{
			if(i.getName().endsWith("Java"))
			{
				// Bind a data roller to our connection (totally safe)
				@SuppressWarnings("unchecked")
				DataRoller<Float> memory = (DataRoller<Float>) c.roll(i, i.getSensor("Memory"), 64);

				// Open a connection to memory
				System.out.println("Opening Conection");
				if(memory.open())
				{
					System.out.println("Scheduling / running");
					sched(() ->
					{
						float last = memory.getConveyor().getData().get(memory.getConveyor().getOccupancy() - 1);
						float rollingAverage = 0;

						for(float j : memory.getConveyor().getData())
						{
							rollingAverage += j;
						}

						rollingAverage /= memory.getConveyor().getOccupancy();
						System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
						System.out.println("Conveyor Size: " + memory.getConveyor().getOccupancy());
						System.out.println("Memory MB NOW: " + last);
						System.out.println("Rolling Average: " + rollingAverage);
						System.out.println("\n\n\n");
					}, 50);
				}

				break;
			}
		}
	}

	public static void sched(Runnable r, long interval)
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while(!Thread.interrupted())
				{
					r.run();

					try
					{
						Thread.sleep(interval);
					}

					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
	}
}
