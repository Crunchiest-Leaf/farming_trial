package com.crunchiest;

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
 * duthor: Crunchiest_Leaf
 *
 * desc: Trial Plugin for LOTC java team
 *       see link for outline.
 * 
 * link: https://docs.google.com/document/d/1zpQpmroUDSb7b6XRdxoifJIs6ig295lM0LOI0gdOvGk/edit#heading=h.h6zgogey5tcq
 * 
 */

public class PluginDataManager {

 /** 
  *  Plugin Data manager;
  *  Handles config file loading / saving
  *  for plugin farming / player data.
  */
  private FarmingTrial plugin;
  private FileConfiguration playerConfig = null;
  private FileConfiguration farmingConfig = null;
  private File playerConfigFile = null;
  private File farmingConfigFile = null;

  /**
   * PluginDataManager:
   * constructor, used to initialise plugin startup data.
   */
  public PluginDataManager(FarmingTrial plugin) {
    this.plugin = plugin;
    // init configs on startup
    saveDefaultConfigs();
  }

  /**
   * reloadConfig:
   * reloads data held in memory with data held in files.
   * 
   * @return void
   */
  public void reloadConfig(){

    if (this.playerConfigFile == null){
      this.playerConfigFile = new File(this.plugin.getDataFolder(), "playerData.yml");
    }
    if (this.farmingConfigFile == null){
      this.playerConfigFile = new File(this.plugin.getDataFolder(), "farmingData.yml");
    }

    this.playerConfig = YamlConfiguration.loadConfiguration(this.playerConfigFile);
    this.farmingConfig = YamlConfiguration.loadConfiguration(this.farmingConfigFile);

    InputStream playerStream = this.plugin.getResource("playerData.yml");
    if (playerStream != null){
      YamlConfiguration playerStreamConfig = YamlConfiguration
            .loadConfiguration(new InputStreamReader(playerStream));
      this.playerConfig.setDefaults(playerStreamConfig);
    } 

    InputStream farmingStream = this.plugin.getResource("farmingData.yml");
    if (farmingStream != null) {
      YamlConfiguration farmingStreamConfig = YamlConfiguration
            .loadConfiguration(new InputStreamReader(farmingStream));
      this.farmingConfig.setDefaults(farmingStreamConfig);
    }  

  }

  /**
   * getPlayerConfig:
   * getter for Player config data.
   * 
   * @return FileConfiguration
   */
  public FileConfiguration getPlayerConfig(){
    if (this.playerConfig == null) {
      reloadConfig();
    }
    return this.playerConfig; 
  }

  /**
   * getFarmingConfig:
   * getter for Farming config data
   * 
   * @return FileConfiguration
   */
  public FileConfiguration getFarmingConfig(){
    if (this.farmingConfig == null) {
      reloadConfig();
    }
    return this.farmingConfig; 
  }

  /**
   * savePlayerConfig:
   * Saves Player config data to file.
   * 
   * @return void
   */
  public void savePlayerConfig() {
    if (this.playerConfig == null || this.playerConfigFile == null) {
      return;
    }
    try {
      this.getPlayerConfig().save(this.playerConfigFile);
    } catch (IOException e) {
      plugin.getLogger().log(Level.SEVERE, "PLAYER CONFIG SAVE FAILED!", e);
    }
  }

  /**
   * saveFarmingConfig:
   * Saves Farming config data to file.
   * 
   * @return void
   */
  public void saveFarmingConfig() {
    if (this.farmingConfig == null || this.farmingConfigFile == null) {
      return;
    }
    try {
      this.getFarmingConfig().save(this.farmingConfigFile);
    } catch (IOException e) {
      plugin.getLogger().log(Level.SEVERE, "FARMING CONFIG SAVE FAILED!", e);
    }
  }

  /**
   * saveDefaultConfigs
   * Saves default configs.
   * 
   * @return void
   */
  public void saveDefaultConfigs() {
    if (this.playerConfigFile == null) {
      this.playerConfigFile = new File(this.plugin.getDataFolder(), "playerData.yml");
    }
    if (!this.playerConfigFile.exists()) {
      this.plugin.saveResource("playerData.yml", false);
    }

    if (this.farmingConfigFile == null) {
      this.farmingConfigFile = new File(this.plugin.getDataFolder(), "farmingData.yml");
    }
    if (!this.farmingConfigFile.exists()) {
      this.plugin.saveResource("farmingData.yml", false);
    }
  }

}
