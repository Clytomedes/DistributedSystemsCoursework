import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.ExportException;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class Meter {

	public static void main(String[] args) {
		try {
			// Attempt to start rmiregistry
			try {
				LocateRegistry.createRegistry(1099);
			} catch (ExportException e) {
				// Catch Exception if it is already started
			}

			RemoteServerInterface server;
			// Retry loop until server is found
			while (true) {
				try {
					// Get a reference to the server object
					server = (RemoteServerInterface) Naming.lookup(RemoteServerInterface.serverURI);
					break;
				} catch (RemoteException e1) {
					System.err.println("Failed to connect to to server, retrying");
					// Try every 10 seconds
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e2) {
					}
				} catch (NotBoundException e1) {
					System.err.println("Failed to connect to to server, retrying");
					// Try every 10 seconds
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e2) {
					}
				}
			}
			System.out.println("Connected to Server");

			int serial;
			// Retry until serial is received
			while (true) {
				try {
					serial = server.getSerial();
					System.out.println("Serial Received");
					break;
				} catch (RemoteException e) {
					System.err.println("Connection Failure, retrying");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e2) {
					}
				}
			}

			while (true) {
				try {
					// Create and bind object to registry
					MeterObject meter = new MeterObject(server, serial);
					String name = "Meter-" + serial;
					Naming.rebind(name, meter);

					// Register the object with the server
					server.registerMeter(name, serial);
					System.out.println("Meter (" + name + ") ready");

					// Start the GUI
					MeterGUI gui = new MeterGUI(meter);
					meter.setGUI(gui);

					// Start the Threads
					MeterReadingThread thread = new MeterReadingThread(meter);
					thread.start();
					MeterSendResultsThread thread2 = new MeterSendResultsThread(meter);
					thread2.start();
					break;
				} catch (ConnectException e) {
					System.err.println("Connection Failure, aborting method");
					return;
				} catch (RemoteException e) {
					System.err.println("Connection Failure, retrying");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e2) {
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
