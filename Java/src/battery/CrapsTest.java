package battery;
import java.util.*;
import util.*;
public class CrapsTest extends AbstractTest {
	public static final int STANDARD_GAMES=200000;
	private int games=STANDARD_GAMES;
	public CrapsTest() {}
	/**
	 * 
	 */
	@Override
	public double[] test(Random rngToTest) {
		return doTest(rngToTest, games, useFloats);
	}
	/**
	 * 
	 */
	@Override
	public double[] quickTest(Random rngToTest) {
		return doTest(rngToTest, STANDARD_GAMES, true);
	}
	/**
	 * Plays multiple games of craps and uses the number of wins and number of throws to end a game to produce two p-values. The rules for craps are as follows: <br>
	 * <ol type="1">
	 * <li>The game gets played with two six-sided die.</li>
	 * <li>If the first throw results in a sum of 7 or 11, the player wins.</li>
	 * <li>If the first throw results in a sum of 2, 3, or 12, the player loses.</li>
	 * <li>For anything else, the game continues. The sum gets called a <i>Point</i>.</li>
	 * <li>With the second throw, the following rules apply:
	 * <ol type="A">
	 * <li>If the throw results in the same value as the <i>Point</i>, the player wins.</li>
	 * <li>If the throw results in a sum of 7, the player loses.</li>
	 * <li>For any other sum, the game continues.</li>
	 * </ol>
	 * </li>
	 * </ol>
	 * @param rngToTest
	 * @param games
	 * @param useFloats
	 * @return
	 * @implNote A game of craps can potentially never end. It is unlikely but possible.
	 */
	public static double[] doTest(Random rngToTest, int games, boolean useFloats) {
		double[] expected=new double[21];
		double sum, mean, std, t, pValue_w, pValue_th;
		int[] actual=new int[21];
		int i, numberOfWins=0, numberOfThrows, out1st, outnxt;
		sum=expected[0]=1/3.0;
		for (i=1;i<20;i++) {
			actual[i]=0;
			expected[i]=(27*Math.pow(27/36.0, i-1)+40*Math.pow(26/36.0, i-1)+55*Math.pow(25/36.0, i-1))/648;
			sum+=expected[i];
		}
		expected[20]=1-sum;
		actual[0]=actual[20]=0;
		for (i=0;i<games;i++) {
			out1st=throwDie(rngToTest, useFloats)+throwDie(rngToTest, useFloats); //Throw first pair.
			numberOfThrows=0;
			if(out1st==7 || out1st==11) {
				numberOfWins++;
				actual[0]++;
				//We won, next game
			} else if (out1st==2 || out1st==3 || out1st==12) {
				actual[0]++;
				//We lost, next game.
			} else {
				while (true) {
					outnxt=throwDie(rngToTest, useFloats)+throwDie(rngToTest, useFloats); //Throw next pair
					numberOfThrows++;
					if(outnxt==7) {
						actual[Math.min(20, numberOfThrows)]++;
						break;
						//We lost, next game
					} else if (outnxt==out1st) {
						numberOfWins++;
						actual[Math.min(20, numberOfThrows)]++;
						break;
						//We won, next game.
					}
				}
			}
		}
		mean=244.0*games/495.0;
		std=Math.sqrt(mean*251.0/495.0);
		t=(numberOfWins-mean)/std;
		pValue_w=1-Functions.cdfStandardNormal(t);
		sum=0;
		for (i=0;i<21;i++) {
			mean=games*expected[i];
			t=(actual[i]-mean)*(actual[i]-mean)/mean;
			sum+=t;
		}
		pValue_th=1-Functions.cdfChiSquare(20, sum);
		return new double[] {pValue_w, pValue_th};
	}
	/**
	 * Throw a normal, six-sided dice. D&D and Pathfinder-Players would call it a D6.
	 * @param rngToTest
	 * @param useFloats
	 * @return
	 */
	private static int throwDie(Random rngToTest, boolean useFloats) {
		return (int) (1+6*getUniformValue(rngToTest, useFloats));
	}
	@Override
	public void setSetting(String identifier, Object value) {
		switch (identifier.toLowerCase()) {
		case "games":
			if(value instanceof Number) {
				setGames(((Number)value).intValue());
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
		case "games": return games;
		default : return super.getSetting(identifier);
		}
	}
	@Override
	public String[] availableSettings() {
		return new String[] {"usefloats","games"};
	}
	public int getGames() {
		return games;
	}
	public void setGames(int games) {
		if(games<0) {
			this.games=-games;
		} else {
			this.games=games;
		}
	}
}