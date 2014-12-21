package com.gmail.tylerb318;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LegendaryInventory {
	
	private int setsIndex = 0, randSetsIndex = 0;											//Original and random sets index
	private int invIndex = 0, randInvIndex = 0;												//Original and random current inventory index
	
	private ItemMeta meta;																	//Temporary ItemMeta for use in setting up any item meta
	private Inventory legendaryInventory;													//Current legendaryInventory
	private Inventory randomInventory;														//Current randomInventory
	
	private ArrayList<ArrayList<ItemStack[]>> legendarySets;								//Total list of the original inventory contents
	private ArrayList<ItemStack[]> randomSets;												//Total list of the random inventory contents
	private ArrayList<ItemStack[]> legendaryInventorys;										//Temporary array of items in initializeInventory()
	
	private static ItemStack backBook = new ItemStack(Material.ENCHANTED_BOOK);				//Enchanted book used in going a page back
	private static ItemStack fowardBook = new ItemStack(Material.ENCHANTED_BOOK);			//Enchanted book used in going a page forward
	private static ItemStack backSetBook = new ItemStack(Material.ENCHANTED_BOOK);			//Enchanted book used in going a set back
	private static ItemStack nextSetBook = new ItemStack(Material.ENCHANTED_BOOK);			//Enchanted book used in going a page forward
	private static ItemStack selectItemBook = new ItemStack(Material.ENCHANTED_BOOK);		//Enchanted book used in selecting an item in the random inventory
	
	/**
	 * 
	 * Default constructor for LegendaryInventory
	 * @param itemsMap
	 */
	public LegendaryInventory(){
		//Load initial variables
		legendarySets = new ArrayList<ArrayList<ItemStack[]>>();
		randomSets = new ArrayList<ItemStack[]>();
		legendaryInventorys = new ArrayList<ItemStack[]>();
		//Create Inventors
		this.legendaryInventory = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('§', LegendaryStart.getPlugin(LegendaryStart.class).getConfig().get("InventoryName").toString()));
		this.randomInventory = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('§', LegendaryStart.getPlugin(LegendaryStart.class).getConfig().get("RandomInventoryName").toString()));
		
		loadCustomItems();
		initializeInventory();
		makeRandomInventory();
	}
	
	/**
	 * 
	 * Creates and sets the custom items to use in the inventory
	 */
	private void loadCustomItems(){
		//Meta Loading BackBook
		meta = backBook.getItemMeta();
		meta.setDisplayName("Back"); meta.setLore(Arrays.asList("Go back a page"));
		backBook.setItemMeta(meta);
		//Meta Loading FowardBook
		meta = fowardBook.getItemMeta();
		meta.setDisplayName("Forward"); meta.setLore(Arrays.asList("Go forward a page"));
		fowardBook.setItemMeta(meta);
		//Meta Loading BackSetBook
		meta = backSetBook.getItemMeta();
		meta.setDisplayName("Previous set"); meta.setLore(Arrays.asList("Go to the Previous Set"));
		backSetBook.setItemMeta(meta);
		//Meta Loading NextSetBook
		meta = nextSetBook.getItemMeta();
		meta.setDisplayName("Next set"); meta.setLore(Arrays.asList("Go to the next set"));
		nextSetBook.setItemMeta(meta);
		//Meta Loading SelectItemBook
		meta = selectItemBook.getItemMeta();
		meta.setDisplayName("Select Item");
		selectItemBook.setItemMeta(meta);
	}
	
	/**
	 * 
	 * Creates and fills the pages for the inventory to use
	 */
	private void initializeInventory(){
		//Loads the directory of the item sets
		File file = new File(LegendaryStart.getPlugin(LegendaryStart.class).getDataFolder(), "InventorySets");
		
		for(int i=0;i<file.listFiles().length;i++){
			//Loads the files configuration in order and grabs the keys
			YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(file.listFiles()[i]);
			String[] keys = new String[10]; keys = tempConfig.getKeys(true).toArray(keys);
			int minus = keys.length;//Used for determining proper placement and length of keys
			int tmp = 0;
			//Settings for [browseSets] configuration && initial inventory setup
			if(LegendaryStart.getPlugin(LegendaryStart.class).getConfig().getBoolean("browseSets") && file.listFiles().length != 1)
				legendaryInventory.setItem(45, backSetBook);
			legendaryInventory.setItem(53, fowardBook);
			//Loops through all of the keys
			for(int j=0,m=0;j<keys.length;j++, m++){
				if(m == 45 || m == 53){
					if(LegendaryStart.getPlugin(LegendaryStart.class).getConfig().getBoolean("browseSets") && file.listFiles().length != 1){
						j--;
						continue;
					}
					//If [browseSets] is not enabled delete the nextSets, backSets books and or skip it if on wrong page
					else
						switch(m){
						case 45:
							if(j-tmp<54) this.legendaryInventory.clear(45);
							else{j--; continue;}
							break;
						case 53:
							if((j-minus)>(Math.ceil(keys.length/54.0)*54)-54) this.legendaryInventory.clear(53);
							else{j--; continue;}
							break;
						}
				}
				//If at end of clear inventory and set up the next one
				if(m==54){
					m=-1;
					j--;
					legendaryInventorys.add(legendaryInventory.getContents());
					legendaryInventory.clear();
					setUpPage(legendaryInventory);
					continue;
				}
				String[] path = keys[j].split("\\.");
				if(path.length>1){
					if(path[1].equals("Type")){
						//Setting up the custom item
						ItemStack tempItem = new ItemStack(Material.getMaterial(tempConfig.getString(keys[j])), tempConfig.getInt(path[0]+".Amount"), Short.valueOf(tempConfig.getString(path[0]+".Meta")));
						ItemMeta tempMeta = tempItem.getItemMeta();
						tempMeta.setDisplayName(tempConfig.getString(path[0]+".DisplayName"));
						tempMeta.setLore(tempConfig.getStringList(path[0]+".Lore"));
						//Custom item enchantment
						List<String> enchantList = tempConfig.getStringList(path[0]+".Enchantments");
						for(int k=0;k<enchantList.size();k++){
							String[] enRay = enchantList.get(k).split(" ");
							tempMeta.addEnchant(Enchantment.getByName(enRay[0]), Integer.valueOf(enRay[1]), Boolean.getBoolean(enRay[2]));
						}
						tempItem.setItemMeta(tempMeta);
						//End of custom item
						legendaryInventory.setItem(m, tempItem);
						j+=5;
						minus-=5;
						tmp +=5;
						continue;
					}
					legendaryInventory.setItem(m, new ItemStack(Material.getMaterial(path[0]), tempConfig.getInt(keys[j]), Short.valueOf(path[1])));
				}
				else{
					if(tempConfig.getInt(path[0], 0) != 0){
						legendaryInventory.setItem(m, new ItemStack(Material.getMaterial(path[0]), tempConfig.getInt(path[0])));
					}else{
						minus--;
						tmp++;
						m--;
					}
				}
			}
			//If brows sets is on, set nextSetBook
			if(LegendaryStart.getPlugin(LegendaryStart.class).getConfig().getBoolean("browseSets") && file.listFiles().length != 1){
				legendaryInventory.clear(53);
				legendaryInventory.setItem(53, nextSetBook);
			}
			//else delete it
			else{
				legendaryInventory.clear(53);
			}
			legendaryInventorys.add(legendaryInventory.getContents());
			legendaryInventory.clear();
			//If there are less than 54 items
			if(keys.length < 54){//FIXME
				if(LegendaryStart.getPlugin(LegendaryStart.class).getConfig().getBoolean("browseSets") && file.listFiles().length > 1){
					legendaryInventory.setItem(45, backSetBook);
					legendaryInventory.setItem(53, nextSetBook);
				}
				legendaryInventorys.add(legendaryInventory.getContents());
				legendaryInventory.clear();
			}
			//Add current array of inventory's then reset it
			legendarySets.add(new ArrayList<ItemStack[]>(legendaryInventorys));
			legendaryInventorys.clear();
		}
		//Prepare inventory for first open
		legendaryInventory.clear();
		legendaryInventory.setContents(legendarySets.get(0).get(0));
	}
	
	private void makeRandomInventory(){
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		File file = new File(LegendaryStart.getPlugin(LegendaryStart.class).getDataFolder(), "RandomInventorySets");
		for(int i=0;i<file.listFiles().length;i++){
			YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(file.listFiles()[i]);
			String[] keys = new String[10]; keys = tempConfig.getKeys(true).toArray(keys);
			for(int j=0;j<keys.length;j++){
				
				String[] path = keys[j].split("\\.");
				if(path.length>1){
					if(path[1].equals("Type")){
						//Setting up the custom item
						ItemStack tempItem = new ItemStack(Material.getMaterial(tempConfig.getString(keys[j])), tempConfig.getInt(path[0]+".Amount"), Short.valueOf(tempConfig.getString(path[0]+".Meta")));
						ItemMeta tempMeta = tempItem.getItemMeta();
						tempMeta.setDisplayName(tempConfig.getString(path[0]+".DisplayName"));
						tempMeta.setLore(tempConfig.getStringList(path[0]+".Lore"));
						//Custom item enchantment
						List<String> enchantList = tempConfig.getStringList(path[0]+".Enchantments");
						for(int k=0;k<enchantList.size();k++){
							String[] enRay = enchantList.get(k).split(" ");
							tempMeta.addEnchant(Enchantment.getByName(enRay[0]), Integer.valueOf(enRay[1]), Boolean.getBoolean(enRay[2]));
						}
						tempItem.setItemMeta(tempMeta);
						//End of custom item
						stacks.add(tempItem);
						j+=5;
						continue;
					}
					stacks.add(new ItemStack(Material.getMaterial(path[0]), tempConfig.getInt(keys[j]), Short.valueOf(path[1])));
				}
				else{
					if(tempConfig.getInt(path[0], 0) != 0){
						stacks.add(new ItemStack(Material.getMaterial(path[0]), tempConfig.getInt(path[0])));
					}
				}
			}
			ItemStack[] tmp = new ItemStack[0]; tmp = stacks.toArray(tmp);
			randomSets.add(tmp);
		}
		if(LegendaryStart.getPlugin(LegendaryStart.class).getConfig().getBoolean("browseSets") && file.listFiles().length > 1){
			randomInventory.setItem(10, backSetBook);
			randomInventory.setItem(16, nextSetBook);
		}
		randomInventory.setItem(22, selectItemBook);
	}
	
	/**
	 * 
	 * Places 2 enchanted book in the bottom left and right slots of the inventory
	 * @param inv The inventory to be set
	 */
	private void setUpPage(Inventory inv){
		inv.setItem(45, backBook);
		inv.setItem(53, fowardBook);
	}
	
	/**
	 * 
	 * Returns the current sudo random item
	 * @return ItemStack
	 */
	public ItemStack getCurrRandItem(){
		return randomSets.get(randSetsIndex)[randInvIndex];
	}
	
	/**
	 * 
	 * Returns a java based sudo random item
	 * @return ItemStack
	 */
	public synchronized ItemStack getRandItem(){
		return randomSets.get(getRandSetsIndex())[new Random().nextInt(randomSets.get(getRandSetsIndex()).length-1)];
	}
	
	/**
	 * 
	 * Returns the inventory associated with this object
	 * @return legendaryInventory (Inventory)
	 */
	public Inventory getInventory() {
		return legendaryInventory;
	}
	
	/**
	 * 
	 * Returns the current random  original inventory
	 * @return Inventory
	 */
	public Inventory getRandomInventory(){
		return randomInventory;
	}
	
	/**
	 * 
	 * Returns the current setsIndex
	 * @return int
	 */
	private synchronized int getRandSetsIndex()
	{
		return randSetsIndex;
	}

	/**
	 * 
	 * Sets the current randIndex
	 * @param randSetsIndex
	 */
	private synchronized void setRandSetsIndex(int randSetsIndex)
	{
		this.randSetsIndex = randSetsIndex;
	}

	/**
	 * 
	 * Returns the current randInvIndex
	 * @return
	 */
	private synchronized int getRandInvIndex()
	{
		return randInvIndex;
	}

	/**
	 * 
	 * Sets the current randInvIndex
	 * @param randInvIndex
	 */
	private synchronized void setRandInvIndex(int randInvIndex)
	{
		this.randInvIndex = randInvIndex;
	}

	/**
	 * 
	 * Goes to the next available item in the current randSet
	 */
	public synchronized void randItemplus(){
		if(getRandInvIndex() == randomSets.get(getRandSetsIndex()).length-1){
			setRandInvIndex(0);
		}
		else{
			setRandInvIndex(getRandInvIndex()+1);
		}
		randomInventory.setItem(13, randomSets.get(getRandSetsIndex())[getRandInvIndex()]);
	}
	
	/**
	 * 
	 * Goes forward 1 set in the random inventory
	 */
	public synchronized void randSetFoward(){
		if(getRandSetsIndex() == this.randomSets.size()-1){
			setRandInvIndex(0);
			setRandSetsIndex(0);
		}
		else{
			setRandInvIndex(0);
			setRandSetsIndex(getRandSetsIndex()+1);
		}
	}
	/**
	 * 
	 * Goes back 1 set in the random inventory
	 */
	public synchronized void randSetBack(){
		if(getRandSetsIndex() == 0){
			setRandInvIndex(0);
			setRandSetsIndex(this.randomSets.size()-1);
		}
		else{
			setRandInvIndex(0);
			setRandSetsIndex(getRandSetsIndex()-1);
		}
	}
	
	/**
	 * 
	 * Makes the original inventory go back a page
	 */
	public void goBack(){
		if(invIndex == 0)
			invIndex = this.legendarySets.get(setsIndex).size()-1;
		else
			invIndex--;
		legendaryInventory.setContents(this.legendarySets.get(setsIndex).get(invIndex));
	}
	
	/**
	 * 
	 * Makes the original inventory go forward a page
	 */
	public void goFoward(){
		if(invIndex == this.legendarySets.get(setsIndex).size()-1)
			invIndex = 0;
		else invIndex++;
		legendaryInventory.setContents(this.legendarySets.get(setsIndex).get(invIndex));
	}

	/**
	 * 
	 * Shows you the next original set
	 */
	public void setForward(){
		if(setsIndex == this.legendarySets.size()-1){
			setsIndex = 0;
			invIndex = 0;
		}
		else setsIndex++;
		legendaryInventory.setContents(this.legendarySets.get(setsIndex).get(0));
	}
	
	/**
	 * 
	 * Show you the previous original set
	 */
	public void setBack(){
		if(setsIndex == 0){
			setsIndex = this.legendarySets.size()-1;
			invIndex = this.legendarySets.get(setsIndex).size()-1;
		}
		else setsIndex--;
		legendaryInventory.setContents(this.legendarySets.get(setsIndex).get(invIndex));
	}
}
