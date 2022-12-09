package test;
import util.*;
import java.util.*;
@FunctionalInterface
public interface ITest {
	/**
	 * 
	 * @param rng
	 * The Source of randomness to test.
	 * @param parameters
	 * A test can choose
	 */
	void runTestOn(Random rng, StandardTest...parameters);
}