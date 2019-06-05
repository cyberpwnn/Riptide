package riptide.device;

import riptide.net.Riptide;

public class LocalDevice extends GenericDevice
{
	public LocalDevice(String name)
	{
		super(Riptide.name + " / " + name);
	}
}
