package com.crunchiest;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

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
  Plugin plugin = this;

  public void onEnable()
  {
    LOGGER.info("farming_trial enabled");
    getServer().getPluginManager().registerEvents(new FarmEventListener(), this);
    getCommand("set_trampling").setExecutor(new FarmCommands());
    getCommand("set_trampling").setTabCompleter(new FarmTabComplete());
  }

  public void onDisable()
  {
    LOGGER.info("farming_trial disabled");
  }
}
