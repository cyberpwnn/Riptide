package riptide.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import riptide.cache.CacheProvider;
import riptide.device.Device;
import riptide.device.GenericDevice;
import riptide.device.RemoteSensor;
import riptide.device.Sensor;
import riptide.json.RTJSONArray;
import riptide.stream.WitholdingDataStream;

public class Riptide
{
	public static final int PORT = 34783;
	private static RiptideHost host;
	public static String name = getHostName();
	private static Map<String, List<Device>> alldevices;
	public static CacheProvider cache;

	@SuppressWarnings("unchecked")
	public static <T> WitholdingDataStream<T> stream(Device device, Sensor sensor) throws IOException
	{
		Socket s = new Socket(device.getName().split("\\Q/\\E")[0].trim(), Riptide.PORT);
		DataInputStream din = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		dos.writeUTF("stream");
		dos.writeUTF(device.getName());
		dos.writeUTF(sensor.getName());
		dos.flush();
		System.out.println("Flushed");

		if(din.readUTF().equals("start"))
		{
			WitholdingDataStream<T> t = (WitholdingDataStream<T>) (((RemoteSensor<T>) sensor).openStream(s.getInputStream(), s, 32));
			return t;
		}

		try
		{
			s.close();
		}

		catch(Throwable e)
		{

		}

		return null;
	}

	public static List<Device> getDevices()
	{
		List<Device> d = new ArrayList<>();

		for(String i : alldevices.keySet())
		{
			d.addAll(alldevices.get(i));
		}

		return d;
	}

	public static void refreshDevices() throws IOException, InterruptedException
	{
		refreshDevices(new Runnable()
		{
			@Override
			public void run()
			{

			}
		});
	}

	public static void refreshDevices(Runnable update) throws IOException, InterruptedException
	{
		alldevices = new HashMap<>();
		Map<String, String> netmap = LMap.mapLan();
		ExecutorService ex = Executors.newWorkStealingPool(netmap.size());

		for(String i : netmap.keySet())
		{
			ex.submit(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						List<Device> devices = refreshDevices(i);

						for(Device i : devices)
						{
							System.out.println("[Riptide]: Found Device \"" + i.getName() + "\" with " + i.getSensors().size() + " sensers & " + i.getEmitters().size() + " emitters.");

							for(Sensor j : i.getSensors())
							{
								System.out.println("[Riptide]: - Sensor \"" + j.getName() + "\" Type: " + j.getDataType().name());
							}
						}

						synchronized(alldevices)
						{
							alldevices.put(i, devices);
							update.run();
						}
					}

					catch(Throwable e)
					{

					}
				}
			});
		}

		ex.shutdown();
		ex.awaitTermination(5, TimeUnit.SECONDS);
	}

	public static List<Device> refreshDevices(String ip) throws Throwable
	{
		List<Device> devices = new ArrayList<>();
		Socket s = new Socket(ip, PORT);
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		DataInputStream din = new DataInputStream(s.getInputStream());
		dos.writeUTF("list");
		dos.flush();
		RTJSONArray j = new RTJSONArray(din.readUTF());
		s.close();

		for(int i = 0; i < j.length(); i++)
		{
			Device d = new GenericDevice("");
			d.fromJSON(j.getJSONObject(i));
			devices.add(d);
		}

		return devices;
	}

	public static RiptideHost getHost()
	{
		if(host == null)
		{
			try
			{
				host = new RiptideHost();
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		return host;
	}

	private static String getHostName()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}

		catch(Throwable ex)
		{
			ex.printStackTrace();
		}

		return "CP" + (int) (Math.random() * 99);
	}

	public static Device findDevice(String deviceName)
	{
		for(Device i : getDevices())
		{
			if(i.getName().equals(deviceName) || i.getName().endsWith(deviceName))
			{
				return i;
			}
		}

		return null;
	}
}
