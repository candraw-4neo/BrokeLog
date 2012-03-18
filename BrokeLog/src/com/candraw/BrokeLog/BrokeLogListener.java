package com.candraw.BrokeLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BrokeLogListener implements Listener {
	
	String root = "plugins/BrokeLog/";
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (!e.isCancelled()) {
			
			Player p = e.getPlayer();
			Calendar now = Calendar.getInstance();
			BrokeLog.log(now.get(11) + ":" + now.get(12) + " " + p.getName() + " broke " + e.getBlock().getTypeId() + " @" + e.getBlock().getX() + ";" + e.getBlock().getY() + ";" + e.getBlock().getZ());
			
			try {
				BufferedWriter bwriter = new BufferedWriter(new FileWriter(root + "log." + p.getWorld().getName() + ".txt", true));
				bwriter.write(now.get(11) + ":" + now.get(12) + "/" + now.get(5) + "/" + (now.get(2) + 1) + "/" + now.get(1) + "." + p.getName() + ".broke." + e.getBlock().getTypeId() + "." + e.getBlock().getX() + ":" + e.getBlock().getY() + ":" + e.getBlock().getZ());
				bwriter.newLine();
				bwriter.close();
			} catch (Exception ex) {
				BrokeLog.log("Could not write to File!");
				ex.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!e.isCancelled()) {
			
			Player p = e.getPlayer();
			Calendar now = Calendar.getInstance();
			BrokeLog.log(now.get(11) + ":" + now.get(12) + " " + p.getName() + " placed " + e.getBlock().getTypeId() + " @" + e.getBlock().getX() + ";" + e.getBlock().getY() + ";" + e.getBlock().getZ());
			
			try {
				BufferedWriter bwriter = new BufferedWriter(new FileWriter(root + "log." + p.getWorld().getName() + ".txt", true));
				bwriter.write(now.get(11) + ":" + now.get(12) + "/" + now.get(5) + "/" + (now.get(2) + 1) + "/" + now.get(1) + "." + p.getName() + ".placed." + e.getBlock().getTypeId() + "." + e.getBlock().getX() + ":" + e.getBlock().getY() + ":" + e.getBlock().getZ());
				bwriter.newLine();
				bwriter.close();
			} catch (Exception ex) {
				BrokeLog.log("Could not write to File!\n");
				ex.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction().name().equals("RIGHT_CLICK_BLOCK")) {
			if (BrokeLog.currCheckUsers.contains(e.getPlayer())) {
				try {
					BufferedReader breader = new BufferedReader(new FileReader(root + "log." + e.getPlayer().getWorld().getName() + ".txt"));
					String currLine;
					List<String> lines = new ArrayList<String>();
					String blockMessage = "Block is not listed.";
					String clickedBlockCoord = e.getClickedBlock().getX() + ":" + e.getClickedBlock().getY() + ":" + e.getClickedBlock().getZ();
					
					while ((currLine = breader.readLine()) != null) {
						lines.add(currLine);
					}
					
					for (int i=lines.size() - 1; i>=0; i--) {
						if (lines.get(i).split("\\.")[2].equals("placed")) {
							if (clickedBlockCoord.equals(lines.get(i).split("\\.")[4])) {
								blockMessage = "Block was placed by: " + lines.get(i).split("\\.")[1];
								break;
							}
						}
					}
					
					e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "[BrokeLog] " + ChatColor.GREEN + blockMessage);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
}
