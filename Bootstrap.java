
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class Bootstrap extends UnicastRemoteObject implements BInterface,Serializable {

	InetAddress bootStrapIp;
	
	Bootstrap() throws RemoteException
	{
		bootStrapIp=null;
	}
	
	@Override
	public InetAddress getBootstrapIp() throws RemoteException {
		
		return bootStrapIp;
	}

	@Override
	public void setBootstrapIp(InetAddress ip) throws RemoteException{
		bootStrapIp=ip;
		
		if(ip!=null)
		System.out.println("Bootstrap running on Ip Address: "+ip.getHostAddress());
	}
	public static void main(String args[])
	{
		try {
			Registry registry=LocateRegistry.createRegistry(8534);
			BInterface bNode= new Bootstrap();
			try {
				registry.bind("Bootstrap", bNode);
				System.out.println("Bootstrap Server started...Waiting for Client");
			} catch (AlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
