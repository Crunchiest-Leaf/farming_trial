package com.crunchiest.data;

import com.crunchiest.FarmingTrial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
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
* FarmingPotionManager: 
* Handles management of custom potions.
*/
public class FarmingPotionManager implements Listener {
  
  private final FarmingTrial plugin;
  private final Map<String, CustomPotion> customPotions = new HashMap<>();
  
  public FarmingPotionManager(FarmingTrial plugin) {
    this.plugin = plugin;
  }
  
  /** 
   * registerCustomPotion: 
   * registers a custom potion within the plugin.
   *
   * @param potionName name of potion.
   * @param potionId potion Id.
   * @return CustomPotion
   */
  public CustomPotion registerCustomPotion(String potionName, String potionId, 
      boolean isSplash, int potionRadius) {
    Material potionType = isSplash ? Material.SPLASH_POTION : Material.POTION;
    ItemStack potionItem = new ItemStack(potionType);
    ItemMeta meta = potionItem.getItemMeta();
    
    if (meta != null) {
      meta.setDisplayName(potionName);
      NamespacedKey key = new NamespacedKey(plugin, potionId);
      meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, potionId);
      potionItem.setItemMeta(meta);
    }
    
    CustomPotion customPotion = 
        new CustomPotion(potionItem, potionName, potionId, isSplash, potionRadius);
    customPotions.put(potionId, customPotion);
    return customPotion;
  }
  
  /** 
   * getCustomPotion: 
   * return registered custom potion by Id.
   *
   * @param potionId potion Id.
   * @return CustomPotion
   */
  public CustomPotion getCustomPotion(String potionId) {
    return customPotions.get(potionId);
  }

  /** 
   * getCustomPotion: 
   * return registered custom potion by Id.
   *
   * @return ArrayList returns list of all registered potion ID's.
   */
  public ArrayList<String> getAllCustomPotionIds() {
    return customPotions.values().stream()
                        .map(CustomPotion::getPotionId)
                        .collect(Collectors.toCollection(ArrayList::new));
  }
  
  /** 
   * giveCustomPotion: 
   * return registered custom potion by Id.
   *
   * @param player target player.
   * @param potionId Id of registered potion to be given.
   */
  public void giveCustomPotion(Player player, String potionId) {
    CustomPotion customPotion = getCustomPotion(potionId);
    if (customPotion != null) {
      ItemStack potionItem = 
          new ItemStack(customPotion.isSplash() ? Material.SPLASH_POTION : Material.POTION);
      ItemMeta meta = potionItem.getItemMeta();
      if (meta != null) {
        NamespacedKey key = new NamespacedKey(plugin, customPotion.getPotionId());
        meta.getPersistentDataContainer()
            .set(key, PersistentDataType.STRING, customPotion.getPotionId());
        meta.setDisplayName(customPotion.getPotionName());
        potionItem.setItemMeta(meta);
        player.getInventory().addItem(potionItem);
        player.sendMessage("You have received a custom potion: " + customPotion.getPotionName());
      }
    } else {
      player.sendMessage("Custom potion not found: " + potionId);
    }
  }

  /** 
   * CustomPotion: 
   * class representation of registered
   * potion.
   */
  public static class CustomPotion {
    private final String displayName;
    private final String potionId;
    private final ItemStack potionItem;
    private final boolean isSplash;
    private final int potionRadius;
    
    /** 
     * CustomPotion: 
     * constructor.
     *
     * @param potionItem potionItem
     * @param potionDisplayName string display name
     * @param potionId potion unique Id, for container use.
     * @param isSplash boolean, is potion splash.
     * @param potionRadius radius of potion effect.
     */
    public CustomPotion(ItemStack potionItem, String potionDisplayName, 
        String potionId, boolean isSplash, int potionRadius) {
      this.displayName = potionDisplayName;
      this.potionItem = potionItem;
      this.potionId = potionId;
      this.isSplash = isSplash;
      this.potionRadius = potionRadius;
    }
    
    /** 
     * getPotionItem: 
     * return potion Item.
     *
     * @return ItemStack potion item.
     */
    public ItemStack getPotionItem() {
      return potionItem;
    }
    
    /** 
     * isSplash: 
     * return if potion is splash potion.
     *
     * @return Boolean splash potion?.
     */
    public boolean isSplash() {
      return isSplash;
    }
    
    /** 
     * getPotionRadius: 
     * return radius of potion effect.
     *
     * @return Int splash radius.
     */
    public int getPotionRadius() {
      return potionRadius;
    }


    /** 
     * getPotionId: 
     * return potion Id.
     * for use w/ persistent containers.
     *
     * @return String potion Id.
     */
    public String getPotionId() {
      return potionId;
    }
    
    /** 
     * getPotionName: 
     * return potion display name.
     *
     * @return String potion name.
     */
    public String getPotionName() {
      return displayName;
    }
  }
}