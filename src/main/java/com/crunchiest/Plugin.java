package com.crunchiest;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

/*
 * farming_trial java plugin
 */
public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER=Logger.getLogger("farming_trial");

  Plugin plugin = this;
  Player player;

  public void onEnable()
  {
    LOGGER.info("farming_trial enabled");
    getServer().getPluginManager().registerEvents(new FarmEventListener(), this);
  }

  public void onDisable()
  {
    LOGGER.info("farming_trial disabled");
  }

  
}
