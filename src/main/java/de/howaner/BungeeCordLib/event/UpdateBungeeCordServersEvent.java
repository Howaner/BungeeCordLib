package de.howaner.BungeeCordLib.event;

import java.util.List;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 * This was call, when BungeeCord send me a List with all BungeeCord Servers
 */
public class UpdateBungeeCordServersEvent extends Event {
	private List<String> servers;
	
	public UpdateBungeeCordServersEvent(List<String> servers) {
		this.servers = servers;
	}
	
	public List<String> getServers() {
		return this.servers;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}
	
}
