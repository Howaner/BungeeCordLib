package de.howaner.BungeeCordLib.listener;

import de.howaner.BungeeCordLib.BungeeCord;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		BungeeCord.getManager().receiveBungeeServers();
		BungeeCord.getManager().receiveServerPlayers();
	}
	
}
