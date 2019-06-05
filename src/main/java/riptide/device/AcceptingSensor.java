package riptide.device;

import java.io.InputStream;
import java.net.Socket;

import riptide.stream.WitholdingDataStream;

public interface AcceptingSensor<T> extends Sensor
{
	public WitholdingDataStream<T> openStream(InputStream in, Socket socket, int beltsize);
}
