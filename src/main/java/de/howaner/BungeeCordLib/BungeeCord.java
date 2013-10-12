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
	
	/*
	 * Get the BungeeCord instance
	 */
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
	
	/*
	 * Get Online Players in all BungeeCord Servers
	 * 
	 * @return Online Players in a String Array
	 */
	public String[] getOnlinePlayers() {
		return this.onlinePlayers;
	}
	
	/*
	 * Set Online Players (its for my Plugin, not for your!)
	 * 
	 * @param players The Array with the Players
	 */
	public void setOnlinePlayers(String[] players) {
		this.onlinePlayers = players;
	}
	
	/*
	 * Return the right Player Ip. (When you use this in PlayerJoinEvent, please make a Scheduler with 5 ticks)
	 * 
	 * @return IP:Port
	 */
	public String getPlayerIp(String player) {
		return playerIps.get(player.toLowerCase());
	}
	
	/*
	 * Set the right Player Ip (its for my Plugin, not for your!)
	 * 
	 * @praram player The Playername
	 * @param ip The Player Ip with Address and Port
	 */
	public void setPlayerIp(String player, String ip) {
		this.playerIps.put(player.toLowerCase(), ip);
	}
	
	/*
	 * Is the Player in the List?
	 * 
	 * @param player The Playername
	 */
	public boolean havePlayerIp(String player) {
		return this.playerIps.containsKey(player.toLowerCase());
	}
	
	/*
	 * Remove the player in the IP List
	 * 
	 * @param player The Playername
	 */
	public void removePlayerIp(String player) {
		this.playerIps.remove(player.toLowerCase());
	}
	
	/*
	 * Get the Name of this Server (in BungeeCord)
	 * Warning! To fill this, Players must join this Server!
	 * 
	 * @return The Servername
	 */
	public String getServerName() {
		return this.serverName;
	}
	
	/*
	 * Set the Servername (its for my Plugin)
	 * 
	 * @param server The Servername
	 */
	public void setServerName(String server) {
		this.serverName = server;
	}
	
	/*
	 * Send a request to update the Player-Ip from this Player
	 * 
	 * @param player The Player Object
	 */
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
	
	/*
	 * Send a request to update the Servername
	 * Warning! Online Players required
	 * 
	 */
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
	
	/*
	 * Send a request to update the Online Players
	 * Warning! Online Players required
	 */
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
	
	/*
	 * Send a request to Update the bungeeServers List.
	 * Warning! Online Players required!
	 */
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
	
	/*
	 * Send a message to this player
	 * Warning! Online Players required!
	 */
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
	
	/*
	 * Return all Servers in the BungeeCord config.yml
	 * Warning! This fill only, when a Player join this Server!
	 * 
	 * @return All Servers
	 */
	public List<String> getBungeeServers() {
		if (this.bungeeServers.isEmpty()) this.receiveBungeeServers();
		return this.bungeeServers;
	}
	
	/*
	 * Set the bungeeServers List. (its for my Plugin)
	 * 
	 * @param servers The Server List
	 */
	public void setBungeeServers(List<String> servers) {
		this.bungeeServers = servers;
	}
	
	/*
	 * List all Packet Servers
	 * 
	 * @return The Ids from all Packet Servers
	 */
	public List<Integer> getPacketServers() {
		List<Integer> serverList = new ArrayList<Integer>();
		serverList.addAll(this.packetServers.keySet());
		return serverList;
	}
	
	/*
	 * Exists a Packet Server with this id?
	 * 
	 * @return true or false
	 */
	public boolean containsPacketServer(int id) {
		return this.packetServers.containsKey(id);
	}
	
	/*
	 * Remove the Packet Server with this id.
	 * 
	 * @args id The Id
	 */
	public void removePacketServer(int id) {
		Thread thread = this.packetServers.get(id);
		if (thread == null) return;
		thread.stop();
		this.packetServers.remove(id);
	}
	
	/*
	 * Create a new Packet Server
	 * 
	 * @param port A Port for this Server
	 * @param server The Listener for this PacketServer
	 */
	public int addPacketServer(final int port, final BungeePacketServer server) {
		int id = 0;
		while (this.packetServers.containsKey(id))
			id += 1;
		
		Thread thread = new ServerConnectionThread(port, server);
		this.packetServers.put(id, thread);
		thread.start();
		return id;
	}
	
	/*
	 * Add a BungeeServer
	 * 
	 * @param name The Servername in the BungeeCord config.
	 * @param ip The Ip from this Server
	 */
	public BungeeServer addServer(String name, String ip) {
		if (this.servers.containsKey(name)) return this.servers.get(name);
		BungeeServer server = new BungeeServer(name, ip);
		this.servers.put(name, server);
		Bukkit.getPluginManager().callEvent(new BungeeAddServerEvent(name, ip));
		return server;
	}
	
	/*
	 * Get the Server instance
	 * 
	 * @ærgs name The Servername
	 * @return The Server object
	 */
	public BungeeServer getServer(String name) {
		return this.servers.get(name);
	}
	
	/*
	 * Return all registed BungeeServer
	 */
	public List<BungeeServer> getServers() {
		List<BungeeServer> serverList = new ArrayList<BungeeServer>();
		serverList.addAll(this.servers.values());
		return serverList;
	}
	
	/*
	 * Is this Server registed?
	 * 
	 * @param name The Servername
	 */
	public boolean existsServer(String name) {
		return this.servers.containsKey(name);
	}
	
	/*
	 * Remove a Registed Server
	 * 
	 * @param name The Servername
	 */
	public void removeServer(String name) {
		if (!this.existsServer(name)) return;
		Bukkit.getPluginManager().callEvent(new BungeeRemoveServerEvent(this.servers.get(name)));
		this.servers.remove(name);
	}
	
}
