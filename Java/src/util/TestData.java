package util;
public class TestData {
	/**
	 * The name of the test.
	 */
	private String name;
	/**
	 * The short name of the test, also its call name.
	 */
	private String shortName;
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
}