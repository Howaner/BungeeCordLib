package de.howaner.BungeeCordLib.server;

import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.BungeePlugin;
import de.howaner.BungeeCordLib.util.MinecraftPingProtocol;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import org.bukkit.entity.Player;

public class BungeeServer {
	private final String name;
	private String ip;
	
	public BungeeServer(final String name, String ip) {
		this.name = name;
		this.ip = ip;
	}
	
	/**
	 * Get the Servername
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the Server IP
	 */
	public String getIp() {
		return this.ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	/**
	 * This makes a Ping to the Server
	 * 
	 * @return The Motd, Slots and Online Players
	 */
	public ServerData getData() {
		try {
			MinecraftPingProtocol packet = new MinecraftPingProtocol(this.ip);
			Object[] values = packet.read();
			ServerData data = new ServerData(this, (Integer)values[2], (Integer)values[1], (String)values[0]);
			return data;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Teleport a Player to this Server
	 * 
	 * @param player The Player
	 */
	public void teleportPlayer(Player player) throws Exception {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		out.writeUTF("Connect");
		out.writeUTF(this.name);
		player.sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
		out.close();
		b.close();
	}
	
	/**
	 * Send a Packet to this Servers
	 * 
	 * @param serverPort The Port of this Server
	 * @param packet The Packet
	 */
	public void sendPacket(final int serverPort, final BungeePacket packet) throws Exception {
		if (BungeeCord.getManager().getServerName() == null || BungeeCord.getManager().getServerName().isEmpty())
			throw new Exception("This server has no name!");
		final String address = (this.ip.contains(":")) ? this.ip.split(":")[0] : this.ip;
		
		new Thread() {
			@Override
			public void run() {
				try {
					//Connect
					Socket client = new Socket(address, serverPort);
					client.setSoTimeout(3000);
					PrintStream out = new PrintStream(client.getOutputStream());
					
					//Send Message
					StringBuilder builder = new StringBuilder();
					builder.append(BungeeCord.getManager().getServerName());
					builder.append('\n');
					builder.append(packet.getTitle());
					builder.append('\n');
					builder.append(packet.write());
					builder.append('\n');
					out.print(builder.toString());
					out.flush();
					
					//Receive Messages
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					packet.read(in.readLine());
					
					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}
