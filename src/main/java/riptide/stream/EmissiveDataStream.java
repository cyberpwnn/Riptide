package riptide.stream;

import java.io.DataOutputStream;

import riptide.device.ProvidingSensor;

public interface EmissiveDataStream<T>
{
	public ProvidingSensor<T> getSensor();

	public DataOutputStream getOutputStream();

	public void push(T t);

	public void close();
}
