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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(p);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(pValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + points;
		temp = Double.doubleToLongBits(sigma);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TestPoint)) {
			return false;
		}
		TestPoint other = (TestPoint) obj;
		if (Double.doubleToLongBits(p) != Double.doubleToLongBits(other.p)) {
			return false;
		}
		if (Double.doubleToLongBits(pValue) != Double.doubleToLongBits(other.pValue)) {
			return false;
		}
		if (points != other.points) {
			return false;
		}
		if (Double.doubleToLongBits(sigma) != Double.doubleToLongBits(other.sigma)) {
			return false;
		}
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "TestPoint [points=" + points + ", p=" + p + ", x=" + x + ", y=" + y + ", sigma=" + sigma + ", pValue="
				+ pValue + "]";
	}
}