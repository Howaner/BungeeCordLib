package de.howaner.BungeeCordLib.util;

import java.util.List;

public class StatusResponse {
	private String description;
	private Players players;
	private Version version;
	private String favicon;
	private int time;
	
	public String getDescription() {
		return description;
	}
	
	public Players getPlayers() {
		return players;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public String getFavicon() {
		return favicon;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public class Players {
		private int max;
		private int online;
		private List<Player> sample;
		
		public int getMax() {
			return max;
		}
		
		public int getOnline() {
			return online;
		}
		
		public List<Player> getSample() {
			return sample;
		}
	}
	
	public class Player {
		private String name;
		private String id;
		
		public String getName() {
			return name;
		}
		
		public String getId() {
			return id;
		}
	}
	
	public class Version {
		private String name;
		private String protocol;
		
		public String getName() {
			return name;
		}
		
		public String getProtocol() {
			return protocol;
		}
	}
}
