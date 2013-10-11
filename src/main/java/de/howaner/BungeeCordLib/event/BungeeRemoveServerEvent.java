package de.howaner.BungeeCordLib.event;

import de.howaner.BungeeCordLib.server.BungeeServer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeeRemoveServerEvent extends Event {
	private BungeeServer server;
	
	public BungeeRemoveServerEvent(BungeeServer server) {
		this.server = server;
	}
	
	public BungeeServer getServer() {
		return this.server;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}
	
}
