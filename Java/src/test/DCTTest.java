package test;

import java.util.*;
import util.*;

public class DCTTest implements ITest {
	public static final TestData DCT;
	static {
		DCT = new TestData();
		DCT.setName("Discrete Cosinus Transformation");
		DCT.setDescription("");
		DCT.setpSamplesStandard(1);
		DCT.settSamplesStandard(50000);
		DCT.setNkps(1);
		DCT.setTestMethod(new DCTTest());
		DCT.setExtra(256);
	}

	public DCTTest() {
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource = new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest current : parameters) {
			if (current.getXyz() == null || current.getXyz().length < 1) {
				current.setXyz(256);
			}
			if (current.getnTuple() == 0) {
				current.setnTuple((byte) 16);
			}
			final int v = 1 << (current.getnTuple() - 1);
			final double mean = current.getXyz()[0] * (v - 0.5);
			final double sd = Math.sqrt(current.getXyz()[0] / 6.0) * v;
			final boolean useFallback = current.gettSamples() < 5 * current.getXyz()[0];
			for (int psample = 0; psample < current.getpSamples(); psample++) {
				double[] dct = null;
				int[] input = new int[(int) current.getXyz()[0]];
				long[] positionCounts = useFallback ? null : new long[input.length];
				double[] pValues = useFallback ? new double[input.length * current.gettSamples()] : null;
				TestPoint pTest = new TestPoint();
				pTest.setSigma(1);
				pTest.setY(0);
				for (int tSample = 0; tSample < current.gettSamples(); tSample++) {
					for (int i = 0; i < input.length; i++) {
						input[i] = (int) bitSource.getBits(current.getnTuple());
					}
					dct = discreteCosineTransform(input);
					dct[0] -= mean;
					dct[0] /= Math.sqrt(2);
					if (useFallback) {
						for (int i = 0; i < dct.length; i++) {
							pTest.setX(dct[i] / sd);
							pTest.evaluate();
							pValues[tSample * dct.length + i] = pTest.getpValue();
						}
					} else {
						double max = 0;
						int pos = -1;
						for (int i = 0; i < dct.length; i++) {
							if (Math.abs(dct[i]) > max) {
								pos = i;
								max = Math.abs(dct[i]);
							}
						}
						positionCounts[pos]++;
					}
				}
				if (useFallback) {
					current.getpValues()[psample] = Functions.ksTest(pValues);
				} else {
					TestVector p = new TestVector();
					p.setNvec(input.length);
					p.setCutoff(0);
					for (int i = 0; i < input.length; i++) {
						p.getX()[i] = positionCounts[i];
						p.getY()[i] = current.gettSamples() / current.getXyz()[0];
					}
					p.evaluate();
					current.getpValues()[psample] = p.getpValue();
				}
			}
			current.evaluate();
			current.getPvLabels()[0] = useFallback ? "Uniformity of entire DCT" : "Uniformity of DCT-Peaks";
		}
	}

	private static double[] discreteCosineTransform(int[] input) {
		double[] output = new double[input.length];
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				output[i] += input[j] * Math.cos((Math.PI / input.length) * (j + 0.5) * i);
			}
		}
		return output;
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = DCT.createTest(48, 0x1000);
		test.setXyz(64);
		test.setnTuple((byte) 16);
		DCT.getTestMethod().runTestOn(new util.randoms.ArcfourPRG(), test);
		System.out.println(test.getPvLabels()[0]);
		for (double p : test.getpValues()) {
			System.out.println(p);
		}
		System.out.println("Final KS p-Value: " + test.getKs_pValue());
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