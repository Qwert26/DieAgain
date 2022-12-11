package test;
import java.util.*;
import util.*;
public class DCTTest implements ITest {
	public static final TestData DCT;
	static {
		DCT=new TestData();
		DCT.setName("Discrete Cosinus Transformation");
		DCT.setDescription("");
		DCT.setpSamplesStandard(1);
		DCT.settSamplesStandard(50000);
		DCT.setNkps(1);
		DCT.setTestMethod(new DCTTest());
		DCT.setExtra(256);
	}
	public DCTTest() {}
	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource=new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest current:parameters) {
			if (current.getXyz()==null || current.getXyz().length<1) {
				current.setXyz(256);
			}
			if (current.getnTuple()==0) {
				current.setnTuple((byte)32);
			}
			final int v=1<<(current.getnTuple()-1);
			final double mean=current.getXyz()[0]*(v-0.5);
			final double sd=Math.sqrt(current.getXyz()[0]/6.0)*v;
		}
	}
	private static double[] discreteCosineTransform(int[] input) {
		double[] output=new double[input.length];
		for (int i=0;i<input.length;i++) {
			for (int j=0;j<input.length;j++) {
				output[i]+=input[j]*Math.cos((Math.PI/input.length)*(j+0.5)*i);
			}
		}
		return output;
	}
	@Deprecated
	public static void main(String...args) {}
}