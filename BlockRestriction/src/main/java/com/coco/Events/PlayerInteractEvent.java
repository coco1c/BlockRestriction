package com.coco.Events;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

import com.coco.Classes.WandSelectors.LocationType;
import com.coco.Main.Main;

public class PlayerInteractEvent implements Listener {

	private final Main main;
	
	@Nonnull
	public PlayerInteractEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {		
		
		/*if (!event.getAction().name().contains("_AIR")) {
			// checking the region
			for (RegionData regionData : main.regionConfig.getRegions()) {
		    	if (regionData.isInside(event.getClickedBlock().getLocation())) {
		    		event.setCancelled(true);
		    	}	
		    }
		}*/
		
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		
		Action action = event.getAction();
		if (action != Action.RIGHT_CLICK_BLOCK &&
			action != Action.LEFT_CLICK_BLOCK)
			return;
		
		Player player = event.getPlayer();
		PlayerInventory playerInventory = player.getInventory();
		if (playerInventory.getItemInMainHand() == null)
			return;
		
		Material materialInMainHand = playerInventory.getItemInMainHand().getType();
		if (materialInMainHand != Material.STONE_AXE &&
			!playerInventory.getItemInMainHand().hasItemMeta())
			return;
		
		if (!playerInventory.getItemInMainHand().getItemMeta().isUnbreakable())
			return;
		
		Location blockLocation = event.getClickedBlock().getLocation();
		String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-wand-region-select")
			.replace("{x}", String.valueOf(blockLocation.getBlockX()))
			.replace("{y}", String.valueOf(blockLocation.getBlockY()))
			.replace("{z}", String.valueOf(blockLocation.getBlockZ()));
		
		// Position A selected
		if (action == Action.LEFT_CLICK_BLOCK) {
			configuredMessage = configuredMessage.replace("{point}", "A");
			player.sendMessage(main.helpers.colorizeText(configuredMessage));
			main.wandSelectors.addLocation(player.getUniqueId(), blockLocation, LocationType.A);
		}
		
		// Position B selected
		else if (action == Action.RIGHT_CLICK_BLOCK) {
			configuredMessage = configuredMessage.replace("{point}", "B");
			player.sendMessage(main.helpers.colorizeText(configuredMessage));
			main.wandSelectors.addLocation(player.getUniqueId(), blockLocation, LocationType.B);
		}
		
		event.setCancelled(true);
	}
}
