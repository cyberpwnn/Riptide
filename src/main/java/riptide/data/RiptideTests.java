package riptide.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import riptide.device.Device;
import riptide.device.LocalDevice;
import riptide.device.LocalSensor;
import riptide.device.ProvidingSensor;
import riptide.device.RemoteSensor;
import riptide.net.Riptide;
import riptide.stream.WitholdingDataStream;

public class RiptideTests
{
	public static void main(String[] a)
	{
		Device device = new LocalDevice("JVM");
		ProvidingSensor<Float> memory = new LocalSensor<>("memory", DataType.FLOAT);
		device.getSensors().add(memory);
		Runtime r = Runtime.getRuntime();
		Thread t = new Thread(() ->
		{
			while(!Thread.interrupted())
			{
				memory.provide((float) (r.totalMemory() - r.freeMemory()) / 1024F / 1024F);

				try
				{
					Thread.sleep(50);
				}

				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		});
		t.start();
		Riptide.getHost().hostDevice(device);
		try
		{
			Riptide.refreshDevices(new Runnable()
			{
				@Override
				public void run()
				{

				}
			});
		}
		catch(Throwable e1)
		{
			e1.printStackTrace();
		}

		try
		{
			Device d = Riptide.getDevices().get(0);

			Socket s = new Socket(d.getName().split("\\Q/\\E")[0].trim(), Riptide.PORT);
			DataInputStream din = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF("stream");
			dos.writeUTF(d.getName());
			dos.writeUTF("memory");
			dos.flush();

			if(din.readUTF().equals("start"))
			{
				@SuppressWarnings("unchecked")
				WitholdingDataStream<Float> stream = ((RemoteSensor<Float>) d.getSensor("memory")).openStream(s.getInputStream(), 32);

				Thread tx = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						while(!Thread.interrupted())
						{
							System.out.println("MemoryMB: " + stream.pull());
							try
							{
								Thread.sleep(25);
							}
							catch(InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});

				tx.start();
				tx.join();
			}
		}

		catch(Throwable e)
		{

		}

		try
		{
			t.join();
		}

		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
