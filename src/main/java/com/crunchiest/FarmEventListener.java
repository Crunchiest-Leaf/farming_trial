package com.crunchiest;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
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
  
  private static final Logger LOGGER=Logger.getLogger("farming_trial"); // logger for debugging
  
  // lookup dictionaries for Accepted Farm crops & tools.
  private static Dictionary<Material, Integer> hoeTiers = new Hashtable<>();
  private static Dictionary<Material, Material> cropToSeed = new Hashtable<>();
  private static Dictionary<Material, Material> cropToDrop = new Hashtable<>();

  static { // static initialiser, runs onEnable().
    initMaterialLookups(); 
  } 
  
  /**
   *  InitMaterialLookups:
   *  initialised main plugin lookups
   *  for internal use.
   *  statically enabled onEnable()
   *  modify / add for extra functionality
   * 
   *  todo: maybe throw in a config file.
   */
  private static void initMaterialLookups() {
    // Dictionary to lookup custom hoe value enums
    hoeTiers.put(Material.WOODEN_HOE, 1);
    hoeTiers.put(Material.STONE_HOE, 1);
    hoeTiers.put(Material.IRON_HOE, 2);
    hoeTiers.put(Material.GOLDEN_HOE, 2);
    hoeTiers.put(Material.DIAMOND_HOE, 3);
    hoeTiers.put(Material.NETHERITE_HOE, 3);
    
    // Dictionary to lookup crop to seed drop enums
    cropToSeed.put(Material.WHEAT, Material.WHEAT_SEEDS);
    cropToSeed.put(Material.CARROTS, Material.CARROT);
    cropToSeed.put(Material.POTATOES, Material.POTATO);
    cropToSeed.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
    
    // Dictionary to lookup crop to crop drop enums
    cropToDrop.put(Material.WHEAT, Material.WHEAT);
    cropToDrop.put(Material.CARROTS, Material.CARROT);
    cropToDrop.put(Material.POTATOES, Material.POTATO);
    cropToDrop.put(Material.BEETROOTS, Material.BEETROOT);
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
                        int seedCount, Player player) {
    
    ItemStack cropDrop = new ItemStack(crop, cropCount);
    ItemStack seedDrop = new ItemStack(seed, seedCount);
    
    player.getWorld().dropItemNaturally(player.getLocation(), cropDrop);
    player.getWorld().dropItemNaturally(player.getLocation(), seedDrop);
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
    world.playSound(locBlock.getLocation(), Sound.ENTITY_WITCH_AMBIENT, 10, 1);
    villager.setCustomName(name);
  }
  
  /**
   * RandomFarmEvent:
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
    int randomNum = ThreadLocalRandom.current().nextInt(0, 10 + 1);
    switch (randomNum) {
      case 0:
        spawnNamedEntity(block, EntityType.ZOMBIE_VILLAGER, "Undead Farmer");
        break;
      // add cases for other events.
      default:
      ;;
    }
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
      if ((block == null) || (event.getPlayer().hasPermission("farm_trial.trample.toggle"))) {
        return;
      }
      
      Material blockType = block.getType();
      
      if (blockType == Material.FARMLAND) {
        // cancels trample event...
        event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
        event.setCancelled(true);
        
        // ... and resets block data.
        block.setType(blockType);
        block.setBlockData(block.getBlockData());
      }
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerHoe(PlayerInteractEvent event) {

    /**
     * onPlayerHoe:
     * Event Listener to handle the interaction of the player with farm-blocks;
     * Harness for the Overall Farm interaction side of things.
     * permission: farm_trial.trample.toggle
     */
    
    Player player = event.getPlayer();
    
    ItemStack heldItem = player.getInventory().getItemInMainHand();
    
    if ((event.getAction() == Action.LEFT_CLICK_BLOCK)) {
      Block block = event.getClickedBlock();
      if (block == null) {
        return;
      }
      
      Material blockType = block.getType();
      
      // filters out other non-farm blocks
      if (cropToSeed.get(blockType) != null) {
        Ageable ageable = (Ageable) block.getBlockData();
        event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
        event.setCancelled(true);
        
        if (ageable.getAge() < ageable.getMaximumAge() 
            || (hoeTiers.get(heldItem.getType()) == null)) {
          // if crop not grown, or not using hoe
          // reset block data to pre-event.
          block.setType(blockType);
          ageable.setAge(ageable.getAge());
          block.setBlockData(ageable);
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
                    dropCounts[1], player);
          randomFarmEvent(block, player);
          damageHoe(heldItem, player);
        }
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
    
    Block block = event.getToBlock();
    Material blockType = block.getType();
    if (cropToSeed.get(blockType) != null) {
      event.setCancelled(true);
      block.setType(Material.AIR, true);
      block.setBlockData(null);
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
    Material blockAboveType = block.getRelative(BlockFace.UP).getType();
    if (cropToSeed.get(blockAboveType) != null) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPistonPushCrop(BlockPistonExtendEvent event) {

    /** 
     *  onPistonPushCrop:
     *  Event Listener to handle piston breaking crops;
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




