package me.tzmo.harvesterhoes;

import me.tzmo.harvesterhoes.cmd.CmdAutoSell;
import me.tzmo.harvesterhoes.cmd.CmdHarvesterHoe;
import me.tzmo.harvesterhoes.engine.Engine;
import me.tzmo.harvesterhoes.task.TaskAutoSell;
import me.tzmo.harvesterhoes.util.CommentedConfiguration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class HarvesterHoes extends JavaPlugin {

    static HarvesterHoes plugin;
    static CommentedConfiguration config;
    static Economy economy;

    public static final Map<Player, Integer> caneMined = new HashMap<>();
    public static final Map<Player, Integer> toSell = new HashMap<>();

    public static final DecimalFormat df = new DecimalFormat("###,###,###,###,###.##");

    @Override
    public void onEnable()
    {
        // Plugin startup logic

        plugin = this;

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) plugin.saveResource("config.yml", false);
        config = CommentedConfiguration.loadConfiguration(configFile);

        // events
        getServer().getPluginManager().registerEvents(new Engine(), this);

        // tasks
        TaskAutoSell.startAutoSellTask();

        // commands
        getCommand("harvesterhoe").setExecutor(new CmdHarvesterHoe());
        getCommand("autosell").setExecutor(new CmdAutoSell());

        hookIntoVaultApi();
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }

    public void hookIntoVaultApi()
    {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null)
        {
            this.getLogger().info("hooked into Vault");
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

            if (rsp == null)
            {
                this.getLogger().log(Level.SEVERE, "could not hook into Vault, shutting down...");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }

            economy = rsp.getProvider();
        } else
        {
            this.getLogger().log(Level.SEVERE, "could not hook into Vault, shutting down...");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public static HarvesterHoes getPlugin()
    {
        return plugin;
    }

    public static Economy getEconomy()
    {
        return economy;
    }

    public static CommentedConfiguration getCfg()
    {
        return config;
    }

    public static void reloadCfg()
    {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) plugin.saveResource("config.yml", false);
        config = CommentedConfiguration.loadConfiguration(configFile);
    }
}
