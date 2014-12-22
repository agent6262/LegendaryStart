/**
 * AJGL, an abstract java game library that provides useful functions for making a game.
 * Copyright (C) 2014 Tyler Bucher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.tylerb318.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.tylerb318.LegendaryInventory;
import com.gmail.tylerb318.LegendaryStart;
import com.gmail.tylerb318.LegendaryStartUtil;


/**
 * This class is designed to be used for the single command use of "legendarystartclaim".
 * @author Tyler Bucher
 */
public class LSClaimCommand implements CommandExecutor {
    LegendaryStart mainClass = LegendaryStart.getPlugin(LegendaryStart.class);  // Main class
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String ailis, String[] args) {
        if(cmd.getName().equalsIgnoreCase("legendarystartclaim") && args.length==0) {
            if(sender instanceof Player){
                final Player player = (Player) sender;
                // Initial Setup
                String uuid = player.getUniqueId().toString();
                Long playerTime = mainClass.playerConfig.getLong(uuid+"."+player.getName()+".LastChosenTime");
                Long systemTime = System.currentTimeMillis();
                // Check to see if player can get a new item
                if(systemTime-playerTime>=mainClass.config.getInt("LoginDelay")){
                    // Inventory sizes
                    int amountOfSets = LegendaryInventory.getInventoryArray().size();
                    // Create the new item and inventory
                    ArrayList<ItemStack> itemsToGet = new ArrayList<>();
                    for(int i=0; i<mainClass.config.getInt("NumberOfItems");i++) {
                        int randSet = new Random().nextInt(amountOfSets-1);
                        int randSetSize = LegendaryInventory.getInventoryArray().get(randSet).size();
                        itemsToGet.add(LegendaryInventory.getInventoryArray().get(randSet).get(new Random().nextInt(randSetSize-1)));
                    }
                    final Inventory rewardInventory = Bukkit.createInventory(player, (int) (Math.ceil(itemsToGet.size()/9.0)*9.0), ChatColor.translateAlternateColorCodes('&', mainClass.config.getString("InventoryName")));
                    for(int i=0; i<itemsToGet.size();i++) {
                        rewardInventory.setItem(i, itemsToGet.get(i));
                    }
                    // open inventory
                    mainClass.getServer().getScheduler().scheduleSyncDelayedTask(mainClass,
                            new Runnable(){
                                @Override
                                public void run(){
                                    player.openInventory(rewardInventory);
                                }
                            }, 5L);
                    mainClass.playerConfig.set(uuid+"."+player.getName()+".LastChosenTime", System.currentTimeMillis());
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED+"you have to wait: "+LegendaryStartUtil.msToTime(mainClass.config.getInt("LoginDelay")-(systemTime-playerTime)));
                    return true; 
                }
            } else {
                sender.sendMessage("You have to be a Player to use this command.");
                return true;
            }
        }
        
        return false;
    }
    
}
