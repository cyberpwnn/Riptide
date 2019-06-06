package riptide.net;

import java.io.IOException;

import riptide.data.DataType;
import riptide.device.Device;
import riptide.device.GenericEmitter;
import riptide.device.LocalDevice;
import riptide.device.LocalSensor;
import riptide.queue.DataRoller;

public class Test
{
	// Demonstrates LOW LEVEL setup with riptide
	public static void main(String[] a) throws IOException, InterruptedException
	{
		setupHost();
		// setupClient();
	}

	private static void setupHost()
	{
		LocalSensor<Float> f;
		LocalSensor<Float> g;
		Device x = new LocalDevice("Java");
		x.getSensors().add(f = new LocalSensor<Float>("Sin", DataType.FLOAT));
		x.getSensors().add(g = new LocalSensor<Float>("Random", DataType.FLOAT));
		Riptide.getHost().hostDevice(x);

		Device a = new LocalDevice("Bungeecord");
		a.getSensors().add(new LocalSensor<Float>("Memory", DataType.FLOAT));
		a.getSensors().add(new LocalSensor<Float>("CPU", DataType.FLOAT));
		a.getSensors().add(new LocalSensor<Float>("Players", DataType.FLOAT));
		a.getSensors().add(new LocalSensor<String>("Console", DataType.UTF));
		a.getEmitters().add(new GenericEmitter("Command"));
		Riptide.getHost().hostDevice(a);

		Device b = new LocalDevice("SkyBlock");
		b.getSensors().add(new LocalSensor<Float>("Memory", DataType.FLOAT));
		b.getSensors().add(new LocalSensor<Float>("CPU", DataType.FLOAT));
		b.getSensors().add(new LocalSensor<Float>("Players", DataType.FLOAT));
		b.getSensors().add(new LocalSensor<String>("Console", DataType.UTF));
		b.getEmitters().add(new GenericEmitter("Command"));
		Riptide.getHost().hostDevice(b);

		Device c = new LocalDevice("Creative");
		c.getSensors().add(new LocalSensor<Float>("Memory", DataType.FLOAT));
		c.getSensors().add(new LocalSensor<Float>("CPU", DataType.FLOAT));
		c.getSensors().add(new LocalSensor<Float>("Players", DataType.FLOAT));
		c.getSensors().add(new LocalSensor<String>("Console", DataType.UTF));
		c.getEmitters().add(new GenericEmitter("Command"));
		Riptide.getHost().hostDevice(c);

		Device d = new LocalDevice("Smartwatch");
		d.getSensors().add(new LocalSensor<Float>("Memory", DataType.FLOAT));
		d.getSensors().add(new LocalSensor<Float>("CPU", DataType.FLOAT));
		d.getEmitters().add(new GenericEmitter("Notify"));
		Riptide.getHost().hostDevice(d);
		double[] fxx = new double[] {0};
		sched(() -> f.provide(((((float) Math.sin(fxx[0] += 0.1) + 0) / 1F))), 50);
		sched(() -> g.provide((float) Math.random()), 50);
	}

	private static void setupClient()
	{
		// CLIENT
		RiptideClient c = new RiptideClient();

		// Scan for devices on LAN
		c.scanForDevices(32, 1000, (f) ->
		{
		});

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
