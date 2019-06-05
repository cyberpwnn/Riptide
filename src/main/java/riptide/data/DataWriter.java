package riptide.data;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface DataWriter<T>
{
	public void write(Object t, DataOutputStream dos) throws IOException;
}
