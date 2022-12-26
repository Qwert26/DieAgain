package simulation;

import java.util.*;

public class SBoxCycles {
	public byte bits = 8;

	public SBoxCycles() {
	}

	private static long determineCycle(int[] sbox) {
		if (sbox == null || sbox.length == 0) {
			return 0;
		} else {
			boolean[] visited = new boolean[sbox.length];
			long result = 1;
			for (int i = 0; i < sbox.length; i++) {
				if (!visited[i]) {
					visited[i] = true;
					int current = 1;
					for (int j = sbox[i]; j != i; j = sbox[j]) {
						current++;
						visited[j] = true;
					}
					result *= current / gcd(result, current);
				}
			}
			return result;
		}
	}

	private static long gcd(long a, long b) {
		if (a == 0) {
			return b;
		} else if (b == 0) {
			return a;
		} else if (a == b) {
			return a;
		}
		if (a < b) {
			a ^= b;
			b ^= a;
			a ^= b;
		}
		return gcd(b, a % b);
	}

	public int[] createIdentitySBox() {
		int[] ret = new int[1 << bits];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = i;
		}
		return ret;
	}

	public void shuffleSBox(Random rng, int[] sbox) {
		for (int i = 0; i < sbox.length; i++) {
			int j = (i + rng.nextInt(sbox.length)) % sbox.length;
			if (i != j) {
				sbox[i] ^= sbox[j];
				sbox[j] ^= sbox[i];
				sbox[i] ^= sbox[j];
			}
		}
	}

	public static void main(String[] args) {
		SBoxCycles sim = new SBoxCycles();
		sim.bits = 4;
		int[] sbox = sim.createIdentitySBox();
		System.out.println("Numbers: "+sbox.length);
		Random rng = new Random();
		TreeMap<Long, Long> cycleSize2Count = new TreeMap<>();
		for (long count = 1000000L; count > 0; count--) {
			sim.shuffleSBox(rng, sbox);
			cycleSize2Count.compute(SBoxCycles.determineCycle(sbox), (s, c) -> {
				if (c == null || c == 0) {
					return 1L;
				} else {
					return c + 1;
				}
			});
		}
		System.out.println(cycleSize2Count);
	}
}