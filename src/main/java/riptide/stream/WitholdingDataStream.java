package riptide.stream;

import java.io.DataInputStream;
import java.net.Socket;

import riptide.device.AcceptingSensor;
import riptide.queue.ConveyorBelt;

public interface WitholdingDataStream<T>
{
	public Socket getSocket();

	public AcceptingSensor<T> getSensor();

	public DataInputStream getInputStream();

	public T pull();

	public void close();

	public ConveyorBelt<T> getConveyorBelt();
}
