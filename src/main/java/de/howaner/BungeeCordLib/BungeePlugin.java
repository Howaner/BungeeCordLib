package de.howaner.BungeeCordLib;

import de.howaner.BungeeCordLib.listener.BukkitListener;
import de.howaner.BungeeCordLib.listener.BungeeListener;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BungeePlugin extends JavaPlugin {
	public static Logger log;
	public static BungeePlugin instance;
	private static BungeeCord manager;
	
	@Override
	public void onEnable() {
		log = this.getLogger();
		instance = this;
		manager = new BungeeCord();
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
		Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
		manager.initialize();
		log.info("Manager loaded!");
	}
	
	@Override
	public void onDisable() {
		for (int id : manager.getPacketServers())
			manager.removePacketServer(id);
	}
	
	public static BungeeCord getManager() {
		return manager;
	}
	
}
