package me.dann15.primitiveconnector.multicastconnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.List;

import me.dann15.primitiveconnector.listener.DatagramPacketListener;

public class MulticastConnector extends MulticastSocket {
	

	private List<DatagramPacketListener> packetListeners = new ArrayList<DatagramPacketListener>();
	private boolean isFinished;
	private InetAddress host;
	private int maxByteLength;
	private int port;
	
	/**
	 * Creates a device that can join a multicast connection on a certain host.
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
	public MulticastConnector(InetAddress host, int port, int maxByteLength) throws IOException {
		super(port);
		this.host = host;
		this.port = port;
		this.maxByteLength = maxByteLength;
		setTimeToLive(1);
		isFinished = false;
	}	
	
	/**
	 * Creates a device that can join a multicast connection on a certain host.
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
	public MulticastConnector(InetAddress host, int port, int maxByteLength, DatagramPacketListener listener) throws IOException {
		this(host,port,maxByteLength);
		packetListeners.add(listener);
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
	 * @throws IOException
	 */
	public void exit() throws IOException {
		leaveGroup(host);
		isFinished = true;
	}
	
	/**
	 * Sends a byte array of data as a Datagram Packet to the
	 * Multicast Network host.
	 * 
	 * This function checks if buffer length
	 * is greater than allowed maximum. Throws a BufferOverflowException if exceeded.
	 * 
	 * @param buffer Byte array of data to send as a packet.
	 * @throws IOException
	 */
	public void send(byte[] buffer) throws IOException {
		
		if(buffer.length > maxByteLength) {
			throw new BufferOverflowException();
		}
		
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,host,port);
		send(packet);
	}
	
	/**
	 * Sends a byte array of data as a Datagram Packet to the
	 * Multicast Network host.
	 * 
	 * It can be chosen whether or not to ignore buffer overflowing (buffer length > max buffer length).
	 * By default, this check is made.
	 * 
	 * @param buffer Byte array of data to send as a packet.
	 * @param ignoreBufferOverflow If true, does NOT check if buffer size exceeds maximum buffer size.
	 * @throws IOException
	 */
	public void send(byte[] buffer, boolean ignoreBufferOverflow) throws IOException {
		if(ignoreBufferOverflow == false) {
			send(buffer);
			return;
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
				
				
		    	while(!isFinished) { // connection is not finished
		    		byte[] buffer = new byte[maxByteLength];
		    		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,host,port);
		    		
		    		try {
		    			receive(packet);
		    			
		    			for (DatagramPacketListener listener : packetListeners) {
		    				listener.recievedDatagramPacket(packet,buffer);
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
