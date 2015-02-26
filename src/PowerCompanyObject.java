import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class PowerCompanyObject extends UnicastRemoteObject implements RemotePowerCompanyInterface {
	private static final long serialVersionUID = -3027298006762213385L;
	private static final int sleepTime = 10000;
	private ArrayList<Integer> meters;
	private HashMap<Integer, Readings> readings;
	private RemoteServerInterface server;
	public TariffInfo tariffInfo;
	public String companyName;
	private PowerCompanyGUI gui;
	private boolean hasGUI;

	public PowerCompanyObject(RemoteServerInterface remote, String name) throws RemoteException {
		meters = new ArrayList<Integer>();
		readings = new HashMap<Integer, Readings>();
		server = remote;
		tariffInfo = new TariffInfo();
		Random random = new Random();
		tariffInfo.setPricePerUnit((float) (random.nextInt(6) + random.nextFloat()));
		companyName = name;
		gui = null;
		hasGUI = false;
	}

	public void sendCommand(String command, int serial) throws NotRegisteredException {
		// Can only send commands to registered meter
		if (!meters.contains(serial)) {
			printToGUI("Attempt to send command (" + command + ") to non registered Meter (" + serial + ")");
			throw new NotRegisteredException("This Meter (" + serial + ") is not registered to this Power Company");
		}

		while (true) {
			try {
				// Get the reference and execute the method on it
				RemoteMeterInterface meter = server.getMeter(serial);
				meter.runCommand(command);
				printToGUI("Successfully sent command (" + command + ") to Meter (" + serial + ")");
				break;
			} catch (RemoteException e) {
				System.err.println("Connection Failure (" + e.getMessage() + "), Retrying");
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	public void setTariffInfo(TariffInfo tariff) {
		tariffInfo = tariff;
	}

	public void changeTariffPrice(float price) {
		tariffInfo.setPricePerUnit(price);
		printToGUI("Tariff Price Per Unit change to " + Float.toString(price) + "p");
	}

	// Check if connection to the server is still active
	public void checkServer() {
		while (true) {
			try {
				server.test();
				return;
			} catch (RemoteException e) {
				// Try to find the server object again
				try {
					server = ((RemoteServerInterface) Naming.lookup(RemoteServerInterface.serverURI));
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (RemoteException e1) {
				} catch (NotBoundException e1) {
				}
			}
		}
	}

	public void setGUI(PowerCompanyGUI powerCompanyGUI) {
		gui = powerCompanyGUI;
		hasGUI = true;
	}

	// Output useful data to the GUI with a timestamp
	public void printToGUI(String message) {
		if (hasGUI) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa");
			Calendar calendar = Calendar.getInstance();
			String time = dateFormat.format(calendar.getTime());

			gui.textArea.append(time + " - " + message);
			gui.textArea.append("\n");
			gui.textArea.setCaretPosition(gui.textArea.getDocument().getLength());
		}
	}

	// Remote methods:
	@Override
	public synchronized void registerMeter(int serial) throws RemoteException, AlreadyRegisteredException, NotRegisteredException {
		// Can't be registered more than once without unregistering
		if (meters.contains(serial)) {
			printToGUI("Attempt to register already registered Meter (" + serial + ")");
			throw new AlreadyRegisteredException("This meter serial is already registered with this Power Company");
		}

		// Add to list of meters
		meters.add(serial);
		printToGUI("Successfully registered Meter (" + serial + ")");
	}

	@Override
	public synchronized void unregisterMeter(int serial) throws RemoteException {
		// Remove from list of meters
		meters.remove((Integer) serial);
		printToGUI("Successfully unregistered Meter (" + serial + ")");
	}

	@Override
	public synchronized void receiveReadings(Readings newReadings, int serial) throws RemoteException, NotRegisteredException {
		// Can only receive readings from registered meter
		if (!meters.contains(serial)) {
			printToGUI("Attempt to receive readings from non registered Meter (" + serial + ")");
			throw new NotRegisteredException("Readings cannot be accepted from a non registered meter");
		}

		// Update list of readings
		if (!readings.containsKey(serial)) {
			readings.put(serial, newReadings);
		} else {
			readings.get(serial).addReadings(newReadings);
		}
		printToGUI("Successfully received readings from Meter (" + serial + ") of " + newReadings.getReading());
	}

	@Override
	public synchronized void receiveAlert(Alert alert, int serial) throws RemoteException {
		printToGUI("Received Alert from Meter (" + serial + ")");
	}

	@Override
	public synchronized TariffInfo getTariffInfo() throws RemoteException {
		return tariffInfo;
	}

	@Override
	public synchronized String getName() throws RemoteException {
		return companyName;
	}
}
