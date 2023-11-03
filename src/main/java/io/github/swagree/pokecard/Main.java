package io.github.swagree.pokecard;

import io.github.swagree.pokecard.command.CommandCard;
import io.github.swagree.pokecard.event.EventExtra;
import io.github.swagree.pokecard.event.EventMain;
import io.github.swagree.pokecard.gui.*;
import io.github.swagree.pokecard.papi.PapiRegister;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Map;

public class Main extends JavaPlugin {
    public static Main plugin;
    private YamlConfiguration blackListConfig;
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§7[RePokeCard] §b作者§fSwagRee §cQQ:§f352208610");

        Bukkit.getPluginManager().registerEvents(new EventMain(), this);
        Bukkit.getPluginManager().registerEvents(new EventExtra(), this);
        Bukkit.getPluginManager().registerEvents(new GuiModifyIvs(), this);
        Bukkit.getPluginManager().registerEvents(new GuiModifyEvs(), this);
        Bukkit.getPluginManager().registerEvents(new GuiModifyGrowth(), this);
        Bukkit.getPluginManager().registerEvents(new GuiModifyNature(), this);
        Bukkit.getPluginManager().registerEvents(new GuiModifyForm(), this);
        Bukkit.getPluginManager().registerEvents(new GuiModifyFormLegendaryOrFormCommon(), this);
        Bukkit.getPluginManager().registerEvents(new GuiModifyPokeBall(), this);
        Bukkit.getPluginManager().registerEvents(new GuiModifyPokeMove(), this);

        getCommand("rpc").setExecutor(new CommandCard());

        PapiRegister papiRegister = new PapiRegister();
        if(!papiRegister.isRegistered()){
            papiRegister.register();
        }


        loadConfig();


        plugin = this;
    }

    public void onDisable() {

    }
    public void loadConfig() {

        getDataFolder().mkdirs();

        File customConfigFile = new File(getDataFolder(), "message.yml");
        if (!customConfigFile.exists()) {
            saveResource("message.yml", false);
        }

        File blackListFile = new File(getDataFolder(), "blackList.yml");

        if (!blackListFile.exists()) {
            saveResource("blackList.yml", false);
        }


        this.saveConfig();
        this.saveDefaultConfig();

        this.reloadConfig();


    }


}