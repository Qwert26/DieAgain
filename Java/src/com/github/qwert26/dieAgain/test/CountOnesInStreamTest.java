package com.github.qwert26.dieAgain.test;

import java.util.*;
import com.github.qwert26.dieAgain.util.*;
import com.github.qwert26.dieAgain.util.randoms.*;

public class CountOnesInStreamTest implements ITest {
	public static final TestData COUNT1S_STREAM;
	private static final byte[] BYTE_TO_LETTER = { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 2, 0, 0, 0, 1, 0, 1, 1,
			2, 0, 1, 1, 2, 1, 2, 2, 3, 0, 0, 0, 1, 0, 1, 1, 2, 0, 1, 1, 2, 1, 2, 2, 3, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2,
			3, 2, 3, 3, 4, 0, 0, 0, 1, 0, 1, 1, 2, 0, 1, 1, 2, 1, 2, 2, 3, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3,
			4, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 4, 0, 0, 0,
			1, 0, 1, 1, 2, 0, 1, 1, 2, 1, 2, 2, 3, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 0, 1, 1, 2, 1, 2, 2,
			3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 4, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2,
			3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4,
			4, 2, 3, 3, 4, 3, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4 };
	private static final double[] LETTER_PROBABILITIES = { 0.14453125, 0.21875, 0.2734375, 0.21875, 0.14453125 };
	static {
		COUNT1S_STREAM = new TestData();
		COUNT1S_STREAM.setName("Count 1s in Stream");
		COUNT1S_STREAM.setDescription("");
		COUNT1S_STREAM.setNkps(1);
		COUNT1S_STREAM.setpSamplesStandard(100);
		COUNT1S_STREAM.settSamplesStandard(256000);
		COUNT1S_STREAM.setTestMethod(new CountOnesInStreamTest());
	}

	public CountOnesInStreamTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser byteSource = new Dispenser();
		byteSource.setRandom(rng);
		for (StandardTest currentTest : parameters) {
			currentTest.setnTuple((byte) 0);
			TestPoint pTest = new TestPoint();
			pTest.setY(2500);
			pTest.setSigma(Math.sqrt(5000));
			pTest.setPoints(currentTest.gettSamples());
			TestVector vTest4 = new TestVector(), vTest5 = new TestVector();
			vTest4.setNvec(625);
			vTest4.setCutoff(5);
			vTest4.setNdof(0);
			vTest5.setNvec(3125);
			vTest5.setCutoff(5);
			vTest5.setNdof(0);
			for (int i = 0, j; i < 625; i++) {
				vTest4.getY()[i] = currentTest.gettSamples();
				j = i;
				for (int k = 0; k < 4; k++) {
					vTest4.getY()[i] *= LETTER_PROBABILITIES[j % 5];
					j /= 5;
				}
			}
			for (int i = 0, j; i < 3125; i++) {
				vTest5.getY()[i] = currentTest.gettSamples();
				j = i;
				for (int k = 0; k < 5; k++) {
					vTest5.getY()[i] *= LETTER_PROBABILITIES[j % 5];
					j /= 5;
				}
			}
			for (int pSample = 0; pSample < currentTest.getpSamples(); pSample++) {
				for (int i = 0; i < 625; i++) {
					vTest4.getX()[i] = 0.0;
				}
				for (int i = 0; i < 3125; i++) {
					vTest5.getX()[i] = 0.0;
				}
				int index = 0;
				for (int i = 0; i < 4; i++) {
					index *= 5;
					index += BYTE_TO_LETTER[byteSource.getBitsAsInteger((byte) 8)];
				}
				for (int tSample = 0; tSample < currentTest.gettSamples(); tSample++) {
					index *= 5;
					index += BYTE_TO_LETTER[byteSource.getBitsAsInteger((byte) 8)];
					index %= 3125;
					vTest5.getX()[index]++;
					vTest4.getX()[index % 625]++;
				}
				vTest4.evaluate();
				vTest5.evaluate();
				pTest.setX(vTest5.getChsq()-vTest4.getChsq());
				pTest.evaluate();
				currentTest.getpValues()[pSample]=pTest.getpValue();
			}
			currentTest.getPvLabels()[0]="Normal Distribution of Chi-Square Difference";
			currentTest.evaluate();
		}
	}
	@Deprecated
	public static void main(String... args) {
		StandardTest test = COUNT1S_STREAM.createTest(50);
		COUNT1S_STREAM.getTestMethod().runTestOn(new PCGHash(), test);
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