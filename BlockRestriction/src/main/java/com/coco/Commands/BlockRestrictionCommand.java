package com.coco.Commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.coco.Config.RegionConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.coco.Classes.CachedData;
import com.coco.Classes.RegionData;
import com.coco.Classes.WandSelectors.LocationInfo;
import com.coco.Classes.WandSelectors.LocationType;
import com.coco.Exceptions.NonExistentRegion;
import com.coco.Main.Main;

public class BlockRestrictionCommand implements TabExecutor {

	private Main main;
	
	public BlockRestrictionCommand(Main instance) {
		main = instance;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player))
			return Collections.emptyList();
		
		Player player = (Player) sender;
		if (!player.hasPermission("blockrestriction.cmd"))
			return Collections.emptyList();
		
		// show all subcommands
		if (args.length == 1) {
			return Arrays.asList(
				"menu", "wand", "explosions", "tp", "create", "remove", "select", 
				"allow-block-placing", "allow-block-breaking", "set-block", "set-player"
			);
		}
		
		// command <...> <X> 
		if (args.length == 2) {
			if (args[0].equals("explosions")) 
				return Arrays.asList("true", "false");
			
			if (args[0].equals("create"))
				return Arrays.asList("<region>");
			
			if (Arrays.asList("tp", "remove").contains(args[0])) 
				return main.regionConfig.getRegionNames();
			
			if (args[0].equals("select")) {
				List<String> allRegions = main.regionConfig.getRegionNames();
	            String lastEnteredRegion = args[1];
	            
	            // Use suggestions based on the last entered region name or provide all region names as suggestions
	            if (!lastEnteredRegion.isEmpty()) {
	                List<String> matchingRegions = allRegions.stream()
	                        .filter(region -> region.toLowerCase().startsWith(lastEnteredRegion.toLowerCase()))
	                        .map(region -> args[1] + region.substring(lastEnteredRegion.length()))
	                        .collect(Collectors.toList());
	                return matchingRegions;
	            } else {
	                return allRegions;
	            }
			}
			
			if (Arrays.asList("allow-block-placing", "allow-block-breaking").contains(args[0]))
				return Arrays.asList("true", "false");
			
			if (args[0].equals("set-block")) {
				List<String> blockMaterials = CachedData.getAllMaterialBlocks().stream()
				        .map(material -> material.toString().toLowerCase())
				        .collect(Collectors.toList());
				return blockMaterials;
			}
			
			if (args[0].equals("set-player"))
				return Arrays.asList("allow-ENTER", "deny-ENTER");
			
			return Collections.emptyList();
		}
		
		// command <...> <...> <X> 
		if (args.length == 3) {
			if (args[0].equals("explosions") && Arrays.asList("true", "false").contains(args[1]))
				return main.regionConfig.getRegionNames();
			
			List<String> onlinePlayers = main.helpers.getAllPlayers();
			if ((args[0].equals("allow-block-placing") || args[0].equals("allow-block-breaking")) && 
					Arrays.asList("true", "false").contains(args[1]))
				return onlinePlayers;
			
			if (args[0].equals("set-block"))
				return Arrays.asList("allow-PLACE", "allow-BREAK", "deny-PLACE", "deny-BREAK");
			
			if (args[0].equals("set-player") && Arrays.asList("allow-ENTER", "deny-ENTER").contains(args[1]))
				return onlinePlayers;
				
			return Collections.emptyList();
		}
		
		// command <...> <...> <...> <X> 
		if (args.length == 4) { 
			if (args[0].equals("set-block") && Arrays.asList("allow-PLACE", "allow-BREAK", "deny-PLACE", "deny-BREAK")
				.contains(args[2])) {
				List<String> onlinePlayers = main.helpers.getAllPlayers();
				return onlinePlayers;
			}
			
			Collections.emptyList();
		}
		return Collections.emptyList();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// check who executed the command
		if (!(sender instanceof Player))
			return true;
		
		Player player = (Player) sender;
		String noPermissionMessage = main.helpers.colorizeText(main.defaultConfigSynchronizer.config.getString("messages.no-permission"));
		
		// only main command was typed
		if (args.length == 0) {
			if (!player.hasPermission("blockrestriction.cmd")) {
				player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.cmd"));
				return true;
			}
			for (String line : main.defaultConfigSynchronizer.config.getStringList("messages.cmd-blockrestriction"))
				main.helpers.sendMessage(player, line);
			return true;
		}
		
		// command with 1 parameter was typed, exposing args[0]
		args[0] = args[0].toLowerCase();
