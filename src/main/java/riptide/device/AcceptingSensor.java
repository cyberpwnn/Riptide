package riptide.device;

import java.io.InputStream;

import riptide.stream.WitholdingDataStream;

public interface AcceptingSensor<T> extends Sensor
{
	public WitholdingDataStream<T> openStream(InputStream in, int beltsize);
}
