import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface NodeInterface extends Remote {

	NodeInterface join(InetAddress ip, double x, double y)
			throws RemoteException;

	ArrayList<NodeInterface> getNeighbours() throws RemoteException;

	void addNeighbour(NodeInterface n) throws RemoteException;

	void removeNeighbour(NodeInterface n) throws RemoteException;

	void updateNeighbours() throws RemoteException;

	Zone getZone() throws RemoteException;

	String view() throws RemoteException;

	String getId() throws RemoteException;

	InetAddress getIpAddress() throws RemoteException;

	String insertFile(NodeInterface node, String fileLocation, String filename,
			int x, int y, String route) throws RemoteException;

	String searchFile(String keyword, int x, int y, String route)
			throws RemoteException;

	void mergeZone(Zone zone) throws RemoteException;

	void putAllFile(HashMap<Point, String> files) throws RemoteException;

	void leave() throws RemoteException;

	byte[] downloadFile(String fileLocaiton, String filename)
			throws RemoteException;

	HashMap<Point,String> getAllFiles() throws RemoteException;
	NodeInterface getParent() throws RemoteException;
    NodeInterface getTemp() throws RemoteException;
	void initiateSwap(NodeInterface node) throws RemoteException;
	public void setIsBootstrap(boolean val) throws RemoteException;
	public boolean getIsBootstrap()throws RemoteException;
}
