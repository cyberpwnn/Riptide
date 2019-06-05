package riptide.queue;

import java.util.ArrayList;
import java.util.List;

public class GenericConveyorBelt<T> implements ConveyorBelt<T>
{
	private int size;
	private List<T> data;

	public GenericConveyorBelt(int size)
	{
		this.size = size;
		data = new ArrayList<T>(size);
	}

	@Override
	public void push(T t)
	{
		data.add(t);
		trim();
	}

	private void trim()
	{
		while(getOccupancy() > getSize())
		{
			data.remove(0);
		}
	}

	@Override
	public T pull()
	{
		if(!canPull())
		{
			return null;
		}

		T t = data.get(0);
		data.remove(0);
		return t;
	}

	@Override
	public boolean canPull()
	{
		return getOccupancy() > 0;
	}

	@Override
	public int getOccupancy()
	{
		return data.size();
	}

	@Override
	public int getSize()
	{
		return size;
	}

	@Override
	public void setSize(int size)
	{
		this.size = size;
		trim();
	}

	public GenericConveyorBelt<T> setData(List<T> t)
	{
		data = t;
		return this;
	}

	@Override
	public ConveyorBelt<T> clone()
	{
		return new GenericConveyorBelt<T>(size).setData(data);
	}

	@Override
	public List<T> getData()
	{
		return data;
	}

}
