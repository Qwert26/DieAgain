package util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

public class ArcfourAPlusPRG extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -522360271923578108L;
	private AtomicIntegerArray s1, s2;
	private AtomicInteger i, j1, j2, k1, k2;
	private boolean initialized = false;
	public static final int W = 3;
	public static final int W1 = 15;
	public static final int W2 = 17;
	public static final int B1 = 1;
	public static final int B2 = 1;

	public ArcfourAPlusPRG() {
		this(System.nanoTime());
	}

	public ArcfourAPlusPRG(long seed) {
		super(seed);
		s1 = new AtomicIntegerArray(256);
		s2 = new AtomicIntegerArray(256);
		i = new AtomicInteger(0);
		j1 = new AtomicInteger(0);
		j2 = new AtomicInteger(0);
		k1 = new AtomicInteger(0);
		k2 = new AtomicInteger(0);
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			for (int ii = 0; ii < 256; ii++) {
				s1.set(ii, ii);
				s2.set(ii, ii);
			}
			for (int ii = 0, ij1 = 0, ij2 = 0, tmp; ii < 256; ii++) {
				ij1 = (int) ((ij1 + s1.get(ij1) + Long.rotateLeft(seed, ii)) & 0xFF);
				ij2 = (int) ((ij2 + s1.get(ij2) + Long.rotateRight(seed, ii)) & 0xFF);
				tmp = s1.get(ij1);
				s1.set(ij1, s1.get(ii));
				s1.set(ii, tmp);
				tmp = s2.get(ij2);
				s2.set(ij2, s2.get(ii));
				s2.set(ii, tmp);
			}
			for (int ii = 0, ij1 = 0, ij2 = 0, tmp; ii < 1024; ii++) {
				ij1 = (int) ((ij1 + s1.get(ij1) + W1) % 256);
				ij2 = (int) ((ij2 + s1.get(ij2) + W2) % 256);
				tmp = s1.get(ij1);
				s1.set(ij1, s1.get(ii % 256));
				s1.set(ii % 256, tmp);
				tmp = s2.get(ij2);
				s2.set(ij2, s2.get(ii % 256));
				s2.set(ii % 256, tmp);
			}
			i.set(0);
			j1.set(0);
			j2.set(0);
			k1.set(0);
			k2.set(0);
		}
	}

	@Override
	protected int next(int bits) {
		int ret = 0, c1, c2, a1, a2, output, tmp;
		int oldI, oldJ1, oldJ2, oldK1, oldK2;
		int newI, newJ1, newJ2, newK1, newK2;
		for (byte b = 0; b < 2; b++) {
			ret <<= 16;
			do {
				oldI = i.get();
				oldJ1 = j1.get();
				oldJ2 = j2.get();
				oldK1 = k1.get();
				oldK2 = k2.get();

				newI = (oldI + W) % 256;
				a1 = s1.get(newI);
				a2 = s2.get(newI);
				newJ1 = (oldK1 + a1 + s1.get((oldJ1 + s2.get(newI)) % 256)) % 256;
				newK1 = (oldK1 + newI + s1.get(newJ1)) % 256;
				newJ2 = (oldK2 + a2 + s2.get((oldJ2 + s1.get(newI)) % 256)) % 256;
				newK2 = (oldK2 + newI + s2.get(newJ2)) % 256;

				c1 = (s1.get(((newI << 5) ^ (newJ1 >> 3)) % 256) + s1.get(((newJ1 << 5) ^ (newI >> 3)) % 256)) % 256;
				c2 = (s2.get(((newI << 5) ^ (newJ2 >> 3)) % 256) + s2.get(((newJ2 << 5) ^ (newI >> 3)) % 256)) % 256;

				output = s1.get((newJ1 + s2.get(
						(newI + (s1.get((a1 + B1) % 256) + s1.get(c1 ^ 0xAA)) ^ (s1.get((newJ1 + B1) % 256) + newK1))
								% 256))
						% 256);
				output <<= 8;
				output ^= s2.get((newJ2 + s1.get(
						(newI + (s2.get((a2 + B2) % 256) + s2.get(c2 ^ 0x55)) ^ (s2.get((newJ2 + B2) % 256) + newK2))
								% 256))
						% 256);

				tmp = s1.get(newJ1);
				s1.set(newJ1, s1.get(newI));
				s1.set(newI, tmp);
				tmp = s2.get(newJ2);
				s2.set(newJ2, s2.get(newI));
				s2.set(newI, tmp);
			} while (!(i.compareAndSet(oldI, newI) && j1.compareAndSet(oldJ1, newJ1) && j2.compareAndSet(oldJ2, newJ2)
					&& k1.compareAndSet(oldK1, newK1) && k2.compareAndSet(oldK2, newK2)));
			ret ^= output;
		}
		return ret;
	}

	@Override
	public String toString() {
		return "ArcfourAPlusPRG [" + (s1 != null ? "s1=" + s1 + ", " : "") + (s2 != null ? "s2=" + s2 + ", " : "")
				+ (i != null ? "i=" + i + ", " : "") + (j1 != null ? "j1=" + j1 + ", " : "")
				+ (j2 != null ? "j2=" + j2 + ", " : "") + (k1 != null ? "k1=" + k1 + ", " : "")
				+ (k2 != null ? "k2=" + k2 : "") + "]";
	}
}