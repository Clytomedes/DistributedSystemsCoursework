import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class PowerCompany {

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

			// Use program argument for the name of the Power Company
			String name = null;
			if (args.length == 0) {
				name = Integer.toString(server.getPowerCompanyNumber());
				System.out.println("Received Power Company Name");
			} else {
				name = args[0].replace(" ", "_");
			}

			String objectName = "PowerCompany-" + name;

			while (true) {
				try {
					// Create and bind object to registry
					PowerCompanyObject powerCompany = new PowerCompanyObject(server, name);
					Naming.rebind(objectName, powerCompany);

					// Register the object with the server
					server.registerPowerCompany(objectName, name);
					System.out.println("Power Company (" + objectName + ") ready");

					// Start the GUI
					PowerCompanyGUI gui = new PowerCompanyGUI(powerCompany);
					powerCompany.setGUI(gui);
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
