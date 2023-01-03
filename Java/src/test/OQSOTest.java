package test;

import java.util.*;
import util.*;
import util.randoms.*;

public class OQSOTest implements ITest {
	public static final TestData OQSO;
	static {
		OQSO = new TestData();
		OQSO.setName("Overlapping-Quadruples-Sparse-Occupancy Test");
		OQSO.setDescription("");
		OQSO.setNkps(1);
		OQSO.setpSamplesStandard(100);
		OQSO.settSamplesStandard(2097152);
		OQSO.setTestMethod(new OPSOTest());
	}
	public static final int T_SAMPLES = 2097152;
	public static final double MEAN = 141909.6005321316;
	public static final double SIGMA = 294.6558723658;

	public OQSOTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		for (StandardTest currentTest : parameters) {
			currentTest.setNkps(0);
			currentTest.settSamples(T_SAMPLES);
			TestPoint pTest = new TestPoint();
			pTest.setY(MEAN);
			pTest.setSigma(SIGMA);
			pTest.setPoints(T_SAMPLES);
			Dispenser index0 = new Dispenser(), index1 = new Dispenser(), index2 = new Dispenser(),
					index3 = new Dispenser();
			index0.setRandom(rng);
			index1.setRandom(rng);
			index2.setRandom(rng);
			index3.setRandom(rng);
			//Konvertiere zu Parallel.
			for (byte i = 0; i < 3; i++) {
				index0.getBits((byte) 32);
				index1.getBits((byte) 32);
				index2.getBits((byte) 32);
				index3.getBits((byte) 32);
			}
			for (int pSample = 0; pSample < currentTest.getpSamples(); pSample++) {
				boolean[][][][] words = new boolean[32][32][32][32];
				for (int tSample = 0; tSample < T_SAMPLES; tSample++) {
					words[index0.getBitsAsInteger((byte) 5)][index1.getBitsAsInteger((byte) 5)][index2
							.getBitsAsInteger((byte) 5)][index3.getBitsAsInteger((byte) 5)] = true;
				}
				for (boolean[][][] firstLetter : words) {
					for (boolean[][] secondLetter : firstLetter) {
						for (boolean[] thirdLetter : secondLetter) {
							for (boolean fourthLetter : thirdLetter) {
								if (!fourthLetter) {
									pTest.setX(pTest.getX() + 1.0);
								}
							}
						}
					}
				}
				pTest.evaluate();
				currentTest.getpValues()[pSample]=pTest.getpValue();
			}
			currentTest.evaluate();
			currentTest.getPvLabels()[0] = "Normal Distribution of missing words";
		}
	}
	@Deprecated
	public static final void main(String... args) {
		StandardTest test = OQSO.createTest(50);
		OQSO.getTestMethod().runTestOn(new ArcfourAPlusPRG(), test);
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