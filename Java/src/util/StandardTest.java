package util;
public class StandardTest {
	/**
	 * Number of samples per test if applicable
	 */
	private int tSamples;
	/**
	 * Number of test runs per final KS p-value;
	 */
	private int pSamples;
	/**
	 * Number of Bits in nTuples being tested.
	 */
	private int nTuple;
	/**
	 * Array of length {@link #pSamples} to hold test p-values.
	 */
	private double[] pValues;
	/**
	 * Holds labels for p-values.
	 */
	private String pvLabels;
	/**
	 * Final KS p-value from run of many tests.
	 */
	private double ks_pValue;
	/**
	 * Extra Variables
	 */
	private double x, y, z;
	public StandardTest() {}
	public int gettSamples() {
		return tSamples;
	}
	public void settSamples(int tSamples) {
		this.tSamples = tSamples;
	}
	public int getpSamples() {
		return pSamples;
	}
	public void setpSamples(int pSamples) {
		this.pSamples = pSamples;
		pValues = new double[pSamples];
	}
	public int getnTuple() {
		return nTuple;
	}
	public void setnTuple(int nTuple) {
		this.nTuple = nTuple;
	}
	public double[] getpValues() {
		return pValues;
	}
	public String getPvLabels() {
		return pvLabels;
	}
	public void setPvLabels(String pvLabels) {
		this.pvLabels=pvLabels;
	}
	public double getKs_pValue() {
		return ks_pValue;
	}
	public void setKs_pValue(double ks_pValue) {
		this.ks_pValue = ks_pValue;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
}