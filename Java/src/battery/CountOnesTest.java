package battery;
import java.util.*;
import java.util.function.*;
import util.*;
public class CountOnesTest extends AbstractTest {
	private static final long STANDARD_WORD_COUNT=256000;
	private static final double MEAN=2500;//5^5-5^4
	private static final double STANDARD_DEVIATION=Math.sqrt(5000);//mean*2
	private static final double[] LETTER_PROBABILITIES = {37/256.0, 56/256.0, 70/256.0, 56/256.0, 37/256.0};
	private boolean byteStream=true;
	private int rightShift=24;
	private long numberOfWords=STANDARD_WORD_COUNT;
	public CountOnesTest() {}
	@Override
	public double[] test(Random rngToTest) {
		return countStats(rngToTest, numberOfWords, rightShift, byteStream);
	}
	@Override
	public double[] quickTest(Random rngToTest) {
		return countStats(rngToTest, STANDARD_WORD_COUNT, 0, true);
	}
	/**
	 * Converts a "byte" into a "letter".
	 * @param b The Byte to convert, only the lowest 8 bits are considered.
	 * @return 0 for 0, 1 or 2 bits set, 1 for 3 bits set, 2 for 4 bits set, 3 for 5 bits set and 4 for 6, 7 or 8 bits set.
	 */
	private static int byteToLetter(int b) {
		int cnt1s=0;
		for (int i=0;i<8;i++) {
			if (((b>>i)&1)==1) {
				cnt1s++;
			}
		}
		switch (cnt1s) {
		case 0:
		case 1:
		case 2: return 0;
		case 3: return 1;
		case 4: return 2;
		case 5: return 3;
		case 6:
		case 7:
		case 8: return 4;
		default:
			throw new IllegalArgumentException("Unexpected value: " + cnt1s);
		}
	}
	/**
	 * Executes the "Count 1s" test: For this it generates {@link #numberOfWords} 4- and 5-letter long, overlapping words. The count of individual letters of each word length
	 * gets subtracted from each other according to Q5-Q4.
	 * @param rngToTest
	 * @param numberOfWords
	 * @param rightShift
	 * @param byteStream
	 * @return
	 */
	public static double[] countStats(Random rngToTest, long numberOfWords, int rightShift, boolean byteStream) {
		int word;
		long[] actual4=new long[5*5*5*5], actual5=new long[actual4.length*5];
		double chsq=0, expected;
		IntUnaryOperator getter;
		if(byteStream) {
			getter = new IntUnaryOperator() {
				short rest=0;
				int pseudoByte=0;
				@Override
				public int applyAsInt(int value) {
					if(rest==0) {
						pseudoByte=rngToTest.nextInt();
						rest=4;
					}
					rest--;
					return (pseudoByte>>rest*8)&255;
				}
			};
		} else {
			getter=new IntUnaryOperator() {
				@Override
				public int applyAsInt(int value) {
					return (rngToTest.nextInt()>>value)&255;
				}
			};
		}
		word = 625*byteToLetter(getter.applyAsInt(rightShift))+
				125*byteToLetter(getter.applyAsInt(rightShift))+
				25*byteToLetter(getter.applyAsInt(rightShift))+
				5*byteToLetter(getter.applyAsInt(rightShift))+
				byteToLetter(getter.applyAsInt(rightShift));
		for (long i=0;i<numberOfWords;i++) {
			word%=625;
			actual4[word]++;
			word=5*word+byteToLetter(getter.applyAsInt(rightShift));
			actual5[word]++;
		}
		for (int i=0;i<actual4.length;i++) { //Starts at AAAA.
			expected=numberOfWords;
			word=i;
			for(int j=0;j<4;j++) {
				expected*=LETTER_PROBABILITIES[word%5];
				word/=5;
			}
			chsq+=(actual4[i]-expected)*(actual4[i]-expected)/expected;
		}
		chsq=-chsq;
		for (int i=0;i<actual5.length;i++) { //Starts at AAAAA.
			expected=numberOfWords;
			word=i;
			for(int j=0;j<5;j++) {
				expected*=LETTER_PROBABILITIES[word%5];
				word/=5;
			}
			chsq+=(actual5[i]-expected)*(actual5[i]-expected)/expected;
		}
		return new double[] {1-Functions.cdfStandardNormal((chsq-MEAN)/STANDARD_DEVIATION)};
	}
	@Override
	public void setSetting(String identifier, Object value) {
		switch (identifier.toLowerCase()) {
		case "stream":
			if(value instanceof Boolean) {
				byteStream=((Boolean) value).booleanValue();
			} else if(value instanceof Number) {
				byteStream=((Number) value).doubleValue() != 0;
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "shift":
			if(value instanceof Number) {
				setRightShift(((Number)value).intValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "wordcount":
			if(value instanceof Number) {
				setNumberOfWords(((Number)value).longValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
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
		case "stream": return byteStream;
		case "shift": return rightShift;
		case "wordcount": return numberOfWords;
		default: return super.getSetting(identifier);
		}
	}
	/**
	 * Available Parameters in this Test. As it uses integers directly, the parameter "useFloats" is ignored.
	 */
	@Override
	public String[] availableSettings() {
		return new String[] {"stream","shift","wordcount"};
	}
	public boolean isByteStream() {
		return byteStream;
	}
	public void setByteStream(boolean byteStream) {
		this.byteStream = byteStream;
	}
	public int getRightShift() {
		return rightShift;
	}
	public void setRightShift(int rightShift) {
		if (rightShift<0) {
			this.rightShift=0;
		} else if (rightShift>24) {
			this.rightShift=24;
		} else {
			this.rightShift=rightShift;
		}
	}
	public long getNumberOfWords() {
		return numberOfWords;
	}
	public void setNumberOfWords(long numberOfWords) {
		if(numberOfWords<0) {
			this.numberOfWords=-numberOfWords;
		} else {
			this.numberOfWords=numberOfWords;
		}
	}
}