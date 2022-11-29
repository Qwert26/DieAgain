package util;
import static java.lang.Math.*;
public final class Functions {
	private Functions() {}
	private static final double[] erfCoefficientsT = { -1.26551223, 1.00002368, 0.37409196, 0.09678418, -0.18628806, 0.27886807, -1.13520398, 1.48851587, -0.82215223, 0.17087277};
	/**
	 * The gamma-function, for integer <tt>z</tt> it is equal to the factorial-function.
	 * @param z
	 * @return
	 */
	public static double gamma(double z) {
		int tmp=(int) (2*z);
		if (tmp!=2*z) {
			System.err.println("2*z was not an integer! Result might be inaccurate.");
		}
		switch(tmp) {
		case 0: return Double.POSITIVE_INFINITY;
		case 1: return sqrt(PI);
		case 2: return 1;
		default: break;
		}
		return (z-1)*gamma(z-1);
	}
	public static double pdfStandardNormal(double x) {
		return exp(-x*x/2)/sqrt(2*PI);
	}
	public static double erf(double x) {
		double t=1/(1+abs(x)/2);
		double polynomial=0;
		for(int e=0;e<erfCoefficientsT.length;e++) {
			polynomial+=erfCoefficientsT[e]*pow(t,e);
		}
		double tau=exp(-x*x+polynomial);
		if(x<0) {
			return tau-1;
		} else {
			return 1-tau;
		}
	}
	public static double cdfStandardNormal(double x) {
		double tmp=x/sqrt(2);
		tmp=1+erf(tmp);
		return tmp/2;
	}
	public static double pdfChiSquare(int df, double x) {
		return (pow(x/2,(df-2)/2.0)*exp(-x/2)/(2*gamma(df/2.0)));
	}
	public static double cdfChiSquare(int df, double x) {
		switch(df) {
			case 1: return 2*cdfStandardNormal(sqrt(x))-1;
			case 2: return 1-exp(-x/2);
			default: return cdfChiSquare(df-2,x)-2*pdfChiSquare(df, x);
		}
	}
	public static double pdfPoisson(double lambda, int k) {
		if(k==0) {
			return exp(-lambda);
		} else {
			return exp(-lambda)*pow(lambda,k)/gamma(k+1);
		}
	}
}