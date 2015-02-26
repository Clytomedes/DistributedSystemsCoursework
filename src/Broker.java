import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class Broker {

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

			// Use program argument for the name of the Broker
			String name = null;
			if (args.length == 0) {
				// Default name if none specified
				name = Integer.toString(server.getBrokerNumber());
				System.out.println("Received Broker Name");
			} else {
				name = args[0].replace(" ", "_");
			}

			String objectName = "Broker-" + name;

			// Retry loop to make sure of connection success
			while (true) {
				try {
					// Create and bind object to registry
					BrokerObject broker = new BrokerObject(server, name);
					Naming.rebind(objectName, broker);

					// Register the object with the server
					server.registerBroker(objectName, name);
					System.out.println("Broker (" + objectName + ") ready");

					// Start the GUI
					BrokerGUI gui = new BrokerGUI(broker);
					broker.setGUI(gui);
					break;
				} catch (RemoteException e) {
					System.err.println("Connection Failure, retrying");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e2) {
					}
				} catch (AlreadyRegisteredException e) {
					System.err.println(e);
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
