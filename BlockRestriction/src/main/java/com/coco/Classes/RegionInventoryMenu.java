package com.coco.Classes;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class RegionInventoryMenu {

	public static final Inventory getMenuInventory() {
		
		Inventory inventory = Bukkit.createInventory(null, 9);
		
		// get items configuration from config
		// TODO
		
		return inventory;
	}
}
