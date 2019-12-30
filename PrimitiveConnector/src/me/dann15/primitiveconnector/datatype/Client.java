package me.dann15.primitiveconnector.datatype;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * 
 * The purpose of this class is to store very basic information
 * about a Client that is connected to a server.
 * 
 * This is achieved by making a "Client" datatype (as done here).
 * 
 * This class will also contain any useful methods in dealing with Clients.
 *
 */
public class Client {

	private InetAddress ip;
	private int port;
	
	/**
	 * 
	 * Extracts the IP address and port of a client who had sent a DatagramPacket.
	 * 
	 * Returns IP and Port as a Client datatype.
	 * 
	 * @param packet Packet received from a client.
	 * @return Client that the DatagramPacket is from.
	 */
	public static Client extractClient(DatagramPacket packet) {
		return new Client(packet.getAddress(),packet.getPort());
	}
	
	public Client(InetAddress ip, int port) {
		this.ip=ip;
		this.port=port;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
}
