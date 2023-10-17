package com.coco.Events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Classes.RegionData;
import com.coco.Main.Main;

public class PlayerTeleportEvent implements Listener {

	private Main main;
	
	public PlayerTeleportEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onPlayerTeleport(org.bukkit.event.player.PlayerTeleportEvent event) {
		
		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();
		
		List<String> regionNames = main.regionConfig.getRegionNames();
	    int regionNamesCounter = 0;
	    if (regionNames.size() == 0)
	    	return;
		
	    for (RegionData regionData : main.regionConfig.getRegions()) {
	    	if (regionData.isInside(event.getTo())) {
	    		if (!main.regionConfig.getPlayersEnteringList(regionNames.get(regionNamesCounter)).contains(player.getName())) {
    				event.setCancelled(true);
    			}
	    		regionNamesCounter++;
	    	}
	    }
	}
}
