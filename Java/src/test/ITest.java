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
	 * Each complete run of a single Test uses one of the given parameters.
	 */
	void runTestOn(Random rng, StandardTest...parameters);
}