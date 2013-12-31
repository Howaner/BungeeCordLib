package de.howaner.BungeeCordLib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This was call, when BungeeCord send me a new List with the Online Players
 */
public class UpdateOnlinePlayersEvent extends Event {
	private String[] players;
	private static HandlerList handlers = new HandlerList();
	
	public UpdateOnlinePlayersEvent(String[] players) {
		this.players = players;
	}
	
	public String[] getPlayers() {
		return this.players;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
