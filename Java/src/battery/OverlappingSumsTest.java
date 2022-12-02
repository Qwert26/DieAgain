package battery;
import java.util.*;
import util.*;
public class OverlappingSumsTest extends AbstractTest {
	public static final int STANDARD_OBSERATIONS=10;
	public static final int STANDARD_SUMS=100;
	public static final int STANDARD_NUMBERS=100;
	public static final int MINIMUM_NUMBERS=12;
	private int observations=STANDARD_OBSERATIONS;
	private int sums=STANDARD_SUMS;
	private int numbers=STANDARD_NUMBERS;
	public OverlappingSumsTest() {}
	@Override
	public double[] test(Random rngToTest) {
		return new double[] {multipleTests(rngToTest, observations, sums, numbers, useFloats)};
	}
	@Override
	public double[] quickTest(Random rngToTest) {
		return new double[] {subTest(rngToTest, MINIMUM_NUMBERS, true)};
	}
	public static double multipleTests(Random rngToTest, int numberOfObservations, int numberOfSums, int numberOfNumbers, boolean useFloats) {
		double[] pv=new double[numberOfObservations];
		for (int i=0;i<numberOfObservations;i++) {
			pv[i]=singleTest(rngToTest, numberOfSums, numberOfNumbers, useFloats);
		}
		return Functions.ksTest(pv);
	}
	public static double singleTest(Random rngToTest, int numberOfSums, int numberOfNumbers, boolean useFloats) {
		double[] p=new double[numberOfSums];
		for (int i=0;i<numberOfSums;i++) {
			p[i]=subTest(rngToTest, numberOfNumbers, useFloats);
		}
		return Functions.ksTest(p);
	}
	public static double subTest(Random rngToTest, int numberOfNumbers, boolean useFloats) {
		double x[]=new double[numberOfNumbers], y[]=new double[numberOfNumbers], sum=0, tmp, mean=numberOfNumbers*0.5, rstd=Math.sqrt(12), a, b;
		for (int i=0;i<numberOfNumbers;i++) {
			y[i]=getUniformValue(rngToTest, useFloats);
			sum+=y[i];
		}
		for (int i=1;i<numberOfNumbers;i++) {
			tmp=y[i-1];
			y[i-1]=(sum-mean)*rstd;
			sum-=tmp;
			sum+=getUniformValue(rngToTest, useFloats);
		}
		y[numberOfNumbers-1]=(sum-mean)*rstd;
		x[0]=y[0]/Math.sqrt(numberOfNumbers);
		x[1]=-x[0]*(numberOfNumbers-1)/Math.sqrt(2*numberOfNumbers-1)+y[1]*Math.sqrt(numberOfNumbers/(2.0*numberOfNumbers+1.0));
		x[0]=Functions.cdfStandardNormal(x[0]);
		x[1]=Functions.cdfStandardNormal(x[1]);
		for (int i=2;i<numberOfNumbers;i++) {
			a=2*numberOfNumbers+1-i;
			b=2*a-2;
			x[i]=y[i-2]/Math.sqrt(a*b)-y[i-1]*Math.sqrt((a-1)/(b+2))+y[i]*Math.sqrt(a/b);
			x[i]=Functions.cdfStandardNormal(x[i]);
		}
		return Functions.ksTest(x);
	}
	@Override
	public void setSetting(String identifier, Object value) {
		switch (identifier.toLowerCase()) {
		case "observations":
			if (value instanceof Number) {
				setObservations(((Number)value).intValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "sums":
			if (value instanceof Number) {
				setSums(((Number)value).intValue());
			} else {
				throw new IllegalArgumentException("Can not set setting "+identifier+": value can not be converted!");
			}
			break;
		case "numbers":
			if (value instanceof Number) {
				setNumbers(((Number)value).intValue());
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
		switch (identifier.toLowerCase()) {
		case "observations": return observations;
		case "sums": return sums;
		case "numbers": return numbers;
		default: return super.getSetting(identifier);
		}
	}
	@Override
	public String[] availableSettings() {
		return new String[] {"useFloats","observations","sums","numbers"};
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
	public int getSums() {
		return sums;
	}
	public void setSums(int sums) {
		if(sums<0) {
			this.sums=-sums;
		} else {
			this.sums=sums;
		}
	}
	public int getNumbers() {
		return numbers;
	}
	public void setNumbers(int numbers) {
		if(numbers<MINIMUM_NUMBERS) {
			this.numbers=MINIMUM_NUMBERS;
		} else {
			this.numbers=numbers;
		}
	}
}