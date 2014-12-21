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

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import com.gmail.tylerb318.LegendaryStart;


/**
 * This class is designed to be used for the single command use of "legendarystartreload".
 * @author Tyler Bucher
 */
public class LSCommand implements CommandExecutor {
    LegendaryStart mainClass = LegendaryStart.getPlugin(LegendaryStart.class);  // main class
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String ailis, String[] args) {
        if(cmd.getName().equalsIgnoreCase("legendarystartreload") && args.length==0) {
            try {
                mainClass.config.load(new File(mainClass.getDataFolder(), "config.yml"));
                mainClass.playerConfig.save(new File(mainClass.getDataFolder(), "players.yml"));
                mainClass.playerConfig.load(new File(mainClass.getDataFolder(), "players.yml"));
            } catch (IOException | InvalidConfigurationException e) {
                sender.sendMessage(ChatColor.RED+"Reload Error");
                mainClass.getLogger().severe("Reload Error: "+e.getMessage());
            }
            return true;
        }
        return false;
    }
    
}
