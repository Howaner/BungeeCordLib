package de.howaner.BungeeCordLib.util;

import de.howaner.BungeeCordLib.BungeeCord;
import de.howaner.BungeeCordLib.BungeePlugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionThread extends Thread {
	public ServerSocket serverSocket;
	
	public ServerConnectionThread(ServerSocket socket) {
		this.serverSocket = socket;
	}
	
	@Override
	public void run() {
		try {
			Socket socket;
			while ((socket = serverSocket.accept()) != null) {
				try {
					//Read
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String channel = reader.readLine();
					String server = reader.readLine();
					String message = reader.readLine();
					
					BungeeCord.getManager().callListener(channel, server, message);
					/*PrintStream writer = new PrintStream(socket.getOutputStream());
					writer.print(listener.run(server, title, message) + '\n');
					writer.flush();
					writer.close();*/
					
					reader.close();
					socket.close();
				} catch (Exception e) {
					BungeePlugin.log.warning("Packet Read Error: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			BungeePlugin.log.warning(e.getMessage());
		}
	}
	
}
