package com.coco.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coco.Main.Main;

public class PlayerQuitEvent implements Listener {

	@SuppressWarnings("unused")
	private Main main;
	
	public PlayerQuitEvent(Main instance) {
		main = instance;
	}
	
	@EventHandler
	public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
	}
}
