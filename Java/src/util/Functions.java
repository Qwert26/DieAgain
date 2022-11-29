package util;
import static java.lang.Math.*;
public final class Functions {
	private Functions() {}
	/**
	 * 
	 * @param z
	 * @return
	 */
	public static double Gamma(double z) {
		int tmp=(int) (2*z);
		if (tmp!=2*z || z==0) {
			System.err.println("2*z was not an integer or z is zero!");
		}
		switch(tmp) {
		case 0: return Double.POSITIVE_INFINITY;
		case 1: return sqrt(PI);
		case 2: return 1;
		default: break;
		}
		return (z-1)*Gamma(z-1);
	}
}