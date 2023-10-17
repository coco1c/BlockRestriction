package com.coco.Classes;

import java.util.HashMap;
import java.util.UUID;

public class RegionSelectorsNames {

	private HashMap<UUID, String> selectors = new HashMap<UUID, String>();
	
	public void addPlayer(UUID playerUUID, String regionName) {
		selectors.put(playerUUID, regionName);
	}
	
	public void removePlayer(UUID playerUUID, String regionName) {
		selectors.remove(playerUUID, regionName);
	}
	
	public boolean playerIsInHashMap(UUID playerUUID) {
		return selectors.containsKey(playerUUID);
	}
	
	public String getPlayerLastRegionName(UUID playerUUID) {
		return selectors.get(playerUUID);
	}
}
