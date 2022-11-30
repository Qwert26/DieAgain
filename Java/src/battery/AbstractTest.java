package battery;
import java.util.*;
public abstract class AbstractTest implements ITest {
	protected boolean useFloats = true;
	/**
	 * 
	 */
	@Override
	public void setSetting(String identifier, Object value) {
		switch(identifier.toLowerCase()) {
		case "usefloats":
			if(value instanceof Boolean) {
				useFloats=((Boolean) value).booleanValue();
			} else if(value instanceof Number) {
				useFloats=((Number) value).doubleValue() != 0;
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		default:
			throw new IllegalArgumentException("Can not get setting: "+identifier);
		}
	}
	/**
	 * 
	 */
	@Override
	public Object getSetting(String identifier) {
		switch(identifier.toLowerCase()) {
		case "usefloats": return useFloats;
		default: throw new IllegalArgumentException("Can not get setting: "+identifier);
		}
	}
	/**
	 * 
	 */
	@Override
	public String[] availableSettings() {
		return new String[] {"usefloats"};
	}
	/**
	 * Determines if {@link Random#nextFloat()} or {@link Random#nextDouble()} should be used in the test.
	 * @param floats
	 */
	public void setUseFloats(boolean floats) {
		useFloats=floats;
	}
	/**
	 * 
	 * @return If this test is using floats or doubles.
	 */
	public boolean isUsingFloats() {
		return useFloats;
	}
	/**
	 * 
	 * @param rng
	 * @param useFloats
	 * @return A supposedly uniform value from 0 to 1 by either calling {@link Random#nextFloat()} or {@link Random#nextDouble()}.
	 */
	protected static double getUniformValue(Random rng, boolean useFloats) {
		return useFloats?rng.nextFloat():rng.nextDouble();
	}
}