package com.coco.Classes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

public class WandSelectors {

	private Map<UUID, LinkedList<LocationInfo>> selections = new HashMap<>();

    public WandSelectors() {
        selections = new HashMap<>();
    }

    public void addLocation(UUID playerUUID, Location location, LocationType type) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null");
        
        LinkedList<LocationInfo> locations = selections.getOrDefault(playerUUID, new LinkedList<>());
        locations.addLast(new LocationInfo(location, type));
        if (locations.size() > 2)
            locations.removeFirst();
        selections.put(playerUUID, locations);
    }

    public void removePlayerEntirely(UUID playerUUID) {
        selections.remove(playerUUID);
    }
    
    public List<LocationInfo> getLastTwoSelections(UUID playerUUID) {
        LinkedList<LocationInfo> locations = selections.get(playerUUID);
        if (locations == null || locations.size() == 0)
            return null;
        
        return new LinkedList<>(locations);
    }

    public enum LocationType {
        A,
        B,
    }
    
    public class LocationInfo {

        private Location location;
        private LocationType type;

        public LocationInfo(Location location, LocationType type) {
            this.location = location;
            this.type = type;
        }

        public Location getLocation() {
            return location;
        }

        public LocationType getType() {
            return type;
        }
    }
}