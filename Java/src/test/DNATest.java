package test;

import java.util.*;
import util.*;
import util.randoms.*;

public class DNATest implements ITest {
	public static final TestData DNA;
	static {
		DNA = new TestData();
		DNA.setName("Overlapping DNA-Words Test");
		DNA.setDescription("Uses overlapping bits to generate ten letter long words from an alphabet with only four letters. "
				+ "The number of missing words is known only for a fixed sample size.");
		DNA.settSamplesStandard(2097152);
		DNA.setpSamplesStandard(100);
		DNA.setNkps(1);
		DNA.setTestMethod(new DNATest());
	}
	public static final double MEAN = 141910.4026047629;
	public static final double SIGMA = 337.2901506904;
	public static final int T_SAMPLE_COUNT = 2097152;
	public static final int FLATTENED = 4 * 4 * 4 * 4 * 4 * 4 * 4 * 4 * 4 * 4;

	public DNATest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		for (StandardTest currentTest : parameters) {
			currentTest.setnTuple((byte) 0);
			currentTest.settSamples(T_SAMPLE_COUNT);
			TestPoint pTest = new TestPoint();
			pTest.setY(MEAN);
			pTest.setSigma(SIGMA);
			pTest.setPoints(T_SAMPLE_COUNT);
			for (int pSample = 0; pSample < currentTest.getpSamples(); pSample++) {
				pTest.setX(0);
				boolean[] words = new boolean[FLATTENED];
				int[] indexes = new int[10];
				int index = 0;
				byte offset = 0;
				for (int tSample = 0; tSample < T_SAMPLE_COUNT; tSample++) {
					index = 0;
					if (tSample % 32 == 0) {
						for (int i = 0; i < 10; i++) {
							indexes[i] = rng.nextInt();
						}
						offset = 0;
					}
					for (int i = 0; i < 10; i++) {
						index = 4 * index + (Integer.rotateLeft(indexes[i], offset) & 3);
					}
					words[index] = true;
					offset++;
				}
				for (boolean word : words) {
					if (!word) {
						pTest.setX(pTest.getX() + 1.0);
					}
				}
				pTest.evaluate();
				currentTest.getpValues()[pSample] = pTest.getpValue();
			}
			currentTest.evaluate();
			currentTest.getPvLabels()[0] = "Normal distribution of missing words";
		}
	}

	@Deprecated
	public static final void main(String... args) {
		StandardTest test = DNA.createTest(50);
		DNA.getTestMethod().runTestOn(new CellularAutomaton(), test);
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