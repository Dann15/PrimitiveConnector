package me.dann15.primitiveconnector.udpmulticastconnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.List;

import me.dann15.primitiveconnector.listener.DatagramPacketListener;

public class UDPMulticastConnector extends MulticastSocket {
	
	// TODO add safe check for calling "join" again on an already closed socket.

	private List<DatagramPacketListener> packetListeners = new ArrayList<DatagramPacketListener>();
	private boolean isFinished;
	private InetAddress host;
	private int maxReceivedByteLength;
	private int port;
	private int maxSendByteLength = -1; // default value; -1 means to ignore.
	
	/**
	 * Creates a device that can join a multicast connection (UDP) on a certain host.
	 *
	 * The connector can send and receive packets to the multicast group.
	 * Note that any packets sent by a connector, will also be received by the connector again,
	 * due to the nature of the multicast connection.
	 * 
	 * This can be accounted for in your own code.
	 * 
	 * @param host
	 * @param port
	 * @param maxByteLength
	 * @throws IOException
	 */
	public UDPMulticastConnector(InetAddress host, int port, int maxReceivedByteLength) throws IOException {
		super(port);
		this.host = host;
		this.port = port;
		this.maxReceivedByteLength = maxReceivedByteLength;
		setTimeToLive(1);
		isFinished = false;
	}	
	
	/**
	 * Creates a device that can join a multicast connection (UDP) on a certain host.
	 * 
	 * The connector can send and receive packets to the multicast group.
	 * Note that any packets sent by a connector, will also be received by the connector again,
	 * due to the nature of the multicast connection.
	 * 
	 * This can be accounted for in your own code.
	 * 
	 * @param host
	 * @param port
	 * @param maxByteLength
	 * @param listener
	 * @throws IOException
	 */
	public UDPMulticastConnector(InetAddress host, int port, int maxByteLength, DatagramPacketListener listener) throws IOException {
		this(host,port,maxByteLength);
		addDatagramPacketListener(listener);
	}
	
	// TODO annotate
	public void setMaxReceivedByteLength(int maxReceivedByteLength) {
		this.maxReceivedByteLength = maxReceivedByteLength;
	}
	
	/**
	 * Adds a DatagramPacketListener that will be triggered when a
	 * Datagram packet is received..
	 * 
	 * @param listener 
	 */
	public void addDatagramPacketListener(DatagramPacketListener listener) {
		packetListeners.add(listener);
	}
	
	/**
	 * Joins network; starts listening for packets.
	 * @throws IOException
	 */
	public void join() throws IOException {
		joinGroup(host);
		isFinished = false;
		listenForPackets();
	}
	
	/**
	 * Exits host group.
	 * Note: to fully stop the connection, after this function, call the {@code close()} function.
	 * @throws IOException
	 */
	public void exit() throws IOException {
		leaveGroup(host);
		isFinished = true;
	}
	
	/**
	 * Set the maximum length in bytes that can be sent at once.
	 * If you set to {@code -1}, there will be no maximum length in bytes to send (default value).
	 * 
	 * The {@code send(byte[])} function of this class will throw a BufferOverload exception 
	 * should the length of the {@code byte[]} exceeds that of the {@code maxSendByteLength} specified.
	 * 
	 * @param maxSendByteLength The maximum length of byte[] data that can be sent.
	 */
	public void setMaxSendByteLength(int maxSendByteLength) {
		this.maxSendByteLength = maxSendByteLength;
	}
	
	/**
	 * Sends a byte array of data as a Datagram Packet to the
	 * Multicast Network host.
	 * 
	 * ONLY if specified by {@code setMaxSendByteLength} function in this class,
	 * this function will throw a BufferOverloadException if size of {@code buffer}
	 * is greater than maximum value as set by the aforementioned function.
	 * 
	 * If no maximum has been specified, no check will be made.
	 * 
	 * 
	 * @param buffer Byte array of data to send as a packet.
	 * @throws IOException
	 */
	public void send(byte[] buffer) throws IOException {
		
		if(maxSendByteLength != -1 && maxSendByteLength > buffer.length) {
			throw new BufferOverflowException();
		}
		
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,host,port);
		send(packet);
	}
	
	/**
	 * Starts listening for Datagram Packets.
	 * Once one is received, triggers all the listeners
	 */
	private void listenForPackets() {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {

		    	while(!isFinished) { // while connection is ongoing
		    		byte[] bufferReceived = new byte[maxReceivedByteLength];
		    		DatagramPacket receivedPacket = new DatagramPacket(bufferReceived, bufferReceived.length,host,port);
		    		
		    		try {
		    			receive(receivedPacket);
		    			
		    			for (DatagramPacketListener listener : packetListeners) {
		    				listener.recievedDatagramPacket(receivedPacket);
		    			}
		    			
		    			
		    		} catch (IOException e) {
		    			// Socket terminated.
		    		}
		    		
		    	}
				
				
			}
		});
		t.start();
	}
	
}
