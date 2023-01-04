package util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

public class Arcfour8PRG extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3960190189758613104L;
	private AtomicIntegerArray s;
	private AtomicInteger i, j;
	private boolean initialized = false;

	public Arcfour8PRG() {
		this(System.nanoTime());
	}

	public Arcfour8PRG(long seed) {
		super(seed);
		s = new AtomicIntegerArray(256);
		i = new AtomicInteger(0);
		j = new AtomicInteger(0);
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			for (int ii = 0; ii < 256; ii++) {
				s.set(ii, ii);
			}
			for (int ii = 0, ij = 0, tmp; ii < 256; ii++) {
				ij = (int) ((ij + s.get(ii) + Long.rotateRight(seed, ii)) & 0xFF);
				tmp = s.get(ij);
				s.set(ii, s.get(ij));
				s.set(ij, tmp);
			}
			i.set(0);
			j.set(0);
		}
	}

	@Override
	protected int next(int bits) {
		byte rounds = (byte) Math.ceil(bits / 8.0);
		int ret = 0;
		for (; rounds > 0; rounds--) {
			int oldI, oldJ, newI, newJ, tmp;
			ret <<= 8;
			do {
				oldI = i.get();
				oldJ = j.get();

				newI = (oldI + 1) % 256;
				newJ = (oldJ + s.get(newI)) % 256;
				tmp = s.get(newI);
				s.set(newI, s.get(newJ));
				s.set(newJ, tmp);
				tmp = s.get((s.get(newI) + s.get(newJ)) % 256);
			} while (!(i.compareAndSet(oldI, newI) && j.compareAndSet(oldJ, newJ)));
			ret ^= tmp;
		}
		return ret >> (8 * rounds - bits);
	}

	@Override
	public String toString() {
		return "Arcfour8PRG [" + (s != null ? "s=" + s + ", " : "") + (i != null ? "i=" + i + ", " : "")
				+ (j != null ? "j=" + j : "") + "]";
	}
}