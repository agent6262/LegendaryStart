package com.gmail.tylerb318;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LegendaryStart extends JavaPlugin
{
	public YamlConfiguration itemsConfig;						//Temporary YAmlConfiguration for creating the files
	public FileConfiguration config;							//Configuration file for the config
	public YamlConfiguration playerConfig;						//Configuration file that loads the players that use this plugin
	public Map<UUID, LegendaryInventory> playerMap;				//The HasMap that dictates who has what inventory
	public Map<UUID, Thread> threadMap;							//Used only for the random inventory
	
	private LegendaryStartCommandExecutor legendaryCommand;		//Private LegendaryStartCommandExecutor for use in registering commands
	private LegendaryStartEventListener legendaryListener;		//Private LegendaryStartEventListenerfor use in registering events
	
	/**
	 * 
	 * Loads everything when the plugin is loaded
	 */
	@Override
	public void onEnable(){
		//Load initial variables
		playerMap = new HashMap<UUID, LegendaryInventory>();
		threadMap = new HashMap<UUID, Thread>();
		
		legendaryCommand = new LegendaryStartCommandExecutor();
		legendaryListener = new LegendaryStartEventListener();
		//loadConfig
		this.loadConfig();
		//load Commands
		this.getCommand("legendarystartreload").setExecutor(legendaryCommand);
		this.getCommand("legendarystartmenu").setExecutor(legendaryCommand);
		//LoadEvents
		this.getServer().getPluginManager().registerEvents(legendaryListener, this);
	}
	
	/**
	 * 
	 * Disables/release everything from memory
	 */
	@Override
	public void onDisable(){
		try{
			playerConfig.save(new File(this.getDataFolder(), "players.yml"));
		} catch (IOException e){
			this.getLogger().severe("Legendary Start could not save the players.yml file for reason: "+e.getMessage());
		}
	}
	
	/**
	 * 
	 * Loads all of the yml files associated with this plugin
	 * @throws IOException
	 * @throws InvalidConfigurationException
	 */
	private void loadConfig(){
		try{
			//Load &| create the config
			File configFile = new File(this.getDataFolder(), "config.yml");
			config = YamlConfiguration.loadConfiguration(configFile);
			if(!configFile.exists()){
				this.saveDefaultConfig();
			}
			config.load(configFile);
			//Load &| create the items sets
			File setsDir = new File(this.getDataFolder(), "InventorySets");
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
			File randSetsDir = new File(this.getDataFolder(), "RandomInventorySets");
			if(!randSetsDir.exists()){
				randSetsDir.mkdir();
				itemsConfig = YamlConfiguration.loadConfiguration(new File(randSetsDir, "set1.yml"));
				itemsConfig.load(new InputStreamReader(this.getResource("set1.yml")));
				itemsConfig.options().copyDefaults(true);
				itemsConfig.save(new File(randSetsDir, "set1.yml"));
				itemsConfig = YamlConfiguration.loadConfiguration(new File(randSetsDir, "set2.yml"));
				itemsConfig.load(new InputStreamReader(this.getResource("set2.yml")));
				itemsConfig.options().copyDefaults(true);
				itemsConfig.save(new File(randSetsDir, "set2.yml"));
			}
			//Load &| create playerConfig
			File playersFile = new File(this.getDataFolder(), "players.yml");
			playerConfig = YamlConfiguration.loadConfiguration(playersFile);
			if(!playersFile.exists()){
				playerConfig.save(playersFile);
			}
			playerConfig.load(playersFile);
		} catch(IOException | InvalidConfigurationException e){
			this.getLogger().severe("Legendary Start could not load the configuration file(s) for reason: "+e.getMessage());
		}
	}
}
