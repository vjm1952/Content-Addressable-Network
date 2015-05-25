
Author: Vishal Manghnani
1. Files
   Server files: Bootstrap.java,BInterface.java,
   Client/Peer files: Node.java, NodeInterface.java,Peer.java,Zone.java, Point.java

2. Functionalities
	a. Insert:  Inserts a file in the CAN network after routing to appropriate Peer. Displays the peer name and Routing details if file successfully inserted
		    else displays Failure. Files are stored in the local directory where the project runs.
	b. Search:  Searches the file in the CAN Network after routing to Peer where file is stored. Displays the peer name and Routing details if file successfully 
		    found else displays Failure
	c. View:    Display current Peers information like Identifier, Ipaddress, Zone, Neighbours and Files Stored
	d. Join:    Joins the peer in the network. If join successful then displays the success message else displays Failure
	e. Leave:   Leaves the network by handing over all the resources to one of the neighbouring peer. 

3. Steps to run
	Server: 
	a. Assuming glados machine used to run the Server files. If different machine used please update the Ipaddress in Peer.java file.
	b. Start rmiregistry using command "rmiregistry 2001 &"  on server machine
	c. Compile Bootstrap.java and BInterface.java files.
	d. Create stub and skeleton using command "rmic Bootstrap"
	e. Start the server using command "java Bootstrap &"

	Client/Peer machines:
	a. Compile all the client files on all the peers.
	b. Create stub and skeleton using command "rmic Node"
	c. Start the client using command "java Peer"


		
		
