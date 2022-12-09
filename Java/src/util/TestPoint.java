package util;
public class TestPoint {
	private int points;
	private double p;
	private double x;
	private double y;
	private double sigma;
	private double pValue;
	public TestPoint() {}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public double getP() {
		return p;
	}
	public void setP(double p) {
		this.p = p;
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
	public double getSigma() {
		return sigma;
	}
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}
	public double getpValue() {
		return pValue;
	}
	public void evaluate() {
		pValue=Functions.cdfStandardNormal((y-x)/sigma);
	}
}