package com.gmail.tylerb318;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public class LegendaryStartCommandExecutor implements CommandExecutor{
	
	private static LegendaryStart mainClass = LegendaryStart.getPlugin(LegendaryStart.class);
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String ailis, String[] args){
		if(cmd.getName().equalsIgnoreCase("legendarystartreload") && args.length == 0){
				try{
					ArrayList<UUID> openInven = new ArrayList<UUID>();
					UUID[] tmp = new UUID[0]; tmp = mainClass.playerMap.keySet().toArray(tmp);
					for(UUID e : tmp){
						if(Bukkit.getPlayer(e).getOpenInventory().getTopInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', mainClass.config.getString("InventoryName"))) || 
								Bukkit.getPlayer(e).getOpenInventory().getTopInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', mainClass.config.getString("RandomInventoryName")))){
							openInven.add(e);
							Bukkit.getPlayer(e).closeInventory();
						}
					}
					mainClass.playerMap.clear();
					
					mainClass.config.load(new File(mainClass.getDataFolder(), "config.yml"));
					mainClass.playerConfig.save(new File(mainClass.getDataFolder(), "players.yml"));
					mainClass.playerConfig.load(new File(mainClass.getDataFolder(), "players.yml"));
					
					for(UUID e : tmp){
						mainClass.playerMap.put(e, new LegendaryInventory());
					}
					for(UUID e : openInven)
						Bukkit.getPlayer(e).sendMessage("Your inventory was closed to insure that your data does not get correpted durring a plugin reload.");
						
				} catch (IOException | InvalidConfigurationException e){
					sender.sendMessage(ChatColor.RED+"Configuration file error");
					return true;
				}
				sender.sendMessage(ChatColor.GREEN+"Configurations reloaded");
				return true;
		}
		if(cmd.getName().equalsIgnoreCase("legendarystartmenu") && args.length <= 1){//FIXME
			if(sender instanceof Player){
				final Player player = (Player)sender;
				if(mainClass.playerMap.containsKey(player.getUniqueId())){
					if(args.length == 0){
						if(player.hasPermission("ls.menu.original") || player.isOp()){
							player.openInventory(mainClass.playerMap.get(player.getUniqueId()).getInventory());
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("random")){
						if(player.hasPermission("ls.menu.random") || player.isOp()){
							player.openInventory(mainClass.playerMap.get(player.getUniqueId()).getRandomInventory());
							mainClass.threadMap.put(player.getUniqueId(), new Thread(new Runnable(){
								@Override
								public void run(){
									UUID tmpUUID = player.getUniqueId();
									while(!Thread.interrupted()){
										mainClass.playerMap.get(tmpUUID).randItemplus();
									}
								}
							}));
							mainClass.threadMap.get(player.getUniqueId()).start();
							return true;
						}
					}
				}
				else{
					player.sendMessage(ChatColor.RED+"It appears that you have all ready chosen your login reward. "+
													"If you believe this is an error please contact your server administrator.");
					return true;
				}
			}
			else{
				sender.sendMessage("You have to be a player to use this command");
				return true;
			}
			
		}
		return false;
	}
	
}
