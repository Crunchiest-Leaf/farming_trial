package com.crunchiest;

import com.crunchiest.Plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
 * duthor: Crunchiest_Leaf
 *
 * desc: Trial Plugin for LOTC java team
 *       see link for outline.
 * 
 * link: https://docs.google.com/document/d/1zpQpmroUDSb7b6XRdxoifJIs6ig295lM0LOI0gdOvGk/edit#heading=h.h6zgogey5tcq
 * 
 */

public class FarmEventListener implements Listener {

  private Plugin plugin;
  
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
  public FarmEventListener(Plugin plugin) {
    this.plugin = plugin;
    ConfigurationSection tools = plugin.data.getFarmingConfig()
          .getConfigurationSection("tools");
    ConfigurationSection crops = plugin.data.getFarmingConfig()
          .getConfigurationSection("crops");

    plugin.getLogger().log(Level.INFO, "----------------");
    plugin.getLogger().log(Level.INFO, "- LOADING TOOLS -");
    for (String key : tools.getKeys(false)) {
      try {
        Material tool = Material.valueOf(key.toUpperCase());
        Integer value = plugin.data.getFarmingConfig().getInt("tools." + key); 
        hoeTiers.put(tool, value); 
        plugin.getLogger().log(Level.INFO, "Tool added: " + key);
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Failed to add tool: " + key);
      }
    }

    plugin.getLogger().log(Level.INFO, "----------------");
    plugin.getLogger().log(Level.INFO, "- LOADING CROPS -");
    for (String key : crops.getKeys(false)) {
      try {
        Material crop = Material.valueOf(key.toUpperCase());
        Material seed = Material.valueOf(plugin.data.getFarmingConfig()
              .getString("crops." + key + ".seed").toUpperCase());
        Material drop = Material.valueOf(plugin.data.getFarmingConfig()
              .getString("crops." + key + ".drop").toUpperCase());
        Float mult = Float.parseFloat(plugin.data.getFarmingConfig()
              .getString("crops." + key + ".multiplier"));
        cropToSeed.put(crop, seed);
        cropToDrop.put(crop, drop);
        cropToMult.put(crop, mult);
        plugin.getLogger().log(Level.INFO, "Crop added: " + key);
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Failed to add crop: " + key);
      }
    } 
    plugin.getLogger().log(Level.INFO, "----------------");
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
    
    int[] counts = new int[2];
    int seedCount = 1 + enchantTier; // min drop one seed.
    int cropCount = toolTier + enchantTier;
    
    counts[0] = cropCount;
    counts[1] = seedCount;
    
    return counts;
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
   * @param player    - player object.
   * @return void
   */
  private void farmDrops(Material crop, int cropCount, Material seed, 
                        int seedCount, float mult, Block block) {

    seedCount = (int) Math.ceil(seedCount * mult);
    cropCount = (int) Math.ceil(cropCount * mult);
    if (seedCount >= 1) {
      ItemStack seedDrop = new ItemStack(seed, seedCount);
      block.getWorld().dropItemNaturally(block.getLocation(), seedDrop);
    }
    if (cropCount >= 1) {
      ItemStack cropDrop = new ItemStack(crop, cropCount);
      block.getWorld().dropItemNaturally(block.getLocation(), cropDrop);
    }
  }
  
  /** 
   * FortuneTier_toInteger:
   * Method to give Integer Value to
   * 'fortune' enchant levels.
   * +1 for every level of fortune.
   *  
   * @param hoe - hoe itemstack object.
   * @return level - custom integer hoe level.
   */
  private int fortuneTierToInteger(ItemStack hoe) {
    Map<Enchantment, Integer> enchantments = hoe.getEnchantments();
    if (hoe.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
      int level = enchantments.get(Enchantment.LOOT_BONUS_BLOCKS);
      return level;
    } else {
      return 0;
    }
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
    int damageModifier = 1; // modify for changes to default durability
    org.bukkit.inventory.meta.Damageable dMeta = (org.bukkit.inventory.meta.Damageable) hoe.getItemMeta();
    int currentDura = dMeta.getDamage();
    int newDura = currentDura + damageModifier;
    int maxDamage = hoe.getType().getMaxDurability();
    
    if ((newDura) <= maxDamage) {
      dMeta.setDamage(newDura);
      hoe.setItemMeta(dMeta);
    } else {
      player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
      hoe.setAmount(0);
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
    org.bukkit.inventory.meta.Damageable dMeta = (org.bukkit.inventory.meta.Damageable) hoe.getItemMeta();
    dMeta.setDamage(0);
    hoe.setItemMeta(dMeta);
    player.playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, 1, 1);
    player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1, 1);

  }

