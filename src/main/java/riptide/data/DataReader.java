package riptide.data;

import java.io.DataInputStream;
import java.io.IOException;

@FunctionalInterface
public interface DataReader<T>
{
	public Object read(DataInputStream in) throws IOException;
}
