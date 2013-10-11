package de.howaner.BungeeCordLib.listener;

import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.BungeePlugin;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String title, Player player, byte[] message) {
		if (!title.equals("BungeeCord")) return;
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		try {
			String channel = in.readUTF();
			if (channel.equals("GetServers")) {
				String[] serverArray = in.readUTF().split(", ");
				List<String> servers = new ArrayList<String>();
				for (String s : serverArray)
					servers.add(s);
				BungeeCord.getManager().setBungeeServers(servers);
			} else if (channel.equals("PlayerList")) {
				String server = in.readUTF();
				if (!BungeeCord.getManager().existsServer(server)) return;
				String[] playerArray = in.readUTF().split(", ");
				List<String> players = new ArrayList<String>();
				for (String s : playerArray)
					players.add(s);
				BungeeCord.getManager().getServer(server).setPlayers(players);
			} else if (channel.equals("GetServer")) {
				String name = in.readUTF();
				BungeeCord.getManager().setServerName(name);
			} else if (channel.equals("IP")) {
				String address = in.readUTF() + ":" + in.readInt();
				BungeeCord.getManager().setPlayerIp(player, address);
			} else {
				BungeePlugin.log.info("Undefined BungeeCord Channel: " + channel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
