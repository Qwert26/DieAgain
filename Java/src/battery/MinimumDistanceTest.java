package battery;
import java.util.*;
import util.*;
/**
 * Minimum distance^2 between n random 2d-points. Mean is about 0.04 for 4000 points in a square of side 1000 and 0.995 for 8000 points in a square of side 10000.
 * Since distance^2 is approximately exponential with mean 0.04 or 0.995, 1-exp(-d^2/mean) should be uniform on [0,1).
 * @author George Marsaglia
 * @author Christian Schürhoff
 */
public class MinimumDistanceTest extends AbstractTest {
	public static final double MEAN_SMALL=0.04;
	public static final double MEAN_LARGE=0.995;
	public static final int POINTS_SMALL=4000;
	public static final int POINTS_LARGE=8000;
	public static final int SIDE_SMALL=1000;
	public static final int SIDE_LARGE=10000;
	public static final int STANDARD_SAMPLES=100;
	private int samples=STANDARD_SAMPLES;
	private boolean uselarge=true;
	public MinimumDistanceTest() {}
	/**
	 * 
	 */
	@Override
	public double[] test(Random rngToTest) {
		return new double[] {doTest(rngToTest, samples, uselarge, useFloats)};
	}
	/**
	 * 
	 */
	@Override
	public double[] quickTest(Random rngToTest) {
		return new double[] {doTest(rngToTest, STANDARD_SAMPLES, false, true)};
	}
	/**
	 * Does the minimum distance test: For the small version, it chooses 4000 random points in a square of side length 1000 and finds the smallest distance between them.
	 * For the large version, it chooses 8000 points in a square of side length 10000.
	 * @param rngToTest
	 * @param samples
	 * @param useLarge
	 * @param useFloats
	 * @return
	 */
	public static double doTest(Random rngToTest, int samples, boolean useLarge, boolean useFloats) {
		double[] data=new double[samples];
		for(int i=0;i<samples;i++) {
			data[i]=generateDataPoint(rngToTest, useLarge, useFloats);
		}
		return Functions.ksTest(data);
	}
	/**
	 * Places the appropiate number of points inside an appropiate sized square.
	 * @param rngToTest
	 * @param useLarge
	 * @param useFloats
	 * @return
	 */
	private static double generateDataPoint(Random rngToTest, boolean useLarge, boolean useFloats) {
		double mean=useLarge?MEAN_LARGE:MEAN_SMALL;
		int points=useLarge?POINTS_LARGE:POINTS_SMALL;
		int side=useLarge?SIDE_LARGE:SIDE_SMALL;
		double dmin=2*side*side,d;
		double[][]pts=new double[points][2];
		for (int i=0;i<points;i++) {
			pts[i][0]=getUniformValue(rngToTest, useFloats)*side;
			pts[i][1]=getUniformValue(rngToTest, useFloats)*side;
		}
		Arrays.sort(pts, (double[] p1, double[] p2) -> {
			if(p1[0]<p2[0]) return -1;
			if(p1[0]==p2[0]) return 0;
			return 1;
		});
		for (int i=0;i<points-1;i++) {
			for (int j=i+1;j<points;j++) {
				d=(pts[i][0]-pts[j][0])*(pts[i][0]-pts[j][0]);
				if (d>dmin) break;
				else {
					d+=(pts[i][1]-pts[j][1])*(pts[i][1]-pts[j][1]);
					dmin=Math.min(dmin, d);
				}
			}
		}
		return 1-Math.exp(-dmin/mean);
	}
	@Override
	public void setSetting(String identifier, Object value) {
		switch (identifier.toLowerCase()) {
		case "samples":
			if(value instanceof Number) {
				setSamples(((Number)value).intValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "uselarge":
			if(value instanceof Boolean) {
				useFloats=((Boolean) value).booleanValue();
			} else if(value instanceof Number) {
				useFloats=((Number) value).doubleValue() != 0;
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
		case "samples": return samples;
		case "uselarge": return uselarge;
		default: return super.getSetting(identifier);
		}
	}
	@Override
	public String[] availableSettings() {
		return new String[] {"usefloats","samples","uselarge"};
	}
	public int getSamples() {
		return samples;
	}
	public void setSamples(int samples) {
		if(samples<0) {
			this.samples=-samples;
		} else {
			this.samples=samples;
		}
	}
	public boolean isUsinglarge() {
		return uselarge;
	}
	public void setUselarge(boolean uselarge) {
		this.uselarge = uselarge;
	}
}