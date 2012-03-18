package com.candraw.BrokeLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BrokeLog extends JavaPlugin {
	
	public Server server = this.getServer();
	public static Logger log = Logger.getLogger("Minecraft");
	public PluginDescriptionFile info;
	String root = "plugins/BrokeLog/";
	public static List<Player> currCheckUsers = new ArrayList<Player>();
	
	public BrokeLogListener bl = new BrokeLogListener();
	
	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(bl, this);
		
		checkFiles();
		
		info = this.getDescription();
		log("Plugin enabled. Version: " + info.getVersion());
	}
	
	public void onDisable() {
		log("Plugin disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (cmd.getName().equalsIgnoreCase("brokeLog")) {
			sender.sendMessage(ChatColor.DARK_GREEN + "===== BrokeLog =====");
			sender.sendMessage(ChatColor.GRAY + "Version: " + ChatColor.GOLD + info.getVersion());
			sender.sendMessage(ChatColor.DARK_GREEN + "/brokeLog    " + ChatColor.GREEN + "View help");
			sender.sendMessage(ChatColor.DARK_GREEN + "/wholeList    " + ChatColor.GREEN + "View all BlockEvents in a specific world");
			sender.sendMessage(ChatColor.DARK_GREEN + "/timeList    " + ChatColor.GREEN + "View all BlockEvents after a specific time");
			sender.sendMessage(ChatColor.DARK_GREEN + "/checkBlock    " + ChatColor.GREEN + "Activate checkBlock-Mode");
		}
		
		if (cmd.getName().equalsIgnoreCase("wholeList")) {
			if (args.length == 1) {
				sender.sendMessage(ChatColor.DARK_GREEN + "[BrokeLog] " + ChatColor.GREEN + "BlockEvents in world " + args[0]);
				this.wholeList(sender, args[0]);
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "[BrokeLog] " + ChatColor.RED + "SyntaxError! Usage: /" + commandLabel + " [world]");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("timeList")) {
			if (args.length == 6) {
				sender.sendMessage(ChatColor.DARK_GREEN + "[BrokeLog] " + ChatColor.GREEN + "BlockEvents in world " + args[0]);
				this.timeList(sender, args[0], args);
			} else if (args.length == 5 && sender instanceof Player) {
				sender.sendMessage(ChatColor.DARK_GREEN + "[BrokeLog] " + ChatColor.GREEN + "BlockEvents in world " + ((Player)sender).getWorld().getName());
				this.timeList(sender, ((Player)sender).getWorld().getName(), new String[] {null, args[0], args[1], args[2], args[3], args[4]});
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "[BrokeLog] " + ChatColor.RED + "SyntaxError! Usage: /" + commandLabel + " [world] <hour> <minute> <day> <month> <year>");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("checkBlock")) {
			if (sender instanceof Player) {
				if (!currCheckUsers.contains((Player) sender)) {
					currCheckUsers.add((Player) sender);
					sender.sendMessage(ChatColor.DARK_GREEN + "[BrokeLog] " + ChatColor.GREEN + "CheckBlock-Mode activated. Repeat command to deactivate.");
				} else {
					currCheckUsers.remove((Player) sender);
					sender.sendMessage(ChatColor.DARK_GREEN + "[BrokeLog] " + ChatColor.GREEN + "CheckBlock-Mode deactivated.");
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("blockRoll")) {
			if (args.length == 7) {
				this.rollback(sender, args[0], args[1], new String[] {args[2], args[3], args[4], args[5], args[6]});
				sender.sendMessage(ChatColor.DARK_GREEN + "[BrokeLog] " + ChatColor.GREEN + "Rollback succesfull");
			} else if (args.length == 6 && sender instanceof Player) {
				this.rollback(sender, args[0], ((Player)sender).getWorld().getName(), new String[] {args[1], args[2], args[3], args[4], args[5]});
				sender.sendMessage(ChatColor.DARK_GREEN + "[BrokeLog] " + ChatColor.GREEN + "Rollback succesfull");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "[BrokeLog] " + ChatColor.RED + "SyntaxError! Usage: /" + commandLabel + " <player> [world] <hour> <minute> <day> <month> <year>");
			}
		}
		
		return true;
	}
	
	public static void log(String text) {
		log.info("[BrokeLog] " + text);
	}
	
	public void checkFiles() {
		
		File dir = new File(root);
		if (!dir.exists()) dir.mkdir();
		
		List<?> worlds = null;
		worlds = Bukkit.getWorlds();
		for (int i = 0; i < worlds.size(); i++) {
			File f = new File("plugins/BrokeLog/log." + ((World) worlds.get(i)).getName() + ".txt");
			
			if (f.exists()) {
				log("File " + f.getName() + " found.");
			} else {
				log("File " + f.getName() + " could not be found. Createing it.");
				
				try {
					f.createNewFile();
				} catch (Exception e) {
					log("Failed creating the File!");
				}
			}
		}
		
	}
	
	public void wholeList(CommandSender sender, String world) {
		File f = new File(root + "log." + world + ".txt");
		
		if (f.exists()) {
			try {
				BufferedReader breader = new BufferedReader(new FileReader(f));
				String currLine;
				while ((currLine = breader.readLine()) != null) {
					sender.sendMessage(ChatColor.GREEN + currLine);
				}
			} catch (Exception e) {
				
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "[BrokeLog] " + ChatColor.RED + "World doesn't exist.");
		}
	}
	
	public void timeList(CommandSender sender, String world, String[] args) {
		File f = new File(root + "log." + world + ".txt");
		
		if (f.exists()) {
			try {
				BufferedReader breader = new BufferedReader(new FileReader(f));
				String currLine;
				while ((currLine = breader.readLine()) != null) {
					String timeString = currLine.split("\\.")[0];
					String[] time = {args[1], args[2], args[3], args[4], args[5]};
					
					if (timeBefore(timeString, time)) {
						sender.sendMessage(ChatColor.GREEN + currLine);
					}
				}
			} catch (Exception e) { }
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "[BrokeLog] " + ChatColor.RED + "World doesn't exist.");
		}
	}
	
	public void rollback(CommandSender sender, String player, String world, String[] time) {
		try {
			
			BufferedReader breader = new BufferedReader(new FileReader(root + "log." + world + ".txt"));
			String currLine;
			List<String> lines = new ArrayList<String>();
			World w = Bukkit.getWorld(world);
			
			while((currLine = breader.readLine()) != null) {
				lines.add(currLine);
			}
			
			for (int i = lines.size() - 1; i >= 0; i--) {
				currLine = lines.get(i);
				String[] parts = currLine.split("\\."); 
				if (this.timeBefore(parts[0], time)) {
					
					if (parts[1].equalsIgnoreCase(player)) {
						if (parts[2].equals("broke")) {
							w.getBlockAt(Integer.parseInt(parts[4].split(":")[0]),
									Integer.parseInt(parts[4].split(":")[1]),Integer.parseInt(parts[4].split(":")[2])).setTypeId(Integer.parseInt(parts[3]));
						} else if (parts[2].equals("placed")) {
							w.getBlockAt(Integer.parseInt(parts[4].split(":")[0]),
									Integer.parseInt(parts[4].split(":")[1]), Integer.parseInt(parts[4].split(":")[2])).setTypeId(0);
						}
						this.removeLineFromFile(root + "log." + world + ".txt", currLine);
					} 
				} else {
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeLineFromFile(String file, String lineToRemove) {
		
		try {
			
			String currLine;
			File f = new File(file);
			List<String> lines = new ArrayList<String>();
			BufferedReader breader = new BufferedReader(new FileReader(f));
			BufferedWriter bwriter = new BufferedWriter(new FileWriter(f));
			
			while ((currLine = breader.readLine()) != null) {
				if (currLine != lineToRemove) {
					lines.add(currLine);
				}
			}
			
			f.delete();
			f.createNewFile();
			
			for (int i = 0; i < lines.size(); i++) {
				bwriter.write(lines.get(i));
			}
			
			breader.close();
			bwriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	  }
	
	public boolean timeBefore(String timeString, String[] time) {
		
		String[] eventTime = timeString.split("/");
		String[] eventTimeHM = eventTime[0].split(":");
		
		if (Integer.parseInt(eventTime[3]) > Integer.parseInt(time[4])) {
			return true;
		} else if (Integer.parseInt(eventTime[3]) == Integer.parseInt(time[4])) {
			if (Integer.parseInt(eventTime[2]) > Integer.parseInt(time[3])) {
				return true;
			} else if (Integer.parseInt(eventTime[2]) == Integer.parseInt(time[3])) {
				if (Integer.parseInt(eventTime[1]) > Integer.parseInt(time[2])) {
					return true;
				} else if (Integer.parseInt(eventTime[1]) == Integer.parseInt(time[2])) {
					if (Integer.parseInt(eventTimeHM[0]) > Integer.parseInt(time[0])) {
						return true;
					} else if (Integer.parseInt(eventTimeHM[0]) == Integer.parseInt(time[0])) {
						if (Integer.parseInt(eventTimeHM[1]) >= Integer.parseInt(time[1])) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}	
}
