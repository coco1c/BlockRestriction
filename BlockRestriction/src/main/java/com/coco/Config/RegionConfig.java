package com.coco.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.coco.Classes.RegionData;
import com.coco.Exceptions.NonExistentRegion;
import com.coco.Main.Main;

public class RegionConfig {

	private Main main;
	
	public RegionConfig(Main instance) {
		main = instance;
	}
	
	public enum AllowanceState {
		ALLOWED,
		DENIED
	}
	public enum AllowanceBlockState {
		ALLOW_BREAK,
		DENY_BREAK,
		ALLOW_PLACE,
		DENY_PLACE
	}
	
	public void setPlayerEntering(String regionName, String playerName, AllowanceState state) {
		List<String> currentPlayers = new ArrayList<>(this.getPlayersEnteringList(regionName));
		
		switch (state) {
	        case ALLOWED:
	        	
	        	// check if we want to allow all existing players
	        	if (playerName.equals("@everyone")) {
	        		currentPlayers.clear();
	        		
	        		// re add players to the config
	        		for (String iPlayer : main.helpers.getAllPlayers())
	        			currentPlayers.add(iPlayer);
	        		currentPlayers.remove("@everyone"); // hotfix, remove @everyone in config
	        	} 
	        	
	        	// check if we want to add only 1 player
	        	else {
	        		
	        		// check if the player is already in the allowed list
	        		if (currentPlayers.contains(playerName))
		    	        return;
	        		
	        		// player wasn't in allowed list, add them
		            currentPlayers.add(playerName);
	        	}
	            break;
	        case DENIED:
	        	if (playerName.equals("@everyone")) {
	        		currentPlayers.clear();
	        	} else currentPlayers.remove(playerName);
	            break;
	        default:
	            break;
		}
		
		main.dataRegions.set("regions." + regionName + ".players.allowed-entering", currentPlayers);
	    main.configManager.saveConfigByName("data-regions.yml");
	    main.configManager.reloadConfig("data-regions.yml");
	}
	public List<String> getPlayersEnteringList(String regionName) {
		return main.dataRegions.getStringList("regions." + regionName + ".players.allowed-entering");
	}
	
	@Nonnull
	public void setPlayerBreaking(String regionName, String playerName, AllowanceState state) {
		List<String> currentPlayers = new ArrayList<>(this.getPlayersBreakingList(regionName));
		
		switch (state) {
	        case ALLOWED:
	        	
	        	// check if we want to allow all existing players
	        	if (playerName.equals("@everyone")) {
	        		currentPlayers.clear();
	        		
	        		// re add players to the config
	        		for (String iPlayer : main.helpers.getAllPlayers())
	        			currentPlayers.add(iPlayer);
	        		currentPlayers.remove("@everyone"); // hotfix, remove @everyone in config
	        	} 
	        	
	        	// check if we want to add only 1 player
	        	else {
	        		
	        		// check if the player is already in the allowed list
	        		if (currentPlayers.contains(playerName))
		    	        return;
	        		
	        		// player wasn't in allowed list, add them
		            currentPlayers.add(playerName);
	        	}
	            break;
	        case DENIED:
	        	if (playerName.equals("@everyone")) {
	        		currentPlayers.clear();
	        	} else currentPlayers.remove(playerName);
	            break;
	        default:
	            break;
		}
		
		main.dataRegions.set("regions." + regionName + ".players.allowed-breaking", currentPlayers);
	    main.configManager.saveConfigByName("data-regions.yml");
	    main.configManager.reloadConfig("data-regions.yml");
	}
	@Nonnull
	public List<String> getPlayersBreakingList(String regionName) {
		return main.dataRegions.getStringList("regions." + regionName + ".players.allowed-breaking");
	}
	
	@Nonnull
	public void setPlayerPlacing(String regionName, String playerName, AllowanceState state) {
	    List<String> currentPlayers = new ArrayList<>(this.getPlayersPlacingList(regionName));

	    switch (state) {
	        case ALLOWED:
	        	
	        	// check if we want to allow all existing players
	        	if (playerName.equals("@everyone")) {
	        		currentPlayers.clear();
	        		
	        		// re add players to the config
	        		for (String iPlayer : main.helpers.getAllPlayers())
	        			currentPlayers.add(iPlayer);
	        		currentPlayers.remove("@everyone"); // hotfix, remove @everyone in config
	        	} 
	        	
	        	// check if we want to add only 1 player
	        	else {
	        		
	        		// check if the player is already in the allowed list
	        		if (currentPlayers.contains(playerName))
		    	        return;
	        		
	        		// player wasn't in allowed list, add them
		            currentPlayers.add(playerName);
	        	}
	            break;
	        case DENIED:
	        	if (playerName.equals("@everyone")) {
	        		currentPlayers.clear();
	        	} else currentPlayers.remove(playerName);
	            break;
	        default:
	            break;
	    }
	
		main.dataRegions.set("regions." + regionName + ".players.allowed-placing", currentPlayers);
	    main.configManager.saveConfigByName("data-regions.yml");
	    main.configManager.reloadConfig("data-regions.yml");
	}
	@Nonnull
	public List<String> getPlayersPlacingList(String regionName) {
		return main.dataRegions.getStringList("regions." + regionName + ".players.allowed-placing");
	}
	
