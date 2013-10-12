package de.howaner.BungeeCordLib.listener;

import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.BungeePlugin;
import de.howaner.BungeeCordLib.event.UpdateBungeeCordServersEvent;
import de.howaner.BungeeCordLib.event.UpdateOnlinePlayersEvent;
import de.howaner.BungeeCordLib.event.UpdatePlayerIpEvent;
import de.howaner.BungeeCordLib.event.UpdateServerNameEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
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
			BungeePlugin.log.info("Received BungeeCord Packet: " + channel);
			if (channel.equals("GetServers")) {
				String[] serverArray = in.readUTF().split(", ");
				List<String> servers = new ArrayList<String>();
				for (String s : serverArray)
					servers.add(s);
				BungeeCord.getManager().setBungeeServers(servers);
				UpdateBungeeCordServersEvent event = new UpdateBungeeCordServersEvent(servers);
				Bukkit.getPluginManager().callEvent(event);
			} else if (channel.equals("PlayerList")) {
				if (!in.readUTF().equalsIgnoreCase("ALL")) return;
				String[] players = in.readUTF().split(", ");
				BungeeCord.getManager().setOnlinePlayers(players);
				UpdateOnlinePlayersEvent event = new UpdateOnlinePlayersEvent(players);
				Bukkit.getPluginManager().callEvent(event);
			} else if (channel.equals("GetServer")) {
				String name = in.readUTF();
				BungeeCord.getManager().setServerName(name);
				UpdateServerNameEvent event = new UpdateServerNameEvent(name);
				Bukkit.getPluginManager().callEvent(event);
			} else if (channel.equals("IP")) {
				String address = in.readUTF();
				int port = in.readInt();
				BungeeCord.getManager().setPlayerIp(player.getName(), address + ":" + port);
				UpdatePlayerIpEvent event = new UpdatePlayerIpEvent(player.getName(), address, port);
				Bukkit.getPluginManager().callEvent(event);
			} else {
				BungeePlugin.log.info("Undefined BungeeCord Channel: " + channel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
