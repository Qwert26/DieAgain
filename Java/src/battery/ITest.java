package battery;
import java.util.*;
public interface ITest {
	/**
	 * Perfoms a standard test of the given Random-instance and produces two-sided p-values.
	 * @param rngToTest
	 * @return
	 */
	double[] test(Random rngToTest);
	/**
	 * Perfoms a quick test of the given Random-instance and produces two-sided p-values.
	 * @param rngToTest
	 * @return
	 */
	double[] quickTest(Random rngToTest);
	/**
	 * Sets a parameter inside the test itself
	 * @param identifier
	 * @param value
	 */
	void setSetting(String identifier, Object value);
	/**
	 * Gets a parameter inside the test itself
	 * @param identifier
	 * @return
	 */
	Object getSetting(String identifier);
	/**
	 * Available parameters inside the test itself
	 * @return
	 */
	String[] availableSettings();
}