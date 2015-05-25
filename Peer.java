import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

public class Peer {

	final String bootstrapHost = "129.21.30.38";

	NodeInterface localNode;
	BInterface bootstrap;
	InetAddress localInet;

	Peer() {
		localNode = null;
		try {

			localInet = InetAddress.getLocalHost();
			bootstrap = (BInterface) LocateRegistry.getRegistry(bootstrapHost,
					8534).lookup("Bootstrap");

		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to Bootstrap");
	}

	public void join() {

		if (localNode != null) {
			System.out.println("Failure: Peer already joined");
			return;
		}
		try {
			Random rand = new Random();

			Registry registry = LocateRegistry.createRegistry(8534);
			if (bootstrap != null && bootstrap.getBootstrapIp() == null) {
				localNode = new Node(localInet);
				registry.rebind("Peer", localNode);
				bootstrap.setBootstrapIp(localInet);
				localNode.setIsBootstrap(true);
				System.out.println("Bootstraping this computer");
				return;
			} else {

				NodeInterface node = (NodeInterface) LocateRegistry
						.getRegistry(
								bootstrap.getBootstrapIp().getHostAddress(),
								8534).lookup("Peer");

				if (localNode == null) {

					localNode = node.join(localInet, rand.nextInt(10),
							rand.nextInt(10));
					if (localNode != null) {

						registry.rebind("Peer", localNode);
						localNode.updateNeighbours();
						System.out.println("Peer successfully joined");
						return;
					}

				}
			}
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Failure: Failed to join.");
	}

	public int calcHashX(String keyword) {
		int hashX = 0;
		for (int i = 1; i < keyword.length(); i = i + 2) {
			hashX += keyword.charAt(i);
		}
		return hashX % 10;
	}

	public int calcHashY(String keyword) {
		int hashY = 0;
		for (int i = 0; i < keyword.length(); i = i + 2) {
			hashY += keyword.charAt(i);
		}
		return hashY % 10;
	}

	public void view() {

		if (localNode == null) {
			System.out.println("Please first join this peer to Network");
			return;
		} else {

			try {
				System.out.println(localNode.view());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addFile(String filelocation) {

		if (localNode == null) {
			System.out.println("Please first Join this peer to network");
			return;
		}
		try {
			FileReader fr = new FileReader(filelocation);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("No such file found ");
			return;
		}

		String filename = filelocation;
		if (filelocation.contains("/")) {
			filename = filelocation
					.substring(filelocation.lastIndexOf("/") + 1);
			filelocation = filelocation.substring(0,
					filelocation.lastIndexOf("/") + 1);
		}
		int x = calcHashX(filename);
		int y = calcHashX(filename);
		String route = "";
		try {
			if (bootstrap != null && bootstrap.getBootstrapIp() != null) {
				NodeInterface node = (NodeInterface) LocateRegistry
						.getRegistry(
								bootstrap.getBootstrapIp().getHostAddress(),
								8534).lookup("Peer");
				String success = node.insertFile(localNode, filelocation,
						filename, x, y, route);
				if (success == null) {
					System.out.println("Failure");
				} else {
					System.out.println(success);

				}
			}
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void searchFile(String filelocation) {

		if (localNode == null) {
			System.out.println("Please first Join this peer to network");
			return;
		}
		String filename = filelocation;
		try {
			FileReader fr = new FileReader(filelocation);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("No such file found ");
			return;
		}
		if (filelocation.contains("/")) {
			filename = filelocation
					.substring(filelocation.lastIndexOf("/") + 1);
			filelocation = filelocation.substring(0,
					filelocation.lastIndexOf("/") + 1);
		}
		int x = calcHashX(filename);
		int y = calcHashX(filename);
		String route = "";
		try {
			if (bootstrap != null && bootstrap.getBootstrapIp() != null) {
				NodeInterface node = (NodeInterface) LocateRegistry
						.getRegistry(
								bootstrap.getBootstrapIp().getHostAddress(),
								8534).lookup("Peer");
				String found = node.searchFile(filename, x, y, route);
				if (found == null) {
					System.out.println("Failure");
				} else {
					System.out.println(found);

				}
			}
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		if (bootstrap != null && bootstrap.getBootstrapIp() != null
				&& localInet.equals(bootstrap.getBootstrapIp())) {
			bootstrap.setBootstrapIp(null);
		}
	}

	public void leave() {
		if (localNode == null) {
			System.out.println("Please first Join this peer to network");
			return;
		}
		try {
			System.out.println("Leaving node");
			localNode.leave();
			System.out.println("Bye Bye");
			System.exit(0);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws IOException {

		Peer peer = new Peer();
		Random rand = new Random();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("\n");
			System.out.println("1. Insert");
			System.out.println("2. Search");
			System.out.println("3. View");
			System.out.println("4. Join");
			System.out.println("5. Leave");
			System.out.println("6. Exit");
			System.out.println("Please enter your choice: ");

			int n = 0;
			try {
				n = Integer.parseInt(br.readLine());
			} catch (Exception e) {
				System.out.println("Please enter valid input");
			}

			switch (n) {
			case 1:
				System.out.println("FileLocation : ");
				String keyword = br.readLine();
				peer.addFile(keyword);
				break;
			case 2:
				System.out.println("FileLocation: ");
				keyword = br.readLine();
				peer.searchFile(keyword);
				break;
			case 3:
				peer.view();
				break;
			case 4:
				peer.join();
				break;
			case 5:
				peer.leave();
				break;
			case 6:
				if (peer.bootstrap.getBootstrapIp() != null
						&& peer.bootstrap
								.getBootstrapIp()
								.getHostAddress()
								.equals(InetAddress.getLocalHost()
										.getHostAddress())) {
					try {
						peer.bootstrap.setBootstrapIp(null);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				System.exit(0);
				break;
			default:
				System.out.println("Please enter correct choice");
			}

		}
	}
}
