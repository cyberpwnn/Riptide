package riptide.device;

import riptide.data.DataType;

public interface Sensor extends Named, Mapped
{
	public DataType getDataType();
}
