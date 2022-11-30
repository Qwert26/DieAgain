package battery;
import java.util.*;
import util.*;
public class ParkingLotTest extends AbstractTest {
	private static final int STANDARD_TRIALS=10;
	private static final int OBSERVATIONS=12000;
	private static final double SIDE=100;
	private static final int MEAN=3523;
	private static final double SIGMA=21.9;
	private int trials=STANDARD_TRIALS;
	public ParkingLotTest() {}
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
	 * Does the actual parking lot test.
	 * @param rngToTest
	 * @param trials
	 * @param useFloats
	 * @return
	 */
	public static double[] doTest(Random rngToTest, int trials, boolean useFloats) {
		double[] p=new double[trials];
		for (int i=0;i<trials;i++) {
			p[i]=generateDataPoint(rngToTest, useFloats);
		}
		return new double[] {Functions.ksTest(p)};
	}
	/**
	 * Generates a single datapoint of the test: For this try to place up to 12000 axis-aligned squares of size 1x1 with their center on a 100x100 square,
	 * such that no two squares overlap. Extensive simulations by George Marsaglia showed that the number of squares placed after 12000 attempts is very close
	 * to a normal distribution with a mean of 3523 and a standard deviation of 21.9.<br>
	 * <br>
	 * Assuming a perfect grid placement, the 100x100 square can house a maximum of 10201 1x1-squares.
	 * @param rngToTest
	 * @param useFloats
	 * @return
	 */
	public static double generateDataPoint(Random rngToTest, boolean useFloats) {
		double[] x=new double[OBSERVATIONS], y=new double[OBSERVATIONS];
		double xtmp, ytmp;
		x[0]=SIDE*getUniformValue(rngToTest, useFloats);
		y[0]=SIDE*getUniformValue(rngToTest, useFloats);
		int sucesses=1;
		for (int i=1;i<OBSERVATIONS;i++) {
			xtmp=SIDE*getUniformValue(rngToTest, useFloats);
			ytmp=SIDE*getUniformValue(rngToTest, useFloats);
			for (int k=0;;) {
				if (Math.abs(x[k]-xtmp)<=1 && Math.abs(y[k]-ytmp)<=1) {
					break;
				}
				if(++k==sucesses) {
					x[sucesses]=xtmp;
					y[sucesses]=ytmp;
					sucesses++;
				}
			}
		}
		return 1.0-Functions.cdfStandardNormal((sucesses-MEAN)/SIGMA);
	}
	/**
	 * 
	 */
	@Override
	public void setSetting(String identifier, Object value) {
		switch (identifier.toLowerCase()) {
		case "trials":
			if (value instanceof Number) {
				setTrials(((Number)value).intValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		default:
			super.setSetting(identifier, value);
			break;
		}
	}
	/**
	 * 
	 */
	@Override
	public Object getSetting(String identifier) {
		switch (identifier.toLowerCase()) {
		case "trials": return trials;
		default: return super.getSetting(identifier);
		}
	}
	/**
	 * 
	 */
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
			this.trials=trials;
		}
	}
}