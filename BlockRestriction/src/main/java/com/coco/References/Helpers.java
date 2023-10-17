package com.coco.References;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.coco.Classes.RegionData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Helpers {

	/**
	 * Constructs a new Helpers object
	 */
	public Helpers() { }

	/**
	 * Colorizes a text string by replacing color codes with their respective ChatColor values.
	 *
	 * @param text the text to colorize
	 * @return the colorized text
	 */
	@Nonnull
	public final String colorizeText(final String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public void teleportPlayerAroundRegion(Player player, RegionData regionData, int minRange, int maxRange) {
	    World world = player.getWorld();

	    int minX = regionData.getMinX();
	    int minZ = regionData.getMinZ();
	    int maxX = regionData.getMaxX();
	    int maxZ = regionData.getMaxZ();

	    int randomX, randomZ;
	    
	    // calculate x
	    if (Math.random() < 0.5) {
	        randomX = getRandomNumberInRange(minX - maxRange, minX - minRange);
	    } else {
	        randomX = getRandomNumberInRange(maxX + minRange, maxX + maxRange);
	    }

	    // calculate z
	    if (Math.random() < 0.5) {
	        randomZ = getRandomNumberInRange(minZ - maxRange, minZ - minRange);
	    } else {
	        randomZ = getRandomNumberInRange(maxZ + minRange, maxZ + maxRange);
	    }

	    // Find the highest solid block at the selected X-Z location
	    int highestY = world.getHighestBlockYAt(randomX, randomZ);

	    double teleportX = randomX + 0.5;
	    double teleportY = highestY + 1;
	    double teleportZ = randomZ + 0.5;

	    player.teleport(new Location(world, teleportX, teleportY, teleportZ));
	}

	private int getRandomNumberInRange(int min, int max) {
	    return min + (int) (Math.random() * ((max - min) + 1));
	}
	
	public void spawnVerticalCornerParticles(Location location1, Location location2, Particle particle, int count) {
		World world = location1.getWorld();

	    // Get the border locations
	    double minX = Math.min(location1.getX(), location2.getX());
	    double maxX = Math.max(location1.getX(), location2.getX());
	    double minY = Math.min(location1.getY(), location2.getY());
	    double maxY = Math.max(location1.getY(), location2.getY());
	    double minZ = Math.min(location1.getZ(), location2.getZ());
	    double maxZ = Math.max(location1.getZ(), location2.getZ());

	    // Spawn particles at the vertical borders
	    for (double y = minY; y <= maxY; y += 0.5) {
	    	Location border1 = new Location(world, minX + 0.5, y + 0.5, minZ + 0.5);
	        Location border2 = new Location(world, maxX + 0.5, y + 0.5, minZ + 0.5);
	        Location border3 = new Location(world, minX + 0.5, y + 0.5, maxZ + 0.5);
	        Location border4 = new Location(world, maxX + 0.5, y + 0.5, maxZ + 0.5);

	        world.spawnParticle(particle, border1, count);
	        world.spawnParticle(particle, border2, count);
	        world.spawnParticle(particle, border3, count);
	        world.spawnParticle(particle, border4, count);
	    }
	}
	
	public final List<String> getAllPlayers() {
		List<String> combinedOfflineOnline = new ArrayList<>();
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		if (offlinePlayers != null && offlinePlayers.length > 0)
			for (OfflinePlayer oP : offlinePlayers)
				if (!combinedOfflineOnline.contains(oP.getName()))
					combinedOfflineOnline.add(oP.getName());
		for (Player pL : Bukkit.getOnlinePlayers())
			if (!combinedOfflineOnline.contains(pL.getName()))
				combinedOfflineOnline.add(pL.getName());
			
		// support @everyone tag, tags everyone offline+online
		combinedOfflineOnline.add("@everyone");
		
		return combinedOfflineOnline;
	}
	
	/**
	 * Checks if a string contains any symbols other than letters or digits.
	 *
	 * @param inputStr the input string to check
	 * @return true if the string contains symbols, false otherwise
	 */
	@Nonnull
	public final boolean stringContainsSymbols(final String inputStr) {
		for (char c : inputStr.toCharArray())
			if (!Character.isLetterOrDigit(c))
				return true;
		return false;
	}
	
	/**
	 * Sends a formatted message to a player.
	 *
	 * @param player the player to send the message to
	 * @param text the text of the message
	 */
	@Nonnull
	public final void sendMessage(final Player player, final String text) {
		if (text == null || text.length() == 0)
			return;
		String[] optionalLines = getNiceChatLineConfig(text);
		player.sendMessage(colorizeText(convertColors(text.replace(optionalLines[1], optionalLines[0]))));
	}
	
	public boolean isPlayerFacingBlock(Player player, Location blockLocation) {
        Vector playerDirection = player.getLocation().getDirection();
        Vector playerToBlock = blockLocation.toVector().subtract(player.getLocation().toVector()).normalize();

        double dotProduct = playerDirection.dot(playerToBlock);
        return !(dotProduct > 0); // True if player is facing towards the block, false otherwise
    }
	
	public void bouncePlayer(Player player, Location blockLocation, double strength) {
		Vector playerDirection = player.getLocation().getDirection();
        Vector bounceDirection = blockLocation.toVector().subtract(player.getLocation().toVector()).normalize();
        Bukkit.broadcastMessage(org.bukkit.ChatColor.GREEN + "y: " + bounceDirection.getY());
        
        double dotProduct = playerDirection.dot(bounceDirection);
        if (dotProduct > 0) {
            bounceDirection.multiply(-1); // Reverse the bounce direction if player is facing towards the block
        }

        Vector velocity = bounceDirection.multiply(strength);
        player.setVelocity(velocity);
    }
	
	 /**
	 * Retrieves the configuration for a nice chat line.
	  *
	  * @param confString the configuration string
	  * @return an array containing the symbol repeated x times and the original string
	  */
	@Nonnull
	public final String[] getNiceChatLineConfig(final String confString) {
		
		if (!confString.contains("{line:"))
			return new String [] { "", "" };
		try {
			int indexStartString = confString.indexOf("{line:"), 
				indexEndString = 0;
			String foundLineString = "";
			boolean countSkipC = false;
			
			// extract the string
			for (int i = indexStartString; i < confString.length(); i++) {
				if (confString.charAt(i) != '}' && !countSkipC) {
					foundLineString += confString.charAt(i);
					indexEndString = i;
				}	
				if (confString.charAt(i) == '}') {
					foundLineString += confString.charAt(i);
					indexEndString = i;
					countSkipC = true;
				}
			}
			
			// reformat the string, take out integers, validate
			String[] foundLineStringFormatted = foundLineString.replace("{", "").replace("}", "").split(":");
			int lineLength = Integer.parseInt(foundLineStringFormatted[1]);
			if (lineLength <= 0)
				return new String [] { "", "" };
			
			String symbolCombined = ChatColor.STRIKETHROUGH + "";
			for (int i = 0; i < lineLength; i++)
				symbolCombined += " ";
			
			// [0] the result, symbol repeated x times
			// [1] original string that needs to be replaced
			return new String[] {symbolCombined, foundLineString};
		} catch(Exception e) {
			return new String [] { "", "" };
		}
	}
	
	/**
	 * Converts color codes in a string to their respective ChatColor values using regular expressions.
	 *
	 * @param lineString the string to convert
	 * @return the converted string with color codes replaced
	 */
	@Nonnull
	public final String convertColors(String lineString) {
		
		Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
		Matcher match = pattern.matcher(lineString);
		while (match.find()) {
			String color = lineString.substring(match.start(), match.end());
			lineString = lineString.replace(color, String.valueOf(ChatColor.of(color)));
			match = pattern.matcher(lineString);
		}
		return lineString;
	}
}
