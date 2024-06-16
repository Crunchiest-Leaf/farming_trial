package com.crunchiest.data;

import com.crunchiest.FarmingTrial;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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

public class PluginConfigManager {
  
  private FarmingTrial plugin;
  private File playerConfigFile;
  private File farmingConfigFile;
  private FileConfiguration playerConfig;
  private FileConfiguration farmingConfig;

  /**
  * PluginConfigManager:
  * handles loading of yml config data.
  *
  * @param plugin main plugin instance.
  */
  public PluginConfigManager(FarmingTrial plugin) {
    this.plugin = plugin;
    this.playerConfigFile = new File(plugin.getDataFolder(), "playerData.yml");
    this.farmingConfigFile = new File(plugin.getDataFolder(), "farmingData.yml");
    saveDefaultConfigs();
    reloadConfig();
  }
  
  /**
  * reloadConfig: 
  * Reloads configuration files.
  */
  public void reloadConfig() {
    plugin.logInfo("Loading Configs");
    this.playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
    this.farmingConfig = YamlConfiguration.loadConfiguration(farmingConfigFile);
    
    // Load defaults from JAR if they don't exist
    loadDefaultConfig("playerData.yml", playerConfig);
    loadDefaultConfig("farmingData.yml", farmingConfig);
    plugin.logInfo("Configs Loaded");
  }
  
  /**
  * getPlayerConfig:
  * Retrieves the player configuration.
  *
  * @return Player configuration
  */
  public FileConfiguration getPlayerConfig() {
    return this.playerConfig;
  }
  
  /**
  * getFarmingConfig:
  * Retrieves the farming configuration.
  *
  * @return Farming configuration
  */
  public FileConfiguration getFarmingConfig() {
    return this.farmingConfig;
  }
  
  /**
  * savePlayerConfig:
  * Saves the player configuration to disk.
  */
  public void savePlayerConfig() {
    saveConfig(playerConfig, playerConfigFile, "PLAYER CONFIG SAVE FAILED!");
  }
  
  /**
  * saveFarmingConfig: 
  * Saves the farming configuration to disk.
  */
  public void saveFarmingConfig() {
    saveConfig(farmingConfig, farmingConfigFile, "FARMING CONFIG SAVE FAILED!");
  }
  
  /**
  * saveDefaultConfigs: 
  * Saves default configurations from resources if they don't exist.
  */
  private void saveDefaultConfigs() {
    saveResource("playerData.yml", playerConfigFile);
    saveResource("farmingData.yml", farmingConfigFile);
  }
  
  /**
  * saveResource:
  * Saves a default configuration file from the plugin resources.
  *
  * @param resourcePath Path to the resource in the plugin JAR
  * @param file         File to save the resource to
  * @return void
  */
  private void saveResource(String resourcePath, File file) {
    if (!file.exists()) {
      plugin.saveResource(resourcePath, false);
    }
  }
  
  /**
  * loadDefaultConfig:
  * Loads default configuration from resources if not already existing.
  *
  * @param fileName   Name of the file to load
  * @param config     Configuration to load into
  * @return void
  */
  private void loadDefaultConfig(String fileName, FileConfiguration config) {
    InputStream inputStream = plugin.getResource(fileName);
    if (inputStream != null) {
      YamlConfiguration defaultConfig = 
          YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
      config.setDefaults(defaultConfig);
    }
  }
  
  /**
  * Saves a configuration file.
  *
  * @param config     Configuration to save
  * @param configFile File to save the configuration to
  * @param errorMessage Error message to log in case of failure
  * @return void
  */
  private void saveConfig(FileConfiguration config, File configFile, String errorMessage) {
    if (config != null && configFile != null) {
      try {
        config.save(configFile);
      } catch (IOException e) {
        plugin.getLogger().log(Level.SEVERE, errorMessage, e);
      }
    }
  }
}