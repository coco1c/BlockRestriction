package com.coco.Main;

import com.coco.Classes.CachedData;
import com.coco.Classes.RegionSelectorsNames;
import com.coco.Classes.WandSelectors;
import com.coco.Commands.BlockRestrictionCommand;
import com.coco.Config.ConfigManager;
import com.coco.Config.DefaultConfigSynchronizer;
import com.coco.Config.RegionConfig;
import com.coco.References.Helpers;
import com.coco.Schedulers.CachedRegionsScheduler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.coco.Events.BlockBreakEvent;
import com.coco.Events.BlockExplodeEvent;
import com.coco.Events.BlockPlaceEvent;
import com.coco.Events.EntityExplodeEvent;
import com.coco.Events.PlayerChangedWorldEvent;
import com.coco.Events.PlayerInteractEvent;
import com.coco.Events.PlayerJoinEvent;
import com.coco.Events.PlayerMoveEvent;
import com.coco.Events.PlayerQuitEvent;
import com.coco.Events.PlayerTeleportEvent;
import com.coco.Events.VehicleEnterEvent;
import com.coco.Events.VehicleMoveEvent;

public class Main extends JavaPlugin {

	public Main main;
	
	public ConfigManager configManager;
	public DefaultConfigSynchronizer defaultConfigSynchronizer;
	public FileConfiguration dataRegions;
	public FileConfiguration dataPlayers;
	
	public RegionConfig regionConfig;
	public RegionSelectorsNames regionSelectorsNames;
	public CachedRegionsScheduler cachedRegionsScheduler;
	
	public Helpers helpers;
	public CachedData staticItems;
	public WandSelectors wandSelectors;

	@Override
	public void onEnable() {
		main = this;
		
		// config
		saveDefaultConfig();
		defaultConfigSynchronizer = new DefaultConfigSynchronizer(main);
		defaultConfigSynchronizer.startSynchronization();
		
		configManager = new ConfigManager(main);
		configManager.setupEnvironment();
		configManager.reloadConfig("data-regions.yml");
		regionConfig = new RegionConfig(main);
		
		// helper
		helpers = new Helpers();
		staticItems = new CachedData(main);
		wandSelectors = new WandSelectors();
		regionSelectorsNames = new RegionSelectorsNames();
		cachedRegionsScheduler = new CachedRegionsScheduler(main);
		cachedRegionsScheduler.startNew();
		
		// events
		PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		// events: players
		pluginManager.registerEvents(new PlayerInteractEvent(main), main);
		pluginManager.registerEvents(new PlayerMoveEvent(main), main);
		pluginManager.registerEvents(new PlayerJoinEvent(main), main);
		pluginManager.registerEvents(new PlayerQuitEvent(main), main);
		pluginManager.registerEvents(new PlayerChangedWorldEvent(main), main);
		pluginManager.registerEvents(new PlayerTeleportEvent(main), main);
		// events: block
		pluginManager.registerEvents(new BlockPlaceEvent(main), main);
		pluginManager.registerEvents(new BlockBreakEvent(main), main);
		// events: vehicles
		pluginManager.registerEvents(new VehicleMoveEvent(main), main);
		pluginManager.registerEvents(new VehicleEnterEvent(main), main);
		// events: explosions
		pluginManager.registerEvents(new BlockExplodeEvent(main), main);
		pluginManager.registerEvents(new EntityExplodeEvent(main), main);
		
		// commands
		Bukkit.getPluginCommand("blockrestriction").setExecutor(new BlockRestrictionCommand(main));
	}
	
	@Override
	public void onDisable() {
		defaultConfigSynchronizer.stopSynchronization();
		cachedRegionsScheduler.stop();
	}
}