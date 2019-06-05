package riptide.device;

import riptide.data.DataType;
import riptide.json.RTJSONObject;

public class GenericSensor implements Sensor
{
	private String name;
	private DataType type;

	public GenericSensor(String name, DataType type)
	{
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public DataType getDataType()
	{
		return type;
	}

	@Override
	public RTJSONObject toJSON()
	{
		RTJSONObject o = new RTJSONObject();
		o.put("n", getName());
		o.put("t", getDataType().ordinal());

		return o;
	}

	@Override
	public void fromJSON(RTJSONObject j)
	{
		name = j.getString("n");
		type = DataType.values()[j.getInt("t")];
	}
}
