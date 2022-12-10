package util;
public class TestVector {
	/**
	 * Length of the two arrays.
	 */
	private int nvec;
	/**
	 * Degrees of freedom, usually {@link #nvec}-1.
	 */
	private int ndof;
	/**
	 * An element in y must be greater than this value to be included. The default is 5, unless specified otherwise.
	 */
	private double cutoff=5;
	/**
	 * Measured values.
	 */
	private double[] x;
	/**
	 * Expected values.
	 */
	private double[] y;
	/**
	 * Resulting Pearson's Chi-Square.
	 */
	private double chsq;
	/**
	 * Resulting p-value.
	 */
	private double pValue;
	public TestVector() {}
	public int getNvec() {
		return nvec;
	}
	public void setNvec(int nvec) {
		this.nvec = nvec;
		ndof=nvec-1;
		x=new double[nvec];
		y=new double[nvec];
	}
	public int getNdof() {
		return ndof;
	}
	public void setNdof(int ndof) {
		this.ndof = ndof;
	}
	public double getCutoff() {
		return cutoff;
	}
	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}
	public double[] getX() {
		return x;
	}
	public double[] getY() {
		return y;
	}
	public double getChsq() {
		return chsq;
	}
	public void setChsq(double chsq) {
		this.chsq = chsq;
	}
	public double getpValue() {
		return pValue;
	}
	public void setpValue(double pValue) {
		this.pValue = pValue;
	}
	public void evaluate() {
		chsq=0;
		boolean calcNDOF=ndof==0;
		int indexTail=-1;
		for (int i=0;i<nvec;i++) {
			if (y[i]>=cutoff) {
				chsq+=(x[i]-y[i])*(x[i]-y[i])/y[i];
				if(calcNDOF) {
					ndof++;
				}
			} else {
				if (indexTail==-1) {
					indexTail=i;
				} else {
					x[indexTail]+=x[i];
					y[indexTail]+=y[i];
				}
			}
		}
		if(indexTail!=-1) {
			if(y[indexTail]>=cutoff) {
				chsq+=(x[indexTail]-y[indexTail])*(x[indexTail]-y[indexTail])/y[indexTail];
				if(calcNDOF) {
					ndof++;
				}
			}
		}
		if(calcNDOF) {
			ndof--;
		}
		pValue=Functions.cdfChiSquare(ndof, chsq);
	}
}