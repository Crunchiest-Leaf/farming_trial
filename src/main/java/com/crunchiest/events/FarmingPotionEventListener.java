package com.crunchiest.events;

import com.crunchiest.FarmingTrial;
import com.crunchiest.data.FarmingDataManager;
import com.crunchiest.data.FarmingPotionManager.CustomPotion;
import com.crunchiest.utils.PotionEffectUtils;
import java.util.ArrayList;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

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
* Listener class to handle events related to farming potions.
*/
public class FarmingPotionEventListener implements Listener {
  
  private final FarmingTrial plugin;
  private final FarmingDataManager farmData;
  
  /**
  * Constructor for FarmingPotionEventListener.
  *
  * @param plugin The main plugin instance of FarmingTrial.
  */
  public FarmingPotionEventListener(FarmingTrial plugin) {
    this.plugin = plugin;
    this.farmData = plugin.getFarmingDataManager();
  }
  
  /**
  * Event handler for the Potion of Growth projectile hit event.
  * Applies growth effect to blocks affected by the potion splash.
  *
  * @param event The ProjectileHitEvent triggered by the potion splash.
  */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPotionOfGrowth(ProjectileHitEvent event) {
    CustomPotion customPotion = 
        PotionEffectUtils.processCustomPotion(event, "POTION_OF_GROWTH", plugin);
    if (customPotion == null) {
      return;
    }
    // potentially offload to async process.
    ArrayList<Block> splashedBlocks = PotionEffectUtils.getSplashedBlocks(event, customPotion);
    if (splashedBlocks.isEmpty()) {
      return;
    }
    for (Block block : splashedBlocks) {
      PotionEffectUtils.applyGrowthEffect(block, farmData);
    }
    splashedBlocks.get(0).getWorld().playSound(splashedBlocks.get(0).getLocation(),
        Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
  }
  
  /**
  * Event handler for the Potion of Harvesting projectile hit event.
  * Applies harvesting effect to blocks affected by the potion splash.
  *
  * @param event The ProjectileHitEvent triggered by the potion splash.
  */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPotionOfHarvesting(ProjectileHitEvent event) {
    CustomPotion customPotion = 
        PotionEffectUtils.processCustomPotion(event, "POTION_OF_HARVESTING", plugin);
    if (customPotion == null) {
      return;
    }
    // potentially offload to async process.
    ArrayList<Block> splashedBlocks = PotionEffectUtils.getSplashedBlocks(event, customPotion);
    if (splashedBlocks.isEmpty()) {
      return;
    }
    for (Block block : splashedBlocks) {
      PotionEffectUtils.applyHarvestingEffect(block, farmData);
    }
    splashedBlocks.get(0).getWorld().playSound(splashedBlocks.get(0).getLocation(),
        Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
  }
}