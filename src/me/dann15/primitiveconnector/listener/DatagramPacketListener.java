package me.dann15.primitiveconnector.listener;

import java.net.DatagramPacket;

public interface DatagramPacketListener {

	void recievedDatagramPacket(DatagramPacket packet, byte[] buffer);
	
}
