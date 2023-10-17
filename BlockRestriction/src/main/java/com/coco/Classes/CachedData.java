package com.coco.Classes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coco.Main.Main;

public class CachedData {

	private final Main main;
	public final ItemStack wandItemStack;
	
	public CachedData(Main instance) {
		main = instance;
		wandItemStack = getWandItemStack();
	}
	
	// cached list of all blocks in Minecraft
	public final static List<String> getAllMaterialBlocks() {
		List<String> blockMaterials = Arrays.stream(Material.values())
		        .filter(Material::isBlock)
		        .map(material -> material.toString().toLowerCase())
		        .collect(Collectors.toList());
		
		// added support for selecting all blocks
		blockMaterials.add("@everything");
		
		return blockMaterials;
	}
	
	// cached wand item
	private ItemStack getWandItemStack() {
		ItemStack wandItem = new ItemStack(Material.STONE_AXE);
		ItemMeta wandItemMeta = wandItem.getItemMeta();
		
		// wand item identifier
		wandItemMeta.setUnbreakable(true); 
		
		// hide item attributes and unbreakable message
		wandItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		
		// check for display name
		String configuredDisplayName = main.defaultConfigSynchronizer.config.getString("settings.wand.display-name");
		if (configuredDisplayName != null && configuredDisplayName != "")
			wandItemMeta.setDisplayName(main.helpers.colorizeText(configuredDisplayName));
		
		// check for lore & convert colors
		List<String> configuredItemLore = main.defaultConfigSynchronizer.config.getStringList("settings.wand.lore");
		if (configuredItemLore != null && !configuredItemLore.isEmpty()) {
			for (int i = 0; i < configuredItemLore.size(); i++)
				configuredItemLore.set(i, main.helpers.colorizeText(configuredItemLore.get(i)));
			wandItemMeta.setLore(configuredItemLore);
		}
		
		wandItem.setItemMeta(wandItemMeta);
		return wandItem;
	}
	
}
