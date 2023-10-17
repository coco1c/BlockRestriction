package com.coco.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Classes.RegionData;
import com.coco.Main.Main;

public class VehicleEnterEvent implements Listener {

private Main main;
	
	public VehicleEnterEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onVehicleEnter(org.bukkit.event.vehicle.VehicleEnterEvent event) {
		
		List<Entity> passangers = event.getVehicle().getPassengers();
		if (passangers.size() == 0)
			return;
		
		List<Player> playerPassangers = new ArrayList<>();
		for (Entity entity : passangers)
			if (entity instanceof Player)
				playerPassangers.add((Player) entity);
		
	    Location location = event.getVehicle().getLocation();
	    List<String> regionNames = main.regionConfig.getRegionNames();
	    int regionNamesCounter = 0;
	    if (regionNames.size() == 0)
	    	return;
		
	    for (RegionData regionData : main.regionConfig.getRegions()) {
	    	if (regionData.isInside(location)) {
	    		for (Player iPlayer : playerPassangers) {
	    			if (!main.regionConfig.getPlayersEnteringList(regionNames.get(regionNamesCounter)).contains(iPlayer.getName())) {
	    				event.setCancelled(true);
	    			}
	    		}
	    		regionNamesCounter++;
	    	}
	    }
	}
}