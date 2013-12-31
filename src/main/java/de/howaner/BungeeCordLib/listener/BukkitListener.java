package de.howaner.BungeeCordLib.listener;

import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.BungeePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitListener implements Listener {
	
	@EventHandler (priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(BungeePlugin.instance, new Runnable() {
			@Override
			public void run() {
				if (!player.isOnline()) return;
				if (BungeeCord.getManager().getServerName() == null || BungeeCord.getManager().getServerName().isEmpty())
					BungeeCord.getManager().requestServerName();
				BungeeCord.getManager().requestBungeeServers();
				BungeeCord.getManager().requestOnlinePlayers();
				BungeeCord.getManager().requestPlayerIp(player);
				BungeeCord.getManager().requestPlayerUUID(player);
			}
		}, 30L);
	}
	
}
