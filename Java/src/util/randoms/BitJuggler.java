package util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

public class BitJuggler extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1690861520758930997L;
	private boolean initialized = false;
	private AtomicLongArray data;
	private AtomicInteger index;
	public static final byte STEP = 18;
	public static final int SIZE = 64 * STEP;

	public BitJuggler() {
		this(System.nanoTime());
	}

	public BitJuggler(long seed) {
		super(seed);
		data = new AtomicLongArray(SIZE);
		index = new AtomicInteger();
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(final long seed) {
		if (initialized) {
			for (int i = 0, rot = 0; i < SIZE; i += STEP, rot++) {
				data.set(i, Long.rotateLeft(seed, rot));
				data.set(i + 1, Long.rotateLeft(Long.reverse(seed), rot));
				data.set(i + 2, Long.rotateLeft(Long.reverseBytes(seed), rot));
				data.set(i + 3, Long.rotateLeft(Long.reverseBytes(Long.reverse(seed)), rot));
				data.set(i + 4, Long.reverse(Long.rotateLeft(seed, rot)));
				data.set(i + 5, Long.reverseBytes(Long.rotateLeft(seed, rot)));
				data.set(i + 6, Long.reverseBytes(Long.reverse(Long.rotateLeft(seed, rot))));
				data.set(i + 7, Long.reverseBytes(Long.rotateLeft(Long.reverse(seed), rot)));
				data.set(i + 8, Long.reverse(Long.rotateLeft(Long.reverseBytes(seed), rot)));
				data.set(i + 9, Long.rotateLeft(~seed, rot));
				data.set(i + 10, Long.rotateLeft(Long.reverse(~seed), rot));
				data.set(i + 11, Long.rotateLeft(Long.reverseBytes(~seed), rot));
				data.set(i + 12, Long.rotateLeft(Long.reverseBytes(Long.reverse(~seed)), rot));
				data.set(i + 13, Long.reverse(Long.rotateLeft(~seed, rot)));
				data.set(i + 14, Long.reverseBytes(Long.rotateLeft(~seed, rot)));
				data.set(i + 15, Long.reverseBytes(Long.reverse(Long.rotateLeft(~seed, rot))));
				data.set(i + 16, Long.reverseBytes(Long.rotateLeft(Long.reverse(~seed), rot)));
				data.set(i + 17, Long.reverse(Long.rotateLeft(Long.reverseBytes(~seed), rot)));
			}
			index.set(0);
			for (int i = 0; i < SIZE; i++) {
				next(0);
			}
		}
	}

	@Override
	protected int next(int bits) {
		int oldIndex, newIndex;
		long oldData, newData;
		do {
			newIndex = oldIndex = index.get();
			newData = data.get(newIndex);
			newIndex = (newIndex + 1) % SIZE;
			newData = Math.multiplyHigh(newData, data.get(newIndex));
			oldData = data.get((newIndex + SIZE - 1) % SIZE);
		} while (!(index.compareAndSet(oldIndex, newIndex)
				&& data.compareAndSet((newIndex + SIZE - 1) % SIZE, oldData, oldData + newData)));
		return (int) ((newData ^ (newData << 32)) >>> (64 - bits));
	}

	@Deprecated
	public static void main(String... args) {
		BitJuggler rng = new BitJuggler();
		for (int i = 0; i < 50; i++) {
			System.out.println("%16X".formatted(rng.nextLong()));
		}
	}
}