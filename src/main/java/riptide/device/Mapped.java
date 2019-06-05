package riptide.device;

import riptide.json.RTJSONObject;

public interface Mapped
{
	public RTJSONObject toJSON();

	public void fromJSON(RTJSONObject j);
}
