package com.fetal.combatflycontrol.listeners;

import com.fetal.combatflycontrol.CombatFlyControl;
import com.fetal.combatflycontrol.managers.CombatManager;
import com.fetal.combatflycontrol.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class MaceListener implements Listener {
    
    private final CombatFlyControl plugin;
    private final CombatManager combatManager;
    private final ConfigManager configManager;
    private final Material maceMaterial;
    
    public MaceListener(CombatFlyControl plugin) {
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();
        this.configManager = plugin.getConfigManager();
        this.maceMaterial = Material.valueOf("MACE");
    }
    
    /**
     * Prevent holding Mace while flying
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (!configManager.isPreventMaceWhileFlying()) return;
        
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        
        if (newItem != null && newItem.getType() == maceMaterial) {
            if (player.isFlying() || isElytraFlying(player)) {
                if (!player.hasPermission("combatflycontrol.bypass")) {
                    // Cancel the hotbar switch
                    event.setCancelled(true);
                    
                    String message = configManager.getMessage("cannot-hold-mace-flying");
                    player.sendMessage(message);
                    
                    configManager.debug("Blocked mace equip for flying player " + player.getName());
                }
            }
        }
    }
    
    /**
     * Prevent interacting with Mace while flying
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!configManager.isPreventMaceWhileFlying()) return;
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || item.getType() != maceMaterial) return;
        
        if (player.isFlying() || isElytraFlying(player)) {
            if (!player.hasPermission("combatflycontrol.bypass")) {
                event.setCancelled(true);
                
                String message = configManager.getMessage("cannot-hold-mace-flying");
                player.sendMessage(message);
                
                configManager.debug("Blocked mace use for flying player " + player.getName());
            }
        }
    }
    
    /**
     * Prevent picking up Mace into main hand while flying
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!configManager.isPreventMaceWhileFlying()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        
        // Check if trying to place mace in main hand slot (slot 0-8, hotbar)
        int slot = event.getSlot();
        int hotbarSlot = event.getHotbarButton();
        
        // If clicking in hotbar (0-8) and player is flying
        if (slot >= 0 && slot <= 8) {
            if ((currentItem != null && currentItem.getType() == maceMaterial) ||
                (cursorItem != null && cursorItem.getType() == maceMaterial)) {
                
                if (player.isFlying() || isElytraFlying(player)) {
                    if (!player.hasPermission("combatflycontrol.bypass")) {
                        event.setCancelled(true);
                        
                        String message = configManager.getMessage("cannot-hold-mace-flying");
                        player.sendMessage(message);
                        
                        configManager.debug("Blocked mace inventory move for flying player " + player.getName());
                    }
                }
            }
        }
        
        // Also check hotbar button press
        if (hotbarSlot >= 0 && hotbarSlot <= 8) {
            ItemStack hotbarItem = player.getInventory().getItem(hotbarSlot);
            if (hotbarItem != null && hotbarItem.getType() == maceMaterial) {
                if (player.isFlying() || isElytraFlying(player)) {
                    if (!player.hasPermission("combatflycontrol.bypass")) {
                        event.setCancelled(true);
                        
                        String message = configManager.getMessage("cannot-hold-mace-flying");
                        player.sendMessage(message);
                    }
                }
            }
        }
    }
    
    private boolean isElytraFlying(Player player) {
        return player.isGliding();
    }
}