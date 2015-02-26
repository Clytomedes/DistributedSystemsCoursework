import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class ServerObject extends UnicastRemoteObject implements RemoteServerInterface {
	private static final long serialVersionUID = -9131747717300440871L;
	private HashMap<Integer, String> meters;
	private HashMap<String, String> powerCompanies;
	private HashMap<String, String> brokers;
	private int currentSerial, currentPowerCompany, currentBroker;
	private boolean hasGUI;
	private ServerGUI gui;

	public ServerObject() throws RemoteException {
		meters = new HashMap<Integer, String>();
		powerCompanies = new HashMap<String, String>();
		brokers = new HashMap<String, String>();
		currentSerial = 0;
		currentPowerCompany = 0;
		currentBroker = 0;
		hasGUI = false;
		gui = null;
	}

	public void setGUI(ServerGUI serverGUI) {
		gui = serverGUI;
		hasGUI = true;
	}

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

	@Override
	public synchronized RemoteMeterInterface getMeter(int serial) throws RemoteException, NotRegisteredException {
		// Can only look up an existing object
		if (!meters.containsKey(serial)) {
			printToGUI("Failed attempt to find never registered Meter (" + serial + ")");
			throw new NotRegisteredException("This Meter (" + serial + ") does not exist");
		}

		RemoteMeterInterface meter = null;
		try {
			// Look up with constructed URI
			meter = (RemoteMeterInterface) Naming.lookup("rmi://localhost/" + meters.get(serial));
			printToGUI("Meter (" + serial + ") was successfully looked up");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			printToGUI("Failed attempt to find previously registered Meter (" + serial + ")");
			throw new NotRegisteredException("This Meter (" + serial + ") does not exist");
		}
		return meter;
	}

	@Override
	public synchronized RemotePowerCompanyInterface getPowerCompany(String name) throws RemoteException, NotRegisteredException {
		// Can only look up an existing object
		if (!powerCompanies.containsKey(name)) {
			printToGUI("Failed attempt to find never registered Power Company (" + name + ")");
			throw new NotRegisteredException("This Power Company (" + name + ") does not exist");
		}

		RemotePowerCompanyInterface powerCompany = null;
		try {
			// Look up with constructed URI
			powerCompany = (RemotePowerCompanyInterface) Naming.lookup("rmi://localhost/" + powerCompanies.get(name));
			printToGUI("Power Company (" + name + ") was successfully looked up");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			printToGUI("Failed attempt to find previously registered Power Company (" + name + ")");
			throw new NotRegisteredException("This Power Company (" + name + ") does not exist");
		}
		return powerCompany;
	}

	@Override
	public synchronized RemoteBrokerInterface getBroker(String name) throws RemoteException, NotRegisteredException {
		// Can only look up an existing object
		if (!brokers.containsKey(name)) {
			printToGUI("Failed attempt to find never registered Broker (" + name + ")");
			throw new NotRegisteredException("This Broker (" + name + ") does not exist");
		}

		RemoteBrokerInterface broker = null;
		try {
			// Look up with constructed URI
			broker = (RemoteBrokerInterface) Naming.lookup("rmi://localhost/" + brokers.get(name));
			printToGUI("Broker (" + name + ") was successfully looked up");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			printToGUI("Failed attempt to find previously registered Broker (" + name + ")");
			throw new NotRegisteredException("This Broker (" + name + ") does not exist");
		}
		return broker;
	}

	@Override
	public int getSerial() throws RemoteException {
		currentSerial++;
		return currentSerial;
	}

	@Override
	public int getPowerCompanyNumber() throws RemoteException {
		currentPowerCompany++;
		return currentPowerCompany;
	}

	@Override
	public int getBrokerNumber() throws RemoteException {
		currentBroker++;
		return currentBroker;
	}

	@Override
	public synchronized String[] getPowerCompanies() throws RemoteException {
		String[] array = new String[powerCompanies.size()];
		powerCompanies.keySet().toArray(array);
		return array;
	}

	@Override
	public synchronized String[] getBrokers() throws RemoteException {
		String[] array = new String[brokers.size()];
		brokers.keySet().toArray(array);
		return array;
	}

	@Override
	public synchronized void registerMeter(String uri, int serial) throws RemoteException {
		// All meters get a serial so no need for checking
		meters.put(serial, uri);
		printToGUI("Meter (" + serial + ") successfully registered");
	}

	@Override
	public synchronized void registerPowerCompany(String uri, String name) throws RemoteException, AlreadyRegisteredException {
		// Can only register a name once
		if (powerCompanies.containsKey(name)) {
			printToGUI("Attempt to reregister Power Company (" + name + ")");
			throw new AlreadyRegisteredException("This name is already registered to a Power Company");
		}
		powerCompanies.put(name, uri);
		printToGUI("Power Company (" + name + ") successfully registered");
	}

	@Override
	public synchronized void registerBroker(String uri, String name) throws RemoteException, AlreadyRegisteredException {
		// Can only register a name once
		if (brokers.containsKey(name)) {
			printToGUI("Attempt to reregister Broker (" + name + ")");
			throw new AlreadyRegisteredException("This name is already registered to a Broker");
		}
		brokers.put(name, uri);
		printToGUI("Broker (" + name + ") successfully registered");
	}

	@Override
	public void test() throws RemoteException {
	}

	@Override
	public boolean existsPowerCompanies() throws RemoteException {
		return !powerCompanies.isEmpty();
	}

	@Override
	public boolean existsBrokers() throws RemoteException {
		return !brokers.isEmpty();
	}
}
