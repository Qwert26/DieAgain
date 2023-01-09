package util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

public class AVPRG extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4344425970872148065L;
	private boolean initialized = false;
	private AtomicInteger a, b, c, d;
	private AtomicInteger iii, jjj, kkk;

	public AVPRG() {
		this(System.nanoTime());
	}

	public AVPRG(long seed) {
		super(seed);
		a = new AtomicInteger(0);
		b = new AtomicInteger(0);
		c = new AtomicInteger(0);
		d = new AtomicInteger(0);
		iii = new AtomicInteger(1);
		jjj = new AtomicInteger(1);
		kkk = new AtomicInteger(1);
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			a.set(0xf1ea5eed);
			b.set(a.get());
			c.set(a.get());
			d.set((int) (seed - a.get()));
			for (int i = 0; i < 20; i++) {
				next(0);
			}
		}
	}

	@Override
	protected int next(int bits) {
		int oldA, oldB, oldC, oldD, oldIII, oldJJJ, oldKKK;
		int newA, newB, newC, newD, newIII, newJJJ, newKKK;
		int e;
		do {
			newA = oldA = a.get();
			newB = oldB = b.get();
			newC = oldC = c.get();
			newD = oldD = d.get();
			newIII = oldIII = iii.get();
			newJJJ = oldJJJ = jjj.get();
			newKKK = oldKKK = kkk.get();
			e = newA - Integer.rotateRight(newB, oldIII);
			newA = newB ^ Integer.rotateRight(newC, oldJJJ);
			newB = newC + Integer.rotateRight(newD, oldKKK);
			newC = newD + e;
			newD = e + newA;
			newKKK++;
			if (newKKK == 32) {
				newKKK = 1;
				newJJJ++;
				if (newJJJ == 32) {
					newJJJ = 1;
					newIII++;
					if (newIII == 32) {
						newIII = 1;
					}
				}
			}
		} while (!(a.compareAndSet(oldA, newA) && b.compareAndSet(oldB, newB) && c.compareAndSet(oldC, newC)
				&& d.compareAndSet(oldD, newD) && iii.compareAndSet(oldIII, newIII) && jjj.compareAndSet(oldJJJ, newJJJ)
				&& kkk.compareAndSet(oldKKK, newKKK)));
		return newD >>> (32 - bits);
	}

	@Override
	public String toString() {
		return "AVPRG [" + (a != null ? "a=" + a + ", " : "") + (b != null ? "b=" + b + ", " : "")
				+ (c != null ? "c=" + c + ", " : "") + (d != null ? "d=" + d + ", " : "")
				+ (iii != null ? "iii=" + iii + ", " : "") + (jjj != null ? "jjj=" + jjj + ", " : "")
				+ (kkk != null ? "kkk=" + kkk : "") + "]";
	}
}