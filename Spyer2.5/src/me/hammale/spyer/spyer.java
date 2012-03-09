package me.hammale.spyer;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class spyer extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	ArrayList<String> hidden = new ArrayList<String>();
	
	public int taskId = 0;
	
	public FileConfiguration config;
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[Spyer] Version: " + pdfFile.getVersion() + " Enabled!");
		getServer().getPluginManager().registerEvents(new spyerPlayer(this), this);
		loadConfiguration();
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[Spyer] Version: " + pdfFile.getVersion() + " Disabled!");
	}
	
	public void loadConfiguration(){
	    config = getConfig();
	    config.options().copyDefaults(true); 
	    
	    for(World w : this.getServer().getWorlds()) {
	    String wrld = w.getName();
	    
	    String path = "World." + wrld + "." + "ItemBurning.Item";
	    String path1 = "World." + wrld + "." + "ItemBurning.RateInTicks";
	    
	    config.addDefault(path, 266);
	    config.addDefault(path1, 1000);
	    
	    }
	    config.options().copyDefaults(true);  
	    saveConfig();
	}
	
	public int getItem(World w){
	    config = getConfig();
	    String wrld = w.getName();
	    int amnt = config.getInt("World." + wrld + "." + "ItemBurning.Item"); 
	    return amnt;
	}
	public int getRate(World w){
	    config = getConfig();
	    String wrld = w.getName();
	    int amnt = config.getInt("World." + wrld + "." + "ItemBurning.RateInTicks"); 
	    return amnt;
	}	
	
	public void hidePlayer(Player mainp){
		hidden.add(mainp.getName());
		for(Player p : getServer().getOnlinePlayers()){
			p.hidePlayer(mainp);
		}
	}
	
	public void unhidePlayer(Player mainp){
		hidden.add(mainp.getName());
		for(Player p : getServer().getOnlinePlayers()){
			p.showPlayer(mainp);
		}
	}
	
	public void hideHidden(Player mainp){
		for(String s : hidden){
			Player p = getServer().getPlayer(s);
			mainp.hidePlayer(p);
		}
	}
	
	public void showVisable(Player mainp){
		for(Player p : getServer().getOnlinePlayers()){
			if(!(hidden.contains(p.getName()))){
				mainp.showPlayer(p);
			}
		}
	}
	
	public void timedHide(final Player player){
		
		taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

		  public void run() {
		try {
			  if (!player.isOnline()) {
				  unhidePlayer(player);			  
				  cancelTask();
				  return;
			  }
			  
			  Player[] playerList = getServer().getOnlinePlayers();
			  for (Player p : playerList) {
				  hidePlayer(p);
			  }
			  if (!(hasItem(player)) || (!(hidden.contains(player.getName())))) {
				  unhidePlayer(player);
				  return;
			  }
	
			  if (hasItem(player)) {
				  removeItem(player);
			  }
	
		} catch (Exception e) {
				  e.printStackTrace();
			  }
			  }
		  }, getRate(player.getWorld()), getRate(player.getWorld()));
	}
	  public boolean hasItem(Player player) {
		  World w = player.getWorld();
		  final int id = getItem(w);
		  Inventory inv = player.getInventory();
		  if(inv.contains(id, 2)){
			  return true;
		  }
		  if(inv.contains(id)){
			  int sec = getRate(w)/20;
			  player.sendMessage(ChatColor.RED + "WARNING! You have " + sec + " seconds of invisibility left!");
			  return true;
		  }else{
			  return false;
		  }
	  }
	  
	  public void removeItem(Player player) {
		  World w = player.getWorld();
		  final int id = getItem(w);
		  Inventory inv = player.getInventory();
		  Material m = Material.getMaterial(id);
		  inv.removeItem(new ItemStack (m, 1));
	  }
		public void cancelTask() {
			getServer().getScheduler().cancelTask(taskId);
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		PluginDescriptionFile pdfFile = this.getDescription();
		
		if (cmd.getName().equalsIgnoreCase("spy")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				if(args.length == 0){
					return false;
				}else if(args.length == 1) {
					  if (args[0].equalsIgnoreCase("off")) {
						  if (player.hasPermission("spyer.user") == true || player.hasPermission("spy.admin") == true || player.hasPermission("spy.noitem") == true || player.isOp()){					  
							  unhidePlayer(player);
							  return true;
						  }
					  }else if (args[0].equalsIgnoreCase("on")) {
						  if (player.hasPermission("spyer.user") == true || player.hasPermission("spy.admin") == true || player.hasPermission("spy.noitem") == true || player.isOp()){
						  timedHide(player);
						  return true;
						  }
					  }else if (args[0].equalsIgnoreCase("status")) {
						  ChatColor color = ChatColor.RED;
						  boolean status = hidden.contains(player.getName());
						  String stat = "visable";
						  if(status == true){
							 color = ChatColor.GREEN;
							 stat = "invisable"; 					  
						  }
						  if(player.hasPermission("spyer.admin") == true || player.isOp()){
							  player.sendMessage(ChatColor.GOLD + "Current status for " + ChatColor.DARK_AQUA + player.getName() + ": " + color  + stat);
						  }
						  return true;
					  }else if (args[0].equalsIgnoreCase("reload")) {
						  if(player.hasPermission("spyer.admin") == true || player.isOp()){
					        reloadConfig();
					        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Spyer config reloaded!");
					        return true;
						  }
					  }else if (args[0].equalsIgnoreCase("info")) {
						  if(player.hasPermission("spyer.admin") == true || player.isOp()){
					        sender.sendMessage(ChatColor.GOLD + "<---SPYER VERSION: " + pdfFile.getVersion() + "--->");
					        sender.sendMessage(ChatColor.DARK_AQUA + "Developed by --" + ChatColor.WHITE + " hammale & nickguletskii");				  
					        return true;
						  }
					  }else  if (args[0].equalsIgnoreCase("logout")) {
					        sender.sendMessage(ChatColor.YELLOW + player.getName() + " left the game");	   
					        return true;			  
					  }else if (args[0].equalsIgnoreCase("login")) {
					        sender.sendMessage(ChatColor.YELLOW + player.getName() + " joined the game");	  
					        return true;				  
					  }else if (args[0].equalsIgnoreCase("help")) {
						  if(player.hasPermission("spyer.user") == true || player.hasPermission("spyer.admin") == true || player.hasPermission("spyer.noitem") == true || player.isOp()){			        
						        sender.sendMessage(ChatColor.GOLD+ "<---SPYER VERSION: " + pdfFile.getVersion() + "--->");
						        sender.sendMessage(ChatColor.DARK_AQUA + "/spy <off/on> --" + ChatColor.WHITE + " Make yourself (in)visable");
						        sender.sendMessage(ChatColor.DARK_AQUA + "/spy status --" + ChatColor.WHITE + " Check you Spyer ststus");
						        sender.sendMessage(ChatColor.DARK_AQUA + "/spy <logout/login> --" + ChatColor.WHITE + " Fake a login/logout");		       
						        if(player.hasPermission("spyer.admin") == true || player.hasPermission("spyer.noitem") == true || player.isOp()){
						        	sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin unlimited <off/on> --" + ChatColor.WHITE + " Make yourself (in)visable WITHOUT losing items");
						        }
						        if(player.hasPermission("spyer.admin") == true || player.isOp()){
							        sender.sendMessage(ChatColor.GOLD + "<---SPYER ADMIN--->");
							        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin all unlimited <off/on> --" + ChatColor.WHITE + " Make EVERYONE online (in)visable WITHOUT losing items");		       
							        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin <player> <off/on> --" + ChatColor.WHITE + " Make <player> (in)visable");
							        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin <player> unlimited <off/on> --" + ChatColor.WHITE + " Make <player> (in)visable WITHOUT losing items");				      
							        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin all <off/on> --" + ChatColor.WHITE + " Make EVERYONE online (in)visable");
							        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin <player> <logout/login> --" + ChatColor.WHITE + " Fake a login/logout for <player>");
							        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin status <player> --" + ChatColor.WHITE + " Check <player>'s Spyer ststus");
							        sender.sendMessage(ChatColor.DARK_AQUA + "/spy reload --" + ChatColor.WHITE + " Reloads the Spyer config file");
							        sender.sendMessage(ChatColor.DARK_AQUA + "/spy info --" + ChatColor.WHITE + " Spyer version information");
							    }
						        return true;
						  }else{
							  sender.sendMessage(ChatColor.RED + "You ain't got no perms for this!");
						  }
					  }
					  return false;
				}else if (args[0].equalsIgnoreCase("admin")) {
					  if(args.length == 1) {
						  return false;
					  }
					  
						if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("status") && args[2] != null) {
							  ChatColor color = ChatColor.RED;
							  boolean status = hidden.contains(args[2]);
							  String stat = "visable";
							  if(status == true){
								 color = ChatColor.GREEN;
								 stat = "invisable";
							  }
							  if(player.hasPermission("spyer.admin") == true || player.isOp()){
								  player.sendMessage(ChatColor.GOLD + "Current status for " + ChatColor.DARK_AQUA + args[2] + ": " + color + stat);
								  return true;
							  }
							  return false;
						}else if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("unlimited") && args[2].equalsIgnoreCase("off")) {
							  if (player.hasPermission("spyer.admin") || player.hasPermission("spyer.noitem") || player.isOp()){					  
								  unhidePlayer(player);
								  return true;
							  }
							  return false;
						}else if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("unlimited") && args[2].equalsIgnoreCase("on")) {
							  if (player.hasPermission("spyer.noitem") || player.hasPermission("spyer.admin") || player.isOp()){
								  hidePlayer(player);
								  return true;
							  }
							  return false;
						}else if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("off")) {
							  if(!(args[1].equals("all"))){
								  Player p = this.getServer().getPlayer(args[1]);
								  if(p.isOnline()){
									  if (player.hasPermission("spyer.admin") || player.isOp()){
										  unhidePlayer(p);
										  return true;
									  }
									  return false;
								  }else{
									  player.sendMessage(ChatColor.RED + p.getName() + " is not online!");
									  return true;
								  }
							  }else{
								for(Player p : getServer().getOnlinePlayers()){
									if(hidden.contains(p.getName())){
										unhidePlayer(p);
									}
									player.sendMessage(ChatColor.GREEN + "All are unhidden!");
								}
							  }
						}else if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("login")) {
							  if(!(args[1].equals("all"))){
								  if (player.hasPermission("spyer.admin") || player.isOp()){
								        sender.sendMessage(ChatColor.YELLOW + args[1] + " joined the game");
									  return true;
								  }
								  return false;
							  }
						}else if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("logout")) {
							  if(!(args[1].equals("all"))){
								  if (player.hasPermission("spyer.admin") || player.isOp()){
								      sender.sendMessage(ChatColor.YELLOW + args[1] + " left the game");
									  return true;
								  }
								  return false;
							  }
						}else if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("on")) {
							  Player p = getServer().getPlayer(args[1]);
							  if(!(args[1].equals("all"))){
								  if(p.isOnline()){
									  if (player.hasPermission("spyer.admin") || player.isOp()){
										  timedHide(p);
										  return true;
									  }
								  return false;
								  }else{
									  player.sendMessage(ChatColor.RED + p.getName() + " is not online!");
									  return true;
								  }	  
							  }else{
									for(Player p1 : getServer().getOnlinePlayers()){
										if(hidden.contains(p1.getName())){
											if(hidden.contains(p1.getName())){
												unhidePlayer(p1);
											}
											timedHide(p);
										}
									}
									player.sendMessage(ChatColor.GREEN + "All are hidden!");
							  }
							  return false;
						}else if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("unlimited") && args[3].equalsIgnoreCase("off")) {		  
							  Player p = this.getServer().getPlayer(args[1]);
							  if(p.isOnline()){
								  if (player.hasPermission("spyer.admin") || player.isOp()){
									  unhidePlayer(p);
									  return true;
								  }
							  return false;
							  }else{
								  player.sendMessage(ChatColor.RED + p.getName() + " is not online!");
								  return true;
							  }
						}else if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("unlimited") && args[3].equalsIgnoreCase("on")) {
							  Player p = this.getServer().getPlayer(args[1]);
							  if(p.isOnline()){
								  if (player.hasPermission("spyer.admin") || player.isOp()){
									  hidePlayer(p);
									  return true;
								  }
							  return false;
							  }else{
								  player.sendMessage(ChatColor.RED + p.getName() + " is not online!");
								  return true;
							  }
						}else if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("all") && args[2].equalsIgnoreCase("on")) {
							  if (player.hasPermission("spyer.admin") || player.isOp()){
								  Player[] players = getServer().getOnlinePlayers();				  
								  for(Player pl : players){					  
										hidePlayer(pl);
								  }
									return true;
							  }
								  return false;
						}else if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("all") && args[2].equalsIgnoreCase("off")) {
							  if (player.hasPermission("spyer.admin") || player.isOp()){
								  Player[] players = getServer().getOnlinePlayers();				  
								  for(Player pl : players){
										unhidePlayer(pl);
								  }
									return true;
							  }
						}else if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("all") && args[2].equalsIgnoreCase("unlimited") && args[3].equalsIgnoreCase("on")) {
							  if (player.hasPermission("spyer.admin") || player.isOp()){
								  Player[] players = getServer().getOnlinePlayers();				  
								  for(Player pl : players){			  
										hidePlayer(pl);
								  }
									return true;
							  }
								  return false;
						}else if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("all") && args[2].equalsIgnoreCase("unlimited") && args[3].equalsIgnoreCase("off")) {
							  if (player.hasPermission("spyer.admin") || player.isOp()){
								  Player[] players = getServer().getOnlinePlayers();				  
								  for(Player pl : players){				  
										unhidePlayer(pl);
								  }	
									return true;
							  }
								  return false;
						}else{
							return false;
						}
				}
			}
		}  
		
		return true;
	}
}