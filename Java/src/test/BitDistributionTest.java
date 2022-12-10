package test;
import java.util.*;
import util.*;
public class BitDistributionTest implements ITest {
	public static final TestData BIT_DISTRIBUTION;
	static {
		BIT_DISTRIBUTION=new TestData();
		BIT_DISTRIBUTION.setName("Bit Patterns Distributions");
		BIT_DISTRIBUTION.setNkps(2);
		BIT_DISTRIBUTION.setpSamplesStandard(32);
		BIT_DISTRIBUTION.settSamplesStandard(4096);
		BIT_DISTRIBUTION.setTestMethod(new BitDistributionTest());
	}
	public BitDistributionTest() {}
	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource=new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest current:parameters) {
			if (current.getnTuple()==0) {
				current.setnTuple((byte)2);
			} else if (current.getnTuple()>30) {
				continue;
			}
			final double success=Math.pow(2.0, -current.getnTuple());
			TreeMap<Integer,Integer> distance2Frequency=new TreeMap<>();
			for (int run=0;run<current.getpSamples();run++) {
				TestVector counts=new TestVector();
				counts.setNvec(1<<current.getnTuple());
				counts.setCutoff(0);
				distance2Frequency.clear();
				int pattern;
				int[] lastSeen=new int[counts.getNvec()];
				for (pattern=0;pattern<lastSeen.length;pattern++) {
					lastSeen[pattern]=-1;
					counts.getY()[pattern]=current.gettSamples()*success;
				}
				for (int sample=0;sample<current.gettSamples();sample++) {
					pattern=(int) bitSource.getBits(current.getnTuple());
					counts.getX()[pattern]+=1.0;
					for (int p=0;p<lastSeen.length;p++) {
						if (lastSeen[p]!=-1) {
							if(p==pattern) {
								distance2Frequency.compute(lastSeen[p], (key, oldV) -> {
									if(oldV==null || oldV==0) {
										return 1;
									} else {
										return oldV+1;
									}
								});
							} else {
								lastSeen[p]++;
							}
						}
					}
					lastSeen[pattern]=0;
				}
				TestVector distances=new TestVector();
				distances.setNvec(distance2Frequency.lastKey()+1);
				distances.setCutoff(0);
				distance2Frequency.forEach((key, value) -> distances.getX()[key]=value);
				distances.getY()[0]=current.gettSamples()*success;
				for (int d=1;d<distances.getNvec();d++) {
					distances.getY()[d]=distances.getY()[d-1]*(1.0-success);
				}
				counts.evaluate();
				distances.evaluate();
				current.getpValues()[2*run]=counts.getpValue();
				current.getpValues()[2*run+1]=distances.getpValue();
			}
			current.getPvLabels()[0]="Uniformity BitPatterns";
			current.getPvLabels()[1]="Geometric Waiting Times";
			current.evaluate();
		}
	}
	@Deprecated
	public static void main(String...args) {
		StandardTest test=BIT_DISTRIBUTION.createTest(32, 4096);
		BIT_DISTRIBUTION.getTestMethod().runTestOn(new util.randoms.SuperKISS(), test);
		//System.out.println(test);
		for (int nk=0;nk<test.getNkps();nk++) {
			System.out.print(test.getPvLabels()[nk]+"\t");
		}
		System.out.println();
		for (int pSample=0;pSample<test.getpSamples();pSample++) {
			for (int nk=0;nk<test.getNkps();nk++) {
				System.out.print(test.getpValues()[pSample*test.getNkps()+nk]+"\t");
			}
			System.out.println();
		}
		System.out.println("Final p-Value of KS-Test: "+test.getKs_pValue());
		if (test.hasFailed()) {
			System.out.println("Bits are not random!");
		} else if (test.isWeak()) {
			System.out.println("Bits are weakly non-random.");
		} else {
			System.out.println("Bits are random.");
		}
	}
}