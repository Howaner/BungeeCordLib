package de.howaner.BungeeCordLib;

import de.howaner.BungeeCordLib.event.BungeeAddServerEvent;
import de.howaner.BungeeCordLib.event.BungeeRemoveServerEvent;
import de.howaner.BungeeCordLib.server.BungeeServer;
import de.howaner.BungeeCordLib.util.PacketListener;
import de.howaner.BungeeCordLib.util.ServerConnectionThread;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BungeeCord {
	private Map<String, BungeeServer> servers = new HashMap<String, BungeeServer>();
	private String serverName;
	private String[] onlinePlayers = new String[0];
	private Map<String, String> playerIps = new HashMap<String, String>();
	private Map<String, String> playerUUID = new HashMap<String, String>();
	private ServerConnectionThread packetServer;
	private Map<PacketListener, String> listeners = new HashMap<PacketListener, String>();
	
	/**
	 * Get the BungeeCord instance
	 */
	public static BungeeCord getManager() {
		return BungeePlugin.getManager();
	}
	
	/**
	 * Start the Packet Server
	 */
	public void startPacketServer() {
		if (this.packetServer != null || this.serverName == null || this.serverName.isEmpty()) return;
		try {
			InetAddress address = InetAddress.getByName(((Bukkit.getIp().isEmpty()) ? "0.0.0.0" : Bukkit.getIp()));
			int port = this.generatePacketPort(this.serverName);
			
			ServerSocket socket = new ServerSocket(port, 50, address);
			this.packetServer = new ServerConnectionThread(socket);
			this.packetServer.start();
			
			BungeePlugin.log.info("Packet Server started on " + address.getHostAddress() + ":" + port + "!");
		} catch (Exception e) {
			BungeePlugin.log.warning("Error while starting the Packet Server: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Stop the Packet Server
	 */
	public void stopPacketServer() {
		if (this.packetServer == null) return;
		try {
			this.packetServer.interrupt();
			this.packetServer.serverSocket.close();
			this.packetServer = null;
			BungeePlugin.log.info("Packet Server stopped!");
		} catch (Exception e) {
			BungeePlugin.log.warning("Error while stopping the Packet Server: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void initialize() {
		if (Bukkit.getOnlinePlayers().length != 0) {
			this.requestBungeeServers();
			this.requestOnlinePlayers();
			this.requestServerName();
			for (Player player : Bukkit.getOnlinePlayers()) {
				this.requestPlayerIp(player);
				this.requestPlayerUUID(player);
			}
		}
	}
	
	/**
	 * Get Online Players in all BungeeCord Servers
	 * 
	 * @return Online Players in a String Array
	 */
	public String[] getOnlinePlayers() {
		return this.onlinePlayers;
	}
	
	/**
	 * Set Online Players (its for my Plugin, not for your!)
	 * 
	 * @param players The Array with the Players
	 */
	public void setOnlinePlayers(String[] players) {
		this.onlinePlayers = players;
	}
	
	/**
	 * Return the right Player Ip.
	 * 
	 * @return IP:Port
	 */
	public String getPlayerIp(String player) {
		return this.playerIps.get(player.toLowerCase());
	}
	
	/**
	 * Set the right Player Ip (its for my Plugin, not for your!)
	 * 
	 * @param player The Playername
	 * @param ip The Player Ip with Address and Port
	 */
	public void setPlayerIp(String player, String ip) {
		this.playerIps.put(player.toLowerCase(), ip);
	}
	
	/**
	 * Return the right Player UUID. 
	 * 
	 * @return UUID of the Player
	 */
	public String getPlayerUUID(String player) {
		return this.playerUUID.get(player.toLowerCase());
	}
	
	/**
	 * Set the right Player UUID (its for my Plugin, not for your!)
	 * 
	 * @param player The Playername
	 * @param uuid The Player UUID
	 */
	public void setPlayerUUID(String player, String uuid) {
		this.playerUUID.put(player.toLowerCase(), uuid);
	}
	
	/**
	 * Is the Player in the List?
	 * 
	 * @param player The Playername
	 */
	public boolean havePlayerIp(String player) {
		return this.playerIps.containsKey(player.toLowerCase());
	}
	
	/**
	 * Remove the player in the IP List
	 * 
	 * @param player The Playername
	 */
	public void removePlayerIp(String player) {
		this.playerIps.remove(player.toLowerCase());
	}
	
	/**
	 * Get the Name of this Server (in BungeeCord)
	 * Warning! To fill this automatic, Players must join this Server!
	 * 
	 * @return The Servername
	 */
	public String getServerName() {
		return this.serverName;
	}
	
	/**
	 * Set the Servername (Required for Packetserver!)
	 * 
	 * @param server The Servername
	 */
	public void setServerName(String server) {
		this.serverName = server;
		//Restart Packet Server with new Port
		this.stopPacketServer();
		this.startPacketServer();
	}
	
	/**
	 * Send a request to update the Player-Ip from this Player
	 * 
	 * @param player The Player Object
	 */
	public void requestPlayerIp(Player player) {
		if (player == null) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("IP");
			out.flush();
			b.flush();
			
			player.sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a request to update the Player UUID
	 * 
	 * @param player The Player Object
	 */
	public void requestPlayerUUID(Player player) {
		if (player == null) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("UUID");
			out.flush();
			b.flush();
			
			player.sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a request to update the Servername
	 * Warning! Online Players required
	 * 
	 */
	public void requestServerName() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("GetServer");
			out.flush();
			b.flush();
			
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a request to update the Online Players
	 * Warning! Online Players required
	 */
	public void requestOnlinePlayers() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("PlayerList");
			out.writeUTF("ALL");
			out.flush();
			b.flush();
			
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a request to Update the bungeeServers List.
	 * Warning! Online Players required!
	 */
	public void requestBungeeServers() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("GetServers");
			out.flush();
			b.flush();
			
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a specific Player to the Server.
	 * This work for all Players on all Servers!
	 * Warning! Require a Online Player!
	 * 
	 * @param player Playername
	 * @param server Servername
	 */
	public void sendPlayerToServer(String player, String server) {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("ConnectOther");
			out.writeUTF(player);
			out.writeUTF(server);
			out.flush();
			b.flush();
			
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a message to this player
	 * Warning! Online Players required!
	 */
	public void sendMessageToPlayer(String player, String message) {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		message = ChatColor.translateAlternateColorCodes('&', message);
		try {
			out.writeUTF("Message");
			out.writeUTF(player);
			out.writeUTF(message);
			out.flush();
			b.flush();
			
			Bukkit.getOnlinePlayers()[0].sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a BungeeServer
	 * 
	 * @param name The Servername in the BungeeCord config.
	 * @param ip The Ip from this Server
	 */
	public BungeeServer addServer(String name) {
		if (this.servers.containsKey(name.toLowerCase())) return this.servers.get(name.toLowerCase());
		BungeeServer server = new BungeeServer(name);
		this.servers.put(name.toLowerCase(), server);
		Bukkit.getPluginManager().callEvent(new BungeeAddServerEvent(name));
		return server;
	}
	
	/**
	 * Get the Server instance
	 * 
	 * @ærgs name The Servername
	 * @return The Server object
	 */
	public BungeeServer getServer(String name) {
		return this.servers.get(name.toLowerCase());
	}
	
	/**
	 * Return all registed BungeeServer
	 */
	public List<BungeeServer> getServers() {
		List<BungeeServer> serverList = new ArrayList<BungeeServer>();
		serverList.addAll(this.servers.values());
		return serverList;
	}
	
	/**
	 * Is this Server registed?
	 * 
	 * @param name The Servername
	 */
	public boolean existsServer(String name) {
		return this.servers.containsKey(name.toLowerCase());
	}
	
	/**
	 * Remove a Registed Server
	 * 
	 * @param name The Servername
	 */
	public void removeServer(String name) {
		if (!this.existsServer(name)) return;
		Bukkit.getPluginManager().callEvent(new BungeeRemoveServerEvent(this.servers.get(name.toLowerCase())));
		this.servers.remove(name.toLowerCase());
	}
	
	/**
	 * Generate the Port for a PacketServer
	 * @param name The Servername
	 */
	public int generatePacketPort(String name) {
		int port = 1024;
		for (byte b : name.toLowerCase().getBytes(Charset.forName("UTF-8")))
			port += b;
		return port;
	}
	
	/**
	 * Remove all Listeners from a Channel
	 * 
	 * @param channel The Channel
	 */
	public void removeListenersFromChannel(String channel) {
		for (PacketListener listener : this.getListenersFromChannel(channel))
			this.removeListener(listener);
	}
	
	/**
	 * Remove a Listener
	 * 
	 * @param listener The Listener to remove
	 */
	public void removeListener(PacketListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Get all Listeners from a Channel
	 * 
	 * @param channel The Channel
	 */
	public List<PacketListener> getListenersFromChannel(String channel) {
		List<PacketListener> list = new ArrayList<PacketListener>();
		for (Entry<PacketListener, String> e : this.listeners.entrySet())
			if (e.getValue().equalsIgnoreCase(channel))
				list.add(e.getKey());
		return list;
	}
	
	/**
	 * Register a Listener
	 * 
	 * @param channel The Channel what do you will receive
	 * @param listener The Listener
	 */
	public void registerListener(String channel, PacketListener listener) {
		if (this.listeners.containsKey(listener)) return;
		this.listeners.put(listener, channel);
	}
	
	/**
	 * Call the Listener
	 */
	public void callListener(String channel, String server, String message) {
		for (PacketListener l : this.getListenersFromChannel(channel))
			l.receive(server, message);
	}
	
}
