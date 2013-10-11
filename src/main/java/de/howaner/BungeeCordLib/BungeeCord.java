package de.howaner.BungeeCordLib;

import de.howaner.BungeeCordLib.event.BungeeAddServerEvent;
import de.howaner.BungeeCordLib.event.BungeeRemoveServerEvent;
import de.howaner.BungeeCordLib.server.BungeePacketServer;
import de.howaner.BungeeCordLib.server.BungeeServer;
import de.howaner.BungeeCordLib.util.ServerConnectionThread;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BungeeCord {
	private BungeePlugin plugin;
	private Map<String, BungeeServer> servers = new HashMap<String, BungeeServer>();
	private Map<Integer, Thread> packetServers = new HashMap<Integer, Thread>();
	private List<String> bungeeServers = new ArrayList<String>();
	private String serverName;
	private Map<Player, String> playerIps = new HashMap<Player, String>();
	
	public BungeeCord(BungeePlugin plugin) {
		this.plugin = plugin;
	}
	
	public static BungeeCord getManager() {
		return BungeePlugin.getManager();
	}
	
	public void initialize() {
		if (Bukkit.getOnlinePlayers().length != 0) {
			this.receiveBungeeServers();
			this.receiveServerPlayers();
			this.receiveServerName();
		}
	}
	
	public String getPlayerIp(Player player) {
		return playerIps.get(player);
	}
	
	public void setPlayerIp(Player player, String ip) {
		this.playerIps.put(player, ip);
	}
	
	public boolean havePlayerIp(Player player) {
		return this.playerIps.containsKey(player);
	}
	
	public void removePlayerIp(Player player) {
		this.playerIps.remove(player);
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	public void setServerName(String server) {
		this.serverName = server;
	}
	
	public void receivePlayerIp(Player player) {
		if (player == null) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("IP");
			player.sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveServerName() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("GetServer");
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveServerPlayers() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		
		//TODO: Fix Bug (No Response from BungeeCord)
		for (String server : this.servers.keySet()) {
			BungeePlugin.log.info("Send: " + server);
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);

			try {
				out.writeUTF("PlayerList");
				out.writeUTF(server);
				Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
				out.close();
				b.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void receiveBungeeServers() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("GetServers");
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessageToPlayer(String player, String message) {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		message = ChatColor.translateAlternateColorCodes('&', message);
		try {
			out.writeUTF("Message");
			out.writeUTF(player);
			out.writeUTF(message);
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getBungeeServers() {
		if (this.bungeeServers.isEmpty()) this.receiveBungeeServers();
		return this.bungeeServers;
	}
	
	public void setBungeeServers(List<String> servers) {
		this.bungeeServers = servers;
	}
	
	public List<Integer> getPacketServers() {
		List<Integer> serverList = new ArrayList<Integer>();
		serverList.addAll(this.packetServers.keySet());
		return serverList;
	}
	
	public boolean containsPacketServer(int id) {
		return this.packetServers.containsKey(id);
	}
	
	public void removePacketServer(int id) {
		Thread thread = this.packetServers.get(id);
		if (thread == null) return;
		thread.stop();
		this.packetServers.remove(id);
	}
	
	public int addPacketServer(final int port, final BungeePacketServer server) {
		int id = 0;
		while (this.packetServers.containsKey(id))
			id += 1;
		
		Thread thread = new ServerConnectionThread(port, server);
		this.packetServers.put(id, thread);
		thread.start();
		return id;
	}
	
	public BungeeServer addServer(String name, String ip) {
		if (this.servers.containsKey(name)) return this.servers.get(name);
		BungeeServer server = new BungeeServer(name, ip);
		this.servers.put(name, server);
		Bukkit.getPluginManager().callEvent(new BungeeAddServerEvent(name, ip));
		return server;
	}
	
	public BungeeServer getServer(String name) {
		return this.servers.get(name);
	}
	
	public List<BungeeServer> getServers() {
		List<BungeeServer> serverList = new ArrayList<BungeeServer>();
		serverList.addAll(this.servers.values());
		return serverList;
	}
	
	public boolean existsServer(String name) {
		return this.servers.containsKey(name);
	}
	
	public void removeServer(String name) {
		if (!this.existsServer(name)) return;
		Bukkit.getPluginManager().callEvent(new BungeeRemoveServerEvent(this.servers.get(name)));
		this.servers.remove(name);
	}
	
}
