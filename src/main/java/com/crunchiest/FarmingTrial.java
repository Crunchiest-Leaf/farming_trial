package com.crunchiest;

import com.crunchiest.commands.GiveFarmingPotionCommand;
import com.crunchiest.commands.ReloadFarmTrialCommand;
import com.crunchiest.commands.ToggleTramplingCommand;
import com.crunchiest.completion.GiveFarmingPotionTabComplete;
import com.crunchiest.completion.ReloadFarmTrialTabComplete;
import com.crunchiest.completion.ToggleTramplingTabComplete;
import com.crunchiest.data.FarmingDataManager;
import com.crunchiest.data.FarmingPotionManager;
import com.crunchiest.data.PluginConfigManager;
import com.crunchiest.events.FarmEventListener;
import com.crunchiest.events.FarmingPotionEventListener;
import com.crunchiest.utils.ResourceUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;


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
 * FarmingTrial: 
 * main plugin class.
 */
public class FarmingTrial extends JavaPlugin {
  private static final Logger LOGGER = Logger.getLogger("FarmingTrial");
  private PluginConfigManager configManager;
  private FarmingPotionManager potionManager;
  private FarmingDataManager farmingDataManager;
  FarmingTrial plugin = this;
  Boolean initialised = false;
  
  /** 
   * onEnable: 
   * runs on plugin enable.
   * runs initialisation process of 
   * plugin components.
   */
  @Override
  public void onEnable() {
    loadPlugin();
    logInfo("FarmingTrial enabled");
  }
  
  /** 
   * onDisable: 
   * runs on plugin disable.
   */
  @Override
  public void onDisable() {
    logInfo("FarmingTrial disabled");
  }

  /** 
   * loadFarmingData: 
   * loads farming data manager.
   */
  private void loadFarmingData() {
    farmingDataManager = new FarmingDataManager(this);
  }

  /** 
   * loadPluginConfigs: 
   * loads config manager.
   */
  private void loadPluginConfigs() {
    configManager = new PluginConfigManager(this);
  }

  /** 
   * loadCustomPotions: 
   * loads Potion manager, and registers custom potions.
   */
  private void loadCustomPotions() {
    // Custom Potions! Custom Mechanics! Very Cool!
    potionManager = new FarmingPotionManager(plugin);
    //Potion of Growth: Bonemeal potion, makes REGISTERED crops in radius grow to full.
    potionManager.registerCustomPotion("Potion of Growth", "POTION_OF_GROWTH", true, 2); 
    //Potion of Harvesting: Harvest potion, harvests fully grown REGISTERED crops in radius.
    potionManager.registerCustomPotion("Potion of Harvesting", "POTION_OF_HARVESTING", true, 2);
  }

  /** 
   * registerCommands: 
   * registers plugin command executors & completion.
   */
  private void registerCommands() {
    getCommand("toggle_trampling").setExecutor(new ToggleTramplingCommand(plugin));
    getCommand("toggle_trampling").setTabCompleter(new ToggleTramplingTabComplete());
    getCommand("give_potion").setExecutor(new GiveFarmingPotionCommand(plugin));
    getCommand("give_potion").setTabCompleter(new GiveFarmingPotionTabComplete(plugin));
    getCommand("farmtrial").setExecutor(new ReloadFarmTrialCommand(plugin));
    getCommand("farmtrial").setTabCompleter(new ReloadFarmTrialTabComplete());
  }

  /** 
   * registerEvents: 
   * registers plugin event listeners.
   */
  public void registerEvents() {
    getServer().getPluginManager().registerEvents(new FarmEventListener(plugin), this);
    getServer().getPluginManager().registerEvents(new FarmingPotionEventListener(plugin), this);
  }

  /** 
   * reloadPlugin: 
   * handles loading of plugin components.
   * used on startup, and when reloading.
   */
  public void loadPlugin() {
    logInfo(initialised ? "Re-loading Farming Plugin!" : "Loading Farming Plugin!");
    initialised = true;

    // load in plugin data / configs.
    loadPluginConfigs();
    loadFarmingData();
    loadCustomPotions();

    /* clear previously registered events 
     * and re-register them. */
    HandlerList.unregisterAll(this);
    registerEvents();
    registerCommands();

    //unpack readme from jar to plugin folder.
    copyReadmeToPluginFolder();
  }

  /** 
   * getFarmingDataManager: 
   * getter for main farm Data manager.
   */
  public FarmingDataManager getFarmingDataManager() {
    return farmingDataManager;
  }

  /** 
   * getPluginDataManager: 
   * getter for main plugin Data manager.
   */
  public PluginConfigManager getPluginDataManager() {
    return configManager;
  }

  /** 
   * getFarmingPotionManager: 
   * getter for potion Data manager.
   */
  public FarmingPotionManager getFarmingPotionManager() {
    return potionManager;
  }

  /** 
   * copyReadmeToPluginFolder: 
   * Copies README.md to the plugin folder from JAR resources.
   */
  public void copyReadmeToPluginFolder() {
    ResourceUtils.copyReadmeToPluginFolder(plugin);
  }

  /** 
   * logInfo: 
   * console log message, at info level.
   *
   * @param message message string
   */
  public void logInfo(String message) {
    LOGGER.log(Level.INFO, message);
  }
  
  /** 
   * logWarning: 
   * console log message, at Warning level.
   *
   * @param message message string
   */
  public void logWarning(String message) {
    LOGGER.log(Level.WARNING, message);
  }

}
