package me.DuppyIsCool.WGBuildMode.Main;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin implements Listener {
	//Variables
	ConfigManager cfgm = new ConfigManager();
	ArrayList<UUID> playersInBuildMode = new ArrayList<UUID>();
	//On startup
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Enabling WGBuildMode...");
		//Setup Main instance
		Plugin.plugin = this;
		
		//Setup Config
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		cfgm.setup();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabled WGBuildMode"); 
		
		//Register events
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Disabling WGBuildMode...");
		//Save default config
		this.saveDefaultConfig();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Disabled WGBuildMode"); 
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Check if command 'build' is run
		if(command.getName().equalsIgnoreCase("build")) {
			//Prevent console/command blocks from executing command
			if(sender instanceof Player) {
				//Cast type to player
				Player p = (Player) sender;
				//Check if user has permission or is op
				if(p.hasPermission("WGBuildMode.build") || p.isOp()) {
					//Put the player in build mode if they are not
					if(!playersInBuildMode.contains(p.getUniqueId())) {
						playersInBuildMode.add(p.getUniqueId());
						sendMessage(p, cfgm.getEnterModeMessage(), cfgm.getEnterModeArea());
					}
					//Remove player from build mode if they are in it.
					else {
						playersInBuildMode.remove(p.getUniqueId());
						sendMessage(p,cfgm.getLeaveModeMessage(),cfgm.getLeaveModeArea());
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Only players may use this command");
				return true;
			}
		}
        return true;
    }
	
	@EventHandler
	public void blockBreakEvent(BlockBreakEvent e) {
		//If in build mode
		if(!playersInBuildMode.contains(e.getPlayer().getUniqueId()) && (e.getPlayer().hasPermission("WGBuildMode.build") || e.getPlayer().isOp())) {
			//If it's blocked everywhere, no need to check for regions therefore cancel event
			if(cfgm.isBlockEverywhere()) {
				e.setCancelled(true);
				return;
			}
			
			//Check if the block broken is only in whitelisted regions. If not, deny build
			else {
				//Setup WG regions
				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionManager regions = container.get(BukkitAdapter.adapt(e.getBlock().getLocation().getWorld()));
				ApplicableRegionSet regionsAtLocation = regions.getApplicableRegions(BukkitAdapter.asBlockVector((e.getBlock().getLocation())));
				//Loop through all regions at location
				boolean canBuild = true;
				for(ProtectedRegion region: regionsAtLocation) {
					if(!cfgm.getWhitelistedRegions().isEmpty())
						if(cfgm.getWhitelistedRegions().contains(region.getId())) {
							canBuild = true;
						}
						else {
							canBuild = false;
							break;
						}
				}
				
				//Deny build if location is not in a whitelisted region
				if(!canBuild) {
					e.setCancelled(true);
					sendMessage(e.getPlayer(), cfgm.getDenyBuildMessage(), cfgm.getDenyBuildArea());
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent e) {
		//If in build mode
		if(!playersInBuildMode.contains(e.getPlayer().getUniqueId()) && (e.getPlayer().hasPermission("WGBuildMode.build") || e.getPlayer().isOp())) {
			//If it's blocked everywhere, no need to check for regions therefore cancel event
			if(cfgm.isBlockEverywhere()) {
				e.setCancelled(true);
				return;
			}
			
			//Check if the block placed is only in whitelisted regions. If not, deny build
			else {
				//Setup WG regions
				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionManager regions = container.get(BukkitAdapter.adapt(e.getBlockPlaced().getLocation().getWorld()));
				ApplicableRegionSet regionsAtLocation = regions.getApplicableRegions(BukkitAdapter.asBlockVector((e.getBlock().getLocation())));
				//Loop through all regions at location
				boolean canBuild = true;
				for(ProtectedRegion region: regionsAtLocation) {
					if(!cfgm.getWhitelistedRegions().isEmpty())
						if(cfgm.getWhitelistedRegions().contains(region.getId()))
							canBuild = true;
						else {
							canBuild = false;
							break;
						}
				}
				
				//Deny build if location is not in a whitelisted region
				if(!canBuild) {
					e.setCancelled(true);
					sendMessage(e.getPlayer(), cfgm.getDenyBuildMessage(), cfgm.getDenyBuildArea());
					return;
				}
			}
		}
	}
	
	public void sendMessage(Player p, String message, String area) {
		if(message.length() > 0)
			switch(area) {
				case "TITLE":
					p.sendTitle(message, "", 5, 20, 5);
					break;
				case "SUBTITLE":
					p.sendTitle("", message, 5, 20, 5);
					break;
				case "CHAT":
					p.sendMessage(message);
					break;
				case "ACTION_BAR":
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
					break;
			}
	}
}
