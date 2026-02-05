package com.fetal.combatflycontrol;

import com.fetal.combatflycontrol.listeners.CombatListener;
import com.fetal.combatflycontrol.listeners.FlyListener;
import com.fetal.combatflycontrol.listeners.MaceListener;
import com.fetal.combatflycontrol.managers.CombatManager;
import com.fetal.combatflycontrol.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatFlyControl extends JavaPlugin {
    
    private static CombatFlyControl instance;
    private ConfigManager configManager;
    private CombatManager combatManager;
    private boolean isMaceAvailable;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Check if Mace is available (1.21+)
        try {
            Material.valueOf("MACE");
            isMaceAvailable = true;
            getLogger().info("Mace detected! 1.21+ features enabled.");
        } catch (IllegalArgumentException e) {
            isMaceAvailable = false;
            getLogger().warning("Mace not found. Running in compatibility mode (pre-1.21).");
        }
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.combatManager = new CombatManager(this);
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new FlyListener(this), this);
        
        // Only register MaceListener if Mace is available
        if (isMaceAvailable) {
            getServer().getPluginManager().registerEvents(new MaceListener(this), this);
        }
        
        getLogger().info("CombatFlyControl v1.0 by FeTaL has been enabled!");
        getLogger().info("Overrides WorldGuard and EssentialsX fly permissions during combat.");
    }
    
    @Override
    public void onDisable() {
        combatManager.clearAllCombatTags();
        getLogger().info("CombatFlyControl has been disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("combatflycontrol")) {
            if (args.length == 0) {
                sender.sendMessage(configManager.getMessage("prefix") + "&7CombatFlyControl v1.0 by &cFeTaL");
                sender.sendMessage(configManager.getMessage("prefix") + "&7Use &f/cfc reload &7to reload config.");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("combatflycontrol.admin")) {
                    sender.sendMessage(configManager.getMessage("prefix") + "&cYou don't have permission!");
                    return true;
                }
                
                reloadConfig();
                configManager.reload();
                sender.sendMessage(configManager.getMessage("prefix") + "&aConfiguration reloaded!");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("status")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Console cannot have combat status!");
                    return true;
                }
                
                Player player = (Player) sender;
                if (combatManager.isInCombat(player)) {
                    int timeLeft = combatManager.getCombatTimeLeft(player);
                    sender.sendMessage(configManager.getMessage("prefix") + 
                        "&cYou are in combat for &e" + timeLeft + " &cseconds.");
                } else {
                    sender.sendMessage(configManager.getMessage("prefix") + "&aYou are not in combat.");
                }
                return true;
            }
        }
        return false;
    }
    
    public static CombatFlyControl getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public CombatManager getCombatManager() {
        return combatManager;
    }
    
    public boolean isMaceAvailable() {
        return isMaceAvailable;
    }
}