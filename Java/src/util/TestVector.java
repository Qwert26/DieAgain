package util;

import java.util.*;

public class TestVector {
	public static final double E_SQUARE = Math.E * Math.E;

	public static enum Transform {
		IDENTITY {
			@Override
			public double forward(double d) {
				return d;
			}

			@Override
			public double backward(double d) {
				return d / 2;
			}
		},
		LOGARITHMIC {
			@Override
			public double forward(double d) {
				return Math.log1p(d);
			}

			@Override
			public double backward(double d) {
				return Math.expm1(d) / E_SQUARE;
			}
		},
		EXPONENTIAL {
			@Override
			public double forward(double d) {
				return Math.expm1(d);
			}

			@Override
			public double backward(double d) {
				return Math.log(d - 1);
			}
		};

		public abstract double forward(double d);

		public abstract double backward(double d);
	}

	/**
	 * Length of the two arrays.
	 */
	private int nvec;
	/**
	 * Degrees of freedom, usually {@link #nvec}-1.
	 */
	private int ndof;
	/**
	 * An element in y must be greater than this value to be included. The default
	 * is 5, unless specified otherwise.
	 */
	private double cutoff = 5;
	/**
	 * Measured values.
	 */
	private double[] x;
	/**
	 * Expected values.
	 */
	private double[] y;
	/**
	 * Resulting Chi-Square test statistic.
	 */
	private double chsq;
	/**
	 * Resulting p-value.
	 */
	private double pValue;
	/**
	 * Resulting G test statistic.
	 */
	private double g;

	public TestVector() {
		super();
	}

	/**
	 * Get the length of the two arrays.
	 * 
	 * @return
	 */
	public int getNvec() {
		return nvec;
	}

	/**
	 * Sets the length of the two arrays and also sets {@code ndof} to
	 * {@code nvec - 1}.
	 * 
	 * @param nvec
	 */
	public void setNvec(int nvec) {
		this.nvec = nvec;
		ndof = nvec - 1;
		x = new double[nvec];
		y = new double[nvec];
		chsq = -1;
		pValue = -1;
	}

	/**
	 * Gets the number of degrees of freedom
	 * 
	 * @return
	 */
	public int getNdof() {
		return ndof;
	}

	/**
	 * Sets the number of degrees of freedom. Setting it to zero indicates that this
	 * value is currently unkown must be figured out during evaluation. The cutoff
	 * must have been specified or else its default alue will be used.
	 * 
	 * @param ndof
	 * @see #setCutoff(double)
	 */
	public void setNdof(int ndof) {
		this.ndof = ndof;
	}

	/**
	 * Gets the current cutoff-value for expected values.
	 * 
	 * @return
	 */
	public double getCutoff() {
		return cutoff;
	}

