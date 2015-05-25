import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Node extends UnicastRemoteObject implements NodeInterface,
		Serializable {

	String id;
	Zone zone;
	ArrayList<NodeInterface> neighbours;
	InetAddress ipAddress;
	HashMap<Point, String> file;
	NodeInterface parentNode;
	Node tempNodeforSwap;
	boolean isBootstrap=false;
	public String getId() throws RemoteException {
		return id;
	}

	public InetAddress getIpAddress() throws RemoteException {
		return ipAddress;
	}
	public boolean getIsBootstrap()throws RemoteException
	{
		return isBootstrap;
	}
	public void setIsBootstrap(boolean val) throws RemoteException
	{
		isBootstrap=true;
	}
	Node() throws RemoteException {
		neighbours = new ArrayList<NodeInterface>();
		file = new HashMap<Point, String>();

	}

	Node(InetAddress ipAddress) throws RemoteException {
		id = ipAddress.getHostName().toLowerCase();
		this.ipAddress = ipAddress;
		zone = new Zone(new Point(0, 0), new Point(10, 10));
		this.neighbours = new ArrayList<NodeInterface>();
		this.file = new HashMap<Point, String>();
	}

	Node(String identifier, Zone zone, InetAddress ipAdress)
			throws RemoteException {
		this.id = identifier;
		this.zone = zone;
		this.ipAddress = ipAdress;
		this.neighbours = new ArrayList<NodeInterface>();
		this.file = new HashMap<Point, String>();

	}

	public String view() throws RemoteException {
		String display = "";
		display += "Identifier: " + id + "\n";
		display += "Ip Address: " + ipAddress.getHostAddress().toString()
				+ "\n";
		display += "Zone: " + zone + "\n";

		display += "Neighbours: ";
		for (NodeInterface n : neighbours) {
			display += n.getId() + " ";
		}
		display += "\n";
		display += "Files Stored: ";
		for (String filenames : file.values()) {
			display += filenames + " ";
		}
		display += "\n\n";
		return display;
	}

	public ArrayList<NodeInterface> getNeighbours() throws RemoteException {
		return neighbours;
	}

	public Zone getZone() throws RemoteException {
		return zone;
	}

	@Override
	public void addNeighbour(NodeInterface n) throws RemoteException {
		if (neighbours.contains(n))
			return;
		neighbours.add(n);

	}

	@Override
	public void removeNeighbour(NodeInterface n) throws RemoteException {
		neighbours.remove(n);

	}

	public void putAllFile(HashMap<Point, String> files) throws RemoteException {
		this.file.putAll(files);
	}

	public HashMap<Point, String> getAllFiles() throws RemoteException {
		return file;
	}

	public NodeInterface getParent() throws RemoteException
	{
		return parentNode;
	}
	public void setNeighbours(ArrayList<NodeInterface> ng)
			throws RemoteException {
		neighbours = ng;
	}

	public void setFile(HashMap<Point, String> file) throws RemoteException {
		this.file = file;
	}

	public NodeInterface join(InetAddress ip, double x, double y)
			throws RemoteException {
		Node node = null;
		NodeInterface iNode = null;
		Point point = new Point(x, y);
		if (zone.contains(point)) {
			node = new Node(ip.getHostName().toLowerCase(), zone.split(), ip);
			node.neighbours.addAll(neighbours);
			node.neighbours.add(this);
			node.parentNode = this;
			neighbours.add(node);
			iNode = node;

		} else {
			return getRemoteNode(getBestNode(point)).join(ip, x, y);
		}

		return iNode;
	}

	public byte[] downloadFile(String fileLocation, String fileName)
			throws RemoteException {
		byte buffer[] = null;
		try {
			File file = new File(fileLocation + fileName);
			buffer = new byte[(int) file.length()];
			BufferedInputStream input = new BufferedInputStream(
					new FileInputStream(fileLocation + fileName));
			input.read(buffer, 0, buffer.length);
			input.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
		return (buffer);
	}

	public void updateNeighbours() throws RemoteException {

		if(neighbours==null)
		{
			return;
		}
		NodeInterface neighbourArray[] = new NodeInterface[neighbours.size()];
		neighbourArray = neighbours.toArray(neighbourArray);
		for (NodeInterface n : neighbourArray) {
			NodeInterface nb = getRemoteNode(n);
			if (nb.getId() == null) {
				neighbours.remove(n);
				continue;
			}

			ArrayList<NodeInterface> remoteNeighbours = nb.getNeighbours();
			if (!isNeighbour(nb.getZone())) {

				if (remoteNeighbours.contains(this)) {
					nb.removeNeighbour(this);
				}
				neighbours.remove(n);
			} else if (!remoteNeighbours.contains(this)) {
				nb.addNeighbour(this);
			}

		}

		if (parentNode != null) {
			parentNode.updateNeighbours();
		}

	}

	public void removeFromNeighbours() {
		NodeInterface neighbourArray[] = new NodeInterface[neighbours.size()];
		neighbourArray = neighbours.toArray(neighbourArray);
		for (NodeInterface n : neighbourArray) {
			try {
				n.removeNeighbour(this);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public NodeInterface getRemoteNode(NodeInterface n) {
		NodeInterface remoteNode = null;
		try {
			Registry remoteReg = LocateRegistry.getRegistry(n.getIpAddress()
					.getHostAddress(), 8534);
			if (remoteReg != null) {
				remoteNode = (NodeInterface) remoteReg.lookup("Peer");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.print("Unable to connect to remote host");
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			System.out
					.println("Peer not bound to the port: Please restart the process");
		}

		return remoteNode;
	}

	public boolean isNeighbour(Zone neighbour) {
		// From top
		if ((neighbour.bottomLeft.y == zone.topLeft.y && neighbour.bottomLeft.x == zone.topLeft.x)
				|| (neighbour.bottomRight.y == zone.topRight.y && neighbour.bottomRight.x == zone.topRight.x))
			return true;
		// From bottom
		else if ((neighbour.topLeft.y == zone.bottomLeft.y && neighbour.topLeft.x == zone.bottomLeft.x)
				|| (neighbour.topRight.y == zone.bottomRight.y && neighbour.topRight.x == zone.bottomRight.x))
			return true;

		// From Left
		else if ((neighbour.topRight.x == zone.topLeft.x && neighbour.topRight.y == zone.topLeft.y)
				|| (neighbour.bottomRight.x == zone.bottomLeft.x && neighbour.bottomRight.x == zone.bottomLeft.x))
			return true;

		// From Right
		else if ((neighbour.topLeft.x == zone.topRight.x && neighbour.topLeft.y == zone.topRight.y)
				|| (neighbour.bottomLeft.x == zone.bottomRight.x && neighbour.bottomLeft.y == zone.bottomRight.y))
			return true;

		else
			return false;

	}

	private double calcDistance(Point a, Point b) {
		return Math.pow((b.x - a.x), 2) - Math.pow((b.y - a.y), 2);
	}

	private NodeInterface getBestNode(Point point) throws RemoteException {

		NodeInterface best = null;
		double dist = 0;
		double min = calcDistance(zone.centre, point);

		for (NodeInterface r : neighbours) {
			NodeInterface remote = getRemoteNode(r);
			if (remote != null && remote.getZone().contains(point)) {
				best = remote;
			} else if (remote != null
					&& (dist = calcDistance(remote.getZone().centre, point)) < min) {
				min = dist;
				best = remote;
			}
		}

		return best;
	}

	@Override
	public String insertFile(NodeInterface node, String fileLocation,
			String filename, int x, int y, String route) throws RemoteException {
		Point point = new Point(x, y);

		String peer = null;
		if (zone.contains(point)) {
			try {
				route += " -> " + id;
				file.put(point, filename);
				File file = new File(filename);
				byte[] filedata = getRemoteNode(node).downloadFile(
						fileLocation, filename);
				BufferedOutputStream output = new BufferedOutputStream(
						new FileOutputStream(file.getName()));
				output.write(filedata, 0, filedata.length);
				output.flush();
				output.close();
				peer = "Peer " + id + " stores the file\n";
				peer += "Route " + route;
				return peer;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			try {
				route += " -> " + id;
				return getRemoteNode(getBestNode(point)).insertFile(node,
						fileLocation, filename, x, y, route);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return peer;

	}

	@Override
	public String searchFile(String keyword, int x, int y, String route)
			throws RemoteException {
		Point point = new Point(x, y);

		String filename = null;
		String peer = null;
		if (zone.contains(point)) {
			route += " -> " + id;
			filename = file.get(point);
			if (filename == null) {
				return null;
			}
			peer = "Peer " + id + " stores the file " + filename + "\n";
			peer += "Route " + route;
			return peer;
		} else {

			try {
				route += " -> " + id;
				return getRemoteNode(getBestNode(point)).searchFile(keyword, x,
						y, route);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return peer;
	}

	public void mergeZone(Zone zone) throws RemoteException {
		this.zone.merge(zone);
	}

	public void drop() {

		ipAddress = null;
		zone = null;
		id = null;
		neighbours = null;
		file = null;
		parentNode = null;
	}

	public void leave() throws RemoteException {
		// check zones surface area matches with neighbours surface area
		
		for (NodeInterface nb : neighbours) {
			try {
				NodeInterface n= getRemoteNode(nb);
				if (n.getZone().surfaceEqual(zone)) {
					n.mergeZone(zone);
					mergeNode(n);

					return;
				}
				
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		double minSurfaceArea= 1000;
		NodeInterface minNode=null;
		for(NodeInterface n :neighbours)
		{
			NodeInterface nb= getRemoteNode(n);
			try {
				if(nb.getZone().getSurfaceArea()<minSurfaceArea&&!nb.getIsBootstrap())
				{
					minSurfaceArea=nb.getZone().getSurfaceArea();
					minNode= nb;
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		swapNodes(minNode);
		updateNeighbours();
		leave();
		
		
	}

	public void mergeNode(NodeInterface node) {
		try {

			node=getRemoteNode(node);
			for (NodeInterface nb : neighbours) {
				NodeInterface n= getRemoteNode(nb);
				if (n.getId().equals(node.getId()))
					continue;
				node.addNeighbour(n);
			}
			node.putAllFile(file);
			removeFromNeighbours();
			
			drop();
			node.updateNeighbours();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public NodeInterface getTemp()
	{
		return tempNodeforSwap;
	}
	
	public void initiateSwap(NodeInterface node)
	{
		try {
			NodeInterface temp= getRemoteNode(node).getTemp();
			file= temp.getAllFiles();
			neighbours=temp.getNeighbours();
			zone= temp.getZone();
			parentNode=temp.getParent();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void swapNodes(NodeInterface node)
	{
		try {
			tempNodeforSwap=new Node();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tempNodeforSwap.file=file;
		tempNodeforSwap.neighbours=neighbours;
		tempNodeforSwap.zone=zone;
		tempNodeforSwap.parentNode=parentNode;
		
		try {
			node=getRemoteNode(node);
			file= node.getAllFiles();
			neighbours=node.getNeighbours();
			zone= node.getZone();
			parentNode=node.getParent();
			
			node.initiateSwap(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

}
