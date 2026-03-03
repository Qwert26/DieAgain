package io.github.qwert26.dieAgain.util.randoms;

import java.util.*;
import java.util.concurrent.atomic.*;

public class PCGHash extends Random {
	/**
	 * 
	 */
	public static final long LCG_ADD = 2891336453L;
	/**
	 * 
	 */
	public static final long LCG_MULT = 747796405L;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7858768246882098416L;
	private AtomicLong state;

	public PCGHash() {
		this(System.nanoTime());
	}

	public PCGHash(long seed) {
		state = new AtomicLong(0);
		super(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		state.set(seed);
	}

	@Override
	protected int next(int bits) {
		long oldState, newState;
		do {
			oldState = newState = state.get();
			newState = newState * LCG_MULT + LCG_ADD;
			newState = ((newState >> ((newState >>> 59) + 4)) ^ newState) * 277803737L;
			newState = newState ^ (newState >> 22);
		} while (!state.compareAndSet(oldState, newState));
		return (int) newState >>> (32 - bits);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PCGHash [state=");
		builder.append(state);
		builder.append("]");
		return builder.toString();
	}

}