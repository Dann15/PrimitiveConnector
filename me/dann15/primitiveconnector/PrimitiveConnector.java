package me.dann15.primitiveconnector;

public class PrimitiveConnector {

	public static void preferSystemIPV4Stack(boolean preferIPV4) {
		System.setProperty("java.net.preferIPv4Stack", String.valueOf(preferIPV4));
	}
	
	
}
