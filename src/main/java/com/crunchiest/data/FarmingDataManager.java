package com.crunchiest.data;

import com.crunchiest.FarmingTrial;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;


/*
* FARMING TRIAL PLUGIN
* ______                   _____    _       _ 
* |  ___|                 |_   _|  (_)     | |
* | |_ __ _ _ __ _ __ ___   | |_ __ _  __ _| |
* |  _/ _` | '__| '_ ` _ \  | | '__| |/ _` | |
* | || (_| | |  | | | | | | | | |  | | (_| | |
* \_| \__,_|_|  |_| |_| |_| \_/_|  |_|\__,_|_|
*
* Author: Crunchiest_Leaf
*
* desc: Trial Plugin for LOTC java team
*       see link for outline.
* 
* link: https://docs.google.com/document/d/1zpQpmroUDSb7b6XRdxoifJIs6ig295lM0LOI0gdOvGk/edit#heading=h.h6zgogey5tcq
* 
*/

/** 
* FarmingDataManager: 
* loads in tool/crop/player data from yml.
*/
public class FarmingDataManager {
  
  FarmingTrial plugin;
  private HashMap<Material, Integer> hoeTiers = new HashMap<Material, Integer>();
  private HashMap<Material, Material> cropToSeed = new HashMap<Material, Material>();
  private HashMap<Material, Material> cropToDrop = new HashMap<Material, Material>();
  private HashMap<Material, Float> cropToMult = new HashMap<Material, Float>();
  private PluginConfigManager data;
  
  /** 
  * FarmingDataManager: 
  * Constructor.
  *
  * @param plugin main plugin instance.
  */
  public FarmingDataManager(FarmingTrial plugin) {
    this.plugin = plugin;
    this.data = plugin.getPluginDataManager();
    reloadData();
  }
  
  /** 
  * reloadData(): 
  * loads in tool data from yml, putting into hashmap.
  * for use on plugin reload
  */
  public void reloadData() {
    hoeTiers.clear();
    cropToSeed.clear();
    cropToDrop.clear();
    cropToMult.clear();
    loadTools();
    loadCrops();
  }
  
  /** 
  * loadTools: 
  * Loads tool data from the YAML configuration and populates a hashmap.
  * Logs loading progress and errors.
  */
  private void loadTools() {
    plugin.logInfo("---------------------");
    plugin.logInfo("- - LOADING TOOLS - -");
    
    // Retrieve the 'tools' section from the YAML configuration
    ConfigurationSection tools = data.getFarmingConfig().getConfigurationSection("tools");
    
    // Check if 'tools' section exists in the configuration
    if (tools == null) {
      plugin.logWarning("Section 'tools' not found in configuration.");
      return;
    }
    
    // Iterate over each key (tool name) in the 'tools' section
    for (String key : tools.getKeys(false)) {
      try {
        // Convert tool name to Material type
        Material tool = Material.matchMaterial(key.toUpperCase());
        
        // Validate if the material type is valid
        if (tool == null) {
          throw new IllegalArgumentException("Invalid material: " + key);
        }
        
        // Retrieve tool level from the configuration
        int value = data.getFarmingConfig().getInt("tools." + key);
        
        // Store the tool and its level in the hoeTiers hashmap
        hoeTiers.put(tool, value);
        
        // Log successful tool addition
        plugin.logInfo("Tool added: " + key);
      } catch (Exception e) {
        // Log warning if failed to add a tool due to an exception
        plugin.logWarning("Failed to add tool: " + key);
      }
    }
    
    // Log completion of tools loading
    plugin.logInfo("- - TOOLS  LOADED - -");
    plugin.logInfo("---------------------");
  }
  
  /** 
  * loadCrops: 
  * Loads crop data from the YAML configuration and populates hashmaps.
  * Logs loading progress and errors.
  */
  private void loadCrops() {
    plugin.logInfo("- - LOADING CROPS - -");
    
    // Retrieve the 'crops' section from the YAML configuration
    ConfigurationSection crops = data.getFarmingConfig().getConfigurationSection("crops");
    
    // Check if 'crops' section exists in the configuration
    if (crops == null) {
      plugin.logWarning("Section 'crops' not found in configuration.");
      return;
    }
    
    // Iterate over each key (crop name) in the 'crops' section
    for (String key : crops.getKeys(false)) {
      try {
        // Convert crop name to Material type
        Material crop = Material.matchMaterial(key.toUpperCase());
        
        // Validate if the material type is valid
        if (crop == null) {
          throw new IllegalArgumentException("Invalid material: " + key);
        }
        
        // Retrieve seed, drop, and multiplier values from the configuration
        String seedKey = "crops." + key + ".seed";
        String dropKey = "crops." + key + ".drop";
        String multKey = "crops." + key + ".multiplier";
        
        Material seed = Material.matchMaterial(data.getFarmingConfig().getString(seedKey));
        Material drop = Material.matchMaterial(data.getFarmingConfig().getString(dropKey));
        float mult = Float.parseFloat(data.getFarmingConfig().getString(multKey));
        
        // Store crop, seed, drop, and multiplier in respective hashmaps
        cropToSeed.put(crop, seed);
        cropToDrop.put(crop, drop);
        cropToMult.put(crop, mult);
        
        // Log successful crop addition
        plugin.logInfo("Crop added: " + key);
      } catch (Exception e) {
        // Log warning if failed to add a crop due to an exception
        plugin.logWarning("Failed to add crop: " + key);
      }
    }
    
    // Log completion of crops loading
    plugin.logInfo("- - CROPS  LOADED - -");
    plugin.logInfo("---------------------");
  }
  
  /** 
  * getCropToSeed:
  * returns seed associated with registered crop.
  *
  * @param crop crop to search.
  * @return Material
  */
  public Material getCropToSeed(Material crop) {
    return cropToSeed.get(crop);
  }
  
  
  /** 
  * checkCropExists:
  * returns whether given crop is registered.
  *
  * @param crop crop to search.
  * @return boolean
  */
  public boolean checkCropExists(Material crop) {
    return cropToSeed.get(crop) != null ? true : false;
  }
  
  /** 
  * getCropToDrop:
  * returns drop associated with registered crop.
  *
  * @param crop crop to search.
  * @return Material
  */
  public Material getCropToDrop(Material crop) {
    return cropToDrop.get(crop);
  }
  
  
  /** 
  * getHoeTier:
  * returns tier associated with registered hoe.
  *
  * @param hoe hoe to search.
  * @return int
  */
  public int getHoeTier(Material hoe) {
    return hoeTiers.get(hoe);
  }
  
  /** 
  * checkHoeExists:
  * returns whether given hoe is registered.
  *
  * @param hoe hoe to search.
  * @return boolean
  */
  public boolean checkHoeExists(Material hoe) {
    return hoeTiers.get(hoe) != null ? true : false;
  }
  
  /** 
  * getCropMult:
  * returns mult associated with registered crop.
  *
  * @param crop crop to search.
  * @return float
  */
  public float getCropMult(Material crop) {
    return cropToMult.get(crop);
  }
}
