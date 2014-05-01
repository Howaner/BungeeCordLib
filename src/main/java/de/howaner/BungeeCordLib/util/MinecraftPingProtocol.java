package de.howaner.BungeeCordLib.util;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MinecraftPingProtocol {
	private String ip;
	private int port = 25565;
	private Gson gson = new Gson();
	
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
	
	public StatusResponse read() throws IOException {
		Socket socket = new Socket();
		socket.setSoTimeout(2000);
		socket.connect(new InetSocketAddress(this.getAddress(), this.getPort()), 2000);
		
		OutputStream outputStream = socket.getOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		
		InputStream inputStream = socket.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream handshake = new DataOutputStream(b);
		handshake.writeByte(0x00); //Handshake packet id
		writeVarInt(handshake, 4); //Protocol version
		writeVarInt(handshake, this.ip.length()); //host length
		handshake.writeBytes(this.ip); //host string
		handshake.writeShort(this.port); //port
		writeVarInt(handshake, 1); //state (1 for handshake)
		
		//Send Handshake Packet
		writeVarInt(dataOutputStream, b.size());
		dataOutputStream.write(b.toByteArray());
		
		//Send Ping Packet
		dataOutputStream.writeByte(0x01);
		dataOutputStream.writeByte(0x00);
		
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		readVarInt(dataInputStream); //Packet size
		int packetId = readVarInt(dataInputStream);
		
		if (packetId == -1) {
			throw new IOException("Premature end of stream.");
		}
		if (packetId != 0x00) {
			throw new IOException("Invalid packet id received: " + packetId);
		}
		
		int length = readVarInt(dataInputStream);
		if (length == -1) {
			throw new IOException("Premature end of stream.");
		}
		if (length == 0) {
			throw new IOException("Invalid string length.");
		}
		
		byte[] in = new byte[length];
		dataInputStream.readFully(in);
		String json = new String(in);
		
		//Send Time Packet
		long now = System.currentTimeMillis();
		dataOutputStream.writeByte(0x09);
		dataOutputStream.writeByte(0x01);
		dataOutputStream.writeLong(now);
		
		readVarInt(dataInputStream);
		packetId = readVarInt(dataInputStream);
		if (packetId == -1) {
			throw new IOException("Premature end of stream.");
		}
		if (packetId != 0x01) {
			throw new IOException("Invalid packet id received: " + packetId);
		}
		long pingtime = dataInputStream.readLong();
		
		StatusResponse response = gson.fromJson(json, StatusResponse.class);
		response.setTime((int)(now - pingtime));
		
		//Close streams
		dataOutputStream.close();
		outputStream.close();
		inputStreamReader.close();
		inputStream.close();
		socket.close();
		
		return response;
	}
	
	public int readVarInt(DataInputStream in) throws IOException {
		int i = 0;
		int j = 0;
		while (true) {
			int k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5) throw new RuntimeException("VarInt too big");
			if ((k & 0x80) != 128) break;
		}
		return i;
	}
	
	public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
		while (true) {
			if ((paramInt & 0xFFFFFF80) == 0) {
				out.writeByte(paramInt);
				return;
			}
			
			out.writeByte(paramInt & 0x7F | 0x80);
			paramInt >>>= 7;
		}
	}
	
	/*public Object[] read() throws Exception {
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
	}*/
	
}
