package riptide.cache;

import java.util.List;

public interface CacheProvider
{
	public List<String> getKeys();

	public void put(String key, String value);

	public boolean has(String key);

	public String get(String key);

	public void load();

	public void save();

	public void clear();
}
