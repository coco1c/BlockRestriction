package com.coco.Schedulers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.coco.Classes.RegionData;
import com.coco.Main.Main;

public class CachedRegionsScheduler {

	private Main main;
	public int taskId = 0;
	public List<RegionData> cachedRegions = new ArrayList<>();
	
	@Nonnull
	public CachedRegionsScheduler(Main instance) {
		main = instance;
		cachedRegions = main.regionConfig.getRegions();
	}
	
	public void stop() {
		main.getServer().getScheduler().cancelTask(taskId);
	}
	
	public void startNew() {
		BukkitTask task = new BukkitRunnable() {

			@Override
			public void run() {
				
				if (main.regionConfig.getRegionNames().size() == 0)
					cachedRegions = new ArrayList<>();
				else cachedRegions = main.regionConfig.getRegions();
			}
			
		}.runTaskTimerAsynchronously(main, 0, 40L);
		
		// update the task id
		taskId = task.getTaskId();
	}
}