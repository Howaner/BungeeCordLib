package de.howaner.BungeeCordLib.util;

public interface PacketListener {
	
	/**
	 * Packet Receive
	 * @param server The Server who send the Message
	 * @param message The Message who send the Server
	 */
	public void receive(String server, String message);
	
}