	/**
	 * Sets the cutoff for expected values.
	 * 
	 * @param cutoff
	 */
	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}

	/**
	 * Gets the array of measured values.
	 * 
	 * @return
	 */
	public double[] getX() {
		return x;
	}

	/**
	 * Gets the array of expected values.
	 * 
	 * @return
	 */
	public double[] getY() {
		return y;
	}

	/**
	 * Gets the chi-square value.
	 * 
	 * @return
	 */
	public double getChsq() {
		return chsq;
	}

	/**
	 * Gets the g value.
	 * 
	 * @return
	 */
	public double getG() {
		return g;
	}

	/**
	 * Gets the resulting p-value.
	 * 
	 * @return
	 */
	public double getpValue() {
		return pValue;
	}

	/**
	 * Scales the expected values in order to make their sums match the one of
	 * measured values.
	 */
	public void equalize() {
		double sumX = 0, sumY = 0;
		for (int i = 0; i < nvec; i++) {
			sumX += x[i];
			sumY += y[i];
		}
		double ratio = sumX / sumY;
		for (int i = 0; i < nvec; i++) {
			y[i] *= ratio;
		}
	}

	/**
	 * Evaluates the measured data against the expected data.
	 * 
	 * @return If the evaluation was successfull.
	 */
	public boolean evaluate() {
		if (evaluateChiSquareTest()) {
			return true;
		}
		if (evaluateGTest()) {
			return true;
		}
		return evaluateAbsoluteAreaMismatch(Transform.IDENTITY);
	}

	/**
	 * Evaluates the measured data against the expected data using the
	 * Chi-Square-Test.
	 * 
	 * @return If the evaluation was successfull.
	 */
	public boolean evaluateChiSquareTest() {
		chsq = 0;
		boolean calcNDOF = ndof == 0;
		int indexTail = -1;
		for (int i = 0; i < nvec; i++) {
			if (y[i] >= cutoff) {
				chsq += (x[i] - y[i]) * (x[i] - y[i]) / y[i];
				if (calcNDOF) {
					ndof++;
				}
			} else {
				if (indexTail == -1) {
					indexTail = i;
				} else {
					x[indexTail] += x[i];
					y[indexTail] += y[i];
				}
			}
		}
		if (indexTail != -1) {
			if (y[indexTail] >= cutoff) {
				chsq += (x[indexTail] - y[indexTail]) * (x[indexTail] - y[indexTail]) / y[indexTail];
				if (calcNDOF) {
					ndof++;
				}
			}
		}
		if (calcNDOF) {
			ndof--;
		}
		pValue = Math.min(Math.max(1 - Functions.cdfChiSquare(ndof, chsq), 0), 1);
		if (Double.isNaN(pValue)) {
			pValue = 0.5;
			return false;
		}
		return true;
	}

	/**
	 * Evaluates the measured data against the expected data using the G-Test.
	 * 
	 * @return If the evaluation was successfull.
	 */
	public boolean evaluateGTest() {
		g = 0;
		boolean calcNDOF = ndof == 0;
		int indexTail = -1;
		for (int i = 0; i < nvec; i++) {
			if (y[i] > 0 && x[i] > 0) {
				if (calcNDOF) {
					ndof++;
				}
				g += x[i] * Math.log(x[i] / y[i]);
			} else {
				if (indexTail == -1) {
					indexTail = i;
				} else {
					x[indexTail] += x[i];
					y[indexTail] += y[i];
				}
			}
		}
		if (indexTail != -1) {
			if (y[indexTail] > 0 && x[indexTail] > 0) {
				ndof++;
				g += x[indexTail] * Math.log(x[indexTail] / y[indexTail]);
			}
		}
		g *= 2;
		if (calcNDOF) {
			ndof--;
		}
		pValue = Math.min(Math.max(1 - Functions.cdfChiSquare(ndof, g), 0), 1);
		if (Double.isNaN(pValue)) {
			pValue = 0.5;
			return false;
		}
		return true;
	}

	public boolean evaluateAbsoluteAreaMismatch(Transform t) {
		double sumX = 0, sumY = 0;
		for (int i = 0; i < nvec; i++) {
			sumX += x[i];
			sumY += y[i];
		}
		double mismatch = 0, tmp;
		if (t == null) {
			t = Transform.IDENTITY;
		}
		for (int i = 0; i < nvec; i++) {
			tmp = Math.abs(x[i] / sumX - y[i] / sumY);
			tmp = t.forward(tmp);
			mismatch += tmp;
		}
		pValue = 1 - t.backward(mismatch);
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(chsq);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(cutoff);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(g);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ndof;
		result = prime * result + nvec;
		temp = Double.doubleToLongBits(pValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(x);
		result = prime * result + Arrays.hashCode(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TestVector)) {
			return false;
		}
		TestVector other = (TestVector) obj;
		if (Double.doubleToLongBits(chsq) != Double.doubleToLongBits(other.chsq)) {
			return false;
		}
		if (Double.doubleToLongBits(cutoff) != Double.doubleToLongBits(other.cutoff)) {
			return false;
		}
		if (Double.doubleToLongBits(g) != Double.doubleToLongBits(other.g)) {
			return false;
		}
		if (ndof != other.ndof) {
			return false;
		}
		if (nvec != other.nvec) {
			return false;
		}
		if (Double.doubleToLongBits(pValue) != Double.doubleToLongBits(other.pValue)) {
			return false;
		}
		if (!Arrays.equals(x, other.x)) {
			return false;
		}
		if (!Arrays.equals(y, other.y)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TestVector [nvec=" + nvec + ", ndof=" + ndof + ", cutoff=" + cutoff + ", "
				+ (x != null ? "x=" + Arrays.toString(x) + ", " : "")
				+ (y != null ? "y=" + Arrays.toString(y) + ", " : "") + "chsq=" + chsq + ", pValue=" + pValue + ", g="
				+ g + "]";
	}
}