import java.io.Serializable;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class Deal implements Serializable {
	private static final long serialVersionUID = 5394872411245785910L;
	private String powerCompany;
	private int meter;
	private float pricePerUnit;

	// All the needed information for the deal
	public Deal() {
	}

	public void setPowerCompany(String name) {
		powerCompany = name;
	}

	public void setMeter(int name) {
		meter = name;
	}

	public void setPricePerUnit(float price) {
		pricePerUnit = price;
	}

	public String getPowerCompany() {
		return powerCompany;
	}

	public int getMeter() {
		return meter;
	}

	public float getPricePerUnit() {
		return pricePerUnit;
	}
}
