package util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

public class MiddleSquare extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3139031035635606891L;
	public static final long MASK = 0xFFFF00000000L;
	private AtomicLong a, usedSeed;
	private boolean initialized = false;

	public MiddleSquare() {
		this(System.nanoTime());
	}

	public MiddleSquare(long seed) {
		super(seed);
		a = new AtomicLong(seed);
		usedSeed = new AtomicLong(seed);
		initialized = true;
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			a.set(seed);
		}
	}

	@Override
	protected int next(int bits) {
		long oldA, oldUS;
		long newA, newUS;
		do {
			newA = oldA = a.get();
			newUS = oldUS = usedSeed.get();
			if (newA == 0) {
				newUS = Long.rotateLeft(newUS, 1) + 1;
				newA = newUS;
			}
			newA *= newA;
			newA >>>= 16;
			newA = Integer.toUnsignedLong((int) newA) + ((newA & MASK) >>> 32);
		} while (!(a.compareAndSet(oldA, newA) && usedSeed.compareAndSet(oldUS, newUS)));
		return (int) (newA >>> (32 - bits));
	}
}