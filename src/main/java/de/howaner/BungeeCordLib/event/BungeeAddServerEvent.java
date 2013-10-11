package de.howaner.BungeeCordLib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeeAddServerEvent extends Event {
	private String name;
	private String ip;
	
	public BungeeAddServerEvent(String name, String ip) {
		this.name = name;
		this.ip = ip;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getIp() {
		return this.ip;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}
	
}
