package me.dann15.primitiveconnector.examples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import me.dann15.primitiveconnector.PrimitiveConnector;
import me.dann15.primitiveconnector.listener.DatagramPacketListener;
import me.dann15.primitiveconnector.multicastconnection.MulticastConnector;

public class MulticastConnectorExample {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		PrimitiveConnector.preferSystemIPV4Stack(true);
		
		MulticastConnector socket = new MulticastConnector(InetAddress.getByName("239.0.0.1"), 1234, 1000);
		DatagramPacketListener listener = new DatagramPacketListener() {
			
			@Override
			public void recievedDatagramPacket(DatagramPacket packet, byte[] buffer) {
				try {
					String receivedString = new String(buffer,0,packet.getLength(),"UTF-8");
					
					System.out.println("Received: " + receivedString);
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		};
		socket.addDatagramPacketListener(listener); 
		
		socket.join();
		
		System.out.println("Joined host!");
		
		socket.send("Bob says Hey!".getBytes());
		// note that when you send something, it will be sent right back to you
		// since the message is sent to everyone
			
		socket.exit();
		socket.close();
		
		System.out.println("Socket closed.");
	}
	
}
