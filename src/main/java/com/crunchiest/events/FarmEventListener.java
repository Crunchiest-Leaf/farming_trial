package com.crunchiest.events;

import com.crunchiest.FarmingTrial;
import com.crunchiest.data.FarmingDataManager;
import com.crunchiest.data.PluginConfigManager;
import com.crunchiest.utils.FarmingUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;



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

public class FarmEventListener implements Listener {
  
  private FarmingTrial plugin;
  private FarmingDataManager farmData;
  private PluginConfigManager pluginData;
  
  
  
  // startup constructor - unpacks farming data & puts into hashmaps for custom enums.
  
  /**
  * FarmEventListener:
  * constructor, used to initialise plugin startup data.
  *
  * @param plugin - instance of main plugin class.
  */
  public FarmEventListener(FarmingTrial plugin) {
    this.plugin = plugin;
    this.farmData = plugin.getFarmingDataManager();
    this.pluginData = plugin.getPluginDataManager();
  }
  

  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerTrample(PlayerInteractEvent event) {
    /**
    * Checks if player has trampled Farmland block w/ a physical interaction;
    * Then Cancels event, resetting block data to its pre-trampled state.
    * permission: farm_trial.trample.toggle
    */
    if (event.getAction() == Action.PHYSICAL) { // physical interaction catches trampling
      Block block = event.getClickedBlock();
      boolean permission = false;
      try {
        permission = pluginData.getPlayerConfig().getConfigurationSection("players")
        .contains(event.getPlayer().getUniqueId().toString());
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, 
            "Problem with Player Config, please check formatting!");
      }
      if ((block == null) || (permission)) {
        return;
      }
      
      Material blockType = block.getType();
      
      if (blockType == Material.FARMLAND) {
        // cancels trample event...
        event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
        // get pre-event block data.
        Farmland farmland = (Farmland) block.getBlockData();
        int moistureLevel = farmland.getMoisture();
        // cancels trample event...
        event.setCancelled(true);
        // ... and resets block data.
        farmland.setMoisture(moistureLevel);
        block.setBlockData(farmland);
      }
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onMobTrample(EntityInteractEvent event) {
    /**
    * Checks if mob has trampled Farmland block w/ a physical interaction;
    * Then Cancels event, resetting block data to its pre-trampled state.
    */
    Block block = event.getBlock();
    Material blockType = block.getType();
    if (blockType == Material.FARMLAND) {
      // get pre-event block data.
      Farmland farmland = (Farmland) block.getBlockData();
      int moistureLevel = farmland.getMoisture();
      // cancels trample event...
      event.setCancelled(true);
      // ... and resets block data.
      farmland.setMoisture(moistureLevel);
      block.setBlockData(farmland);
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityExplode(EntityExplodeEvent event) {
    
    /**
    * onEntityExplode:
    * event listener to grab entity explosions
    * and cancel the damage to farm related blocks.
    */
    if (event.isCancelled()) {
      return;
    }
    List<Block> effectedBlocks = new ArrayList<Block>();
    effectedBlocks.addAll(event.blockList());
    for (Block block : effectedBlocks) {
      if (farmData.getCropToSeed(block.getType()) != null) { 
        block.setType(Material.AIR);
        block.setBlockData(block.getBlockData());
      }
      if (block.getType() == Material.FARMLAND) { 
        Block blockAbove = block.getRelative(BlockFace.UP);
        blockAbove.setType(Material.AIR);
        blockAbove.setBlockData(blockAbove.getBlockData());
      }
      
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerHoe(BlockBreakEvent event) {
    
    /**
    * onPlayerHoe:
    * Event Listener to handle the interaction of the player with farm-blocks;
    * lets fully grown crops harvest with hoe, otherwise refund seed when broken.
    * Harness for the Overall Farm interaction side of things.
    */
    Player player = event.getPlayer();
    if (player == null) {
      return;
    }
    
    ItemStack heldItem = player.getInventory().getItemInMainHand();
    Material heldItemType = heldItem.getType();
    Block block = event.getBlock();
    if (block == null) {
      return;
    }
    
    Material blockType = block.getType();
    Material blockTypeDrop = blockType;
    
    if (farmData.getCropToSeed(blockType) != null) {
      Ageable ageable = (Ageable) block.getBlockData();
      int age = ageable.getAge();
      int maxAge = ageable.getMaximumAge();
      
      if (age < maxAge || farmData.checkHoeExists(heldItemType) == false) {
        // Crop not fully grown or not using a hoe
        if (blockTypeDrop != Material.AIR) {
          FarmingUtils.farmDrops(farmData.getCropToDrop(blockTypeDrop), 0, 
                farmData.getCropToSeed(blockTypeDrop), 1, 
              farmData.getCropMult(blockType), block);
        }
      } else {
        // Crop fully grown and using a hoe
        block.setType(blockType);
        ageable.setAge(0);
        block.setBlockData(ageable);
        
        int[] dropCounts = FarmingUtils.dropCount(farmData.getHoeTier(heldItemType), 
            FarmingUtils.getEnchantmentLevel(heldItem, Enchantment.LOOT_BONUS_BLOCKS));
        FarmingUtils.farmDrops(farmData.getCropToDrop(blockType), dropCounts[0], 
            farmData.getCropToSeed(blockType), 
            dropCounts[1], farmData.getCropMult(blockType), block);
        FarmingUtils.randomFarmEvent(block, player, plugin);
        FarmingUtils.damageHoe(heldItem, player);
      }
      event.setDropItems(false);
    }
  }
  
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockFlowToCrop(BlockFromToEvent event) {
    /** 
    *  onBlockFlowToCrop:
    *  Event Listener to handle flow of block onto crops;
    *  Prevents water from giving player crop drops.
    *  by cancelling block flow event onto crops.
    */
    if (event.getToBlock().getBlockData() != null) {
      Block block = event.getToBlock();
      Material blockType = block.getType();
      
      if (farmData.getCropToSeed(blockType) != null) {
        event.setCancelled(true);
        // refund seed
        if (blockType != Material.valueOf("AIR")) {
          FarmingUtils.farmDrops(farmData.getCropToDrop(blockType), 
              0, farmData.getCropToSeed(blockType), 
              1, farmData.getCropMult(blockType), block);
        }
        block.setType(Material.AIR, true);
        block.setBlockData(block.getBlockData());
      }
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBucketFarmland(PlayerBucketEmptyEvent event) {
    /** 
    *  onWaterFarmland:
    *  Event Listener to stop water/lava place under crops;
    *  by replacing items dropped.
    */
    Block block = event.getBlockClicked();
    Block blockAbove = block.getRelative(BlockFace.UP);
    if (block.getType() == Material.FARMLAND && farmData.getCropToDrop(blockAbove.getType()) != null) {
      // refund seed
      if (blockAbove.getType() != Material.valueOf("AIR")) {
        FarmingUtils.farmDrops(farmData.getCropToDrop(blockAbove.getType()),     
            0, farmData.getCropToSeed(blockAbove.getType()), 
            1, farmData.getCropMult(blockAbove.getType()), block);
      }
      blockAbove.setType(Material.AIR);
      blockAbove.setBlockData(blockAbove.getBlockData());
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onDispenseLiquid(BlockDispenseEvent event) {
    /** 
    *  onDispenseLiquid:
    *  Event Listener to stop water/lava place under crops;
    *  via dispensers, cancels event if facing crops and using
    *  water / lava bucket.
    */
    ItemStack dispensedItem = event.getItem();
    Block block = event.getBlock();
    if (dispensedItem.getType() == Material.WATER_BUCKET 
          || dispensedItem.getType() == Material.LAVA_BUCKET) {
      if (block.getType() == Material.DISPENSER) {
        Dispenser dispenser = (Dispenser) block.getBlockData();
        Directional direction = (Directional) dispenser;
        Block targetBlock = block.getRelative(direction.getFacing());
        if (farmData.getCropToDrop(targetBlock.getType()) != null) {
          event.setCancelled(true);
        }
      } 
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockUnderBreak(BlockBreakEvent event) {  
    
    /** 
    *  onBlockUnderBreak:
    *  Event Listener to handle break of block below crops;
    *  Prevents block under break from giving player crop drops.
    *  by cancelling block break event.
    */
    Block block = event.getBlock();
    Block blockAbove = block.getRelative(BlockFace.UP);
    Material blockAboveType = blockAbove.getType();
    if (farmData.getCropToSeed(blockAboveType) != null) {
      blockAbove.setType(Material.AIR, true);
      blockAbove.setBlockData(blockAbove.getBlockData());
      // refund seed
      if (blockAboveType != Material.valueOf("AIR")) {
        FarmingUtils.farmDrops(farmData.getCropToDrop(blockAboveType), 
            0, farmData.getCropToSeed(blockAboveType), 
            1, farmData.getCropMult(blockAboveType), blockAbove);
      }
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPistonPullCrop(BlockPistonRetractEvent event) {
    
    /** 
    *  onPistonPullCrop:
    *  Event Listener to handle piston(pull) breaking crops;
    *  Prevents piston from pulling itself, or other blocks from crops.
    *  by cancelling piston extent event.
    */
    List<Block> effectedBlocks = new ArrayList<Block>(event.getBlocks());
    for (int i = 0; i < effectedBlocks.size(); i++) {
      Block checked = effectedBlocks.get(i);
      if (farmData.getCropToSeed(checked.getType()) != null 
          || (checked.getType() == Material.FARMLAND)) {
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPistonPushCrop(BlockPistonExtendEvent event) {
    
    /** 
    *  onPistonPushCrop:
    *  Event Listener to handle piston(push) breaking crops;
    *  Prevents piston from pushing itself, or other blocks into crops.
    *  by cancelling piston extent event.
    */
    List<Block> effectedBlocks = new ArrayList<Block>(event.getBlocks());
    for (int i = 0; i < effectedBlocks.size(); i++) {
      Block checked = effectedBlocks.get(i);
      if (farmData.getCropToSeed(checked.getType()) != null 
            || (checked.getType() == Material.FARMLAND)) {
        event.setCancelled(true);
      }
    }
  }
}