  /**
   * potatoRain:
   * Method to spray an explosion of item from ground
   * on farming event roll.
   * 
   * @param player - block at given farm location.
   * @return void
   */
  private void potatoRain(Player player) {
    final World world = player.getWorld();
    final Player location = player; 
    for (int i = 0; i < 400; i += 10) {
      final int x = i;
      Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
              int randomNum = ThreadLocalRandom.current().nextInt(-5, 5);
              Location drop = location.getLocation();
              double playerX = location.getLocation().getX();
              double playerY = location.getLocation().getY();
              double playerZ = location.getLocation().getZ();
              drop.setX(playerX + randomNum);
              drop.setY(playerY + x);
              drop.setZ(playerZ + randomNum);
              ItemStack potatoes = new ItemStack(Material.BAKED_POTATO, 1);
              Item potato = world.dropItem(drop, potatoes); 
              potato.setPickupDelay(-32768);
              potato.setTicksLived(5600);
              potato.setGlowing(true);
            }
        }, 10L);
    }
    world.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 10, 1);
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
    if (val > min && val < max) return true;
    return false;
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

    } else if (rangeCheck(2, 5, randomNum)) {
      //completely repair hoe
      fixHoe(player.getInventory().getItemInMainHand(), player);
      player.sendMessage(ChatColor.GOLD 
          + "The Farm spirits have Blessed your Hoe with new Life!");

    } else if (rangeCheck(5, 10, randomNum)) { 
      //potato rain. rains cooked potatoes.
      potatoRain(player);
      player.sendMessage(ChatColor.GOLD 
          + "The Potato Gods have noticed your Efforts. Potato Rain!");

    } else if (rangeCheck(12, 16, randomNum)) { 
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
      Boolean permission = plugin.data.getPlayerConfig().getConfigurationSection("players").contains(event.getPlayer().getUniqueId().toString());
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
      if (block.getType() == Material.FARMLAND || cropToSeed.get(block.getType()) != null) { 
        event.blockList().remove(block);
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
     * permission: farm_trial.trample.toggle
     */
    
    Player player = event.getPlayer();
    
    ItemStack heldItem = player.getInventory().getItemInMainHand();
    
    if ((event.getPlayer() != null)) {
      Block block = event.getBlock();
      if (block == null) {
        return;
      }

      Material blockType = block.getType();
      Material blockTypeDrop = blockType;
      
      // filters out other non-farm blocks
      if (cropToSeed.get(blockType) != null) {
        Ageable ageable = (Ageable) block.getBlockData();
        
        if (ageable.getAge() < ageable.getMaximumAge() 
            || (hoeTiers.get(heldItem.getType()) == null)) {
          // if crop not grown, or not using hoe
          // break crop, returning seeds.
          if (blockTypeDrop != Material.valueOf("AIR")) {
            farmDrops(cropToDrop.get(blockTypeDrop), 0, cropToSeed.get(blockTypeDrop), 
                1, cropToMult.get(blockType), block);
          }
        } else {
          // if crop grown, and using hoe
          // Harvest crop, and reset to baby crop.
          block.setType(blockType);
          ageable.setAge(0);
          block.setBlockData(ageable);
          // handle drops & hoe durability change.
          int[] dropCounts = new int[2];
          dropCounts = dropCount(hoeTiers.get(heldItem.getType()), fortuneTierToInteger(heldItem));
          farmDrops(cropToDrop.get(blockType), dropCounts[0], cropToSeed.get(blockType), 
                    dropCounts[1], cropToMult.get(blockType), block);
          randomFarmEvent(block, player);
          damageHoe(heldItem, player);
        }
        event.setDropItems(false);
      }
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
     *  by cancelling bucket flow event onto crops.
     */
    Block block = event.getBlockClicked();
    Block blockAbove = block.getRelative(BlockFace.UP);
    if (block.getType() == Material.FARMLAND && cropToDrop.get(blockAbove.getType()) != null) {
      if (blockAbove.getType() != Material.valueOf("AIR")) {
        farmDrops(cropToDrop.get(blockAbove.getType()), 0, cropToSeed.get(blockAbove.getType()), 
            1, cropToMult.get(blockAbove.getType()), block);
      }
      blockAbove.setType(Material.AIR);
      blockAbove.setBlockData(blockAbove.getBlockData());
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
      if (blockAboveType != Material.valueOf("AIR")) {
        farmDrops(cropToDrop.get(blockAboveType), 0, cropToSeed.get(blockAboveType), 
            1, cropToMult.get(blockAboveType), blockAbove);
      }
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPistonPullCrop(BlockPistonRetractEvent event) {

    /** 
     *  onPistonPushCrop:
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




