package com.crunchiest.events;

import com.crunchiest.FarmingTrial;
import com.crunchiest.data.FarmingDataManager;
import com.crunchiest.data.FarmingPotionManager;
import com.crunchiest.data.FarmingPotionManager.CustomPotion;
import com.crunchiest.utils.FarmingUtils;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


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

public class FarmingPotionEventListener implements Listener {
  private FarmingTrial plugin;
  private FarmingPotionManager potions;
  private FarmingDataManager farmData;
  
  /** 
  * FarmingPotionEventListener:
  * Constructor.
  *
  * @param plugin main plugin instance.
  */
  public FarmingPotionEventListener(FarmingTrial plugin) {
    this.plugin = plugin;
    this.potions = plugin.getFarmingPotionManager();
    this.farmData = plugin.getFarmingDataManager();
  }
  
  /** 
  * getBlocksRadius:
  * returns list of blocks
  * in radius around given block.
  *
  * @param origin origin block hit by potion.
  * @param radius radius of potion effect.
  * @return ArrayList of blocks effected.
  */
  private ArrayList<Block> getBlocksRadius(Block origin, int radius) {
    ArrayList<Block> blocks = new ArrayList<>();
    if (origin == null) {
      plugin.getLogger().log(Level.SEVERE, "BLOCK NULL?!");
      return blocks;
    }
    
    World world = origin.getWorld();
    Location originLoc = origin.getLocation();
    int minX = (int) (originLoc.getX() - radius);
    int maxX = (int) (originLoc.getX() + radius);
    int minY = (int) (originLoc.getY() - radius);
    int maxY = (int) (originLoc.getY() + radius);
    int minZ = (int) (originLoc.getZ() - radius);
    int maxZ = (int) (originLoc.getZ() + radius);
    
    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          Location loc = new Location(world, x, y, z);
          blocks.add(loc.getBlock());
        }
      }
    }
    return blocks;
  }
  
  /** 
  * onPotionOfGrowth:
  * Event handler.
  * Manages use of POTION_OF_GROWTH
  *
  * @param event projectile hit event.
  */

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPotionOfGrowth(ProjectileHitEvent event) {
    CustomPotion customPotion = processCustomPotion(event, "POTION_OF_GROWTH");
    if (customPotion != null) {
      int potionRadius = customPotion.getPotionRadius();
      Block origin = event.getHitBlock();
      ArrayList<Block> splashedBlocks = getBlocksRadius(origin, potionRadius);
      
      for (Block block : splashedBlocks) {
        Material cropType = block.getType();
        
        // Check if the block is a crop and age it to max age
        if (farmData.getCropToDrop(cropType) != null) {
          Ageable ageable = (Ageable) block.getBlockData();
          ageable.setAge(ageable.getMaximumAge());
          block.setBlockData(ageable);
          block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation(), 
              20, 0.5, 0.5, 0.5, 0.01);
        }
      }
      origin.getWorld().playSound(origin.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
    }
  }

  /** 
  * onPotionOfHarvesting:
  * Event handler.
  * Manages use of POTION_OF_HARVESTING
  *
  * @param event projectile hit event.
  */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPotionOfHarvesting(ProjectileHitEvent event) {
    CustomPotion customPotion = processCustomPotion(event, "POTION_OF_HARVESTING");
    if (customPotion != null) {
      int potionRadius = customPotion.getPotionRadius();
      Block origin = event.getHitBlock();
      ArrayList<Block> splashedBlocks = getBlocksRadius(origin, potionRadius);
      
      for (Block block : splashedBlocks) {
        Material cropType = block.getType();
        // Check if the block is a crop and age it to max age
        if (farmData.getCropToDrop(cropType) != null) {
          Ageable ageable = (Ageable) block.getBlockData();
          if (ageable.getAge() == ageable.getMaximumAge()) {
            FarmingUtils.farmDrops(farmData.getCropToDrop(cropType), 1, 
                farmData.getCropToSeed(cropType), 1, 
                farmData.getCropMult(cropType), block);
            block.setType(Material.AIR);
            block.setBlockData(block.getBlockData());
          }
        }
      }
      origin.getWorld().playSound(origin.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
    }
  }
  
  /** 
  * processCustomPotion:
  * processes custom item for validation.
  * ensures custom potion is registered, and of chosen type.
  *
  *
  * @param event projectile hit event.
  * @param potionKey key of registered potion type to be validated.
  * @return CustomPotion. if not null, valid.
  */
  private CustomPotion processCustomPotion(ProjectileHitEvent event, String potionKey) {
    
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
    CustomPotion validatedPotion;
    validatedPotion = potions.getCustomPotion(potionId);
    
    if (validatedPotion == null || !validatedPotion.isSplash()) {
      return null;
    }
    
    // Optionally perform additional checks or actions here
    
    return validatedPotion;
  }
  // ADD MORE LISTENERS FOR MORE POTION EFFECTS!
}
