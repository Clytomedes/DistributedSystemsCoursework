import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class Readings implements Serializable {
	private static final long serialVersionUID = 7161309559982302927L;
	private Integer current;
	private ArrayList<Integer> history;

	public Readings(Integer reading) {
		current = reading;
		history = new ArrayList<Integer>();
	}

	public void updateReading(int reading) {
		history.add(current);
		current = reading;
	}

	public int getReading() {
		return current;
	}

	public void setHistory(ArrayList<Integer> update) {
		history = update;
	}

	public ArrayList<Integer> getHistory() {
		return history;
	}

	public void addReadings(Readings readings) {
		current = readings.getReading();
		history = readings.getHistory();
	}
}
