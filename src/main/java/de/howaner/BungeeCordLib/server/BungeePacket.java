package de.howaner.BungeeCordLib.server;

public interface BungeePacket {
	public String getTitle();
	public String write();
	public void read(String message);
	
}
