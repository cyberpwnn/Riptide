package riptide.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LMap
{
	public static Map<String, String> mapLan() throws IOException
	{
		String lanip = getLanAddress().getHostAddress();
		String[] sub = lanip.split("\\Q.\\E");
		return mapLan(sub[0] + "." + sub[1] + "." + sub[2]);
	}

	public static Map<String, String> mapLan(String... subnets) throws IOException
	{
		Map<String, String> addresses = new HashMap<String, String>();
		ExecutorService ex = Executors.newWorkStealingPool(64);
		int timeout = 1000;

		for(String subnet : subnets)
		{
			for(int ii = 1; ii < 255; ii++)
			{
				int i = ii;

				ex.submit(new Runnable()
				{
					@Override
					public void run()
					{
						String host = subnet + "." + i;

						try
						{
							if(InetAddress.getByName(host).isReachable(timeout))
							{
								String s = InetAddress.getByName(host).getHostName();

								if(s.equals(host))
								{
									try
									{
										s = "Node " + host.split("\\Q.\\E")[3];
									}

									catch(Throwable e)
									{

									}
								}

								else
								{
									try
									{
										s = s.split("\\Q.\\E")[0];
									}

									catch(Throwable e)
									{

									}
								}

								addresses.put(host, s);
								System.out.println("[Riptide]: LANMapper Found " + host + " (" + s + ")");
							}
						}

						catch(IOException e)
						{

						}
					}
				});
			}
		}

		ex.shutdown();
		try
		{
			ex.awaitTermination(10000, TimeUnit.SECONDS);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

		return addresses;
	}

	public static InetAddress getLanAddress() throws UnknownHostException
	{
		try
		{
			InetAddress candidateAddress = null;
			for(Enumeration<?> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();)
			{
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				for(Enumeration<?> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();)
				{
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if(!inetAddr.isLoopbackAddress())
					{

						if(inetAddr.isSiteLocalAddress())
						{
							return inetAddr;
						}
						else if(candidateAddress == null)
						{
							candidateAddress = inetAddr;
						}
					}
				}
			}
			if(candidateAddress != null)
			{

				return candidateAddress;
			}

			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if(jdkSuppliedAddress == null)
			{
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		}
		catch(Exception e)
		{
			UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}
}
