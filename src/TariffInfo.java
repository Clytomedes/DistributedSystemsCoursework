import java.io.Serializable;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class TariffInfo implements Serializable {
	private static final long serialVersionUID = 2891516800256650470L;
	private float pricePerUnit;

	public void compareToReadings(Readings readings) {
		// Return a comparison in full implementation
	}

	public void setPricePerUnit(float price) {
		pricePerUnit = price;
	}

	public float getPricePerUnit() {
		return pricePerUnit;
	}
}
