package io.github.qwert26.dieAgain.util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

public class QSPLFSR extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2151898697988887002L;
	public static final int TAPS_13 = 0x1C80;
	public static final int TAPS_15 = 0x6000;
	public static final int TAPS_17 = 0x12000;
	public static final int TAPS_19 = 0x72000;
	/**
	 * Contains the collective State of four LFSRs:
	 * <ul>
	 * <li>13 Bits</li>
	 * <li>15 Bits</li>
	 * <li>17 Bits</li>
	 * <li>19 Bits</li>
	 * </ul>
	 */
	private AtomicLong states;
	/**
	 * 
	 */
	private AtomicInteger permutation;

	public QSPLFSR() {
		this(System.nanoTime());
	}

	public QSPLFSR(long seed) {
		states = new AtomicLong();
		permutation = new AtomicInteger();
		super(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		states.set(seed);
		permutation.set(Long.bitCount(seed) % 24);
	}

	@Override
	protected int next(final int bits) {
		int ret = 0;
		long lastState, nextState;
		int lastPerm, nextPerm;
		do {
			lastState = nextState = states.get();
			lastPerm = nextPerm = permutation.get();
			for (int produced = 0; produced < bits; produced++) {
				ret <<= 1;
				long workingState = nextState;
				nextState = 0;
				byte[] sizes = decode(nextPerm);
				for (byte size : sizes) {
					int temp = (int) (workingState & ((1 << size) - 1));
					if ((Integer.bitCount(temp & getTaps(size)) & 1) == 0) {
						// Even
						temp <<= 1;
					} else {
						// Odd
						temp <<= 1;
						temp |= 1;
						ret ^= 1;
						nextPerm += (size - 12);
						nextPerm %= 24;
					}
					nextState <<= size;
					nextState |= temp & ((1 << size) - 1);
					workingState >>>= size;
				}
			}
		} while (!states.compareAndSet(lastState, nextState) && !permutation.compareAndSet(lastPerm, nextPerm));
		return ret;
	}

	private static final int getTaps(byte size) {
		return switch (size) {
		case 13 -> TAPS_13;
		case 15 -> TAPS_15;
		case 17 -> TAPS_17;
		case 19 -> TAPS_19;
		default -> throw new IllegalArgumentException("Size outside of valid values!");
		};
	}

	/**
	 * 
	 * @param ordinal
	 * @return
	 */
	private static final byte[] decode(final int ordinal) {
		return switch (ordinal) {
		case 0 -> new byte[] { 13, 15, 17, 19 };
		case 1 -> new byte[] { 13, 15, 19, 17 };
		case 2 -> new byte[] { 13, 17, 15, 19 };
		case 3 -> new byte[] { 13, 17, 19, 15 };
		case 4 -> new byte[] { 13, 19, 15, 19 };
		case 5 -> new byte[] { 13, 19, 19, 15 };

		case 6 -> new byte[] { 15, 13, 17, 19 };
		case 7 -> new byte[] { 15, 13, 19, 17 };
		case 8 -> new byte[] { 15, 17, 13, 19 };
		case 9 -> new byte[] { 15, 17, 19, 13 };
		case 10 -> new byte[] { 15, 19, 13, 17 };
		case 11 -> new byte[] { 15, 19, 17, 13 };

		case 12 -> new byte[] { 17, 15, 13, 19 };
		case 13 -> new byte[] { 17, 15, 19, 13 };
		case 14 -> new byte[] { 17, 13, 15, 19 };
		case 15 -> new byte[] { 17, 13, 19, 15 };
		case 16 -> new byte[] { 17, 19, 15, 13 };
		case 17 -> new byte[] { 17, 19, 13, 15 };

		case 18 -> new byte[] { 19, 13, 17, 15 };
		case 19 -> new byte[] { 19, 13, 15, 17 };
		case 20 -> new byte[] { 19, 17, 13, 15 };
		case 21 -> new byte[] { 19, 17, 15, 13 };
		case 22 -> new byte[] { 19, 15, 13, 17 };
		case 23 -> new byte[] { 19, 15, 17, 13 };
		default -> throw new IllegalArgumentException("Ordinal not in interval [0;23]!");
		};
	}
}