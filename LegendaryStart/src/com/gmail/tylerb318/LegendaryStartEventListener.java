package com.gmail.tylerb318;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LegendaryStartEventListener implements Listener{
	
	private static LegendaryStart mainClass = LegendaryStart.getPlugin(LegendaryStart.class);
	
	@EventHandler
	public void onPLayerJoin(final PlayerJoinEvent evt){
		if(evt.getPlayer().hasPermission("ls.menu.original") ||
			evt.getPlayer().hasPermission("ls.menu.random")){
			//Setup the player info
			playerJoinSetup(evt);
			//Check to see if one time login is enabled
			if(mainClass.config.getBoolean("OneTimeLogin")){
				if(mainClass.playerConfig.getBoolean(evt.getPlayer().getUniqueId().toString()+".hasChosen")) return;
				else mainClass.playerMap.put(evt.getPlayer().getUniqueId(), new LegendaryInventory());
				if(!mainClass.config.getBoolean("PersistantLogin") && !mainClass.playerConfig.getBoolean(evt.getPlayer().getUniqueId().toString()+".hasChosen") && !mainClass.playerConfig.getBoolean(evt.getPlayer().getUniqueId().toString()+".firstTimeLogin")){
					if(mainClass.config.getBoolean("LoginMessage")){
						evt.getPlayer().sendMessage("Type /lsmenu  or /lsmenu random to get your login bonus, you have "+(3-mainClass.playerConfig.getInt(evt.getPlayer().getUniqueId().toString()+".chosenItems"))+" more item(s) to choose.");
					}
				}
				return;
			}
			
			mainClass.playerMap.put(evt.getPlayer().getUniqueId(), new LegendaryInventory());//FIXME /lsr
			/*if(mainClass.config.getBoolean("AutoOpen")&& mainClass.playerConfig.getBoolean(evt.getPlayer().getUniqueId().toString()+".firstTimeLogin")){
				mainClass.getLogger().info("HAHAHHAHAHAHAHHAHAHAHAHA im true");*/
				mainClass.getServer().getScheduler().scheduleSyncDelayedTask(mainClass,
						new Runnable(){
					        @Override
					        public void run(){
					            evt.getPlayer().openInventory(mainClass.playerMap.get(evt.getPlayer().getUniqueId()).getInventory());
					        }
			    		}, 20L);
			//}
			mainClass.playerConfig.set(evt.getPlayer().getUniqueId().toString()+".firstTimeLogin", false);
			//Try and save the player to the file
			try{
				mainClass.playerConfig.save(new File(mainClass.getDataFolder(), "players.yml"));
			} catch (IOException e){
				mainClass.getLogger().severe("Legendary Start could not save the players.yml file for reason: "+e.getMessage());
			}
		}
	}
	
	private void playerJoinSetup(final PlayerJoinEvent evt){
		//Check to see if player exists in players.yml
		if(!mainClass.playerConfig.contains(evt.getPlayer().getUniqueId().toString())){
			mainClass.playerConfig.set(evt.getPlayer().getUniqueId().toString(), null);
			mainClass.playerConfig.set(evt.getPlayer().getUniqueId().toString()+".Name", evt.getPlayer().getName());
			mainClass.playerConfig.set(evt.getPlayer().getUniqueId().toString()+".firstTimeLogin", true);
			mainClass.playerConfig.set(evt.getPlayer().getUniqueId().toString()+".hasChosen", false);
			mainClass.playerConfig.set(evt.getPlayer().getUniqueId().toString()+".chosenItems", 0);
		}
		//Check to see if the player has changed their name sense last login
		else{
			if(!mainClass.playerConfig.getString(evt.getPlayer().getUniqueId()+".Name").equals(evt.getPlayer().getName())){
				mainClass.playerConfig.set(evt.getPlayer().getUniqueId().toString()+".Name", evt.getPlayer().getName());
			}
		}
	}
	
	@EventHandler
	public void onPLayerDiconnect(PlayerQuitEvent evt){
		mainClass.playerMap.remove(evt.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent evt){
		if(evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("RandomInventoryName")))){
			mainClass.threadMap.get(evt.getPlayer().getUniqueId()).interrupt();
			mainClass.threadMap.remove(evt.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent evt){
		if(evt.getCurrentItem() != null && !evt.getCurrentItem().getType().equals(Material.AIR)){
			if(evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("InventoryName"))) || 
				evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("RandomInventoryName")))){
				if(evt.getRawSlot() < evt.getInventory().getSize()){
					if(evt.getCurrentItem().hasItemMeta()){
						switch(evt.getCurrentItem().getItemMeta().getDisplayName()){
						case "Select Item":
							evt.setCancelled(true);
							if(mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")<mainClass.config.getInt("numberOfitems")){
								if(mainClass.config.getString("RandomInventorySetting").equalsIgnoreCase("random")){
									if(evt.getWhoClicked().getInventory().addItem(mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).getRandItem()).size() != 0) return;
								}
								else{
									if(evt.getWhoClicked().getInventory().addItem(mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).getCurrRandItem()).size() != 0) return;
								}
								mainClass.playerConfig.set(evt.getWhoClicked().getUniqueId().toString()+".chosenItems", mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")+1);
							}
							if(mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")==mainClass.config.getInt("numberOfitems")){
								mainClass.threadMap.get(evt.getWhoClicked().getUniqueId()).interrupt();
								mainClass.threadMap.remove(evt.getWhoClicked().getUniqueId());
								mainClass.playerMap.remove(evt.getWhoClicked().getUniqueId());
								mainClass.playerConfig.set(evt.getWhoClicked().getUniqueId().toString()+".hasChosen", true);
								evt.getWhoClicked().closeInventory();
							}
							if(!mainClass.getConfig().getBoolean("browseSets")){
								mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).setForward();
							}
							break;
						case "Back":
							evt.setCancelled(true);
							mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).goBack();
							break;
						case "Forward":
							evt.setCancelled(true);
							mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).goFoward();
							break;
						case "Previous set":
							evt.setCancelled(true);
							if(evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("InventoryName"))))
								mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).setBack();
							else
								mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).randSetBack();
							break;
						case "Next set":
							evt.setCancelled(true);
							if(evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("InventoryName"))))
								mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).setForward();
							else
								mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).randSetFoward();
							break;
						default:
							evt.setCancelled(true);
							if(!evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("RandomInventoryName")))){
								if(mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")<mainClass.config.getInt("numberOfitems")){
									if(evt.getWhoClicked().getInventory().addItem(evt.getCurrentItem()).size() != 0) return;
									mainClass.playerConfig.set(evt.getWhoClicked().getUniqueId().toString()+".chosenItems", mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")+1);
								}
								if(mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")==mainClass.config.getInt("numberOfitems")){
									mainClass.playerMap.remove(evt.getWhoClicked().getUniqueId());
									mainClass.playerConfig.set(evt.getWhoClicked().getUniqueId().toString()+".hasChosen", true);
									evt.getWhoClicked().closeInventory();
								}
								if(!mainClass.getConfig().getBoolean("browseSets")){
									mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).setForward();
								}
							}
							break;
						}
					}
					else{
						evt.setCancelled(true);
						if(!evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("RandomInventoryName")))){
							if(mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")<mainClass.config.getInt("numberOfitems")){
								if(evt.getWhoClicked().getInventory().addItem(evt.getCurrentItem()).size() != 0) return;
								mainClass.playerConfig.set(evt.getWhoClicked().getUniqueId().toString()+".chosenItems", mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")+1);
							}
							if(mainClass.playerConfig.getInt(evt.getWhoClicked().getUniqueId().toString()+".chosenItems")==mainClass.config.getInt("numberOfitems")){
								mainClass.playerMap.remove(evt.getWhoClicked().getUniqueId());
								mainClass.playerConfig.set(evt.getWhoClicked().getUniqueId().toString()+".hasChosen", true);
								evt.getWhoClicked().closeInventory();
							}
							if(!mainClass.getConfig().getBoolean("browseSets")){
								mainClass.playerMap.get(evt.getWhoClicked().getUniqueId()).setForward();
							}
						}
					}
				}
			}
		}
		if(evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("InventoryName"))) || 
			evt.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('§', mainClass.config.getString("RandomInventoryName"))))
			evt.setCancelled(true);
	}
}
