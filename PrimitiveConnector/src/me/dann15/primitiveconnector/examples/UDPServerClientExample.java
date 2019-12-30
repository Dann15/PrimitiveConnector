package me.dann15.primitiveconnector.examples;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import me.dann15.primitiveconnector.datatype.Client;
import me.dann15.primitiveconnector.listener.DatagramPacketListener;
import me.dann15.primitiveconnector.udpserverclient.UDPServer;

public class UDPServerClientExample {

	public static void main(String[] args) throws SocketException, InterruptedException {
		
		UDPServer server = new UDPServer(1234, 1000);
		
		server.addDatagramPacketListener(new DatagramPacketListener() {
			
			@Override
			public void recievedDatagramPacket(DatagramPacket packet) {
				Client client = Client.extractClient(packet);
				
				try {
					
					String receivedString = new String(packet.getData(),0,packet.getLength(),"UTF-8");
					
					System.out.println("Received: " + receivedString);
					
					server.send(client,"Thanks for saying hi!".getBytes());
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}
		});
		
		
		Thread.sleep(3000);

		
		server.stop();
		server.close();
		
		
	}
	
}
