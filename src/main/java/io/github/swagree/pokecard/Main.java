package io.github.swagree.pokecard;

import io.github.swagree.pokecard.EventListener.EventInteract;
import io.github.swagree.pokecard.command.CommandCard;
import io.github.swagree.pokecard.EventListener.guiListener.detailList.EventGuiDetailMain;
import io.github.swagree.pokecard.EventListener.guiListener.detailList.EventGuiDetailPokeForm;
import io.github.swagree.pokecard.EventListener.guiListener.detailList.EventGuiDetailPokeMove;
import io.github.swagree.pokecard.EventListener.guiListener.EventGuiMain;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    public static Main plugin;

    @Override
    public void onEnable() {
        // 初始化插件实例
        plugin = this;
        // 控制台输出插件启用信息
        Bukkit.getConsoleSender().sendMessage(String.format("§7[%s] §b作者§fSwagRee §cQQ:§f352208610", getName()));
        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new EventInteract(), this);
        Bukkit.getPluginManager().registerEvents(new EventGuiMain(), this);
        Bukkit.getPluginManager().registerEvents(new EventGuiDetailMain(), this);
        Bukkit.getPluginManager().registerEvents(new EventGuiDetailPokeMove(), this);
        Bukkit.getPluginManager().registerEvents(new EventGuiDetailPokeForm(), this);
        // 注册命令执行器
        getCommand("rpc").setExecutor(new CommandCard());
        // 加载配置文件
        loadConfig();
    }

    /**
     * 初始化和加载配置文件
     */
    private void loadConfig() {
        // 创建插件数据文件夹
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        // 初始化配置文件
        initConfigFile("message.yml");
        initConfigFile("blackList.yml");
        initConfigFile("afterbind.yml");
        initConfigFile("afterunbreed.yml");
        initConfigFile("gui.yml");
        initConfigFile("giveItemData.yml");
        initConfigFile("log.yml");
        initConfigFile("commands.yml");

        // 保存默认配置文件并重新加载
        saveDefaultConfig();
        reloadConfig();
    }

    /**
     * 通用方法：检查并初始化配置文件
     *
     * @param fileName 配置文件名
     */
    private void initConfigFile(String fileName) {
        File configFile = new File(getDataFolder(), fileName);
        if (!configFile.exists()) {
            saveResource(fileName, false);
            Bukkit.getConsoleSender().sendMessage(String.format("§7[%s] §a已生成默认配置文件：%s", getName(), fileName));
        }
    }
}
