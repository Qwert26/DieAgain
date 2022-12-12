package test;

import java.util.*;
import util.*;

public class LaggedSumsTest implements ITest {
	public static final TestData LAGGED_SUMS;
	static {
		LAGGED_SUMS = new TestData();
		LAGGED_SUMS.setName("Lagged Sums Test");
		LAGGED_SUMS.setDescription("");
		LAGGED_SUMS.setNkps(1);
		LAGGED_SUMS.setpSamplesStandard(100);
		LAGGED_SUMS.settSamplesStandard(1000000);
		LAGGED_SUMS.setTestMethod(new LaggedSumsTest());
	}

	public LaggedSumsTest() {
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		for (StandardTest current : parameters) {
			for (int p = 0; p < current.getpSamples(); p++) {
				TestPoint test = new TestPoint();
				test.setY(current.gettSamples() * 0.5);
				test.setX(0);
				test.setPoints(current.gettSamples());
				test.setSigma(Math.sqrt(current.gettSamples() / 12.0));
				for (int t = 0; t < current.gettSamples(); t++) {
					test.setX(rng.nextFloat() + test.getX());
					for (int lag = 0; lag < current.getnTuple(); lag++) {
						rng.nextFloat();
					}
				}
				test.evaluate();
				current.getpValues()[p] = test.getpValue();
			}
			current.evaluate();
			current.getPvLabels()[0] = "resulting CDF value for lagged Sum";
		}
	}
	
	@Deprecated
	public static void main(String... args) {
		StandardTest test = LAGGED_SUMS.createTest();
		test.setnTuple((byte) 3);
		LAGGED_SUMS.getTestMethod().runTestOn(new Random(), test);
		System.out.println(test.getPvLabels()[0]);
		for (int pv = 0; pv < test.getpSamples(); pv++) {
			System.out.println(test.getpValues()[pv]);
		}
		System.out.println("Final p-Value: " + test.getKs_pValue());
		if (test.hasFailed()) {
			System.out.println("Bits are not random.");
		} else if (test.isWeak()) {
			System.out.println("Bits are weakly non-random.");
		} else {
			System.out.println("Bits are random");
		}
	}
}