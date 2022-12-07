package battery;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import util.*;
public class TakeAndSkipTest extends AbstractTest {
	private long initialTake;
	private long[] skipBits;
	private long[] takeBits;
	private int samples;
	private long values;
	private byte bits;
	public TakeAndSkipTest() {}
	@Override
	public double[] test(Random rngToTest) {
		return doTest(rngToTest, samples, values, bits, initialTake, skipBits, takeBits);
	}
	@Override
	public double[] quickTest(Random rngToTest) {
		return doTest(rngToTest, 64, 64, (byte)4, 2, new long[] {0}, new long[] {2});
	}
	public static double[] doTest(Random rngToTest, int samples, long values, byte bits, final long initial, final long[] skips, final long[] takes) {
		long[][] counts=new long[samples][1<<(bits)];
		final int sharedLength=Math.min(skips.length, takes.length);
		int value;
		byte bitsStored;
		for (int sample=0; sample<samples; sample++) {
			BooleanSupplier bitSupplier=new BooleanSupplier() {
				byte availableBits=0;
				int value;
				long bitsToProvide=initial;
				int index=0;
				boolean skipNext=false;
				@Override
				public boolean getAsBoolean() {
					if (availableBits==0) {
						value=rngToTest.nextInt();
						availableBits=32;
					}
					if(skipNext) {
						for (long drops=skips[index]/32;drops>0;drops--) {
							rngToTest.nextInt();
						}
						byte drops=(byte)(skips[index]%32);
						if(drops>=availableBits) {
							value=rngToTest.nextInt();
							drops-=availableBits;
							availableBits=32;
						}
						value<<=drops;
						availableBits-=drops;
						skipNext=false;
						index=(index+1)%sharedLength;
					}
					boolean ret=value<0;
					bitsToProvide--;
					availableBits--;
					value<<=1;
					if(bitsToProvide==0) {
						skipNext=true;
						bitsToProvide=takes[(index+1)%sharedLength];
					}
					return ret;
				}
			};
			for (long number=0; number<values; number++) {
				value=0;
				for(bitsStored=0;bitsStored<bits;bitsStored++) {
					value=value*2+(bitSupplier.getAsBoolean()?1:0);
				}
				counts[sample][value]++;
			}
		}
		double[] pvsForUniformity=new double[samples], pvsForBinomial=new double[counts[0].length];
		double[][] cdfsForBinomial=new double[counts[0].length][samples];
		double expectedInSample=1.0*values/counts[0].length;
		double chsq;
		double success=1.0/counts[0].length;
		for (int sample=0;sample<samples;sample++) {
			chsq=0;
			for(value=0;value<counts[0].length;value++) {
				chsq+=(counts[sample][value]-expectedInSample)*(counts[sample][value]-expectedInSample)/expectedInSample;
				cdfsForBinomial[value][sample] = Functions.cdfBinomial(counts[sample][value], values, success);
			}
			pvsForUniformity[sample]=1-Functions.cdfChiSquare(counts[0].length-1, chsq);
		}
		for (int i=0;i<pvsForBinomial.length;i++) {
			pvsForBinomial[i]=Functions.ksTest(cdfsForBinomial[i]);
		}
		return new double[] {Functions.ksTest(pvsForUniformity),Functions.ksTest(pvsForBinomial)};
	}
	@Override
	public void setSetting(String identifier, Object value) {
		switch (identifier.toLowerCase()) {
		case "initialtake":
			if (value instanceof Number) {
				setInitialTake(((Number)value).longValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "samples":
			if (value instanceof Number) {
				setSamples(((Number)value).intValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "bits":
			if (value instanceof Number) {
				setBits(((Number)value).byteValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "values":
			if (value instanceof Number) {
				setValues(((Number)value).longValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "skipbits":
			if (value.getClass().isArray()) {
				long[] params=new long[Array.getLength(value)];
				for(int i=0;i<params.length;i++) {
					params[i]=Array.getLong(value, i);
				}
				setSkipBits(params);
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value is not an array!");
			}
			break;
		case "takebits":
			if (value.getClass().isArray()) {
				long[] params=new long[Array.getLength(value)];
				for(int i=0;i<params.length;i++) {
					params[i]=Array.getLong(value, i);
				}
				setTakeBits(params);
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value is not an array!");
			}
			break;
		default:
			super.setSetting(identifier, value);
			break;
		}
	}
	@Override
	public Object getSetting(String identifier) {
		switch (identifier.toLowerCase()) {
		case "initialtake": return initialTake;
		case "skipbits": return skipBits;
		case "takebits": return takeBits;
		case "samples": return samples;
		case "values": return values;
		case "bits": return bits;
		default: return super.getSetting(identifier);
		}
	}
	@Override
	public String[] availableSettings() {
		return new String[] {"initialtake","skipbits","takebits","samples","values","bits"};
	}
	public long getInitialTake() {
		return initialTake;
	}
	public void setInitialTake(long initialTake) {
		this.initialTake = Math.absExact(initialTake);
	}
	public long[] getSkipBits() {
		return skipBits;
	}
	public void setSkipBits(long...skipBits) {
		this.skipBits=new long[skipBits.length];
		for (int i=0;i<skipBits.length;i++) {
			this.skipBits[i]=Math.absExact(skipBits[i]);
		}
	}
	public long[] getTakeBits() {
		return takeBits;
	}
	public void setTakeBits(long...takeBits) {
		this.takeBits=new long[takeBits.length];
		for (int i=0;i<takeBits.length;i++) {
			this.takeBits[i]=Math.absExact(takeBits[i]);
		}
	}
	public int getSamples() {
		return samples;
	}
	public void setSamples(int samples) {
		this.samples = Math.max(Math.abs(samples), 1);
	}
	public long getValues() {
		return values;
	}
	public void setValues(long values) {
		this.values = Math.max(Math.abs(values), 1);
	}
	public byte getBits() {
		return bits;
	}
	public void setBits(byte bits) {
		if(1<=bits&&bits<=30) {
			this.bits=bits;
		}
	}
	@Deprecated
	public static void main(String...args) {
		TakeAndSkipTest test=new TakeAndSkipTest();
		Random r=new Random();
		test.setInitialTake(2);
		test.setSkipBits(6);
		test.setTakeBits(2);
		test.setBits((byte)8);
		test.setSamples(32);
		test.setValues(1024);
		double[] res=test.test(r);
		System.out.println("p-Value for Uniformity: "+res[0]);
		System.out.println("p-Value for Binomial Distributions: "+res[1]);
	}
}