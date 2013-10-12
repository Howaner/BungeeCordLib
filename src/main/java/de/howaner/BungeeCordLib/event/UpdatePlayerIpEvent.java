package de.howaner.BungeeCordLib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This was call, when BungeeCord send the right Player Ip
 */
public class UpdatePlayerIpEvent extends Event {
	private String player;
	private String ip;
	private int port;

	public UpdatePlayerIpEvent(String player, String ip, int port) {
		this.player = player;
		this.ip = ip;
		this.port = port;
	}
	
	public String getPlayer() {
		return this.player;
	}
	
	public String getAddress() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}
	
	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}
	
}
