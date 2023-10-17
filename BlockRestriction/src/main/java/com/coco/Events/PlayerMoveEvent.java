package com.coco.Events;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Classes.RegionData;
import com.coco.Main.Main;

public class PlayerMoveEvent implements Listener {
 
	private final Main main;
	
	@Nonnull
	public PlayerMoveEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
	    Location playerLocation = player.getLocation();

	    Location from = event.getFrom();
	    Location to = event.getTo();

	    if (from.getBlock().equals(to.getBlock())) 
	    	return;
	    
	    List<String> regionNames = main.regionConfig.getRegionNames();
	    if (regionNames.size() == 0)
	    	return;
	    
	    int regionCounter = 0;
	    for (RegionData regionData : main.cachedRegionsScheduler.cachedRegions) {
	        if (regionData.isInside(to)) {
	        	
	        	// check if they're allowed to move in it
	        	if (main.regionConfig.getPlayersEnteringList(regionNames.get(regionCounter)).contains(player.getName()) ||
        			main.regionConfig.getPlayersEnteringList(regionNames.get(regionCounter)).contains("@everyone"))
	        		return;
	        	
	            int currentYAxis = to.getBlockY();
	            int maxRegionYAxis = regionData.getHighestYAxis();

	            // player jumped on the top
	            if (currentYAxis == maxRegionYAxis) {
	                main.helpers.teleportPlayerAroundRegion
	                	(player, regionData, 0, 5);
	                
	                String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.entering-message-top")
                		.replace("{region}", regionNames.get(regionCounter));
	                main.helpers.sendMessage(player, configuredMessage);
	                
	            } else {
	            	String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.entering-message")
            			.replace("{region}", regionNames.get(regionCounter));
	                main.helpers.sendMessage(player, configuredMessage);
	            	event.setCancelled(true);
	            }
	            regionCounter = 0;
	        }
	        regionCounter++;
	    }
	}
}