package battery;
import java.util.*;
import util.*;
/**
 * How many iterations of k=k*uniform()+1 are required to squeeze k down to 1, starting with k=2147483647=2^31-1? The exact distribution of the required j is used,
 * with a chi-square test based on 100000 tries. The mean of j is 23.064779, with a variance of 23.70971151.
 * @author George Marsaglia
 * @author Christian Schürhoff
 *
 */
public class SqueezeTest extends AbstractTest {
	private static final int STANDARD_TRIALS = 100000;
	//private static final double STANDARD_DEVIATION = Math.sqrt(84);
	private static final double[] DEFAULT_EXPECTED = {	21.03, 57.79, 175.54, 467.32, 1107.83, 2367.84,
														4609.44, 8241.16, 13627.81, 20968.49, 30176.12, 40801.97, 52042.03,
														62838.28, 72056.37, 78694.51, 82067.55, 81919.35, 78440.08, 72194.12,
														63986.79, 54709.31, 45198.52, 36136.61, 28000.28, 21055.67, 15386.52,
														10940.20, 7577.96, 5119.56, 3377.26, 2177.87, 1374.39, 849.70, 515.18,
														306.66, 179.39, 103.24, 58.51, 32.69, 18.03, 9.82, 11.21};
	private int trials = STANDARD_TRIALS;
	public SqueezeTest() {}
	/**
	 * 
	 */
	@Override
	public double[] test(Random rngToTest) {
		return doTest(rngToTest, trials, useFloats);
	}
	/**
	 * 
	 */
	@Override
	public double[] quickTest(Random rngToTest) {
		return doTest(rngToTest, STANDARD_TRIALS, true);
	}
	/**
	 * Executes the squeeze test: The value 2147483647, which is 2<sup>31</sup>-1, is being repeatedly multiplied with supposedly uniform values from 0 to 1 and rounded up,
	 * until it reaches 1. The amount of repeats necessary is recorded for each trial. Values less than 6 are all handled the same and after the 48th iteration,
	 * the trial gets aborted as in theory an infinity amount of multiplications can be necessary.
	 * @param rngToTest
	 * @param trials
	 * @param useFloats
	 * @return
	 */
	public static double[] doTest(Random rngToTest, int trials, boolean useFloats) {
		double[]expected=new double[DEFAULT_EXPECTED.length];
		int[] actual=new int[DEFAULT_EXPECTED.length];
		double ratio=0.1*trials/STANDARD_TRIALS, chsq=0, tmp;
		for(int i=0;i<expected.length;i++) {
			actual[i]=0;
			expected[i]=DEFAULT_EXPECTED[i]*ratio;
		}
		int k,j;
		for(int i=0;i<trials;i++) {
			k=Integer.MAX_VALUE;
			j=0;
			while(k!=1&&j<48) {
				k=(int) (k*getUniformValue(rngToTest, useFloats)+1);
				j++;
			}
			j=Math.max(j-6,0);
			actual[j]++;
		}
		for(int i=0;i<expected.length;i++) {
			tmp=(actual[i]-expected[i])/Math.sqrt(expected[i]);
			chsq+=tmp*tmp;
		}
		return new double[] {1-Functions.cdfChiSquare(expected.length-1, chsq)};
	}
	@Override
	public void setSetting(String identifier, Object value) {
		switch(identifier.toLowerCase()) {
		case "trials":
			if(value instanceof Number) {
				setTrials(((Number) value).intValue());
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
		case "trials": return trials;
		default: return super.getSetting(identifier);
		}
	}
	@Override
	public String[] availableSettings() {
		return new String[] {"usefloats","trials"};
	}
	public int getTrials() {
		return trials;
	}
	public void setTrials(int trials) {
		if(trials<0) {
			this.trials=-trials;
		} else {
			this.trials = trials;
		}
	}
}