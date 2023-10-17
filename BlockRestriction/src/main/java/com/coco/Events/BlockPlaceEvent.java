package com.coco.Events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Classes.RegionData;
import com.coco.Config.RegionConfig.AllowanceBlockState;
import com.coco.Main.Main;

public class BlockPlaceEvent implements Listener {

	private Main main;
	
	public BlockPlaceEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
		
		Location blockLocation = event.getBlockPlaced().getLocation();
		Player player = event.getPlayer();
		
		if (main.cachedRegionsScheduler.cachedRegions.size() == 0)
			return;
		
		List<RegionData> regionsList = main.regionConfig.getRegions();
		for (int i = 0; i < regionsList.size(); i++) {
			
			String regionName = main.regionConfig.getRegionNames().get(i);
			RegionData regionData = regionsList.get(i);
			
			if (regionData.isInside(blockLocation)) {
				List<String> playerPlacingList = main.regionConfig.getPlayersPlacingList(regionName);
				if (playerPlacingList.contains("@everyone") || playerPlacingList.contains(player.getName())) {
					List<String> allowedBlocks = main.regionConfig.getPlayerBlockStateList(regionName, player.getName(), AllowanceBlockState.ALLOW_PLACE);
					if (allowedBlocks.contains("@everything")) {
						List<String> deniedBlocks = main.regionConfig.getPlayerBlockStateList(regionName, player.getName(), AllowanceBlockState.DENY_PLACE);
						if (deniedBlocks.size() > 0) {
							if (deniedBlocks.contains(event.getBlock().getType().name().toLowerCase())) {
								String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.placing-specific-block-message")
										.replace("{region}", regionName)
										.replace("{block}", event.getBlock().getType().name().toLowerCase().replace("_", " "));
								player.sendMessage(main.helpers.colorizeText(configuredMessage));
								event.setCancelled(true);
								return; // let's just return here, not to bother with anything else
							}
						}
					}
					if (allowedBlocks.size() == 0) {
						String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.placing-specific-block-message")
								.replace("{region}", regionName)
								.replace("{block}", event.getBlock().getType().name().toLowerCase().replace("_", " "));
						player.sendMessage(main.helpers.colorizeText(configuredMessage));
						event.setCancelled(true);
						return;
					}
					if (allowedBlocks.contains("@everything"))
						return;
					if (!allowedBlocks.contains(event.getBlock().getType().name().toLowerCase())) {
						String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.placing-specific-block-message")
								.replace("{region}", regionName)
								.replace("{block}", event.getBlock().getType().name().toLowerCase().replace("_", " "));
						player.sendMessage(main.helpers.colorizeText(configuredMessage));
						event.setCancelled(true);
						return;
					}
					return;
				}
				if (!playerPlacingList.contains(player.getName())) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.placing-message")
						.replace("{region}", regionName);
					player.sendMessage(main.helpers.colorizeText(configuredMessage));
					event.setCancelled(true);
				}
				return;
			}
		}
		
		
	}
}
