package me.DuppyIsCool.WGBuildMode.Main;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

public class ConfigManager {
	//Begin Variables
	private String enterModeMessage,denyBuildMessage,leaveModeMessage,enterModeArea,denyBuildArea,leaveModeArea;
	private boolean blockEverywhere;
	private ArrayList<String> allowedAreas = new ArrayList<String>(Arrays.asList("ACTION_BAR","TITLE","SUBTITLE","CHAT"));
	private ArrayList<String> whitelistedRegions = new ArrayList<String>();
	//End Variables
	
	//Begin setup code
	public void setup() {
		//Setup config object
		FileConfiguration  config = Plugin.plugin.getConfig();
		
		//Try to grab variables from config
		//Messages
		enterModeMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.enterModeMessage"));
		leaveModeMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.leaveModeMessage"));
		denyBuildMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.denyBuildMessage"));
		
		//Areas
		
		//Enter mode Area
		if(config.contains("area.enterModeArea")) {
			if(allowedAreas.contains(config.getString("area.enterModeArea").toUpperCase()))
				enterModeArea = config.getString("area.enterModeArea").toUpperCase();
			else {
				Bukkit.getConsoleSender().sendMessage(
						ChatColor.RED + "Invalid message area for 'enterModeMessage': "+config.getString("area.enterModeArea") + 
						ChatColor.RED+"\nUsing default values");
				enterModeArea = "ACTION_BAR";
			}
		}
		else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error getting value for enterModeArea due to invalid path");
		}
		
		//Leave mode Area
		if(config.contains("area.leaveModeArea")) {
			if(allowedAreas.contains(config.getString("area.leaveModeArea").toUpperCase()))
				leaveModeArea = config.getString("area.leaveModeArea").toUpperCase();
			else {
				Bukkit.getConsoleSender().sendMessage(
						ChatColor.RED + "Invalid message area for 'leaveModeMessage': "+config.getString("area.leaveModeArea") + 
						ChatColor.RED+"\nUsing default values");
				leaveModeArea = "ACTION_BAR";
			}
		}
		else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error getting value for leaveModeArea due to invalid path");
		}
		
		//Deny build Area
		if(config.contains("area.denyBuildArea")) {
			if(allowedAreas.contains(config.getString("area.denyBuildArea").toUpperCase()))
				denyBuildArea = config.getString("area.denyBuildArea").toUpperCase();
			else {
				Bukkit.getConsoleSender().sendMessage(
						ChatColor.RED + "Invalid message area for 'denyBuildMessage': "+config.getString("area.denyBuildArea") + 
						ChatColor.RED+"\nUsing default values");
				denyBuildArea = "ACTION_BAR";
			}
		}
		else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error getting value for denyBuildArea due to invalid path");
		}
		
		//End Areas
		
		//WorldGuard Settings'
		if(config.contains("settings.blockEverywhere"))
			blockEverywhere = config.getBoolean("settings.blockEverywhere");
		else
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error getting value for blockEverywhere due to invalid path");
		
		if(config.contains("settings.blockEverywhere"))
			whitelistedRegions = (ArrayList<String>) config.getStringList("settings.whitelistedRegions");
		else
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error getting value for whitelistedRegions due to invalid path");
	}
	//End setup code
	
	//Begin getters
	public String getEnterModeMessage() {
		return enterModeMessage;
	}
	public String getDenyBuildMessage() {
		return denyBuildMessage;
	}
	public String getLeaveModeMessage() {
		return leaveModeMessage;
	}
	public String getEnterModeArea() {
		return enterModeArea;
	}
	public String getDenyBuildArea() {
		return denyBuildArea;
	}
	public String getLeaveModeArea() {
		return leaveModeArea;
	}
	public ArrayList<String> getWhitelistedRegions() {
		return whitelistedRegions;
	}
	public boolean isBlockEverywhere() {
		return blockEverywhere;
	}
	//End getters
}
