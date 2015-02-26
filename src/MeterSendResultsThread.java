/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class MeterSendResultsThread extends Thread {
	private static final int updateTime = 60000;
	private MeterObject meter;

	public MeterSendResultsThread(MeterObject object) {
		meter = object;
	}

	@Override
	public void run() {
		while (true) {
			// Send readings every minute if registered
			if (meter.isRegistered()) {
				meter.sendReadings();
			}
			try {
				Thread.sleep(updateTime);
			} catch (InterruptedException e) {
			}
		}
	}
}
