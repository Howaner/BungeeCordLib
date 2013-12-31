package de.howaner.BungeeCordLib.event;

import java.util.List;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This was call, when BungeeCord send me a List with all BungeeCord Servers
 */
public class UpdateBungeeCordServersEvent extends Event {
	private String[] servers;
	private static HandlerList handlers = new HandlerList();
	
	public UpdateBungeeCordServersEvent(String[] servers) {
		this.servers = servers;
	}
	
	public String[] getServers() {
		return this.servers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
