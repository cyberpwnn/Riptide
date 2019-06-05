package riptide.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import riptide.json.RTJSONObject;

public class FileCacheProvider implements CacheProvider
{
	private final File file;
	private final Map<String, String> map;

	public FileCacheProvider(File file)
	{
		map = new HashMap<>();
		this.file = file;
		load();
	}

	@Override
	public void load()
	{
		if(file.exists())
		{
			try
			{
				BufferedReader bu = new BufferedReader(new FileReader(file));
				RTJSONObject o = new RTJSONObject(bu.readLine());
				bu.close();

				for(String i : o.keySet())
				{
					map.put(i, o.getString(i));
				}
			}

			catch(Throwable e)
			{

			}
		}
	}

	@Override
	public void save()
	{
		try
		{
			file.getParentFile().mkdirs();
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			RTJSONObject o = new RTJSONObject();

			for(String i : getKeys())
			{
				o.put(i, get(i));
			}

			pw.println(o.toString(0));
			pw.close();
		}

		catch(Throwable e)
		{

		}
	}

	@Override
	public List<String> getKeys()
	{
		return new ArrayList<>(map.keySet());
	}

	@Override
	public void put(String key, String value)
	{
		map.put(key, value);
	}

	@Override
	public boolean has(String key)
	{
		return map.containsKey(key);
	}

	@Override
	public String get(String key)
	{
		return map.get(key);
	}

	@Override
	public void clear()
	{
		map.clear();
	}
}
