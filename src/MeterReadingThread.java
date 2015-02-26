import java.util.Random;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class MeterReadingThread extends Thread {
	private MeterObject meter;

	public MeterReadingThread(MeterObject object) {
		super("Meter Reading Thread");
		meter = object;
	}

	@Override
	public void run() {
		Random random = new Random();
		// Constantly update reading if registered
		while (true) {
			if (meter.isRegistered()) {
				meter.updateCurrentReading(random.nextInt(10));
				meter.updateReading();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
