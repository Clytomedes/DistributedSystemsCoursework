import java.rmi.*;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public interface RemoteBrokerInterface extends Remote {
	public void findDeal(int serial) throws RemoteException, NotRegisteredException;

	public void acceptDeal(int serial) throws RemoteException, NoDealException;

	public void rejectDeal(int serial) throws RemoteException, NoDealException;
}
