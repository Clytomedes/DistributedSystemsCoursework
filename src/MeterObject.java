import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.text.*;
import java.util.Calendar;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class MeterObject extends UnicastRemoteObject implements RemoteMeterInterface {
	private static final long serialVersionUID = -1309145504592761577L;
	private static final int sleepTime = 10000;
	private int serial;
	private String powerCompany, currentBroker;
	private boolean registered;
	private Readings readingsHistory;
	private RemoteServerInterface server;
	public Integer currentReading;
	private MeterGUI gui;
	private boolean hasGUI;

	public MeterObject(RemoteServerInterface remote, int number) throws RemoteException {
		serial = number;
		registered = false;
		readingsHistory = new Readings(0);
		server = remote;
		currentReading = 0;
		gui = null;
		hasGUI = false;
	}

	public synchronized void sendReadings() {
		// Can only send readings if registered
		if (powerCompany == null) {
			System.err.println(new NotRegisteredException("No Power Company registered"));
			printToGUI("No Power Company registered");
			return;
		}

		while (true) {
			try {
				// Get the reference to the Power Company and then send the readings
				RemotePowerCompanyInterface remote = server.getPowerCompany(powerCompany);
				remote.receiveReadings(readingsHistory, serial);
				printToGUI("Readings Sent");
				return;
			} catch (RemoteException e) {
				System.err.println("Connection Failure" + e.getMessage());
				printToGUI("Connection Failure" + e.getMessage());
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			} catch (NotRegisteredException e) {
				System.err.println(e);
				printToGUI(e.getMessage());
				powerCompany = null;
				return;
			}
		}
	}

	public void sendAlert(Alert alert) {
		// Can only send alerts if registered
		if (powerCompany == null) {
			System.err.println(new NotRegisteredException("This meter is not registered to a Power Company"));
			printToGUI("This meter is not registered to a Power Company");
			powerCompany = null;
			return;
		}

		while (true) {
			try {
				// Get the reference to the Power Company and then send the alert
				RemotePowerCompanyInterface remote = server.getPowerCompany(powerCompany);
				remote.receiveAlert(alert, serial);
				printToGUI("Alert Sent");
				return;
			} catch (RemoteException e) {
				System.err.println("Connection Failure" + e.getMessage());
				printToGUI("Connection Failure" + e.getMessage());
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			} catch (NotRegisteredException e) {
				System.err.println(e.getMessage());
				printToGUI(e.getMessage());
			}
		}
	}

	public void requestDeal(String name) {
		while (true) {
			try {
				// Get a reference to the Broker and send the request
				RemoteBrokerInterface remote = server.getBroker(name);
				remote.findDeal(serial);
				printToGUI("Deal Requested");
				currentBroker = name;
				return;
			} catch (RemoteException e) {
				System.err.println("Connection Failure" + e.getMessage());
				printToGUI("Connection Failure" + e.getMessage());
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			} catch (NotRegisteredException e) {
				System.err.println(e);
				printToGUI(e.getMessage());
				System.exit(0);
			}
		}
	}

	public void acceptDeal() {
		while (true) {
			try {
				// Can only accept if there has been a deal requested
				if (currentBroker == null) {
					throw new NoDealException("Cannot accept a deal if one hasn't been requested");
				}
				RemoteBrokerInterface remote = server.getBroker(currentBroker);
				remote.acceptDeal(serial);
				printToGUI("Deal Accepted");
				return;
			} catch (RemoteException e) {
				System.err.println("Connection Failure" + e.getMessage());
				printToGUI("Connection Failure" + e.getMessage());
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			} catch (NoDealException e) {
				System.err.println(e);
				printToGUI(e.getMessage());
				return;
			} catch (NotRegisteredException e) {
				System.err.println(e);
				printToGUI(e.getMessage());
				System.exit(0);
			}
		}
	}

	public void rejectDeal() {
		while (true) {
			try {
				// Can only reject if there has been a deal requested
				if (currentBroker == null) {
					throw new NoDealException("Cannot reject a deal if one hasn't been requested");
				}
				RemoteBrokerInterface remote = server.getBroker(currentBroker);
				remote.rejectDeal(serial);
				printToGUI("Deal Rejected");
				return;
			} catch (RemoteException e) {
				System.err.println("Connection Failure" + e.getMessage());
				printToGUI("Connection Failure" + e.getMessage());
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			} catch (NoDealException e) {
				System.err.println(e);
				printToGUI(e.getMessage());
				return;
			} catch (NotRegisteredException e) {
				System.err.println(e);
				printToGUI(e.getMessage());
				System.exit(0);
			}
		}
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

	public int getSerial() {
		return serial;
	}

	// Random for demonstration
	public String getRandomPowerCompany() {
		while (true) {
			try {
				if (server.existsPowerCompanies()) {
					return server.getPowerCompanies()[0];
				} else {
					return null;
				}
			} catch (RemoteException e) {
				System.err.println("Connection Faliure (" + e.getMessage() + "), retrying");
				printToGUI("Connection Faliure (" + e.getMessage() + "), retrying");
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	// Random for demonstration
	public String getRandomBroker() {
		while (true) {
			try {
				if (server.existsBrokers()) {
					return server.getBrokers()[0];
				} else {
					return null;
				}
			} catch (RemoteException e) {
				System.err.println("Connection Faliure (" + e.getMessage() + "), retrying");
				printToGUI("Connection Faliure (" + e.getMessage() + "), retrying");
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	// To be able to check against for new deals
	public TariffInfo getCurrentTariff() {
		while (true) {
			try {
				RemotePowerCompanyInterface remote = server.getPowerCompany(powerCompany);
				return remote.getTariffInfo();
			} catch (RemoteException e) {
				System.err.println("Connection Failure" + e.getMessage());
				printToGUI("Connection Failure" + e.getMessage());
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			} catch (NotRegisteredException e) {
				System.err.println(e.getMessage());
				printToGUI(e.getMessage());
			}
		}
	}

	public void setGUI(MeterGUI meterGUI) {
		gui = meterGUI;
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

	public String getCurrentBroker() {
		return currentBroker;
	}

	public synchronized boolean isRegistered() {
		return registered;
	}

	public synchronized void updateCurrentReading(int i) {
		currentReading += i;
	}

	public synchronized void updateReading() {
		readingsHistory.updateReading(currentReading);
		gui.readingText.setText(String.format("%08d", currentReading));
	}

	// Remote methods:
	@Override
	public synchronized void registerPowerCompany(String name) throws RemoteException {
		if (registered == true) {
			this.unregisterPowerCompany();
		}

		while (true) {
			try {
				// Get the reference and execute the method on it
				RemotePowerCompanyInterface remote = server.getPowerCompany(name);
				remote.registerMeter(serial);
				printToGUI("Meter Registered - " + name);
				break;
			} catch (AlreadyRegisteredException e) {
				System.err.println(e);
				System.err.println("Meter failed to register with the Power Company");
				printToGUI(e.getMessage());
				printToGUI("Meter failed to register with the Power Company");
				return;
			} catch (NotRegisteredException e) {
				System.err.println(e);
				printToGUI(e.getMessage());
				e.printStackTrace();
				System.exit(0);
			} catch (RemoteException e) {
				System.err.println("Connection Failure" + e.getMessage());
				printToGUI("Connection Failure" + e.getMessage());
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			}
		}
		powerCompany = name;
		if (hasGUI) {
			gui.companyText.setText("<html>Power Company: " + powerCompany + "</html>");
		}
		registered = true;
	}

	@Override
	public synchronized void unregisterPowerCompany() throws RemoteException {
		if (registered == false) {
			return;
		}

		while (true) {
			try {
				// Get the reference and execute the method on it
				RemotePowerCompanyInterface remote = server.getPowerCompany(powerCompany);
				remote.unregisterMeter(serial);
				printToGUI("Meter Unregistered - " + powerCompany);
				break;
			} catch (RemoteException e) {
				System.err.println("Connection Failure" + e.getMessage());
				printToGUI("Connection Failure" + e.getMessage());
				checkServer();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
				}
			} catch (NotRegisteredException e) {
				System.err.println(e);
				printToGUI(e.getMessage());
				System.exit(0);
			}
		}
		powerCompany = null;
		if (hasGUI) {
			gui.companyText.setText("<html>Power Company: n/a</html>");
		}
		registered = false;
	}

	@Override
	public synchronized void runCommand(String command) throws RemoteException {
		printToGUI("Command Recieved - " + command);
	}

	@Override
	public synchronized Readings getReadingsHistory() throws RemoteException {
		printToGUI("Returning Readings");
		return readingsHistory;
	}

	@Override
	public void recieveDeal(Deal deal) throws RemoteException {
		System.out.println("New Deal = Power Company " + deal.getPowerCompany());
		printToGUI("Received a Deal");

		if (hasGUI) {
			gui.showDealOffer(deal);
		}
	}

	@Override
	public String getCurrentPowerCompany() throws RemoteException {
		return powerCompany;
	}
}
