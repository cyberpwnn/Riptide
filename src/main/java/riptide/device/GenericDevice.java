package riptide.device;

import java.util.ArrayList;
import java.util.List;

import riptide.data.DataType;
import riptide.json.RTJSONArray;
import riptide.json.RTJSONObject;

public class GenericDevice implements Device, Mapped
{
	private String name;
	private final List<Sensor> sensors;
	private final List<Emitter> emitters;

	public GenericDevice(String name)
	{
		this.name = name;
		sensors = new ArrayList<Sensor>();
		emitters = new ArrayList<Emitter>();
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public List<Emitter> getEmitters()
	{
		return emitters;
	}

	@Override
	public Emitter getEmitter(String name)
	{
		for(Emitter i : getEmitters())
		{
			if(i.getName().equals(name))
			{
				return i;
			}
		}

		return null;
	}

	@Override
	public List<Sensor> getSensors()
	{
		return sensors;
	}

	@Override
	public Sensor getSensor(String name)
	{
		for(Sensor i : getSensors())
		{
			if(i.getName().equals(name))
			{
				return i;
			}
		}

		return null;
	}

	@Override
	public RTJSONObject toJSON()
	{
		RTJSONArray jsens = new RTJSONArray();
		RTJSONArray jemit = new RTJSONArray();

		for(Sensor i : getSensors())
		{
			jsens.put(i.toJSON());
		}

		for(Emitter i : getEmitters())
		{
			jemit.put(i.toJSON());
		}

		RTJSONObject o = new RTJSONObject();
		o.put("n", getName());
		o.put("s", jsens);
		o.put("e", jemit);

		return o;
	}

	@Override
	public void fromJSON(RTJSONObject j)
	{
		sensors.clear();
		emitters.clear();
		name = j.getString("n");

		RTJSONArray jsens = j.getJSONArray("s");
		RTJSONArray jemit = j.getJSONArray("e");

		for(int i = 0; i < jsens.length(); i++)
		{
			Sensor s = new GenericSensor("JSON", DataType.UTF);
			s.fromJSON(jsens.getJSONObject(i));

			switch(s.getDataType())
			{
				case DOUBLE:
					s = new RemoteSensor<Double>("JSON", s.getDataType());
					break;
				case FLOAT:
					s = new RemoteSensor<Float>("JSON", s.getDataType());
					break;
				case UTF:
					s = new RemoteSensor<String>("JSON", s.getDataType());
					break;
				default:
					break;
			}

			s.fromJSON(jsens.getJSONObject(i));
			sensors.add(s);
		}

		for(int i = 0; i < jemit.length(); i++)
		{
			Emitter e = new GenericEmitter("JSON");
			e.fromJSON(jemit.getJSONObject(i));
			emitters.add(e);
		}
	}
}
