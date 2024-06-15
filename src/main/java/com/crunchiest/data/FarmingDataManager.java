package com.crunchiest.data;

import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.crunchiest.FarmingTrial;

/**
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
    loadTools();
    loadCrops();
  }
  
  /** 
   * loadTools: 
   * loads in tool data from yml, putting into hashmap.
   */
  private void loadTools() {
    plugin.getLogger().log(Level.INFO, "---------------------");
    plugin.getLogger().log(Level.INFO, "- - LOADING TOOLS - -");
    ConfigurationSection tools = data.getFarmingConfig().getConfigurationSection("tools");
    if (tools == null) {
      plugin.getLogger().log(Level.WARNING, "Section 'tools' not found in configuration.");
      return;
    }
    
    for (String key : tools.getKeys(false)) {
      try {
        Material tool = Material.valueOf(key.toUpperCase());
        int value = data.getFarmingConfig().getInt("tools." + key);
        hoeTiers.put(tool, value);
        plugin.getLogger().log(Level.INFO, "Tool added: " + key);
      } catch (IllegalArgumentException e) {
        plugin.getLogger().log(Level.WARNING, "Failed to add tool: " + key);
      }
    }
    plugin.getLogger().log(Level.INFO, "- - TOOLS  LOADED - -");
    plugin.getLogger().log(Level.INFO, "---------------------");
  }
  
  /** 
   * loadCrops: 
   * loads in crops data from yml, putting into hashmaps.
   */
  private void loadCrops() {
    plugin.getLogger().log(Level.INFO, "- - LOADING CROPS - -");
    ConfigurationSection crops = data.getFarmingConfig().getConfigurationSection("crops");
    if (crops == null) {
      plugin.getLogger().log(Level.WARNING, "Section 'crops' not found in configuration.");
      return;
    }
    
    for (String key : crops.getKeys(false)) {
      try {
        Material crop = Material.valueOf(key.toUpperCase());
        String seedKey = "crops." + key + ".seed";
        String dropKey = "crops." + key + ".drop";
        String multKey = "crops." + key + ".multiplier";
        
        Material seed = Material.valueOf(data.getFarmingConfig()
            .getString(seedKey).toUpperCase());
        Material drop = Material.valueOf(data.getFarmingConfig()
             .getString(dropKey).toUpperCase());
        float mult = Float.parseFloat(data.getFarmingConfig().getString(multKey));
        
        cropToSeed.put(crop, seed);
        cropToDrop.put(crop, drop);
        cropToMult.put(crop, mult);
        
        plugin.getLogger().log(Level.INFO, "Crop added: " + key);
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Failed to add crop: " + key);
      }
    }
    plugin.getLogger().log(Level.INFO, "- - CROPS  LOADED - -");
    plugin.getLogger().log(Level.INFO, "---------------------");
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
    if (cropToSeed.get(crop) != null) {
      return true;
    }
    return false;
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
    if (hoeTiers.get(hoe) != null) {
      return true;
    }
    return false;
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
