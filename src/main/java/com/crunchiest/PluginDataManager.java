package com.crunchiest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
* PluginDataManager:
* Manages configuration files for the plugin.
*/
public class PluginDataManager {
  
  private FarmingTrial plugin;
  private File playerConfigFile;
  private File farmingConfigFile;
  private FileConfiguration playerConfig;
  private FileConfiguration farmingConfig;
  
  public PluginDataManager(FarmingTrial plugin) {
    this.plugin = plugin;
    this.playerConfigFile = new File(plugin.getDataFolder(), "playerData.yml");
    this.farmingConfigFile = new File(plugin.getDataFolder(), "farmingData.yml");
    saveDefaultConfigs();
    reloadConfig();
  }
  
  /**
  * Reloads configuration files.
  * 
  * @return void
  */
  public void reloadConfig() {
    this.playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
    this.farmingConfig = YamlConfiguration.loadConfiguration(farmingConfigFile);
    
    // Load defaults from JAR if they don't exist
    loadDefaultConfig("playerData.yml", playerConfig);
    loadDefaultConfig("farmingData.yml", farmingConfig);
  }
  
  /**
  * Retrieves the player configuration.
  *
  * @return Player configuration
  */
  public FileConfiguration getPlayerConfig() {
    return this.playerConfig;
  }
  
  /**
  * Retrieves the farming configuration.
  *
  * @return Farming configuration
  */
  public FileConfiguration getFarmingConfig() {
    return this.farmingConfig;
  }
  
  /**
  * Saves the player configuration to disk.
  *
  * @return void
  */
  public void savePlayerConfig() {
    saveConfig(playerConfig, playerConfigFile, "PLAYER CONFIG SAVE FAILED!");
  }
  
  /**
  * Saves the farming configuration to disk.
  *
  * @return void
  */
  public void saveFarmingConfig() {
    saveConfig(farmingConfig, farmingConfigFile, "FARMING CONFIG SAVE FAILED!");
  }
  
  /**
  * Saves default configurations from resources if they don't exist.
  *
  * @return void
  */
  private void saveDefaultConfigs() {
    saveResource("playerData.yml", playerConfigFile);
    saveResource("farmingData.yml", farmingConfigFile);
  }
  
  /**
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