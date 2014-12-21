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
package com.gmail.tylerb318;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.tylerb318.commands.LSClaimCommand;
import com.gmail.tylerb318.commands.LSCommand;
import com.gmail.tylerb318.events.PlayerEventListener;

/**
 * This class is the main Plugin class for Legendary Start 2
 * @author Tyler Bucher
 */
public class LegendaryStart extends JavaPlugin{
    
    public YamlConfiguration itemsConfig;   //Temporary YAmlConfiguration for creating the files
    public FileConfiguration config;        //Configuration file for the configuration
    public YamlConfiguration playerConfig;  //Configuration file that loads the players that use this plugin
    
	@Override
	public void onEnable(){
	    // Load the configurations
		loadConfig();
		// Setup Commands
		this.getCommand("legendarystartclaim").setExecutor(new LSClaimCommand());
		this.getCommand("legendarystartreload").setExecutor(new LSCommand());
		// Setup Events
		this.getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
		// Setup Inventory
		LegendaryInventory.init();
	}
	
	@Override
	public void onDisable(){
	    // Save the player configuration
		try {
            playerConfig.save(new File(this.getDataFolder(), "players.yml"));
        } catch (IOException e) {
            this.getLogger().severe("Player file failed to save: "+e.getMessage());
        }
	}
	
	/**
	 * Loads all of the configurations in the jarchive.
	 */
	public void loadConfig(){
		try{
		    //Load &| create the configuration
            File configFile = new File(this.getDataFolder(), "config.yml");
            config = YamlConfiguration.loadConfiguration(configFile);
            if(!configFile.exists()){
                this.saveDefaultConfig();
            }
            config.load(configFile);
            //Load &| create the items sets
            File setsDir = new File(this.getDataFolder(), "Inventory Sets");
            if(!setsDir.exists()){
                setsDir.mkdir();
                itemsConfig = YamlConfiguration.loadConfiguration(new File(setsDir, "set1.yml"));
                itemsConfig.load(new InputStreamReader(this.getResource("set1.yml")));
                itemsConfig.options().copyDefaults(true);
                itemsConfig.save(new File(setsDir, "set1.yml"));
                itemsConfig = YamlConfiguration.loadConfiguration(new File(setsDir, "set2.yml"));
                itemsConfig.load(new InputStreamReader(this.getResource("set2.yml")));
                itemsConfig.options().copyDefaults(true);
                itemsConfig.save(new File(setsDir, "set2.yml"));
            }
            //Load &| create playerConfig
            File playersFile = new File(this.getDataFolder(), "players.yml");
            playerConfig = YamlConfiguration.loadConfiguration(playersFile);
            if(!playersFile.exists()){
                playerConfig.save(playersFile);
            }
            playerConfig.load(playersFile);
		}catch(IOException | InvalidConfigurationException e){
		    this.getLogger().severe("Legendary Start could not load the configuration file(s) for reason: "+e.getMessage());
		}
	}
}
