package de.howaner.BungeeCordLib.util;

import de.howaner.BungeeCordLib.server.BungeePacketServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionThread extends Thread {
	private int port;
	private BungeePacketServer listener;
	
	public ServerConnectionThread(int port, BungeePacketServer listener) {
		this.port = port;
		this.listener = listener;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			
			Socket socket;
			while ((socket = serverSocket.accept()) != null) {
				//Read
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String server = reader.readLine();
				String title = reader.readLine();
				String message = reader.readLine();
				
				PrintStream writer = new PrintStream(socket.getOutputStream());
				writer.print(listener.run(server, title, message) + '\n');
				writer.flush();
				
				reader.close();
				writer.close();
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
