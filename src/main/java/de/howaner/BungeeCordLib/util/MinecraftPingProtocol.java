package de.howaner.BungeeCordLib.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class MinecraftPingProtocol {
	private String ip;
	private int port = 25565;
	
	public String getAddress() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public MinecraftPingProtocol(String ip) {
		this.ip = ip;
		if (ip.contains(":")) {
			this.port = Integer.parseInt(ip.split(":")[1]);
			this.ip = ip.split(":")[0];
		}
	}
	
	public Object[] read() throws Exception {
		Socket socket = new Socket();
		socket.setSoTimeout(100);
		socket.connect(new InetSocketAddress(this.getAddress(), this.getPort()), 100);
		OutputStream outputStream = socket.getOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		InputStream inputStream = socket.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream,Charset.forName("UTF-16BE"));
		dataOutputStream.write(new byte[]{(byte) 0xFE,(byte) 0x01});
		int packetId = inputStream.read();
		if (packetId != 0xFF) {
			dataOutputStream.close();
			outputStream.close();
			inputStreamReader.close();
			inputStream.close();
			socket.close();
			throw new IOException((packetId == -1) ? "No Connection" : "Invalid packet ID (" + packetId + ")");
		}
		int length = inputStreamReader.read();
		if (length == -1 || length == 0) {
			dataOutputStream.close();
			outputStream.close();
			inputStreamReader.close();
			inputStream.close();
			socket.close();
			throw new IOException((length == -1) ? "No Connection" : "Invalid string length");
		}
		char[] chars = new char[length];
		if (inputStreamReader.read(chars, 0, length) != length) {
			dataOutputStream.close();
			outputStream.close();
			inputStreamReader.close();
			inputStream.close();
			socket.close();
			throw new IOException("Invalid string length");
		}
		
		String string = new String(chars);
		String motd;
		int playerCount;
		int slots;
		
		if (string.startsWith("§")) {
			String[] data = string.split("\0");
			motd = data[3];
			playerCount = Integer.parseInt(data[4]);
			slots = Integer.parseInt(data[5]);
		} else {
			String[] data = string.split("§");
			motd = data[0];
			playerCount = Integer.parseInt(data[1]);
			slots = Integer.parseInt(data[2]);
		}
		dataOutputStream.close();
		outputStream.close();
		inputStreamReader.close();
		inputStream.close();
		socket.close();
		
		return new Object[] { motd, playerCount, slots };
	}
	
}
