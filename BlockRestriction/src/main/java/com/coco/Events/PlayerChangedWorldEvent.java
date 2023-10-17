package com.coco.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Main.Main;

public class PlayerChangedWorldEvent implements Listener {

	private Main main;
	
	public PlayerChangedWorldEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onPlayerChangedWorld(org.bukkit.event.player.PlayerChangedWorldEvent event) {
		
		Player player = event.getPlayer();
		
		// clear /bp wand selections
		main.wandSelectors.removePlayerEntirely(player.getUniqueId());
	}
}
