package com.fetal.combatflycontrol.managers;

import com.fetal.combatflycontrol.CombatFlyControl;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    
    private final CombatFlyControl plugin;
    private FileConfiguration config;
    
    private int combatDuration;
    private boolean disableFlightImmediately;
    private boolean cancelFlightAttempts;
    private boolean preventMaceWhileFlying;
    private boolean checkWingsEnchantment;
    private String wingsEnchantmentName;
    private boolean actionBarTimer;
    private boolean broadcastMaceHit;
    private boolean overrideFlyFlag;
    private List<String> disabledRegions;
    private List<String> disabledWorlds;
    private boolean debug;
    
    public ConfigManager(CombatFlyControl plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadConfig();
    }
    
    public void reload() {
        this.config = plugin.getConfig();
        loadConfig();
    }
    
    private void loadConfig() {
        combatDuration = config.getInt("combat-duration", 15);
        disableFlightImmediately = config.getBoolean("settings.disable-flight-immediately", true);
        cancelFlightAttempts = config.getBoolean("settings.cancel-flight-attempts", true);
        preventMaceWhileFlying = config.getBoolean("settings.prevent-mace-while-flying", true);
        checkWingsEnchantment = config.getBoolean("settings.check-wings-enchantment", true);
        wingsEnchantmentName = config.getString("settings.wings-enchantment-name", "WINGS");
        actionBarTimer = config.getBoolean("settings.action-bar-timer", true);
        broadcastMaceHit = config.getBoolean("settings.broadcast-mace-hit", true);
        overrideFlyFlag = config.getBoolean("worldguard.override-fly-flag", true);
        disabledRegions = config.getStringList("worldguard.disabled-regions");
        disabledWorlds = config.getStringList("worldguard.disabled-worlds");
        debug = config.getBoolean("debug", false);
    }
    
    public String getMessage(String key) {
        String message = config.getString("messages." + key, "");
        String prefix = config.getString("messages.prefix", "&8[&cCombatFly&8] &r");
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
    
    public String getRawMessage(String key) {
        String message = config.getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public String formatMessage(String message, String placeholder, String value) {
        return ChatColor.translateAlternateColorCodes('&', message.replace(placeholder, value));
    }
    
    public void debug(String message) {
        if (debug) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
    
    public int getCombatDuration() { return combatDuration; }
    public boolean isDisableFlightImmediately() { return disableFlightImmediately; }
    public boolean isCancelFlightAttempts() { return cancelFlightAttempts; }
    public boolean isPreventMaceWhileFlying() { return preventMaceWhileFlying; }
    public boolean isCheckWingsEnchantment() { return checkWingsEnchantment; }
    public String getWingsEnchantmentName() { return wingsEnchantmentName; }
    public boolean isActionBarTimer() { return actionBarTimer; }
    public boolean isBroadcastMaceHit() { return broadcastMaceHit; }
    public boolean isOverrideFlyFlag() { return overrideFlyFlag; }
    public List<String> getDisabledRegions() { return disabledRegions; }
    public List<String> getDisabledWorlds() { return disabledWorlds; }
}