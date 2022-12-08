package test;
import util.*;
import java.util.*;
@FunctionalInterface
public interface ITest {
	void runTestOn(Random rng, StandardTest[] parameters);
}