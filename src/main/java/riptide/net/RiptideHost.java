package riptide.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import riptide.device.Device;

public class RiptideHost extends Thread
{
	private List<Device> devices;
	private List<RiptideConnection> connections;
	private ServerSocket server;

	public RiptideHost() throws IOException
	{
		devices = new ArrayList<Device>();
		connections = new ArrayList<RiptideConnection>();
		start();
	}

	@Override
	public void run()
	{
		try
		{
			server = new ServerSocket(Riptide.PORT);
			server.setSoTimeout(1000);
		}

		catch(Throwable e)
		{
			e.printStackTrace();
			return;
		}

		while(!interrupted())
		{
			try
			{
				connections.add(new RiptideConnection(server.accept()));
			}

			catch(SocketTimeoutException e)
			{
				continue;
			}

			catch(Throwable e)
			{
				e.printStackTrace();
				break;
			}
		}

		try
		{
			server.close();
		}

		catch(Throwable e)
		{

		}
	}

	public void hostDevice(Device device)
	{
		devices.add(device);
	}

	public List<Device> getDevices()
	{
		return devices;
	}
}