	public void setPlayerBlockState(String regionName, String playerName, String materialName, AllowanceBlockState state) {

		// retrieve the current list & config list path
		List<String> list = getPlayerBlockStateList(regionName, playerName, state);
		String oppositeStatePath = "regions." + regionName + ".players." +
				((state == AllowanceBlockState.ALLOW_BREAK) ? "denied-broken-blocks" : 
				(state == AllowanceBlockState.DENY_BREAK) ? "allowed-broken-blocks" : 
				(state == AllowanceBlockState.ALLOW_PLACE) ? "denied-placed-blocks" : 
				(state == AllowanceBlockState.DENY_PLACE) ? "allowed-placed-blocks" : "") + "." + playerName;
		AllowanceBlockState oppositeStateEnum = 
			state == AllowanceBlockState.ALLOW_BREAK ? AllowanceBlockState.DENY_BREAK :
			state == AllowanceBlockState.DENY_BREAK ? AllowanceBlockState.ALLOW_BREAK :
			state == AllowanceBlockState.ALLOW_PLACE ? AllowanceBlockState.DENY_PLACE :
			state == AllowanceBlockState.DENY_PLACE ? AllowanceBlockState.ALLOW_PLACE : null;
					
		// UPDATE THE ORIGINAL BLOCK
		// if we want to add @everything to the list
		if (materialName.equals("@everything")) {
			
			// since we're adding everything, we should clear the list first
			// regardless, if it already contains @everything and only that
			list.clear();
			list.add("@everything");
		}
		else {
			
			// otherwise -- still have to "check", if this list contains @everything. 
			// if it does, we need to remove it, since we're adding a block to it
			list.remove("@everything");
			list.add(materialName); // we want to add 1 block only
		}
		// save blocks for current state
		setPlayerBlockStateList(regionName, playerName, list, state);
		
		// UPDATE/REMOVE MATCHING BLOCKS in other lists
		// in that case we have to take a look in opposite list
		List<String> oppositeList = getPlayerBlockStateList(regionName, playerName, oppositeStateEnum);
		
		// and check if it also contains the block / @everything
		oppositeList.remove(materialName);
		
		// we save the data for this opposite state
		main.dataRegions.set(oppositeStatePath, oppositeList);
		main.configManager.saveConfigByName("data-regions.yml");
	    main.configManager.reloadConfig("data-regions.yml");
	}
	public List<String> getPlayerBlockStateList(String regionName, String playerName, AllowanceBlockState state) {
		
		List<String> list = new ArrayList<>();
		String rootSearch = "regions." + regionName + ".players.";
		
		switch (state) {
			case ALLOW_BREAK:
				list = main.dataRegions.getStringList(rootSearch + "allowed-broken-blocks." + playerName);
				break;
			case DENY_BREAK:
				list = main.dataRegions.getStringList(rootSearch + "denied-broken-blocks." + playerName);
				break;
			case ALLOW_PLACE:
				list = main.dataRegions.getStringList(rootSearch + "allowed-placed-blocks." + playerName);
				break;
			case DENY_PLACE:
				list = main.dataRegions.getStringList(rootSearch + "denied-placed-blocks." + playerName);
				break;
			default:
				break;
		}
		return list;
	}
	public void setPlayerBlockStateList(String regionName, String playerName, List<String> blocksList, AllowanceBlockState state) {
		String path = "regions." + regionName + ".players." + 
			((state == AllowanceBlockState.ALLOW_BREAK) ? "allowed-broken-blocks" : 
			(state == AllowanceBlockState.DENY_BREAK) ? "denied-broken-blocks" : 
			(state == AllowanceBlockState.ALLOW_PLACE) ? "allowed-placed-blocks" : 
			(state == AllowanceBlockState.DENY_PLACE) ? "denied-placed-blocks" : "") + "." + playerName;
		main.dataRegions.set(path, blocksList);
		main.configManager.saveConfigByName("data-regions.yml");
	    main.configManager.reloadConfig("data-regions.yml");
	}
	
	public void setRegionExplosionsState(String regionName, boolean stateBoolean) {
		main.dataRegions.set("regions." + regionName + ".explosions", stateBoolean);
		main.configManager.saveConfigByName("data-regions.yml");
	    main.configManager.reloadConfig("data-regions.yml");
	}
	public boolean getRegionExplosionsState(String regionName) {
		return main.dataRegions.getBoolean("regions." + regionName + ".explosions");
	}
	
