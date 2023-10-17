package com.coco.Classes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Location;

public class RegionData {

	public Location location1;
	public Location location2;
	
	@Nonnull
	public RegionData(Location location1, Location location2) {
		this.location1 = location1;
		this.location2 = location2;
	}
	
	public Location getFirstLocation() {
		return location1;
	}
	
	public List<Location> getAllLocations() {
	    List<Location> locations = new ArrayList<>();

	    int minX = Math.min(location1.getBlockX(), location2.getBlockX());
	    int minY = Math.min(location1.getBlockY(), location2.getBlockY());
	    int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());

	    int maxX = Math.max(location1.getBlockX(), location2.getBlockX());
	    int maxY = Math.max(location1.getBlockY(), location2.getBlockY());
	    int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());

	    for (int x = minX; x <= maxX; x++) {
	        for (int y = minY; y <= maxY; y++) {
	            for (int z = minZ; z <= maxZ; z++) {
	                Location location = new Location(location1.getWorld(), x, y, z);
	                locations.add(location);
	            }
	        }
	    }

	    return locations;
	}
	
	public Location getMiddleLocation() {
		double middleX = (location1.getX() + location2.getX()) / 2.0;
	    double middleY = (location1.getY() + location2.getY()) / 2.0;
	    double middleZ = (location1.getZ() + location2.getZ()) / 2.0;
	    return new Location(location1.getWorld(), middleX, middleY, middleZ);
	}
	
	public Location getSecondLocation() {
		return location2;
	}
	
	public int getLowestYAxis() {
		int y1 = location1.getBlockY();
		int y2 = location2.getBlockY();
		
		if (y1 < y2)
			return y1;
		else return y2;
			
	}
	
	public int getMinX() {
	    return Math.min(location1.getBlockX(), location2.getBlockX());
	}

	public int getMinZ() {
	    return Math.min(location1.getBlockZ(), location2.getBlockZ());
	}

	public int getMaxX() {
	    return Math.max(location1.getBlockX(), location2.getBlockX());
	}

	public int getMaxZ() {
	    return Math.max(location1.getBlockZ(), location2.getBlockZ());
	}
	
	public int getHighestYAxis() {
		int y1 = location1.getBlockY();
		int y2 = location2.getBlockY();
		
		if (y1 > y2)
			return y1;
		else return y2;
	}
	
	public boolean isInside(Location location) {
		
		if (!this.getFirstLocation().getWorld().equals(location.getWorld()))
			return false;
		
	    int x1 = Math.min(location1.getBlockX(), location2.getBlockX());
	    int y1 = Math.min(location1.getBlockY(), location2.getBlockY());
	    int z1 = Math.min(location1.getBlockZ(), location2.getBlockZ());

	    int x2 = Math.max(location1.getBlockX(), location2.getBlockX());
	    int y2 = Math.max(location1.getBlockY(), location2.getBlockY());
	    int z2 = Math.max(location1.getBlockZ(), location2.getBlockZ());

	    int x = location.getBlockX();
	    int y = location.getBlockY();
	    int z = location.getBlockZ();

	    boolean insideX = x >= x1 && x <= x2;
	    boolean insideY = y >= y1 && y <= y2;
	    boolean insideZ = z >= z1 && z <= z2;

	    return insideX && insideY && insideZ;
	}
}
