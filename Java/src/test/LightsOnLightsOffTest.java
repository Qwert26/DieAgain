package test;

import java.util.*;
import util.*;

public class LightsOnLightsOffTest implements ITest {
	public LightsOnLightsOffTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
	}

	private static final double[][] createAbsorbingMarkovMatrix(byte size) {
		if (size >= 31) {
			throw new IllegalArgumentException("Too many bits!");
		} else {
			int entries = 1 << size;
			double[][] ret = new double[entries][entries];
			ret[entries - 1][entries - 1] = 1;
			for (int end = 0; end < entries; end++) {
				ret[0][end] = 1.0 / entries;
			}
			for (int start = 1, startPop; start < entries - 1; start++) {
				startPop = Integer.bitCount(start);
				for (int end = 0, endPop; end < entries; end++) {
					endPop = Integer.bitCount(end);
					if ((startPop >= endPop && start != end) || ((start & end) == 0)) {
						ret[start][end] = 0;
					} else {
						ret[start][end] = 1.0 / (entries >> startPop);
					}
				}
			}
			return ret;
		}
	}

	@Deprecated
	public static void main(String[] args) {
		byte bits = (byte) 5;
		double[][] matrix = createAbsorbingMarkovMatrix(bits);
		double[] vector = new double[matrix.length];
		vector[0] = 1;
	}
}