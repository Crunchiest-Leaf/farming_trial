package com.crunchiest.utils;

import com.crunchiest.FarmingTrial;
import com.crunchiest.data.FarmingDataManager;
import com.crunchiest.data.FarmingPotionManager.CustomPotion;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
* Utility methods for applying various potion effects related to farming and agriculture.
*/
public class PotionEffectUtils {
  
  /**
  * Applies a growth effect to a block if it contains a registered crop that can be grown.
  *
  * @param block    The block to apply the growth effect to.
  * @param farmData Farming data manager containing crop information.
  */
  public static void applyGrowthEffect(Block block, FarmingDataManager farmData) {
    if (farmData.getCropToDrop(block.getType()) != null) {
      Ageable ageable = (Ageable) block.getBlockData();
      ageable.setAge(ageable.getMaximumAge());
      block.setBlockData(ageable);
      block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation(),
          20, 0.5, 0.5, 0.5, 0.01);
    }
  }
  
  /**
  * Applies a harvesting effect to a block if it contains a registered mature crop.
  *
  * @param block    The block to apply the harvesting effect to.
  * @param farmData Farming data manager containing crop information.
  */
  public static void applyHarvestingEffect(Block block, FarmingDataManager farmData) {
    if (farmData.getCropToDrop(block.getType()) != null) {
      Ageable ageable = (Ageable) block.getBlockData();
      if (ageable.getAge() == ageable.getMaximumAge()) {
        FarmingUtils.farmDrops(farmData.getCropToDrop(block.getType()), 1,
            farmData.getCropToSeed(block.getType()), 1,
            farmData.getCropMult(block.getType()), block);
        block.setType(Material.AIR);
        block.setBlockData(block.getBlockData());
      }
    }
  }
  
  /**
  * Retrieves a list of blocks affected by a splashed potion based on the projectile hit event.
  *
  * @param event  The ProjectileHitEvent triggered by the potion splash.
  * @param potion The CustomPotion representing the properties of the potion.
  * @return An ArrayList of blocks affected by the potion splash.
  */
  public static ArrayList<Block> getSplashedBlocks(ProjectileHitEvent event, CustomPotion potion) {
    ArrayList<Block> affectedBlocks = new ArrayList<>();
    Block origin = event.getHitBlock();
    if (origin != null) {
      affectedBlocks.addAll(FarmingUtils.getBlocksRadius(origin, potion.getPotionRadius()));
    }
    if (event.getHitEntity() != null) {
      Block entityBlock = event.getHitEntity().getLocation().getBlock();
      affectedBlocks.addAll(FarmingUtils.getBlocksRadius(entityBlock, potion.getPotionRadius()));
    }
    return affectedBlocks;
  }
  
  /**
  * Processes a projectile hit event to identify and validate a custom potion.
  *
  * @param event     The ProjectileHitEvent triggered by the thrown potion.
  * @param potionKey The key used to identify the custom potion in the potion's metadata.
  * @param plugin    The main plugin instance to retrieve necessary managers.
  * @return The validated CustomPotion object if the potion is valid and splashed; otherwise, null.
  */
  public static CustomPotion processCustomPotion(ProjectileHitEvent event, 
      String potionKey, FarmingTrial plugin) {
    if (!(event.getEntity() instanceof ThrownPotion)) {
      return null;
    }
    ThrownPotion potion = (ThrownPotion) event.getEntity();
    ItemMeta meta = potion.getItem().getItemMeta(); 
    
    if (meta == null || !meta.hasDisplayName()) {
      return null;
    }
    
    NamespacedKey key = new NamespacedKey(plugin, potionKey);
    
    if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
      return null;
    }
    
    String potionId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    CustomPotion validatedPotion = plugin.getFarmingPotionManager().getCustomPotion(potionId);
    
    if (validatedPotion == null || !validatedPotion.isSplash()) {
      return null;
    }
    
    return validatedPotion;
  }
  
}