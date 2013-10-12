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
	private Map<String, BungeeServer> servers = new HashMap<String, BungeeServer>();
	private Map<Integer, Thread> packetServers = new HashMap<Integer, Thread>();
	private List<String> bungeeServers = new ArrayList<String>();
	private String serverName;
	private String[] onlinePlayers = new String[0];
	private Map<String, String> playerIps = new HashMap<String, String>();
	
	public static BungeeCord getManager() {
		return BungeePlugin.getManager();
	}
	
	public void initialize() {
		if (Bukkit.getOnlinePlayers().length != 0) {
			this.receiveBungeeServers();
			this.receiveOnlinePlayers();
			this.receiveServerName();
			for (Player player : Bukkit.getOnlinePlayers())
				this.receivePlayerIp(player);
		}
	}
	
	public String[] getOnlinePlayers() {
		return this.onlinePlayers;
	}
	
	public void setOnlinePlayers(String[] players) {
		this.onlinePlayers = players;
	}
	
	public String getPlayerIp(String player) {
		return playerIps.get(player.toLowerCase());
	}
	
	public void setPlayerIp(String player, String ip) {
		this.playerIps.put(player.toLowerCase(), ip);
	}
	
	public boolean havePlayerIp(String player) {
		return this.playerIps.containsKey(player.toLowerCase());
	}
	
	public void removePlayerIp(String player) {
		this.playerIps.remove(player.toLowerCase());
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
		
		BungeePlugin.log.info("Player Ip from " + player.getName() + " requested!");
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
		
		BungeePlugin.log.info("ServerName requested!");
		try {
			out.writeUTF("GetServer");
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveOnlinePlayers() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		BungeePlugin.log.info("Online Players Requested!");
		try {
			out.writeUTF("PlayerList");
			out.writeUTF("ALL");
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveBungeeServers() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		BungeePlugin.log.info("Bungee Servers requested!");
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
		
		BungeePlugin.log.info("Send Message to Player " + player + " requested!");
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
