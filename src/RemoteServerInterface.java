import java.rmi.*;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public interface RemoteServerInterface extends Remote {
	// This would be changed to a URL so it could be changed easily
	public static final String serverURI = "rmi://localhost/Server";

	public RemoteMeterInterface getMeter(int serial) throws RemoteException, NotRegisteredException;

	public RemotePowerCompanyInterface getPowerCompany(String name) throws RemoteException, NotRegisteredException;

	public RemoteBrokerInterface getBroker(String name) throws RemoteException, NotRegisteredException;

	public int getSerial() throws RemoteException;

	public int getPowerCompanyNumber() throws RemoteException;

	public int getBrokerNumber() throws RemoteException;

	public String[] getPowerCompanies() throws RemoteException;

	public String[] getBrokers() throws RemoteException;

	public void registerMeter(String uri, int number) throws RemoteException;

	public void registerPowerCompany(String uri, String name) throws RemoteException, AlreadyRegisteredException;

	public void registerBroker(String uri, String name) throws RemoteException, AlreadyRegisteredException;

	public void test() throws RemoteException;

	public boolean existsPowerCompanies() throws RemoteException;

	public boolean existsBrokers() throws RemoteException;
}
