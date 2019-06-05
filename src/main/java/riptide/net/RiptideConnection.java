package riptide.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.UUID;

import riptide.device.Device;
import riptide.device.ProvidingSensor;
import riptide.device.Sensor;
import riptide.json.RTJSONArray;

public class RiptideConnection extends Thread
{
	private Socket socket;
	private UUID clientID;
	private Sensor relatedSensor;

	public RiptideConnection(Socket socket)
	{
		this.socket = socket;
		clientID = UUID.randomUUID();
		start();
	}

	@Override
	public void run()
	{
		try
		{
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			while(!interrupted() && !socket.isClosed())
			{
				String command = in.readUTF();

				if(command.equalsIgnoreCase("list"))
				{
					RTJSONArray ja = new RTJSONArray();

					for(Device i : Riptide.getHost().getDevices())
					{
						ja.put(i.toJSON());
					}

					out.writeUTF(ja.toString(0));
					out.flush();
				}

				else if(command.equalsIgnoreCase("stream"))
				{
					String device = in.readUTF();
					Sensor found = null;

					for(Device i : Riptide.getHost().getDevices())
					{
						if(i.getName().equals(device))
						{
							String sensor = in.readUTF();

							for(Sensor j : i.getSensors())
							{
								if(j.getName().equals(sensor))
								{
									found = j;
									break;
								}
							}

							break;
						}
					}

					if(found != null)
					{
						relatedSensor = found;
						out.writeUTF("start");
						out.flush();
						((ProvidingSensor<?>) found).openStream(clientID.toString(), socket.getOutputStream());
					}

					else
					{
						out.writeUTF("error");
						out.flush();
					}
				}

				else if(command.equalsIgnoreCase("stop"))
				{
					try
					{
						((ProvidingSensor<?>) relatedSensor).closeStream(clientID.toString());
					}

					catch(Throwable e)
					{

					}

					socket.close();
					return;
				}

				Thread.sleep(250);
			}
		}

		catch(Throwable e)
		{

		}
	}
}
