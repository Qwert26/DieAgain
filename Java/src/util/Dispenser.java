package util;
import java.util.*;
/**
 * Wraps a {@link Random}-object and stores the next 3 integers internally. It can than dispense any number of bits between 0 and 64 from the stored integers.
 * After providing those bits, it can be instructed to stay where it is or move any number of bits forward, where the latter one is destructive: It can not be reversed.
 * @author Christian Schürhoff
 */
public class Dispenser {
	/**
	 * 
	 */
	public static final long MASK=0xFFFFFFFFL;
	/**
	 * The {@link Random}-instance to wrap.
	 */
	private Random random;
	/**
	 * The next 3 generated integers.
	 */
	private int[] generated;
	/**
	 * Bits in the first integer. The next two integers are always the full 32 bits.
	 */
	private byte availableBits=0;
	public Dispenser() {}
	public Random getRandom() {
		return random;
	}
	public void setRandom(Random random) {
		this.random = random;
		generated = new int[] {random.nextInt(), random.nextInt(), random.nextInt()};
		availableBits = 32;
	}
	/**
	 * 
	 * @param bits
	 * How long the output is in bits and how many bits to move forward. Values between 0 and 64 are accepted.
	 * @return
	 */
	public long getBits(byte bits) {
		return getBits(bits, bits);
	}
	/**
	 * 
	 * @param bits
	 * How long the output is in bits. Values between 0 and 64 are accepted.
	 * @param bitsToMove
	 * How many bits to move forward in the bitstream of the {@link #Random}-object.
	 * @return
	 */
	public long getBits(byte bits, long bitsToMove) {
		if(0<=bits&&bits<=64) {
			bitsToMove=Math.absExact(bitsToMove);
			long ret=0;
			if(bits<availableBits) {
				ret=(generated[0]>>>(32-bits));
			} else {
				ret=(generated[0]>>>32-availableBits)&MASK;
				bits-=availableBits;
				if(bits>32) {
					ret=(ret<<32)|(generated[1]&0xFFFFFFFFL);
					bits-=32;
					if(bits>0) {
						ret=(ret<<bits)|(generated[2]>>>(32-bits));
					}
				} else {
					ret=(ret<<bits)|((generated[1]>>>(32-bits))&((1L<<bits)-1));
				}
			}
			if(bitsToMove>0) {
				if(bitsToMove<availableBits) {
					generated[0]<<=bitsToMove;
					availableBits-=bitsToMove;
				} else {
					bitsToMove-=availableBits;
					shift();
					for(long drift=bitsToMove/32;drift>0;drift--) {
						shift();
					}
					bitsToMove%=32;
					if(bitsToMove>0) {
						generated[0]<<=bitsToMove;
						availableBits-=bitsToMove;
					}
				}
			}
			return ret;
		} else {
			throw new IllegalArgumentException("Can not provide "+bits+" Bits!");
		}
	}
	public double getBitsAsDouble(byte bits) {
		return getBitsAsDouble(bits, bits);
	}
	public double getBitsAsDouble(byte bits, long bitsToMove) {
		if(bits<=63) {
			long raw=getBits(bits, bitsToMove);
			double mult=Math.pow(2.0, -bits);
			return raw*mult;
		} else {
			throw new IllegalArgumentException("The highest order bit must be 0 at all times!");
		}
	}
	public byte getBitsAsByte(byte bits) {
		return getBitsAsByte(bits, bits);
	}
	public byte getBitsAsByte(byte bits, long bitsToMove) {
		if (bits<=8) {
			return (byte)getBits(bits, bitsToMove);
		} else {
			throw new IllegalArgumentException("Can at most provide 8 Bits!");
		}
	}
	/**
	 * Shift the buffer array over by one, get the next integer and set {@link #availableBits} to 32.
	 */
	private void shift() {
		generated[0]=generated[1];
		generated[1]=generated[2];
		generated[2]=random.nextInt();
		availableBits=32;
	}
}