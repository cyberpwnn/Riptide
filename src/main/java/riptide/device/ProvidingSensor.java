package riptide.device;

import java.io.OutputStream;
import java.util.Map;

import riptide.queue.ConveyorBelt;
import riptide.stream.EmissiveDataStream;

public interface ProvidingSensor<T> extends Sensor
{
	public void provide(T t);

	public Map<String, EmissiveDataStream<T>> getStreams();

	public EmissiveDataStream<T> getStream(String name);

	public void closeStream(String name);

	public void closeAllStreams();

	public ConveyorBelt<T> getConveyorBelt();

	public EmissiveDataStream<T> openStream(String name, OutputStream out);
}
