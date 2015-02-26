import java.rmi.*;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public interface RemotePowerCompanyInterface extends Remote {
	public void registerMeter(int serial) throws RemoteException, AlreadyRegisteredException, NotRegisteredException;

	public void unregisterMeter(int serial) throws RemoteException;

	public void receiveReadings(Readings newReadings, int serial) throws RemoteException, NotRegisteredException;

	public void receiveAlert(Alert alert, int serial) throws RemoteException;

	public TariffInfo getTariffInfo() throws RemoteException;

	public String getName() throws RemoteException;
}
