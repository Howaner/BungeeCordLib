package de.howaner.BungeeCordLib.server;

public interface BungeePacket {
	public String getTitel();
	public String[] write();
	public void read(String[] response);
	
}
