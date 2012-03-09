package me.hammale.spyer;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class spyerPlayer implements Listener {
	
	public ArrayList<String> players = new ArrayList<String>();
	
	public spyer plugin;
	
    public spyerPlayer(spyer instance) {
    	plugin = instance;
    }
    
    @EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		  Player p = e.getPlayer();
		  plugin.hideHidden(p);
		  plugin.showVisable(p);
		  if ((plugin.hidden.contains(p.getName()))) {
			  e.setJoinMessage(null);
			  p.sendMessage(ChatColor.GOLD + "Your current Spyer status is: " + ChatColor.GREEN + "invisable");
			  plugin.hidePlayer(p);
		  }else{
			  p.sendMessage(ChatColor.GOLD + "Your current Spyer status is: " + ChatColor.RED + "visable");
			  plugin.unhidePlayer(p);
		  }
    }
	
    @EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if(plugin.hidden.contains(p.getName())){
			e.setCancelled(true);
		}
	}
    
    @EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
	    	String[] split = event.getMessage().split(" ");
	    	if (split.length < 1) return;
	    	Player pla = event.getPlayer();
	    	String cmd = split[0].trim().substring(1).toLowerCase();
			  if (cmd.equalsIgnoreCase("list") || cmd.equalsIgnoreCase("playerlist") || cmd.equalsIgnoreCase("who") || cmd.equalsIgnoreCase("online") || cmd.equalsIgnoreCase("players")) {
				  event.setCancelled(true);
				  displayMessage(pla);
			  }  	  
	    }
	  
	  public String getPlayers(){
		     String a = "";
		     int length = players.size();
		     int i = 0;
		     for(String names:players)
		     {
			      if(i == length){
			    	  a = a + names;
			      }else{
			    	  a = a + names + ", ";
			      }
			      i++;
		     }
		     players.clear();
		     return a; 
	  }
	  
	  public void displayMessage(Player pla){
		  int i = 0;
			  for(String s: plugin.hidden) {
				  players.add(plugin.getServer().getPlayer(s).getName());
				  i++;
			  }
			  pla.sendMessage(ChatColor.BLUE + "Players online " + ChatColor.RED + i + ChatColor.BLUE +" out of " + ChatColor.RED + plugin.getServer().getMaxPlayers());			  
			  pla.sendMessage(ChatColor.GREEN + getPlayers());  
	  }
}