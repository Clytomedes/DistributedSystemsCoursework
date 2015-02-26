import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class BrokerObject extends UnicastRemoteObject implements RemoteBrokerInterface {
	private static final long serialVersionUID = -7129456392956646588L;
	private static final int sleepTime = 10000;
	private HashMap<Integer, Deal> deals;
	private RemoteServerInterface server;
	public String brokerName;
	private BrokerGUI gui;
	private boolean hasGUI;

	public BrokerObject(RemoteServerInterface remote, String name) throws RemoteException {
		deals = new HashMap<Integer, Deal>();
		server = remote;
		brokerName = name;
		gui = null;
		hasGUI = false;
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

	public void setGUI(BrokerGUI brokerGUI) {
		gui = brokerGUI;
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

	// Remote Methods:
	@Override
	public synchronized void findDeal(int serial) throws RemoteException, NotRegisteredException {
		RemoteMeterInterface meter;
		Readings readings;
		String[] powerCompanies;

		printToGUI("Find Deal Request Received from Meter (" + serial + ")");

		while (true) {
			try {
				// Get Readings from the Meter to compare
				meter = server.getMeter(serial);
				readings = meter.getReadingsHistory();
				printToGUI("Readings received from Meter (" + serial + ")");

				// Loop through all the Power Companies to find the best price
				powerCompanies = server.getPowerCompanies();
				for (int i = 0; i < powerCompanies.length; i++) {
					server.getPowerCompany(powerCompanies[i]).getTariffInfo().compareToReadings(readings);
					// In full implementation this would be the point where the Tariff info is compared
				}
				break;
			} catch (RemoteException e) {
				System.err.println("Connection Failure (" + e.getMessage() + "), retrying");
				printToGUI("Connection Failure (" + e.getMessage() + ")");
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			}
		}

		Random random = new Random();
		Deal deal = new Deal();
		deal.setMeter(serial);

		// Chooses a random power company for demonstration purposes
		int randomNumber = random.nextInt(powerCompanies.length);
		String name = powerCompanies[randomNumber];

		deal.setPowerCompany(name);
		deals.put(serial, deal);

		while (true) {
			try {
				// Push the deal to the meter
				deal.setPricePerUnit(server.getPowerCompany(name).getTariffInfo().getPricePerUnit());
				meter.recieveDeal(deal);
				printToGUI("Successfully sent deal to Meter (" + serial + ")");
				return;
			} catch (RemoteException e) {
				System.err.println("Connection Failure (" + e.getMessage() + "), retrying");
				printToGUI("Connection Failure (" + e.getMessage() + ")");
				checkServer();
			}
		}
	}

	@Override
	public synchronized void acceptDeal(int serial) throws RemoteException, NoDealException {
		// Check there is an active deal
		if (!deals.containsKey(serial)) {
			printToGUI("Attempt to accept a non existint deal from Meter (" + serial + ")");
			throw new NoDealException("Deal needs to be requested before it can be accepted");
		}

		Deal deal = deals.get(serial);
		deals.remove(serial);
		while (true) {
			try {
				// Switch the meters Power Companies
				RemoteMeterInterface meter = server.getMeter(deal.getMeter());
				meter.unregisterPowerCompany();
				meter.registerPowerCompany(deal.getPowerCompany());
				printToGUI("Successfully switch Power Companies for Meter (" + serial + ")");
				return;
			} catch (RemoteException e) {
				System.err.println("Connection Failure (" + e.getMessage() + "), retrying");
				printToGUI("Connection Failure (" + e.getMessage() + ")");
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			} catch (NotRegisteredException e) {
				System.err.println(e);
				System.exit(0);
			}
		}
	}

	@Override
	public synchronized void rejectDeal(int serial) throws RemoteException, NoDealException {
		// Check there is an active deal
		if (!deals.containsKey(serial)) {
			printToGUI("Attempt to reject a non existint deal from Meter (" + serial + ")");
			throw new NoDealException("Deal needs to be requested before it can be rejected");
		}

		// Remove the deal
		deals.remove(serial);
		printToGUI("Successfully rejected deal for Meter (" + serial + ")");
	}
}
