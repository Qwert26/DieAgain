package io.github.qwert26.dieAgain.test;
import java.util.*;

import io.github.qwert26.dieAgain.util.*;
/**
 * 
 * @author Christian Schürhoff
 * @see FunctionalInterface
 *
 */
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