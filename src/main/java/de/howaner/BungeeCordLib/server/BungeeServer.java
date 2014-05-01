package de.howaner.BungeeCordLib.server;

import com.google.common.base.Charsets;
import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.BungeePlugin;
import de.howaner.BungeeCordLib.util.MinecraftPingProtocol;
import de.howaner.BungeeCordLib.util.StatusResponse;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import org.bukkit.entity.Player;

public class BungeeServer {
	private final String name;
	private String ip;
	
	public BungeeServer(final String name) {
		this.name = name;
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
	 * This ping the Server and get out the informations (etc. players, motd, slots, ...)
	 * 
	 * @return StatusResponse
	 */
	public StatusResponse getData() {
		if (this.ip == null || this.ip.isEmpty()) return null; //No Server IP
		try {
			MinecraftPingProtocol packet = new MinecraftPingProtocol(this.ip);
			return packet.read();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Teleport a Player to this Server
	 * 
	 * @param player The Player
	 */
	public void teleport(Player player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("Connect");
			out.writeUTF(this.name);
			player.sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
			out.close();
			b.close();
		} catch (Exception e) {
			player.kickPlayer("BungeeCord Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a Packet to this Servers
	 * 
	 * @param serverPort The Port of this Server
	 * @param packet The Packet
	 */
	public void sendPacket(final String channel, final String message) {
		if (BungeeCord.getManager().getServerName() == null || BungeeCord.getManager().getServerName().isEmpty()) {
			BungeePlugin.log.warning("Can't send Packet " + channel + ": The Server hasn't a Name!");
		}
		//final String address = (this.ip.contains(":")) ? this.ip.split(":")[0] : this.ip;
		String ip = (this.ip == null) ? "127.0.0.1" : this.ip;
		final String address = (ip.contains(":")) ? ip.split(":")[0] : ip;
		
		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Send Packet to " + address + ":" + BungeeCord.getManager().generatePacketPort(BungeeServer.this.getName()) + "!");
					//Connect
					Socket client = new Socket(address, BungeeCord.getManager().generatePacketPort(BungeeServer.this.getName()));
					client.setSoTimeout(3000);
					PrintStream out = new PrintStream(client.getOutputStream(), false, "UTF-8");
					
					//Send Message
					StringBuilder builder = new StringBuilder();
					builder.append(channel);
					builder.append('\n');
					builder.append(BungeeCord.getManager().getServerName());
					builder.append('\n');
					builder.append(message);
					builder.append('\n');
					out.print(builder.toString());
					
					out.flush();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}
