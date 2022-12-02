package battery;
import java.util.*;
import util.*;
/**
 * Count the number of runs and compute the statistics
 * @author George Marsaglia
 * @author Christian Schürhoff
 * @see Algorithm AS 157 Appl. Statist. (1921) vol. 30, No. 1
 */
public class RunTest extends AbstractTest {
	private static final int STANDARD_LENGTH = 10000;
	private static final int MINIMUM_LENGTH = 4000;
	private static final int STANDARD_SEQUENCE_COUNT = 10;
	private static final int STANDARD_SET_COUNT = 3;
	private static final double[][] COVARIANCE_MATRIX = {	{4529.4, 9044.9, 13568.0, 18091.0, 22615.0, 27892.0},
															{9044.9, 18097.0, 27139.0, 36187.0, 45234.0, 55789.0}, 
															{13568.0, 27139.0, 40721.0, 54281.0, 67852.0, 83685.0},
															{18091.0, 36187.0, 54281.0, 72414.0, 90470.0, 111580.0},
															{22615.0, 45234.0, 67852.0, 90470.0, 113262.0, 139476.0},
															{27892.0, 55789.0, 83685.0, 111580.0, 139476.0, 172860.0} };
	public static final double[] CHANCE_ARRAY = {1.0/6, 5.0/24, 11.0/120, 19.0/720, 29.0/5040, 1.0/840};
	private int length=STANDARD_LENGTH;
	private int sequenceCount=STANDARD_SEQUENCE_COUNT;
	private int setCount=STANDARD_SET_COUNT;
	public RunTest() {}
	@Override
	public double[] test(Random rngToTest) {
		return multiTest(rngToTest, length, sequenceCount, setCount, useFloats);
	}
	@Override
	public double[] quickTest(Random rngToTest) {
		return subtest(rngToTest, MINIMUM_LENGTH, true);
	}
	/**
	 * Executes multiple test, but only collects the resulting p-values from the individual Kolmogorow-Smirnov-Tests.
	 * @param rngToTest
	 * @param length
	 * @param sequences
	 * @param sets
	 * @param useFloats
	 * @return
	 */
	public static double[] multiTest(Random rngToTest, int length, int sequences, int sets, boolean useFloats) {
		double[] ret=new double[sets*2], resultSingle;
		for (int i=0;i<sets;i++) {
			resultSingle=singleTest(rngToTest, length, sequences, useFloats);
			ret[2*i]=resultSingle[0];
			ret[2*i+1]=resultSingle[1];
		}
		return ret;
	}
	/**
	 * Executes multiple runs-test, collectes their respective p-values and executes a Kolmogorow-Smirnov-Test of up-runs and down-runs seperatly for uniformity.
	 * @param rngToTest
	 * @param length
	 * @param sequences
	 * @param useFloats
	 * @return
	 * @see Functions#ksTest(double...)
	 */
	public static double[] singleTest(Random rngToTest, int length, int sequences, boolean useFloats) {
		double[] pUps=new double[sequences], pDowns=new double[sequences], resultSub;
		for (int i=0;i<sequences;i++) {
			resultSub=subtest(rngToTest, length, useFloats);
			pUps[i]=resultSub[0];
			pDowns[i]=resultSub[1];
		}
		return new double[] {Functions.ksTest(pUps), Functions.ksTest(pDowns)};
	}
	/**
	 * Executes the runs-test: It counts runs up and runs down in a sequence of supposedly uniform variables. The following example shows of runs are counted:
	 * 0.123, 0.357, 0.789, 0.224, 0.416, 0.950 contains an up-run of length 3, a down-run of length 2 and an up-run of at least length 2, depending on the next values.
	 * @param rngToTest
	 * @param length
	 * @param useFloats
	 * @return
	 */
	public static double[] subtest(Random rngToTest, int length, boolean useFloats) {
		if (length<MINIMUM_LENGTH) {
			throw new IllegalArgumentException("A minimum total of 4000 values needs to be generated!");
		}
		int runUp=0, runDown=0;
		int[] upCount=new int[6], downCount=new int[6];
		double up, x[]=new double[length];
		int i;
		for (i=0;i<length;i++) {
			x[i]=getUniformValue(rngToTest, useFloats);
		}
		for (i=1;i<length;i++) {
			up=x[i]-x[i-1];
			if (up==0) {
				if (x[i]<=0.5) up=-1;
				else up=1;
			}
			if (up>0) {
				downCount[runDown]++;
				runDown=0;
				runUp=Math.min(runUp+1, 5);
			} else /*up<0*/ {
				upCount[runUp]++;
				runUp=0;
				runDown=Math.min(runDown+1, 5);
			}
		}
		upCount[runUp]++;
		downCount[runDown]++;
		double upStat=0, downStat=0;
		for (i=0;i<6;i++) {
			for (int j=0;j<6;j++) {
				upStat+=(upCount[i]-length*CHANCE_ARRAY[i])*(upCount[j]-length*CHANCE_ARRAY[j])*COVARIANCE_MATRIX[i][j];
				downStat+=(downCount[i]-length*CHANCE_ARRAY[i])*(downCount[j]-length*CHANCE_ARRAY[j])*COVARIANCE_MATRIX[i][j];
			}
		}
		upStat/=length;
		downStat/=length;
		return new double[] {Functions.cdfChiSquare(6, upStat), Functions.cdfChiSquare(6, downStat)};
	}
	@Override
	public void setSetting(String identifier, Object value) {
		switch(identifier.toLowerCase()) {
		case "length":
			if(value instanceof Number) {
				setLength(((Number)value).intValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "seqences":
			if(value instanceof Number) {
				setSequenceCount(((Number)value).intValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "sets":
			if(value instanceof Number) {
				setSetCount(((Number)value).intValue());
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
		switch(identifier.toLowerCase()) {
		case "length": return length;
		case "seqences": return sequenceCount;
		case "sets": return setCount;
		default: return super.getSetting(identifier);
		}
	}
	@Override
	public String[] availableSettings() {
		return new String[] {"usefloats","length","seqences","sets"};
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		if(length<MINIMUM_LENGTH) {
			this.length=MINIMUM_LENGTH;
		} else {
			this.length = length;
		}
	}
	public int getSequenceCount() {
		return sequenceCount;
	}
	public void setSequenceCount(int sequenceCount) {
		if(sequenceCount<0) {
			this.sequenceCount=-sequenceCount;
		} else {
			this.sequenceCount=sequenceCount;
		}
	}
	public int getSetCount() {
		return setCount;
	}
	public void setSetCount(int setCount) {
		if(setCount<0) {
			this.setCount=-setCount;
		} else {
			this.setCount=setCount;
		}
	}
}