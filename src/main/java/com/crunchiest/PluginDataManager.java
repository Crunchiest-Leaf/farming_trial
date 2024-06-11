package com.crunchiest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginDataManager {

  private Plugin plugin;
  private FileConfiguration playerConfig = null;
  private FileConfiguration farmingConfig = null;
  private File playerConfigFile = null;
  private File farmingConfigFile = null;

  public PluginDataManager(Plugin plugin) {
    this.plugin = plugin;
    // init configs on startup
    saveDefaultConfigs();
  }

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
      YamlConfiguration playerStreamConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(playerStream));
      this.playerConfig.setDefaults(playerStreamConfig);
    } 

    InputStream farmingStream = this.plugin.getResource("farmingData.yml");
    if (farmingStream != null) {
      YamlConfiguration farmingStreamConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(farmingStream));
      this.farmingConfig.setDefaults(farmingStreamConfig);
    }  

  }

  public FileConfiguration getPlayerConfig(){
    if (this.playerConfig == null) {
      reloadConfig();
    }
    return this.playerConfig; 
  }

  public FileConfiguration getFarmingConfig(){
    if (this.farmingConfig == null) {
      reloadConfig();
    }
    return this.farmingConfig; 
  }

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
