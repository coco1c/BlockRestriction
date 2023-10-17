package com.coco.Classes;

import java.util.HashMap;
import java.util.UUID;

public class RegionSelectors {

	private HashMap<UUID, String> selections = new HashMap<>();
	
	public void addPlayer(UUID playerUUID, String regionName) {
		selections.put(playerUUID, regionName);
	}
	
	public boolean isInHashMap(UUID playerUUID) {
		return selections.containsKey(playerUUID);
	}
	
	public void removePlayer(UUID playerUUID, String regionName) {
		if (isInHashMap(playerUUID))
			selections.remove(playerUUID, regionName);
	}
}
