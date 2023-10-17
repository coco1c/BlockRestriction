package com.coco.Config;

import java.io.File;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.coco.Main.Main;

public class DefaultConfigSynchronizer {

	private Main main;
	
	private File configFile;
    public FileConfiguration config;
    private long lastModified;
	private int taskId;
    private boolean showUpdateMessage = false;
    
    @Nonnull
    public DefaultConfigSynchronizer(Main instance) {
    	main = instance;
    	
    	// load the config instances
    	getLoadedFile("config.yml");
    	getLoadedConfig(configFile);
    }
	
    @Nonnull
    public File getLoadedFile(String fileName) {
    	return configFile = new File(main.getDataFolder(), fileName);
    }
    
    @Nonnull
    public FileConfiguration getLoadedConfig(File file) {
    	return config = YamlConfiguration.loadConfiguration(file);
    }
    
    @Override
    public String toString() {
    	return configFile.getName() + ", timestamp: " + lastModified;
    }
    
    public void stopSynchronization() {
    	main.getServer().getScheduler().cancelTask(taskId);
    }
    
    public void startSynchronization() {
    	
        BukkitTask bukkitTask = new BukkitRunnable() {
        	
            @Override
            public void run() {
                if (configFile.lastModified() > lastModified) {
                    config = YamlConfiguration.loadConfiguration(configFile);
                    lastModified = configFile.lastModified();
                    
                    if (showUpdateMessage)
                    	Bukkit.broadcastMessage(config.getName() + " updated by user. Reloading!");
                }
            }
        }.runTaskTimer(main, 0L, 40L);
        
        // update the task id
        taskId = bukkitTask.getTaskId();
    }
}