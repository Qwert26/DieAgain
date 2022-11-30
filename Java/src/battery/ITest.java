package battery;
import java.util.*;
public interface ITest {
	/**
	 * Perfoms a standard test of the given Random-instance and produces p-values.
	 * @param rngToTest
	 * @return Multiple values between 0 and 1, indicating the likelyhood of randomness. Values under 0.01 can indicate that the tested PNRG has problems.
	 */
	double[] test(Random rngToTest);
	/**
	 * Perfoms a quick test of the given Random-instance and produces p-values.
	 * @param rngToTest
	 * @return Multiple values between 0 and 1, indicating the likelyhood of randomness. Values under 0.01 can indicate that the tested PNRG has problems.
	 */
	double[] quickTest(Random rngToTest);
	/**
	 * Sets a parameter inside the test itself.
	 * @param identifier
	 * @param value
	 */
	void setSetting(String identifier, Object value);
	/**
	 * Gets a parameter inside the test itself.
	 * @param identifier
	 * @return
	 */
	Object getSetting(String identifier);
	/**
	 * Available parameters inside the test itself.
	 * @return
	 */
	String[] availableSettings();
}