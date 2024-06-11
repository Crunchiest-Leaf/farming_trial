package com.crunchiest;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;


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

public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER=Logger.getLogger("farming_trial");

  public PluginDataManager data;

  Plugin plugin = this;

  public void onEnable() {
    this.data = new PluginDataManager(plugin);
    LOGGER.info("FarmingTrial enabled");
    getServer().getPluginManager().registerEvents(new FarmEventListener(plugin), this);
    getCommand("toggle_trampling").setExecutor(new FarmCommands(plugin));
    getCommand("toggle_trampling").setTabCompleter(new FarmTabComplete());
  }

  public void onDisable()
  {
    LOGGER.info("FarmingTrial disabled");
  }
}
