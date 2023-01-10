package test;

import java.util.*;
import util.*;

public class LightsOnLightsOffTest implements ITest {
	public static final TestData LIGHTS_ON_LIGHTS_OFF;
	static {
		LIGHTS_ON_LIGHTS_OFF = new TestData();
		LIGHTS_ON_LIGHTS_OFF.setName("Lights On Lights Off");
		LIGHTS_ON_LIGHTS_OFF.setDescription("");
		LIGHTS_ON_LIGHTS_OFF.setNkps(1);
		LIGHTS_ON_LIGHTS_OFF.settSamplesStandard(1000000);
		LIGHTS_ON_LIGHTS_OFF.setpSamplesStandard(50);
		LIGHTS_ON_LIGHTS_OFF.setTestMethod(new LightsOnLightsOffTest());
	}

	public LightsOnLightsOffTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource = new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest current : parameters) {
			if (current.getnTuple() > 31) {
				current.setnTuple((byte) 31);
			} else if (current.getnTuple() == 0) {
				current.setnTuple((byte) 16);
			}
			byte bits = current.getnTuple();
			for (int pSample = 0; pSample < current.getpSamples(); pSample++) {
				TreeMap<Integer, Integer> tries2Counts = new TreeMap<Integer, Integer>();
				long allOn = (1L << bits) - 1, lights = 0;
				int tries;
				for (int tSample = 0; tSample < current.gettSamples(); tSample++) {
					tries = 0;
					do {
						lights |= bitSource.getBits(bits);
						tries++;
					} while (lights != allOn);
					tries2Counts.compute(tries, (k, v) -> {
						if (v == null || v == 0) {
							return 1;
						} else {
							return v + 1;
						}
					});
					tries = 0;
					do {
						lights &= bitSource.getBits(bits);
						tries++;
					} while (lights != 0);
					tries2Counts.compute(tries, (k, v) -> {
						if (v == null || v == 0) {
							return 1;
						} else {
							return v + 1;
						}
					});
				}
				TestVector pTest = new TestVector();
				pTest.setNvec(tries2Counts.lastKey() + 1);
				pTest.setNdof(0);
				double[] vector = new double[1 << bits];
				Arrays.fill(vector, 0);
				vector[0] = current.gettSamples() * 2.0;
				double sum = 0;
				for (int i = 0; i < pTest.getNvec(); i++) {
					pTest.getY()[i] = vector[vector.length - 1] - sum;
					pTest.getX()[i] = tries2Counts.getOrDefault(i, 0);
					sum = vector[vector.length - 1];
					vector = updateStateVector(vector, bits);
				}
				pTest.equalize();
				pTest.evaluateGTest();
				current.getpValues()[pSample] = pTest.getpValue();
			}
			current.evaluate();
			current.getPvLabels()[0] = "DPTD of light switching";
		}
	}

	public static final double calculateTransitionChance(long start, long end, byte bits) {
		if (start == 0) {
			return Math.pow(0.5, bits);
		}
		byte startPop = (byte) Long.bitCount(start);
		byte endPop = (byte) Long.bitCount(end);
		if ((startPop >= endPop && start != end) || ((start & end) != start)) {
			return 0;
		} else {
			return Math.pow(0.5, bits - startPop);
		}
	}

	private static final double[] updateStateVector(double[] vector, byte bits) {
		double[] ret = new double[vector.length];
		Arrays.fill(ret, 0);
		for (int start = 0; start < vector.length; start++) {
			for (int end = 0; end < vector.length; end++) {
				ret[end] += vector[start] * calculateTransitionChance(start, end, bits);
			}
		}
		return ret;
	}

	@Deprecated
	public static void main(String[] args) {
		StandardTest test = LIGHTS_ON_LIGHTS_OFF.createTest(30, 1000000);
		test.setnTuple((byte) 8);
		LIGHTS_ON_LIGHTS_OFF.getTestMethod().runTestOn(new Random(), test);
		System.out.println(test.getPvLabels()[0]);
		for (int pv = 0; pv < test.getpSamples(); pv++) {
			System.out.println(test.getpValues()[pv]);
		}
		System.out.println("Final p-Value: " + test.getKs_pValue());
		System.out.print("Final Verdict: ");
		if (test.hasFailed()) {
			System.out.println("Failed");
		} else if (test.isWeak()) {
			System.out.println("Weak");
		} else {
			System.out.println("Passed");
		}
	}
}