//1 ARGS
		if (args.length == 1) {
			
			if (args[0].equals("explosions")) {
				if (!player.hasPermission("blockrestriction.explosions")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.explosions"));
					return true;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-explosions-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("menu")) {
				if (!player.hasPermission("blockrestriction.menu")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.menu"));
					return true;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-menu");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("wand")) {
				if (!player.hasPermission("blockrestriction.wand")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.wand"));
					return true;
				}
				
				ItemStack wandItem = main.staticItems.wandItemStack;
				player.getInventory().addItem(wandItem);
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-wand-give");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("tp")) {
				if (!player.hasPermission("blockrestriction.tp")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.tp"));
					return true;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-tp-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("create")) {
				if (!player.hasPermission("blockrestriction.create")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.create"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-create-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;	 
			}
			
			if (args[0].equals("remove")) {
				if (!player.hasPermission("blockrestriction.remove")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.remove"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-remove-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("select")) {
				if (!player.hasPermission("blockrestriction.select")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.select"));
					return true;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-select-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("allow-block-breaking")) {
				if (!player.hasPermission("blockrestriction.allow-block-breaking")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.allow-block-breaking"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-breaking-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("allow-block-placing")) {
				if (!player.hasPermission("blockrestriction.allow-block-placing")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.allow-block-placing"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-placing-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			if (args[0].equals("set-player")) {
				if (!player.hasPermission("blockrestriction.set-player")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.set-player"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-player-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			if (args[0].equals("set-block")) {
				if (!player.hasPermission("blockrestriction.set-block")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.set-block"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-block-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			return false;
		}

//2 ARGS
		// command with 2 parameters was typed, exposing args[1]
		if (args.length == 2) {
			
			if (args[0].equals("explosions")) {
				if (!player.hasPermission("blockrestriction.explosions")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.explosions"));
					return true;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-explosions-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("set-player")) {
				if (!player.hasPermission("blockrestriction.set-player")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.set-player"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-player-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("create")) {
				if (!player.hasPermission("blockrestriction.create")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.create"));
					return false;
				}
				
				// formatting
				if (main.helpers.stringContainsSymbols(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-create-invalid-format");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// no proper selections
				List<LocationInfo> playerSelections = main.wandSelectors.getLastTwoSelections(player.getUniqueId());
				if (playerSelections == null || playerSelections.size() < 2) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-create-no-selection");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// region already exists
				if (main.regionConfig.regionExists(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-create-already-exists")
						.replace("{region}", args[1]);
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// reparse location types
				Location locationA = null, locationB = null;
				for (LocationInfo info : playerSelections) {
				    if (info.getType() == LocationType.A) {
				        locationA = info.getLocation();
				    } else if (info.getType() == LocationType.B) {
				        locationB = info.getLocation();
				    }
				}
				
				// check A and B order, type
				if (locationA == null || locationB == null) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-create-no-selection");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// check if region is interfering with other regions
				RegionData regionData = new RegionData(locationA, locationB);
				
				World world = locationA.getWorld();
			    int x1 = Math.min(locationA.getBlockX(), locationB.getBlockX());
			    int y1 = Math.min(locationA.getBlockY(), locationB.getBlockY());
			    int z1 = Math.min(locationA.getBlockZ(), locationB.getBlockZ());
			    int x2 = Math.max(locationA.getBlockX(), locationB.getBlockX());
			    int y2 = Math.max(locationA.getBlockY(), locationB.getBlockY());
			    int z2 = Math.max(locationA.getBlockZ(), locationB.getBlockZ());
			    
                // some of the regions already exist, let's 
			    // check if there are any interferance
                int interferanceCounter = 0;
                String regionNamesCombined = "";
                boolean isInterfering = false;
			    if (main.regionConfig.getRegionNames().size() > 0) {
			    	for (RegionData tempRegionData : main.regionConfig.getRegions()) {
			    		regionNamesCombined += main.regionConfig.getRegionNames().get(interferanceCounter) + 
		    				((interferanceCounter == main.regionConfig.getRegionNames().size() - 1) ? "" : ", ");
			    		
			    		// for each block check if it's inside another region
			    		// TODO ** make this better, performance impact (mid-high)
			    		for (int x = x1; x <= x2; x++) {
							for (int y = y1; y <= y2; y++) {
								for (int z = z1; z <= z2; z++) {
									Location tempLocation = new Location(world, x, y, z);
							    	if (tempRegionData.isInside(tempLocation))
							    		isInterfering = true;
							    }	
							}
			    		}
			    		interferanceCounter++;
			    	}
			    }
			    
			    if (isInterfering) {
			    	String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-create-interference")
		    			.replace("{regions}", regionNamesCombined);
					main.helpers.sendMessage(player, configuredMessage);
		    		return true;
			    }
			    
				// create the region
			    main.regionConfig.createRegion(regionData, args[1]);
			    
			    // auto select the region
			    main.regionSelectorsNames.addPlayer(player.getUniqueId(), args[1]);
			    
			    // auto set-allowed for each player that has ever played on the server
			    for (String playerName : main.helpers.getAllPlayers()) {
			    	main.regionConfig.setPlayerBreaking(args[1], playerName, RegionConfig.AllowanceState.ALLOWED);
					main.regionConfig.setPlayerPlacing(args[1], playerName, RegionConfig.AllowanceState.ALLOWED);
					main.regionConfig.setPlayerEntering(args[1], playerName, RegionConfig.AllowanceState.ALLOWED);
			    }
		    	
			    String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-create-ok")
			    		.replace("{region}", args[1]);
			    	main.helpers.sendMessage(player, configuredMessage);
			    return true;
			}
			
			if (args[0].equals("tp")) {
				if (!player.hasPermission("blockrestriction.tp")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.tp"));
					return true;
				}
				
				if (main.helpers.stringContainsSymbols(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-tp-invalid-format");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				RegionData regionData = null;
				try {
					regionData = main.regionConfig.getRegion(args[1]);
				} catch (NonExistentRegion e) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-tp-doesnt-exist")
						.replace("{region}", args[1]);
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				Location middleLocation = regionData.getMiddleLocation();
				Location tpLocation = new Location(middleLocation.getWorld(), 
					middleLocation.getX(), regionData.getHighestYAxis() + 2, middleLocation.getZ());
				player.setVelocity(new Vector(0, 0, 0)); // reset their vector
				player.teleport(tpLocation, TeleportCause.PLUGIN); // prevent moved too fast messages
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-tp-ok")
					.replace("{region}", args[1]);
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("remove")) {
				if (!player.hasPermission("blockrestriction.remove")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.remove"));
					return true;
				}
				
				// formatting
				if (main.helpers.stringContainsSymbols(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-remove-invalid-format");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				if (!main.regionConfig.regionExists(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-remove-doesnt-exist")
							.replace("{region}", args[1]);
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				if (main.regionSelectorsNames.playerIsInHashMap(player.getUniqueId()))
					main.regionSelectorsNames.removePlayer(player.getUniqueId(), args[1]);
				main.regionConfig.removeRegion(args[1]);
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-remove-ok")
					.replace("{region}", args[1]);
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("select")) {
				if (!player.hasPermission("blockrestriction.select")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.select"));
					return true;
				}
				
				// formatting
				if (main.helpers.stringContainsSymbols(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-select-invalid-format");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// check if region exists
				if (!main.regionConfig.regionExists(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-select-doesnt-exist")
							.replace("{region}", args[1]);
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// spawn selection particles
				RegionData regionData = null;
				try {
					regionData = main.regionConfig.getRegion(args[1]);
				} catch (NonExistentRegion e) { }
				main.helpers.spawnVerticalCornerParticles(regionData.getFirstLocation(), 
					regionData.getSecondLocation(), Particle.WAX_ON, 5);
				
				// send the message
				main.regionSelectorsNames.addPlayer(player.getUniqueId(), args[1]);
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-select-ok")
					.replace("{region}", args[1]);
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("set-block")) {
				if (!player.hasPermission("blockrestriction.set-block")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.set-block"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-block-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			// usage for placing
			if (args[0].equals("allow-block-placing") && Arrays.asList("true", "false").contains(args[1])) {
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-placing-usage");
					main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			// usage for breaking
			if (args[0].equals("allow-block-breaking") && Arrays.asList("true", "false").contains(args[1])) {
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-breaking-usage");
					main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
		}
		
// 3 ARGS
		if (args.length == 3) {
			
			if (args[0].equals("explosions")) {
				if (!player.hasPermission("blockrestriction.explosions")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.explosions"));
					return true;
				}
				
				// check parameters for true/false
				if (!Arrays.asList("true", "false").contains(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-explosions-usage");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// check for current region selection state
				if (!main.regionSelectorsNames.playerIsInHashMap(player.getUniqueId()) || 
					main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId()) == null) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-explosions-no-region");
						main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				String lastSelectedRegionName = main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId());
				
				// save data
				main.regionConfig.setRegionExplosionsState(lastSelectedRegionName, Boolean.parseBoolean(args[1]));
				
				// send a message
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-explosions-ok")
					.replace("{region}", lastSelectedRegionName)
					.replace("{state-color}", args[1].equals("true") ? 
						ChatColor.GREEN + args[1] : ChatColor.RED + args[1]);
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("set-player")) {
				if (!player.hasPermission("blockrestriction.set-player")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.set-player"));
					return false;
				}
				
				// check for current region selection state
				if (!main.regionSelectorsNames.playerIsInHashMap(player.getUniqueId()) || 
					main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId()) == null) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-player-no-region");
						main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				String lastSelectedRegionName = main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId());
				
				// check params: action
				if (!Arrays.asList("allow-ENTER", "deny-ENTER").contains(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-player-usage");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// check parames: player
				if (!main.helpers.getAllPlayers().contains(args[2])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-invalid-player")
						.replace("{player}", args[2]);
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// TODO
				main.regionConfig.setPlayerEntering(lastSelectedRegionName, args[2], 
						args[1].equals("allow-ENTER") ? RegionConfig.AllowanceState.ALLOWED : RegionConfig.AllowanceState.DENIED);
				
				// teleport players that are in the region, out of the region
				if (args[1].equals("deny-ENTER")) {
					
					RegionData tempRegionData = null;
					try {
						tempRegionData = main.regionConfig.getRegion(lastSelectedRegionName);
					} catch (NonExistentRegion e) {
						String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-player-no-region");
						main.helpers.sendMessage(player, configuredMessage);
						return true;
					}
					
					// check if online players are within the bounds of the region
					for (Player onlinePlayer_ : Bukkit.getOnlinePlayers()) {
						if (tempRegionData.isInside(onlinePlayer_.getLocation())) {
							main.helpers.teleportPlayerAroundRegion(onlinePlayer_, tempRegionData, 1, 10);
						}
					}
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-player-ok")
					.replace("{selected-player}", args[2])
					.replace("{region}", lastSelectedRegionName)
					.replace("{state-color}", args[1].equals("deny-ENTER") ? 
							ChatColor.RED + "DENIED" : ChatColor.GREEN + "ALLOWED");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("set-block")) {
				if (!player.hasPermission("blockrestriction.set-block")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.set-block"));
					return false;
				}
				
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-block-usage");
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("allow-block-breaking") && Arrays.asList("true", "false").contains(args[1])) {
				if (!player.hasPermission("blockrestriction.allow-block-breaking")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.allow-block-breaking"));
					return true;
				}
				
				// check for valid player
				List<String> combinedOfflineOnline = main.helpers.getAllPlayers();
				if (!combinedOfflineOnline.contains(args[2])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-breaking-invalid-player")
						.replace("{player}", args[2]);
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// check for current region selection state
				if (!main.regionSelectorsNames.playerIsInHashMap(player.getUniqueId()) || 
					main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId()) == null) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-breaking-no-region");
						main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				String lastSelectedRegionName = main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId());
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-breaking-ok")
					.replace("{state-color}", args[1].equals("true") ?
						ChatColor.GREEN + "ALLOW" : ChatColor.RED + "DENY")
					.replace("{region}", lastSelectedRegionName)
					.replace("{selected-player}", args[2]);
				
				boolean selectedState = Boolean.parseBoolean(args[1]);
				main.regionConfig.setPlayerBreaking(lastSelectedRegionName, args[2], 
						selectedState ? RegionConfig.AllowanceState.ALLOWED : RegionConfig.AllowanceState.DENIED);
				
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
			
			if (args[0].equals("allow-block-placing") && Arrays.asList("true", "false").contains(args[1])) {
				if (!player.hasPermission("blockrestriction.allow-block-placing")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.allow-block-placing"));
					return true;
				}
				
				// check for valid player
				List<String> combinedOfflineOnline = main.helpers.getAllPlayers();
				if (!combinedOfflineOnline.contains(args[2])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-placing-invalid-player")
						.replace("{player}", args[2]);
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// check for current region selection state
				if (!main.regionSelectorsNames.playerIsInHashMap(player.getUniqueId()) || 
					main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId()) == null) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-placing-no-region");
						main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				String lastSelectedRegionName = main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId());
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-allow-block-placing-ok")
					.replace("{state-color}", args[1].equals("true") ?
						ChatColor.GREEN + "ALLOW" : ChatColor.RED + "DENY")
					.replace("{region}", lastSelectedRegionName)
					.replace("{selected-player}", args[2]);
				
				boolean selectedState = Boolean.parseBoolean(args[1]);
				main.regionConfig.setPlayerPlacing(lastSelectedRegionName, args[2], 
						selectedState ? RegionConfig.AllowanceState.ALLOWED : RegionConfig.AllowanceState.DENIED);
				
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
		}
		
		if (args.length == 4) {
			
			if (args[0].equals("set-block")) {
				if (!player.hasPermission("blockrestriction.set-block")) {
					player.sendMessage(noPermissionMessage.replace("{permission}", "blockrestriction.set-block"));
					return false;
				}
				
				// invalid material
				if (!CachedData.getAllMaterialBlocks().contains(args[1])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-block-invalid-block")
						.replace("{block-type}", args[1]);
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// invalid action
				if (!Arrays.asList("allow-PLACE", "allow-BREAK", "deny-PLACE", "deny-BREAK").contains(args[2])) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-block-usage");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				
				// check for current region selection state
				if (!main.regionSelectorsNames.playerIsInHashMap(player.getUniqueId()) || 
					main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId()) == null) {
					String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-block-no-region");
					main.helpers.sendMessage(player, configuredMessage);
					return true;
				}
				String lastSelectedRegion = main.regionSelectorsNames.getPlayerLastRegionName(player.getUniqueId());
				
				// invalid player, @everyone support
				List<String> allPlayers = main.helpers.getAllPlayers();
				if (!allPlayers.contains(args[3])) {
				    String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-block-invalid-player")
				        .replace("{player}", args[3]);
				    main.helpers.sendMessage(player, configuredMessage);
				    return true;
				}
				
			    allPlayers.remove("@everyone");
			    
				switch (args[2]) {
					case "allow-PLACE":
						if (args[3].equals("@everyone")) {
				            for (String playerName : allPlayers)
				                main.regionConfig.setPlayerBlockState(lastSelectedRegion, playerName, 
			                		args[1], RegionConfig.AllowanceBlockState.ALLOW_PLACE);
				        } else {
				        	main.regionConfig.setPlayerBlockState(lastSelectedRegion, args[3], 
			                		args[1], RegionConfig.AllowanceBlockState.ALLOW_PLACE);
				        }
						break;
					case "deny-PLACE":
						if (args[3].equals("@everyone")) {
				            for (String playerName : allPlayers)
				            	 main.regionConfig.setPlayerBlockState(lastSelectedRegion, playerName, 
				                		args[1], RegionConfig.AllowanceBlockState.DENY_PLACE);
				        } else {
				        	main.regionConfig.setPlayerBlockState(lastSelectedRegion, args[3], 
		                		args[1], RegionConfig.AllowanceBlockState.DENY_PLACE);
				        }
						break;
					case "allow-BREAK":
						if (args[3].equals("@everyone")) {
				            for (String playerName : allPlayers)
				            	 main.regionConfig.setPlayerBlockState(lastSelectedRegion, playerName, 
				                		args[1], RegionConfig.AllowanceBlockState.ALLOW_BREAK);
				        } else {
				        	main.regionConfig.setPlayerBlockState(lastSelectedRegion, args[3], 
		                		args[1], RegionConfig.AllowanceBlockState.ALLOW_BREAK);
				        }
						break;
					case "deny-BREAK":
						if (args[3].equals("@everyone")) {
				            for (String playerName : allPlayers) 
				            	main.regionConfig.setPlayerBlockState(lastSelectedRegion, playerName, 
			                		args[1], RegionConfig.AllowanceBlockState.DENY_BREAK);
				        } else {
				        	main.regionConfig.setPlayerBlockState(lastSelectedRegion, args[3], 
		                		args[1], RegionConfig.AllowanceBlockState.DENY_BREAK);
				        }
						break;
					default:
						break;
				}
				
				// all OK
				String configuredMessage = main.defaultConfigSynchronizer.config.getString("messages.cmd-set-block-ok")
				    .replace("{state-color}", args[2].contains("allow-") ? ChatColor.GREEN + args[2] : ChatColor.RED + args[2])
				    .replace("{block}", args[1])
				    .replace("{region}", lastSelectedRegion)
				    .replace("{selected-player}", args[3]);
				main.helpers.sendMessage(player, configuredMessage);
				return true;
			}
		}
		return false;
	}
}