package util;
import static java.lang.Math.*;
import java.util.*;
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
	/**
	 * Probability density function for the standard normal distribution.
	 * @param x
	 * @return
	 */
	public static double pdfStandardNormal(double x) {
		return exp(-x*x/2)/sqrt(2*PI);
	}
	/**
	 * An approximation of the error function with a maximal error of <tt>1.2 * 10<sup>-7</sup></tt> for any given value.
	 * @param x
	 * @return
	 */
	public static double erf(double x) {
		double t=1/(1+abs(x)/2);
		double polynomial=0;
		for (int e=0;e<erfCoefficientsT.length;e++) {
			polynomial+=erfCoefficientsT[e]*pow(t,e);
		}
		double tau=exp(-x*x+polynomial);
		if (x<0) {
			return tau-1;
		} else {
			return 1-tau;
		}
	}
	/**
	 * Cumulative density function of the standard normal distribution.
	 * @param x
	 * @return
	 */
	public static double cdfStandardNormal(double x) {
		double tmp=x/sqrt(2);
		tmp=1+erf(tmp);
		return tmp/2;
	}
	/**
	 * Probability density function of the chi-square distribution
	 * @param df
	 * @param x
	 * @return
	 */
	public static double pdfChiSquare(int df, double x) {
		return (pow(x/2,(df-2)/2.0)*exp(-x/2)/(2*gamma(df/2.0)));
	}
	/**
	 * Cumulative density function of the chi-square distribution.
	 * @param df
	 * @param x
	 * @return
	 */
	public static double cdfChiSquare(int df, double x) {
		switch(df) {
			case 1: return 2*cdfStandardNormal(sqrt(x))-1;
			case 2: return 1-exp(-x/2);
			default: return cdfChiSquare(df-2,x)-2*pdfChiSquare(df, x);
		}
	}
	/**
	 * Probability density function of the poisson distribution.
	 * @param lambda
	 * @param k
	 * @return
	 */
	public static double pdfPoisson(double lambda, int k) {
		if(k==0) {
			return exp(-lambda);
		} else {
			return exp(-lambda)*pow(lambda,k)/gamma(k+1);
		}
	}
	/**
	 * Probability density function of the anderson darling distribution.
	 * @param z
	 * @return
	 */
	public static double cdfAndersonDarling(double z) {
		if (z<0.01) return 0;
		if (z<=2) return 2*exp(-1.2337/z)*(1+z/8-0.04958*z*z/(1.325+z))/sqrt(z);
		if (z<=4) return 1-0.6621361*exp(-1.091638*z)-0.95095*exp(-2.005138*z);
		return 1-0.4938691*exp(-1.050321*z)-0.5946335*exp(-1.527198*z);
	}
	public static double spline(double x, int n) {
		double tmp=abs(10*x+0.5-n);
		if (n<7) {
			if (tmp>1.5) return 0;
			if (tmp<=0.5) return 1.5-2*tmp*tmp;
			else return 2.25-tmp*(3-tmp);
		} else {
			switch(n) {
			case 7:
				if (x<=0.8||x>=1) return 0;
				else return 100*(x-0.9)*(x-0.9)-1;
			case 8:
				if (x<=0||x>=0.05) return 0;
				if (x<=0.01) return -100*x;
				else return 25*(x-0.05);
			case 9:
				if (x<=0.98||x>=1) return 0;
				else return 0.1-10*abs(x-0.99);
			default:
				throw new ArithmeticException("spline can not be computed for n>9!");
			}
		}
	}
	/**
	 * Kolmogorow-Smirnov-Test
	 * @param x
	 * @return
	 */
	public static double ksTest(double...x) {
		double pvalue, tmp, z=-x.length*x.length, epsilon=pow(10,-20);
		Arrays.sort(x);
		for (int i=0;i<x.length;i++) {
			tmp=x[i]*(1-x[x.length-1-i]);
			tmp=max(epsilon,tmp);
			z-=(2*i+1)*log(tmp);
		}
		z/=x.length;
		pvalue=1-cdfAndersonDarling(z);
		return pvalue;
	}
}