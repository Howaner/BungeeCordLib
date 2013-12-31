package de.howaner.BungeeCordLib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Thats call, when BungeeCord send the right Player UUID
 */
public class UpdatePlayerUUIDEvent extends Event {
	private String player;
	private String uuid;
	private static HandlerList handlers = new HandlerList();

	public UpdatePlayerUUIDEvent(String player, String uuid) {
		this.player = player;
		this.uuid = uuid;
	}
	
	public String getPlayer() {
		return this.player;
	}
	
	public String getUUID() {
		return this.uuid;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