	@Nonnull
	public void createRegion(RegionData region, String regionName) {
		
		// explosions set
		main.dataRegions.set("regions." + regionName + ".explosions", true); // by default enabled
		
		Location locationA = region.getFirstLocation();
		main.dataRegions.set("regions." + regionName + ".locator.A.x", locationA.getBlockX());
		main.dataRegions.set("regions." + regionName + ".locator.A.y", locationA.getBlockY());
		main.dataRegions.set("regions." + regionName + ".locator.A.z", locationA.getBlockZ());
		main.dataRegions.set("regions." + regionName + ".locator.A.world", locationA.getWorld().getName());
		
		Location locationB = region.getSecondLocation();
		main.dataRegions.set("regions." + regionName + ".locator.B.x", locationB.getBlockX());
		main.dataRegions.set("regions." + regionName + ".locator.B.y", locationB.getBlockY());
		main.dataRegions.set("regions." + regionName + ".locator.B.z", locationB.getBlockZ());
		
		List<String> allPlayers = main.helpers.getAllPlayers();
		allPlayers.remove("@everyone");
		
		main.dataRegions.set("regions." + regionName + ".players.allowed-breaking", allPlayers);
		main.dataRegions.set("regions." + regionName + ".players.allowed-placing", allPlayers);
		main.dataRegions.set("regions." + regionName + ".players.allowed-entering", allPlayers);
		
		// create players section for specific block placing & breaking
		for (String playerName : allPlayers) {
			if (playerName != null) {
				if (!playerName.equals("@everyone")) { // skip special string @everyone
					main.dataRegions.set("regions." + regionName + ".players.allowed-placed-blocks." + playerName, Arrays.asList("@everything"));
					main.dataRegions.set("regions." + regionName + ".players.allowed-broken-blocks." + playerName, Arrays.asList("@everything"));
					main.dataRegions.set("regions." + regionName + ".players.denied-placed-blocks." + playerName, Arrays.asList());
					main.dataRegions.set("regions." + regionName + ".players.denied-broken-blocks." + playerName, Arrays.asList());
				}
			} else {
				main.dataRegions.set("regions." + regionName + ".players.allowed-placed-blocks." + playerName, Arrays.asList("@everything"));
				main.dataRegions.set("regions." + regionName + ".players.allowed-broken-blocks." + playerName, Arrays.asList("@everything"));
				main.dataRegions.set("regions." + regionName + ".players.denied-placed-blocks." + playerName, Arrays.asList());
				main.dataRegions.set("regions." + regionName + ".players.denied-broken-blocks." + playerName, Arrays.asList());
			}
		}
		
		main.configManager.saveConfigByName("data-regions.yml");
		main.configManager.reloadConfig("data-regions.yml");
	}
	public void removeRegion(String regionName) {
		main.dataRegions.set("regions." + regionName, null);
		main.configManager.saveConfigByName("data-regions.yml");
		main.configManager.reloadConfig("data-regions.yml");
	}
	public RegionData getRegion(String regionName) throws NonExistentRegion {
		if (!regionExists(regionName))
			throw new NonExistentRegion();
		
		double aX = main.dataRegions.getDouble("regions." + regionName + ".locator.A.x");
		double aY = main.dataRegions.getDouble("regions." + regionName + ".locator.A.y");
		double aZ = main.dataRegions.getDouble("regions." + regionName + ".locator.A.z");
		World aWorld = Bukkit.getWorld(main.dataRegions.getString("regions." + regionName + ".locator.A.world"));
		Location locationA = new Location(aWorld, aX, aY, aZ);
		
		double bX = main.dataRegions.getDouble("regions." + regionName + ".locator.B.x");
		double bY = main.dataRegions.getDouble("regions." + regionName + ".locator.B.y");
		double bZ = main.dataRegions.getDouble("regions." + regionName + ".locator.B.z");
		Location locationB = new Location(aWorld, bX, bY, bZ);
		
		return new RegionData(locationA, locationB);
	}
	@Nonnull
	public boolean regionExists(String regionName) {
		return main.dataRegions.isSet("regions." + regionName);
	}
	
	public List<String> getRegionNames() {
		ConfigurationSection section = main.dataRegions.getConfigurationSection("regions");
		if (section == null || section.getKeys(false).size() == 0)
			return new ArrayList<>();
		return section.getKeys(false).stream().toList();
	}
	public List<RegionData> getRegions() {
		List<RegionData> regions = new ArrayList<>();
		if (getRegionNames().size() == 0)
			return new ArrayList<>();
		
		for (String regionName : getRegionNames()) {
			try { regions.add(getRegion(regionName));
			} catch (NonExistentRegion e) { }
		}
		return regions;
	}
}