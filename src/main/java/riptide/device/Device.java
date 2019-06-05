package riptide.device;

import java.util.List;

/**
 * Represents a device, a collection of sensors and emitters
 *
 * @author cyberpwn
 *
 */
public interface Device extends Named, Mapped
{
	/**
	 * Get a list of emitters for this device
	 *
	 * @return the emitters
	 */
	public List<Emitter> getEmitters();

	/**
	 * Get an emitter by name
	 *
	 * @param name
	 *            the name
	 * @return the emitter or null
	 */
	public Emitter getEmitter(String name);

	/**
	 * Get the sensors this device has
	 *
	 * @return the list of sensors
	 */
	public List<Sensor> getSensors();

	/**
	 * Get a sensor by name
	 *
	 * @param name
	 *            the sensor name
	 * @return the sensor or null
	 */
	public Sensor getSensor(String name);
}
