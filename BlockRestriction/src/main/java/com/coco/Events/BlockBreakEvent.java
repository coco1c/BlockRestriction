package com.coco.Events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Classes.RegionData;
import com.coco.Config.RegionConfig.AllowanceBlockState;
import com.coco.Main.Main;

public class BlockBreakEvent implements Listener {

	private Main main;
	
	public BlockBreakEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
		
		Location blockLocation = event.getBlock().getLocation();
		
		Player player = event.getPlayer();
		
		if (main.cachedRegionsScheduler.cachedRegions.size() == 0)
			return;
		
		List<RegionData> regionsList = main.regionConfig.getRegions();
		for (int i = 0; i < regionsList.size(); i++) {
			
			// retrieve the region name and data, based on an index
			String regionName = main.regionConfig.getRegionNames().get(i);
			RegionData regionData = regionsList.get(i);
			
			// check if the broken block location is actually inside of an existing region
			if (regionData.isInside(blockLocation)) {
				
				// for the player retrieve their allowed broken blocks list..
				List<String> playerBreakingList = main.regionConfig.getPlayersBreakingList(regionName);
				
				// everyone are allowed to break blocks - a general settings
				// OR, the player itself is allowed to break blocks
				if (playerBreakingList.contains("@everyone") || playerBreakingList.contains(player.getName())) {
					
					// here begins the filtering - which blocks can players break?
					// let's retrieve the player blocks, which he's allowed to break
					List<String> allowedBlocks = main.regionConfig.getPlayerBlockStateList(regionName, player.getName(), AllowanceBlockState.ALLOW_BREAK);
					
					// priorities: 1->0, 0->1
					if (allowedBlocks.contains("@everything")) {
						
						// let's check if there are any priorities set in denied break block list
						List<String> deniedBlocks = main.regionConfig.getPlayerBlockStateList(regionName, player.getName(), AllowanceBlockState.DENY_BREAK);
						
						// looks like there are some priorities set
						if (deniedBlocks.size() > 0) {
							
							// let's check which BLOCK priorities were set
							// --> let's compare it to the current one
							if (deniedBlocks.contains(event.getBlock().getType().name().toLowerCase())) {
								
								// there is a priority block..
								// need to cancel an event, since it overrides @everyone in the opposite state
								String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.breaking-specific-block-message")
										.replace("{region}", regionName)
										.replace("{block}", event.getBlock().getType().name().toLowerCase().replace("_", " "));
								player.sendMessage(main.helpers.colorizeText(configuredMessage));
								event.setCancelled(true);
								return; // let's just return here, not to bother with anything else
							}
						}
					}
					
					// if the list size is 0 or [], we automatically know, 
					// that he's not allowed to break anything
					if (allowedBlocks.size() == 0) {
						String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.breaking-specific-block-message")
								.replace("{region}", regionName)
								.replace("{block}", event.getBlock().getType().name().toLowerCase().replace("_", " "));
						player.sendMessage(main.helpers.colorizeText(configuredMessage));
						event.setCancelled(true);
						return;
					}
					
					// on the other hand, if player allowed broken blocks list contains
					// everything, he's allowed to break all blocks, we know that from @everyone tag
					// so let's just return here, no need for anything else
					if (allowedBlocks.contains("@everything"))
						return;
					
					// the if check was passed to this one, so we know something isn't right
					// -- let's check for specific block, if they're allowed to break it
					// let's check their allowed broken blocks list, if it contains the 
					// current block, they tried to break...
					if (!allowedBlocks.contains(event.getBlock().getType().name().toLowerCase())) {
						String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.breaking-specific-block-message")
								.replace("{region}", regionName)
								.replace("{block}", event.getBlock().getType().name().toLowerCase().replace("_", " "));
						player.sendMessage(main.helpers.colorizeText(configuredMessage));
						event.setCancelled(true);
						return;
					}
					return;
				}
				
				// player is denied from breaking blocks in this region in general
				if (!playerBreakingList.contains(player.getName())) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.breaking-message")
						.replace("{region}", regionName);
					player.sendMessage(main.helpers.colorizeText(configuredMessage));
					event.setCancelled(true);
				}
				return;
			}
		}
	}
}
