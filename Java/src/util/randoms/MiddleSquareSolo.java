package util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

public class MiddleSquareSolo extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3139031035635606891L;
	private AtomicLong a, usedSeed;
	private boolean initialized = false;

	public MiddleSquareSolo() {
		this(System.nanoTime());
	}

	public MiddleSquareSolo(long seed) {
		super(seed);
		a = new AtomicLong(seed);
		usedSeed = new AtomicLong(seed);
		initialized = true;
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			a.set(seed);
			usedSeed.set(seed);
		}
	}

	@Override
	protected int next(int bits) {
		long oldA, oldUS;
		long newA, newUS;
		long tempH, tempL;
		do {
			newA = oldA = a.get();
			newUS = oldUS = usedSeed.get();
			if (newA == 0) {
				newUS = Long.rotateLeft(newUS, 1) + 1;
				newA = newUS;
			}
			tempH = Math.multiplyHigh(newA, newA);
			tempL = newA * newA;
			newA = (tempL >>> 32) ^ (tempH << 32);
			newA += (tempL << 32) ^ (tempH >>> 32);
		} while (!(a.compareAndSet(oldA, newA) && usedSeed.compareAndSet(oldUS, newUS)));
		return (int) ((newA ^ (newA << 32)) >>> (64 - bits));
	}
}