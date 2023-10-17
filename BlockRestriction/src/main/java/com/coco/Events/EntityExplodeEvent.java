package com.coco.Events;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Classes.RegionData;
import com.coco.Main.Main;

public class EntityExplodeEvent implements Listener {

	private Main main;
	
	public EntityExplodeEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
		
		List<String> regionNames = main.regionConfig.getRegionNames();
	    if (regionNames.size() == 0)
	    	return;
	    
	    List<RegionData> regions = main.regionConfig.getRegions();
	    for (int i = 0; i < regions.size(); i++) {
	    	if (!main.regionConfig.getRegionExplosionsState(regionNames.get(i))) {
    			
	    		List<Block> blockList = event.blockList();
    			for (Block b : blockList)
    				if (regions.get(i).isInside(b.getLocation()))
    					event.blockList().remove(b);
    		}	
	    }
	}
}
