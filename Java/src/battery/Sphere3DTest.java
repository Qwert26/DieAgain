package battery;
import java.util.*;
import util.*;
public class Sphere3DTest extends AbstractTest {
	public static final int STANDARD_OBSERVATIONS=20;
	public static final int POINTS=4000;
	public static final int EDGE_LENGTH=1000;
	private int observations=STANDARD_OBSERVATIONS;
	public Sphere3DTest() {}
	/**
	 * 
	 */
	@Override
	public double[] test(Random rngToTest) {
		return doTest(rngToTest, observations, useFloats);
	}
	/**
	 * 
	 */
	@Override
	public double[] quickTest(Random rngToTest) {
		return doTest(rngToTest, STANDARD_OBSERVATIONS, true);
	}
	/**
	 * 
	 * @param rngToTest
	 * @param observations
	 * @param useFloats
	 * @return
	 */
	public static double[] doTest(Random rngToTest, int observations, boolean useFloats) {
		double[] data=new double[observations];
		for (int i=0;i<observations;i++) {
			data[i]=generateDataPoint(rngToTest, useFloats);
		}
		return new double[] {Functions.ksTest(data)};
	}
	/**
	 * 
	 * @param rngToTest
	 * @param useFloats
	 * @return
	 */
	public static double generateDataPoint(Random rngToTest, boolean useFloats) {
		double[][] points=new double[POINTS][3];
		for (int i=0;i<POINTS;i++) {
			points[i][0]=EDGE_LENGTH*getUniformValue(rngToTest, useFloats);
			points[i][1]=EDGE_LENGTH*getUniformValue(rngToTest, useFloats);
			points[i][2]=EDGE_LENGTH*getUniformValue(rngToTest, useFloats);
		}
		Arrays.sort(points, (double[] p1, double[] p2) -> {
			if (p1[0]<p2[0]) return -1;
			if (p1[0]>p2[0]) return 1;
			return 0;
		});
		double dmin=10000000, d;
		for (int i=0;i<POINTS-1;i++) {
			for (int j=i+1;j<POINTS;j++) {
				d=(points[i][0]-points[j][0])*(points[i][0]-points[j][0]);
				if (d>=dmin) break;
				d+=(points[i][1]-points[j][1])*(points[i][1]-points[j][1])+(points[i][2]-points[j][2])*(points[i][2]-points[j][2]);
				dmin=Math.min(d, dmin);
			}
		}
		double r3=dmin*Math.sqrt(dmin);
		return 1-Math.exp(-Math.min(r3/30, 20));
	}
	/**
	 * 
	 */
	@Override
	public void setSetting(String identifier, Object value) {
		switch (identifier.toLowerCase()) {
		case "observations":
			if(value instanceof Number) {
				setObservations(((Number)value).intValue());
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
		case "observations": return observations;
		default: return super.getSetting(identifier);
		}
	}
	/**
	 * 
	 */
	@Override
	public String[] availableSettings() {
		return new String[] {"usefloats","observations"};
	}
	public int getObservations() {
		return observations;
	}
	public void setObservations(int observations) {
		if(observations<0) {
			this.observations=-observations;
		} else {
			this.observations=observations;
		}
	}
}