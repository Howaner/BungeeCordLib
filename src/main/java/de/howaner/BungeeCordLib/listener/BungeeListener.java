package de.howaner.BungeeCordLib.listener;

import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.BungeePlugin;
import de.howaner.BungeeCordLib.event.UpdateBungeeCordServersEvent;
import de.howaner.BungeeCordLib.event.UpdateOnlinePlayersEvent;
import de.howaner.BungeeCordLib.event.UpdatePlayerIpEvent;
import de.howaner.BungeeCordLib.event.UpdatePlayerUUIDEvent;
import de.howaner.BungeeCordLib.event.UpdateServerNameEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String title, Player player, byte[] message) {
		if (!title.equals("BungeeCord")) return;
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		
		try {
			String channel = in.readUTF();
			
			if (channel.equalsIgnoreCase("GetServers")) {
				String[] servers = in.readUTF().split(", ");
				for (String server : servers)
					BungeeCord.getManager().addServer(server);
				
				UpdateBungeeCordServersEvent event = new UpdateBungeeCordServersEvent(servers);
				Bukkit.getPluginManager().callEvent(event);
			} else if (channel.equalsIgnoreCase("PlayerList")) {
				if (!in.readUTF().equalsIgnoreCase("ALL")) return;
				String[] players = in.readUTF().split(", ");
				BungeeCord.getManager().setOnlinePlayers(players);
				
				UpdateOnlinePlayersEvent event = new UpdateOnlinePlayersEvent(players);
				Bukkit.getPluginManager().callEvent(event);
			} else if (channel.equalsIgnoreCase("GetServer")) {
				String server = in.readUTF();
				BungeeCord.getManager().setServerName(server);
				
				UpdateServerNameEvent event = new UpdateServerNameEvent(server);
				Bukkit.getPluginManager().callEvent(event);
			} else if (channel.equalsIgnoreCase("IP")) {
				String address = in.readUTF();
				int port = in.readInt();
				BungeeCord.getManager().setPlayerIp(player.getName(), address + ":" + port);
				
				UpdatePlayerIpEvent event = new UpdatePlayerIpEvent(player.getName(), address, port);
				Bukkit.getPluginManager().callEvent(event);
			} else if (channel.equalsIgnoreCase("UUID")) {
				String uuid = in.readUTF();
				BungeeCord.getManager().setPlayerUUID(player.getName(), uuid);
				
				UpdatePlayerUUIDEvent event = new UpdatePlayerUUIDEvent(player.getName(), uuid);
				Bukkit.getPluginManager().callEvent(event);
			} else {
				//BungeePlugin.log.info("Undefined BungeeCord Channel: " + channel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
