import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class Server {

	public static void main(String[] args) {
		try {
			// Attempt to start rmiregistry
			try {
				LocateRegistry.createRegistry(1099);
			} catch (ExportException e) {
				// Catch Exception if it is already started
			}

			// Create and bind object to registry
			ServerObject server = new ServerObject();
			Naming.rebind("Server", server);
			System.out.println("Server now ready");

			// Start the GUI
			ServerGUI gui = new ServerGUI();
			server.setGUI(gui);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
