package util;
public class TestData {
	/**
	 * The name of the test.
	 */
	private String name;
	/**
	 * The short name of the test, also its call name.
	 */
	private String shortName="runTestOn";
	/**
	 * The test description;
	 */
	private String description;
	/**
	 * Tests default value for {@link StandardTest#pSamples}.
	 */
	private int pSamplesStandard;
	/**
	 * Tests default value for {@link StandardTest#tSamples}.
	 */
	private int tSamplesStandard;
	/**
	 * Number of independant statistics generated per run.
	 */
	private int nkps;
	/**
	 * An instance of the class implementing the test.
	 */
	private test.ITest testMethod;
	/**
	 * Extra information for the test.
	 */
	private double[] extra;
	public TestData() {}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getpSamplesStandard() {
		return pSamplesStandard;
	}
	public void setpSamplesStandard(int pSamplesStandard) {
		this.pSamplesStandard = pSamplesStandard;
	}
	public int gettSamplesStandard() {
		return tSamplesStandard;
	}
	public void settSamplesStandard(int tSamplesStandard) {
		this.tSamplesStandard = tSamplesStandard;
	}
	public int getNkps() {
		return nkps;
	}
	public void setNkps(int nkps) {
		this.nkps = nkps;
	}
	public test.ITest getTestMethod() {
		return testMethod;
	}
	public void setTestMethod(test.ITest testMethod) {
		this.testMethod = testMethod;
	}
	public double[] getExtra() {
		return extra;
	}
	public void setExtra(double...extra) {
		this.extra = extra;
	}
	public StandardTest createTest(int pSamples, int tSamples) {
		StandardTest ret=new StandardTest();
		ret.setpSamples(pSamples);
		ret.settSamples(tSamples);
		ret.setNkps(nkps);
		return ret;
	}
	public StandardTest createTest() {
		return createTest(pSamplesStandard, tSamplesStandard);
	}
}