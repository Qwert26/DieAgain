package util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author David Jones
 * @author Christian Sch�rhoff
 */
public class JKISS32 extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5611147565705032105L;
	private AtomicInteger x;
	private AtomicInteger y;
	private AtomicInteger z;
	private AtomicInteger c;
	private boolean initialized = false;
	private static final int LCG_MULT = 314527869;
	private static final int LCG_ADD = 1234567;
	private static final long MWC_MULT = 4294584393L;

	public JKISS32() {
		this(System.nanoTime());
	}

	public JKISS32(long seed) {
		super();
		x = new AtomicInteger();
		y = new AtomicInteger();
		z = new AtomicInteger();
		c = new AtomicInteger();
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			x.set((int) (seed ^ (seed >>> 32)));
			y.set(x.get());
			z.set((int) seed);
			c.set((int) (1 + (seed >>> 32)));
		}
	}

	@Override
	protected int next(int bits) {
		int oldX, oldY, oldZ, oldC;
		int newX, newY, newZ, newC;
		long t;
		do {
			oldX = x.get();
			oldY = y.get();
			oldZ = z.get();
			oldC = c.get();

			newX = LCG_MULT * oldX + LCG_ADD;

			newY = oldY ^ (oldY << 5);
			newY ^= newY >> 7;
			newY ^= newY << 22;

			t = MWC_MULT * oldZ + oldC;
			newC = (int) (t >>> 32);
			newZ = (int) t;
		} while (!(x.compareAndSet(oldX, newX) && y.compareAndSet(oldY, newY) && z.compareAndSet(oldZ, newZ)
				&& c.compareAndSet(oldC, newC)));
		return ((newX + newY + newZ) >>> (32 - bits));
	}

	@Override
	public String toString() {
		return "JKISS32 [" + (x != null ? "x=" + x + ", " : "") + (y != null ? "y=" + y + ", " : "")
				+ (z != null ? "z=" + z + ", " : "") + (c != null ? "c=" + c : "") + "]";
	}
}