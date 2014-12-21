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
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This class is designed to be used to get the array items from the configuration files.
 * @author Tyler Bucher
 */
public class LegendaryInventory {

    private static LegendaryStart mainClass = LegendaryStart.getPlugin(LegendaryStart.class);               // Main class object
    
    private static ArrayList<ArrayList<ItemStack>> inventoryArray = new ArrayList<ArrayList<ItemStack>>();  // Array for the inventory data
    private static YamlConfiguration inventoryConfiguration;                                                // Configuration to load the files which the items reside in
    private static int keyPosition;                                                                         // Universal position of the key in the file
    

    /**
     * This method sets up the loading of this class. This method 
     * should only be called LegendaryStart.class once.
     */
    static void init() {
        preLoad();
        load();
        postLoad();
    }

    /**
     * Initialize basic resources that the LegendaryInventory will use.
     */
    private static void preLoad() {
        
    }

    /**
     * Loads the items form the files into a virtual inventory array.
     */
    private static void load() {
        File file = new File(mainClass.getDataFolder(), "Inventory Sets");
        //Loop through the sets/Files
        for(int i=0;i<file.listFiles().length;i++){
            inventoryArray.add(new ArrayList<ItemStack>());
            inventoryConfiguration = YamlConfiguration.loadConfiguration(file.listFiles()[i]);
            String[] keys = new String[10]; keys = inventoryConfiguration.getValues(true).keySet().toArray(keys);
            keyPosition = 0;
            //loop through the keys of the items
            while(keyPosition<keys.length){
                if(inventoryConfiguration.getInt(keys[keyPosition], 0) != 0){
                    inventoryArray.get(i).add(convertToItemStack(keys[keyPosition], inventoryConfiguration.getInt(keys[keyPosition])));
                }
                keyPosition++;
            }
        }
    }

    /**
     * Deallocate any unnecessary Objects.
     */
    private static void postLoad() {
        inventoryConfiguration = null;
        keyPosition = 0;
    }
    
    /**
     * Converts a key and value to an item stack.
     * @param key - The Material of the item
     * @param value - The amount of the item
     * @return The converted ItemStack
     */
    private static ItemStack convertToItemStack(String key, int value){
        String[] path = key.split("\\.");
        if(path.length==1){
            return new ItemStack(Material.valueOf(key), value);
        }
        else{
            if(path[1].equals("Amount")){
                //Initialize Custom item and its metadata
                ItemStack customItem = new ItemStack(Material.valueOf(inventoryConfiguration.getString(path[0]+".Type")), inventoryConfiguration.getInt(path[0]+".Amount"));
                ItemMeta customMeta = customItem.getItemMeta();
                //Set custom meta values
                customMeta.setDisplayName(inventoryConfiguration.getString(path[0]+".DisplayName"));
                customMeta.setLore(inventoryConfiguration.getStringList(path[0]+".Lore"));
                //Add Enchantment(s)
                for(String list : inventoryConfiguration.getStringList(path[0]+".Enchantments")){
                    String[] enchantArray = list.split(" ");
                    customMeta.addEnchant(Enchantment.getByName(enchantArray[0]), Integer.parseInt(enchantArray[1]), Boolean.getBoolean(enchantArray[2]));
                }
                customItem.setItemMeta(customMeta);
                keyPosition += 4;
                return customItem;
            }
            else{
                return new ItemStack(Material.valueOf(path[0]), value, Short.parseShort(path[1]));
            }
        }
    }

    /**
     * @return The inventory array
     */
    public static ArrayList<ArrayList<ItemStack>> getInventoryArray() {
        return inventoryArray;
    }
    
}