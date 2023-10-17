package com.coco.Events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Classes.RegionData;
import com.coco.Config.RegionConfig.AllowanceBlockState;
import com.coco.Config.RegionConfig.AllowanceState;
import com.coco.Main.Main;

public class PlayerJoinEvent implements Listener {

private Main main;
	
	public PlayerJoinEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
		
		List<String> regions = main.regionConfig.getRegionNames();
		if (regions.isEmpty())
			return;
		
		// player has played before, nothing to change
		Player player = event.getPlayer();
		if (player.hasPlayedBefore()) {
			
			// teleport the player out of the region
			String regionName = "";
			int regionCounterT = 0;
			for (RegionData regiond : main.regionConfig.getRegions()) {
				if (regiond.isInside(player.getLocation())) {
					regionName = regions.get(regionCounterT);
					main.helpers.teleportPlayerAroundRegion(player, regiond, 1, 10);
					
					// send an evacuation message
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.entering-message-evacuated")
							.replace("{region}", regionName);
					main.helpers.sendMessage(player, configuredMessage);
				}
				regionCounterT++;
			}
			regionCounterT = 0;
			return;
		}
		
		// player hasn't played before, let's set their values 
		// to default, depending on current region settings
		for (String regionName : regions) {
			
			// set the current breaking state 
			if (main.regionConfig.getPlayersBreakingList(regionName).contains("@everyone")) {
				// so, it's currently enabled for everyone, let's enable it for this new player as well
				main.regionConfig.setPlayerBreaking(regionName, player.getName(), AllowanceState.ALLOWED);
			} else main.regionConfig.setPlayerBreaking(regionName, player.getName(), AllowanceState.DENIED);
			
			// set the current placing state
			if (main.regionConfig.getPlayersPlacingList(regionName).contains("@everyone")) {
				main.regionConfig.setPlayerPlacing(regionName, player.getName(), AllowanceState.ALLOWED);
			} else main.regionConfig.setPlayerPlacing(regionName, player.getName(), AllowanceState.DENIED);
			
			// set the player entering state
			if (main.regionConfig.getPlayersEnteringList(regionName).contains("@everyone")) {
				main.regionConfig.setPlayerEntering(regionName, player.getName(), AllowanceState.ALLOWED);
			} else main.regionConfig.setPlayerEntering(regionName, player.getName(), AllowanceState.DENIED);
			
			// set the player blocks breaking to unfiltered, default is false
			if (main.regionConfig.getPlayerBlockStateList(regionName, player.getName(), AllowanceBlockState.ALLOW_BREAK).contains("@everyone"))
				main.regionConfig.setPlayerBlockState(regionName, player.getName(), "@everything", AllowanceBlockState.ALLOW_BREAK);
			if (main.regionConfig.getPlayerBlockStateList(regionName, player.getName(), AllowanceBlockState.ALLOW_PLACE).contains("@everyone"))
				main.regionConfig.setPlayerBlockState(regionName, player.getName(), "@everything", AllowanceBlockState.ALLOW_PLACE);
		}
	}
}
