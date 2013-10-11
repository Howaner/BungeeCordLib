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
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class BungeeServer {
	private final String name;
	private String ip;
	private List<String> players;
	
	public BungeeServer(final String name, String ip) {
		this.name = name;
		this.ip = ip;
	}
	
	public List<String> getPlayers() {
		if (this.players.isEmpty()) BungeeCord.getManager().receiveServerPlayers();
		return this.players;
	}
	
	public void setPlayers(List<String> players) {
		this.players = players;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
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
	
	public void teleportPlayer(Player player) throws Exception {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		out.writeUTF("Connect");
		out.writeUTF(this.name);
		player.sendPluginMessage(BungeePlugin.instance, "BungeeCord", b.toByteArray());
		out.close();
		b.close();
	}
	
	public void sendPacket(String absender, int serverPort, BungeePacket packet) throws Exception {
		String ip = this.ip;
		if (ip.contains(":"))
			ip = ip.split(":")[0];
		
		//Connect
		Socket client = new Socket(ip, serverPort);
		PrintStream out = new PrintStream(client.getOutputStream());
		
		//Send Message
		StringBuilder requestBuilder = new StringBuilder();
		requestBuilder.append(packet.getTitel());
		requestBuilder.append('\n');
		for (String s : packet.write()) {
			requestBuilder.append(s);
			requestBuilder.append('\n');
		}
		out.print(requestBuilder.toString());
		out.flush();
		
		//Receive Messages
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		String line;
		List<String> response = new ArrayList<String>();
		while ((line=in.readLine()) != null)
			response.add(line);
		packet.read(response.toArray(new String[response.size()]));
		
		in.close();
		out.close();
	}
	
}
