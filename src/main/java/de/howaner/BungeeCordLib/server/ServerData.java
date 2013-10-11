package de.howaner.BungeeCordLib.server;

public class ServerData {
	private BungeeServer server;
	private int slots;
	private int players;
	private String motd;
	
	public ServerData(BungeeServer server, int slots, int players, String motd) {
		this.server = server;
		this.slots = slots;
		this.players = players;
		this.motd = motd;
	}
	
	public BungeeServer getServer() {
		return this.server;
	}
	
	public int getSlots() {
		return this.slots;
	}
	
	public int getPlayers() {
		return this.players;
	}
	
	public String getMotd() {
		return this.motd;
	}
	
}
