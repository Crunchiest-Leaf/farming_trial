package com.crunchiest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
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
import org.bukkit.inventory.meta.Damageable;



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
  
  // hashmaps for configurable enums
  private HashMap<Material, Integer> hoeTiers = new HashMap<Material, Integer>();
  private HashMap<Material, Material> cropToSeed = new HashMap<Material, Material>();
  private HashMap<Material, Material> cropToDrop = new HashMap<Material, Material>();
  private HashMap<Material, Float> cropToMult = new HashMap<Material, Float>();
  
  // startup constructor - unpacks farming data & puts into hashmaps for custom enums.
  
  /**
  * FarmEventListener:
  * constructor, used to initialise plugin startup data.
  *
  * @param plugin - instance of main plugin class.
  */
  public FarmEventListener(FarmingTrial plugin) {
    this.plugin = plugin;
    loadTools();
    loadCrops();
    
  }
  
  private void loadTools() {
    plugin.getLogger().log(Level.INFO, "---------------------");
    plugin.getLogger().log(Level.INFO, "- - LOADING TOOLS - -");
    ConfigurationSection tools = plugin.data.getFarmingConfig().getConfigurationSection("tools");
    if (tools == null) {
      plugin.getLogger().log(Level.WARNING, "Section 'tools' not found in configuration.");
      return;
    }
    
    for (String key : tools.getKeys(false)) {
      try {
        Material tool = Material.valueOf(key.toUpperCase());
        int value = plugin.data.getFarmingConfig().getInt("tools." + key);
        hoeTiers.put(tool, value);
        plugin.getLogger().log(Level.INFO, "Tool added: " + key);
      } catch (IllegalArgumentException e) {
        plugin.getLogger().log(Level.WARNING, "Failed to add tool: " + key);
      }
    }
    plugin.getLogger().log(Level.INFO, "- - TOOLS  LOADED - -");
    plugin.getLogger().log(Level.INFO, "---------------------");
  }
  
  private void loadCrops() {
    plugin.getLogger().log(Level.INFO, "- - LOADING CROPS - -");
    ConfigurationSection crops = plugin.data.getFarmingConfig().getConfigurationSection("crops");
    if (crops == null) {
      plugin.getLogger().log(Level.WARNING, "Section 'crops' not found in configuration.");
      return;
    }
    
    for (String key : crops.getKeys(false)) {
      try {
        Material crop = Material.valueOf(key.toUpperCase());
        String seedKey = "crops." + key + ".seed";
        String dropKey = "crops." + key + ".drop";
        String multKey = "crops." + key + ".multiplier";
        
        Material seed = Material.valueOf(plugin.data.getFarmingConfig()
            .getString(seedKey).toUpperCase());
        Material drop = Material.valueOf(plugin.data.getFarmingConfig()
              .getString(dropKey).toUpperCase());
        float mult = Float.parseFloat(plugin.data.getFarmingConfig().getString(multKey));
        
        cropToSeed.put(crop, seed);
        cropToDrop.put(crop, drop);
        cropToMult.put(crop, mult);
        
        plugin.getLogger().log(Level.INFO, "Crop added: " + key);
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Failed to add crop: " + key);
      }
    }
    plugin.getLogger().log(Level.INFO, "- - CROPS  LOADED - -");
    plugin.getLogger().log(Level.INFO, "---------------------");
  }
  
  
  /**
  * dropCount:
  * Simple dropCount Calculator Method
  * Takes in tool tier and enchantment
  * Outputs number of items to drop.
  * 
  * @param toolTier    - custom integer value of hoe level
  * @param enchantTier - custom integer value of hoe level
  * @return counts - array of crop and seed counts.
  */
  
  private int[] dropCount(int toolTier, int enchantTier) {
    return new int[]{toolTier + enchantTier, 1 + enchantTier};
  }
  
  /**
  * FarmDrops:
  * Method to Carry out Item Drops.
  * Takes in Item Values and Player Loc
  * Drops Items at player Loc Naturally.
  * 
  * @param crop      - crop type to drop.
  * @param cropCount - Amount of crop.
  * @param seed      - seed type to drop.
  * @param seedCount - Amount of seed.
  * @param mult      - seed Multiplier.
  * @param player    - player object.
  * @return void
  */
  private void farmDrops(Material crop, int cropCount, Material seed, int seedCount, float mult, Block block) {
    World world = block.getWorld();
    Location loc = block.getLocation();
    if (seedCount > 0) {
      world.dropItemNaturally(loc, new ItemStack(seed, 
            (int) Math.ceil((double) (seedCount * mult))));
    }
    if (cropCount > 0) {
      world.dropItemNaturally(loc, new ItemStack(crop, 
          (int) Math.ceil((double) (seedCount * mult))));
    }
  }

  /**
  * Gets the level of a specific enchantment on an item.
  *
  * @param item the item stack
  * @param enchantment the enchantment
  * @return the level of the enchantment
  */
  private int getEnchantmentLevel(ItemStack item, Enchantment enchantment) {
    return item.getEnchantmentLevel(enchantment);
  }
  
  /**
  * DamageHoe:
  * Handles Durability Changes
  * of hoe used to farm.
  * Hoe breaks on max durability use.
  * 
  * @param hoe    - hoe itemstack object.
  * @param player - player object.
  * @return void
  */
  private void damageHoe(ItemStack hoe, Player player) {
    int unbreakingLevel = getEnchantmentLevel(hoe, Enchantment.DURABILITY);
    if (ThreadLocalRandom.current().nextInt(unbreakingLevel + 1) == 0) {
      Damageable meta = (Damageable) hoe.getItemMeta();
      meta.setDamage(meta.getDamage() + 1);
      if (meta.getDamage() < hoe.getType().getMaxDurability()) {
        hoe.setItemMeta(meta);
      } else {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        hoe.setAmount(0);
      }
    }
  }
  
  /**
  * fixHoe:
  * Handles Durability Changes.
  * restores durability to max.
  * (used for random event).
  * 
  * @param hoe    - hoe itemstack object.
  * @param player - player object.
  * @return void
  */

  private void fixHoe(ItemStack hoe, Player player) {
    Damageable meta = (Damageable) hoe.getItemMeta();
    meta.setDamage(0);
    hoe.setItemMeta(meta);
    player.playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, 1, 1);
    player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1, 1);
  }
  
  /**
  * potatoRain:
  * Method to make potatoes rain from sky
  * on farming event roll.
  * 
  * @param player - block at given farm location.
  * @return void
  */
  private void potatoRain(Player player) {
    World world = player.getWorld();
    Location loc = player.getLocation();
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      for (int i = 0; i < 40; i++) {
        Location dropLoc = loc.clone().add(ThreadLocalRandom.current().nextInt(-5, 6), i * 10, 
            ThreadLocalRandom.current().nextInt(-5, 6));
        Item potato = world.dropItem(dropLoc, new ItemStack(Material.BAKED_POTATO, 1));
        potato.setPickupDelay(Integer.MAX_VALUE);
        potato.setTicksLived(5600);
        potato.setGlowing(true);
      }
    }, 10L);
    world.playSound(loc, Sound.ENTITY_EVOKER_CAST_SPELL, 10, 1);
}
  
  /**
  * SpawnNamedEntity:
  * Method to spawn random entity
  * on farming event roll.
  * 
  * @param locBlock - block at given farm location.
  * @param entity   - entity type to spawn.
  * @param name     - name to give entity.
  * @return void
  */
  private void spawnNamedEntity(Block locBlock, EntityType entity, String name) {
    World world = locBlock.getWorld();
    Entity villager = world.spawnEntity(locBlock.getLocation(), entity);
    villager.setCustomName(name);
  }
  
  /**
  * rangeCheck:
  * Method for random event
  * control flow.
  * 
  * returns true if range satisfied.
  * 
  * @param min - minimum value in range.
  * @param max - maximum value in range.
  * @param val - value to test against.
  * @return boolean
  */
  private boolean rangeCheck(int min, int max, int val) {
    return (val > min && val < max);
  }
  
  /**
  * randomFarmEvent:
  * Method to random roll table
  * a set of possible events.
  * MobDrops
  * todo: ItemDrops.
  * 
  * @param block  - block at given farm location.
  * @param player - player object.
  * @return void
  */
  private void randomFarmEvent(Block block, Player player) {
    int randomNum = ThreadLocalRandom.current().nextInt(0, 1000 + 1);
    if (rangeCheck(0, 2, randomNum)) {
      //spawn undead farmer mob
      player.getWorld().playSound(block.getLocation(), Sound.ENTITY_WITCH_AMBIENT, 10, 1);
      spawnNamedEntity(block, EntityType.ZOMBIE_VILLAGER, ChatColor.DARK_PURPLE + "Undead Farmer");
      player.sendMessage(ChatColor.LIGHT_PURPLE 
          + "Unded Farmer: \"ARGGGHHHHH\"");
      
    } else if (rangeCheck(5, 20, randomNum)) {
      //completely repair hoe
      fixHoe(player.getInventory().getItemInMainHand(), player);
      player.sendMessage(ChatColor.GOLD 
          + "The Farm spirits have Blessed your Hoe with new Life!");
      
    } else if (rangeCheck(21, 25, randomNum)) { 
      //potato rain. rains cooked potatoes.
      potatoRain(player);
      player.sendMessage(ChatColor.GOLD 
          + "The Potato Gods have noticed your Efforts. Potato Rain!");
      
    } else if (rangeCheck(26, 30, randomNum)) { 
      //fish?!
      player.getWorld().playSound(block.getLocation(), Sound.ENTITY_DOLPHIN_PLAY, 10, 1);
      spawnNamedEntity(block, EntityType.COD, ChatColor.AQUA + "FISH?!");
      player.sendMessage(ChatColor.AQUA + "FISH?!");
      
    } //else if (rangeCheck(min, max, randomNum))
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
        permission = plugin.data.getPlayerConfig().getConfigurationSection("players")
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
      if (cropToSeed.get(block.getType()) != null) { 
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
    
    if (cropToSeed.containsKey(blockType)) {
      Ageable ageable = (Ageable) block.getBlockData();
      int age = ageable.getAge();
      int maxAge = ageable.getMaximumAge();
      
      if (age < maxAge || hoeTiers.get(heldItemType) == null) {
        // Crop not fully grown or not using a hoe
        if (blockTypeDrop != Material.AIR) {
          farmDrops(cropToDrop.get(blockTypeDrop), 0, 
                cropToSeed.get(blockTypeDrop), 1, 
                cropToMult.get(blockType), block);
        }
      } else {
        // Crop fully grown and using a hoe
        block.setType(blockType);
        ageable.setAge(0);
        block.setBlockData(ageable);
        
        int[] dropCounts = dropCount(hoeTiers.get(heldItemType), 
            getEnchantmentLevel(heldItem, Enchantment.LOOT_BONUS_BLOCKS));
        farmDrops(cropToDrop.get(blockType), dropCounts[0], cropToSeed.get(blockType), 
            dropCounts[1], cropToMult.get(blockType), block);
        randomFarmEvent(block, player);
        damageHoe(heldItem, player);
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
      
      if (cropToSeed.get(blockType) != null) {
        event.setCancelled(true);
        // refund seed
        if (blockType != Material.valueOf("AIR")) {
          farmDrops(cropToDrop.get(blockType), 0, cropToSeed.get(blockType), 
              1, cropToMult.get(blockType), block);
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
    if (block.getType() == Material.FARMLAND && cropToDrop.get(blockAbove.getType()) != null) {
      // refund seed
      if (blockAbove.getType() != Material.valueOf("AIR")) {
        farmDrops(cropToDrop.get(blockAbove.getType()), 0, cropToSeed.get(blockAbove.getType()), 
             1, cropToMult.get(blockAbove.getType()), block);
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
        if (cropToDrop.get(targetBlock.getType()) != null) {
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
    if (cropToSeed.get(blockAboveType) != null) {
      blockAbove.setType(Material.AIR, true);
      blockAbove.setBlockData(blockAbove.getBlockData());
      // refund seed
      if (blockAboveType != Material.valueOf("AIR")) {
        farmDrops(cropToDrop.get(blockAboveType), 0, cropToSeed.get(blockAboveType), 
             1, cropToMult.get(blockAboveType), blockAbove);
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
      if (cropToSeed.get(checked.getType()) != null || (checked.getType() == Material.FARMLAND)) {
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
      if (cropToSeed.get(checked.getType()) != null || (checked.getType() == Material.FARMLAND)) {
        event.setCancelled(true);
      }
    }
  }
}




