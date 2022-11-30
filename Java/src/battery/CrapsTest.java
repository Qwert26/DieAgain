package battery;
import java.util.*;
import util.*;
public class CrapsTest extends AbstractTest {
	private static final int STANDARD_GAMES=200000;
	private int games=STANDARD_GAMES;
	public CrapsTest() {}
	@Override
	public double[] test(Random rngToTest) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double[] quickTest(Random rngToTest) {
		// TODO Auto-generated method stub
		return null;
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