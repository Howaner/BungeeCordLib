package de.howaner.BungeeCordLib;

import de.howaner.BungeeCordLib.event.BungeeAddServerEvent;
import de.howaner.BungeeCordLib.event.BungeeRemoveServerEvent;
import de.howaner.BungeeCordLib.server.BungeePacketServer;
import de.howaner.BungeeCordLib.server.BungeeServer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class BungeeCord {
	private BungeePlugin plugin;
	private Map<String, BungeeServer> servers = new HashMap<String, BungeeServer>();
	private List<Integer> packetServers = new ArrayList<Integer>();
	private List<String> bungeeServers = new ArrayList<String>();
	
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
		}
	}
	
	public void receiveServerPlayers() {
		if (Bukkit.getOnlinePlayers().length == 0) return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("PlayerCount");
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
	
	public boolean containsPacketServer(int id) {
		return this.packetServers.contains(id);
	}
	
	public void removePacketServer(int id) {
		this.packetServers.remove(id);
	}
	
	public int addPacketServer(final int port, final BungeePacketServer server) {
		int newestId = 0;
		for (int key : packetServers)
			if (key >= newestId) newestId += 1;
		
		final int id = newestId;
		this.packetServers.add(id);
		new Thread() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(port);
					BungeePlugin.log.info("Packet Server on Port " + port + " created!");
					
					while (BungeeCord.this.packetServers.contains(id)) {
						Socket socket = serverSocket.accept();
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String s = in.readLine();
						List<String> request = new ArrayList<String>();
						String line;
						while ((line = in.readLine()) != null) {
							request.add(line);
						}
						
						String[] response = server.run(s, request.toArray(new String[request.size()]));
						StringBuilder responseBuilder = new StringBuilder();
						for (String l : response)
							responseBuilder.append(l + '\n');
						
						PrintStream out = new PrintStream(socket.getOutputStream());
						out.print(responseBuilder.toString());
						out.flush();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
				}
			}
		}.start();
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
