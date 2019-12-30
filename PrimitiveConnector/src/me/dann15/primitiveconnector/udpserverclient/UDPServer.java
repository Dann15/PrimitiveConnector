package me.dann15.primitiveconnector.udpserverclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.List;

import me.dann15.primitiveconnector.datatype.Client;
import me.dann15.primitiveconnector.listener.DatagramPacketListener;

public class UDPServer extends DatagramSocket {

	// TODO client side connection
	
	private int maxSendByteLength = -1; // default value; -1 means to ignore.
	private List<DatagramPacketListener> packetListeners = new ArrayList<DatagramPacketListener>();
	private boolean isFinished;
	private int maxReceivedByteLength;
	
	/**
	 * 
	 * Creates a modified DatagramSocket idealized for UDP connections
	 * between a Server and client(s).
	 * 
	 * @param port
	 * @param maxReceivedByteLength
	 * @throws SocketException
	 */
	public UDPServer(int port, int maxReceivedByteLength) throws SocketException {
		super(port);
		this.maxReceivedByteLength = maxReceivedByteLength;
		listenForPackets();
		isFinished=false;
		listenForPackets();
	}
	
	/**
	 * 
	 * Creates a modified DatagramSocket idealized for UDP connections
	 * between a Server and client(s).
	 * 
	 * @param port
	 * @param maxReceivedByteLength
	 * @param listener
	 * @throws SocketException
	 */
	public UDPServer(int port, int maxReceivedByteLength,DatagramPacketListener listener) throws SocketException {
		this(port,maxReceivedByteLength);
		addDatagramPacketListener(listener);
	}
	
	/**
	 * Set the maximum length in bytes that can be sent at once.
	 * If you set to {@code -1}, there will be no maximum length in bytes to send (default value).
	 * 
	 * The {@code send} function of this class will throw a BufferOverload exception 
	 * should the length of the {@code byte[]} exceeds that of the {@code maxSendByteLength} specified.
	 * 
	 * @param maxSendByteLength The maximum length of byte[] data that can be sent.
	 */
	public void setMaxReceivedByteLength(int maxReceivedByteLength) {
		this.maxReceivedByteLength = maxReceivedByteLength;
	}
	
	/**
	 * Stops listening for DatagramPackets from client.
	 * To fully close the connection, make sure to close this socket
	 * alongside this.
	 */
	public void stop() {
		isFinished=true;
	}
	
	/**
	 * Sends a byte array of data as a Datagram Packet to the
	 * specified InetAddress and port.
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
	public void send(InetAddress ip, int port, byte[] buffer) throws IOException {
		
		if(maxSendByteLength != -1 && maxSendByteLength > buffer.length) {
			throw new BufferOverflowException();
		}

		send(new DatagramPacket(buffer, buffer.length, ip, port));
	}
	
	/**
	 * Sends a byte array of data as a Datagram Packet to the
	 * specified {@code Client}.
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
	public void send(Client client, byte[] buffer) throws IOException {
		send(client.getIP(),client.getPort(),buffer);
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
	 * Adds a DatagramPacketListener that will be triggered when a
	 * Datagram packet is received.
	 * 
	 * @param listener 
	 */
	public void addDatagramPacketListener(DatagramPacketListener listener) {
		packetListeners.add(listener);
	}

	/**
	 * Starts listening for Datagram Packets from Client.
	 * 
	 * Once one is received, triggers all the listeners.
	 */
	private void listenForPackets() {
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(!isFinished) { // while connection is ongoing
					byte[] bufferReceived = new byte[maxReceivedByteLength];			
					DatagramPacket receivedPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
					
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
