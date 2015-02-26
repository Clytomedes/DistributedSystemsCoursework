import java.rmi.*;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public interface RemoteMeterInterface extends Remote {
	public void registerPowerCompany(String name) throws RemoteException;

	public void unregisterPowerCompany() throws RemoteException;

	public void runCommand(String command) throws RemoteException;

	public Readings getReadingsHistory() throws RemoteException;

	public void recieveDeal(Deal deal) throws RemoteException;

	public String getCurrentPowerCompany() throws RemoteException;
}
