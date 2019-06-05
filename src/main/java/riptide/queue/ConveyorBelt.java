package riptide.queue;

import java.util.List;

/**
 * Represents a queue like conveyor belt where data is constantly filled up and
 * emptied by a consumer or when it hits it's occupancy limit
 *
 * @author cyberpwn
 *
 * @param <T>
 *            the type of data
 */
public interface ConveyorBelt<T>
{
	public void push(T t);

	public T pull();

	public boolean canPull();

	public int getOccupancy();

	public int getSize();

	public void setSize(int size);

	public ConveyorBelt<T> clone();

	public List<T> getData();
}
