package me.hammale.spyer;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class spyer extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[Spyer] Version: " + pdfFile.getVersion() + " Enabled!");
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[Spyer] Version: " + pdfFile.getVersion() + " Disabled!");
	}
	
	public void vansihPlayer(Player mainp){
		for(Player p : getServer().getOnlinePlayers()){
			p.hidePlayer(mainp);
		}
	}
	
}
