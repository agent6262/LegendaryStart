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
package com.gmail.tylerb318.events;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.gmail.tylerb318.LegendaryStart;
import com.gmail.tylerb318.LegendaryStartUtil;

/**
 * This class is designed to listen for player related events.
 * @author Tyler Bucher
 */
public class PlayerEventListener implements Listener {
    
    LegendaryStart mainClass = LegendaryStart.getPlugin(LegendaryStart.class);  // Main class

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event){
        final Player player = event.getPlayer();
        if(player.hasPermission("ls")) {
            playerJoinSetup(event);
            if(mainClass.config.getBoolean("LoginMessage")) {
                // Initial Setup
                final Long playerTime = mainClass.playerConfig.getLong(player.getName()+".LastChosenTime");
                final Long systemTime = System.currentTimeMillis();
                // Send Player message
                mainClass.getServer().getScheduler().scheduleSyncDelayedTask(mainClass,
                        new Runnable(){
                            @Override
                            public void run(){
                                long time = mainClass.config.getInt("LoginDelay")-(systemTime-playerTime);
                                long cTime = time>=0 ? time : 0;
                                player.sendMessage(ChatColor.RED+"you have to wait: "+LegendaryStartUtil.msToTime(cTime));
                            }
                        }, 20L);                
            }
        }
    }
    
    /**
     * Checks to see if a player exists or if his/her
     * name has changed.
     * @param event - The login event to check
     */
    private void playerJoinSetup(final PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        if(!mainClass.playerConfig.contains(uuid)) {
            mainClass.playerConfig.set(uuid, null);
            mainClass.playerConfig.set(uuid+".Name", player.getName());
            mainClass.playerConfig.set(uuid+".LastChosenTime", 0);
        } else if(!mainClass.playerConfig.getString(uuid+".Name").equals(player.getName())) {
            mainClass.playerConfig.set(uuid+".Name", player.getName());
        }
        try {
            mainClass.playerConfig.save(new File(mainClass.getDataFolder(), "players.yml"));
        } catch (IOException e) {
            mainClass.getLogger().severe("Failed to save players.yml: "+e.getMessage());
        }
    }
}
