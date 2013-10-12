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
	
	/**
	 * Get the Maximum Players
	 */
	public int getSlots() {
		return this.slots;
	}
	
	/**
	 * Get the Online Players of the Server
	 */
	public int getPlayers() {
		return this.players;
	}
	
	/**
	 * Get the Message of the Day
	 */
	public String getMotd() {
		return this.motd;
	}
	
}
