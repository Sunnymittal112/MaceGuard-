package com.fetal.combatflycontrol.managers;

import com.fetal.combatflycontrol.CombatFlyControl;
import com.fetal.combatflycontrol.tasks.CombatTagTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {
    
    private final CombatFlyControl plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Integer> combatTags;
    private final Map<UUID, BukkitTask> combatTasks;
    
    public CombatManager(CombatFlyControl plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.combatTags = new HashMap<>();
        this.combatTasks = new HashMap<>();
    }
    
    /**
     * Tag a player for combat
     */
    public void tagPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Cancel existing task if present
        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
        }
        
        boolean wasInCombat = combatTags.containsKey(uuid);
        combatTags.put(uuid, configManager.getCombatDuration());
        
        // Disable flight immediately if enabled
        if (configManager.isDisableFlightImmediately() && player.isFlying()) {
            disableFlight(player);
        }
        
        // Send message only if not already in combat
        if (!wasInCombat) {
            String message = configManager.getRawMessage("enter-combat");
            message = configManager.formatMessage(message, "%seconds%", String.valueOf(configManager.getCombatDuration()));
            player.sendMessage(message);
            
            // Update action bar
            if (configManager.isActionBarTimer()) {
                updateActionBar(player, configManager.getCombatDuration());
            }
        }
        
        // Start countdown task
        BukkitTask task = new CombatTagTask(plugin, player).runTaskTimer(plugin, 20L, 20L);
        combatTasks.put(uuid, task);
        
        configManager.debug("Tagged player " + player.getName() + " for combat");
    }
    
    /**
     * Tag both players for combat (used when mace hit occurs)
     */
    public void tagBothPlayers(Player attacker, Player victim) {
        tagPlayer(attacker);
        tagPlayer(victim);
        
        if (configManager.isBroadcastMaceHit()) {
            String message = configManager.getRawMessage("mace-combat-trigger");
            message = configManager.formatMessage(message, "%seconds%", String.valueOf(configManager.getCombatDuration()));
            attacker.sendMessage(message);
            victim.sendMessage(message);
        }
    }
    
    /**
     * Remove player from combat
     */
    public void removeCombatTag(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
            combatTasks.remove(uuid);
        }
        
        combatTags.remove(uuid);
        
        String message = configManager.getMessage("leave-combat");
        player.sendMessage(message);
        
        configManager.debug("Removed combat tag from " + player.getName());
    }
    
    /**
     * Check if player is in combat
     */
    public boolean isInCombat(Player player) {
        return combatTags.containsKey(player.getUniqueId());
    }
    
    /**
     * Get remaining combat time
     */
    public int getCombatTimeLeft(Player player) {
        return combatTags.getOrDefault(player.getUniqueId(), 0);
    }
    
    /**
     * Decrement combat time
     */
    public void decrementCombatTime(Player player) {
        UUID uuid = player.getUniqueId();
        if (combatTags.containsKey(uuid)) {
            int timeLeft = combatTags.get(uuid) - 1;
            if (timeLeft <= 0) {
                removeCombatTag(player);
            } else {
                combatTags.put(uuid, timeLeft);
                if (configManager.isActionBarTimer()) {
                    updateActionBar(player, timeLeft);
                }
            }
        }
    }
    
    /**
     * Force disable flight for a player
     */
    public void disableFlight(Player player) {
        // This method forcefully disables flight regardless of permissions
        player.setFlying(false);
        player.setAllowFlight(false);
        
        // Re-enable allow flight if they have permission (for when combat ends)
        if (player.hasPermission("essentials.fly") || player.hasPermission("worldguard.region.bypass.*")) {
            // We'll let the server/plugins re-enable this after combat
        }
        
        configManager.debug("Disabled flight for " + player.getName());
    }
    
    /**
     * Attempt to enable flight (will be blocked if in combat)
     */
    public boolean attemptEnableFlight(Player player) {
        if (isInCombat(player) && configManager.isCancelFlightAttempts()) {
            if (!player.hasPermission("combatflycontrol.bypass")) {
                String message = configManager.getMessage("cannot-fly-combat");
                player.sendMessage(message);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Update action bar with combat timer
     */
    public void updateActionBar(Player player, int seconds) {
        String message = configManager.getRawMessage("action-bar-timer");
        message = configManager.formatMessage(message, "%seconds%", String.valueOf(seconds));
        
        Component component = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().deserialize(message);
        player.sendActionBar(component);
    }
    
    /**
     * Clear all combat tags (used on disable)
     */
    public void clearAllCombatTags() {
        for (BukkitTask task : combatTasks.values()) {
            task.cancel();
        }
        combatTasks.clear();
        combatTags.clear();
    }
}