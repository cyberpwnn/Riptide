package riptide.device;

import riptide.json.RTJSONObject;

public class GenericEmitter implements Emitter
{
	private String name;

	public GenericEmitter(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public RTJSONObject toJSON()
	{
		RTJSONObject o = new RTJSONObject();
		o.put("n", getName());

		return o;
	}

	@Override
	public void fromJSON(RTJSONObject j)
	{
		name = j.getString("n");
	}
}
