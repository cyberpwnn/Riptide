package riptide.net;

@FunctionalInterface
public interface MyOwnDamnConsumer<T>
{
	public void accept(T t);
}
