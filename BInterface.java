import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BInterface extends Remote {

	InetAddress getBootstrapIp() throws RemoteException;

	void setBootstrapIp(InetAddress ip) throws RemoteException;
}
