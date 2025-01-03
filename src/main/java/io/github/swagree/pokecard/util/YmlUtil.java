package io.github.swagree.pokecard.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokecard.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Set;

public class YmlUtil {
    public static File fileMessage = new File(Main.plugin.getDataFolder(), "message.yml");
    public static YamlConfiguration message = YamlConfiguration.loadConfiguration(fileMessage);

    public static File fileBlackList = new File(Main.plugin.getDataFolder(), "blackList.yml");
    public static YamlConfiguration blacklist = YamlConfiguration.loadConfiguration(fileBlackList);


    public static File afterBindFile = new File(Main.plugin.getDataFolder(), "afterbind.yml");
    public static YamlConfiguration afterBindList = YamlConfiguration.loadConfiguration(afterBindFile);

    public static File afterUnBreedFile = new File(Main.plugin.getDataFolder(), "afterunbreed.yml");
    public static YamlConfiguration afterUnBreedFileList = YamlConfiguration.loadConfiguration(afterUnBreedFile);

    public static File guiFile = new File(Main.plugin.getDataFolder(), "gui.yml");
    public static YamlConfiguration guiFileFileList = YamlConfiguration.loadConfiguration(guiFile);


    public static File giveItemDataFile = new File(Main.plugin.getDataFolder(), "giveItemData.yml");
    public static YamlConfiguration giveItemDataFileList = YamlConfiguration.loadConfiguration(giveItemDataFile);

    public static File logFile = new File(Main.plugin.getDataFolder(), "log.yml");
    public static YamlConfiguration logFileList = YamlConfiguration.loadConfiguration(logFile);

    public static File commandsFile = new File(Main.plugin.getDataFolder(), "commands.yml");
    public static YamlConfiguration commandsFileConfig = YamlConfiguration.loadConfiguration(commandsFile);


    public static void setBlacklist(YamlConfiguration blacklist) {
        YmlUtil.blacklist = blacklist;
    }

    public static void setMessage(YamlConfiguration message) {
        YmlUtil.message = message;
    }

    public static void setBindList(YamlConfiguration afterBindList) {
        YmlUtil.afterBindList = afterBindList;
    }

    public static void setUnBreedList(YamlConfiguration afterUnBreedFileList) {
        YmlUtil.afterUnBreedFileList = afterUnBreedFileList;
    }

    public static void setGui(YamlConfiguration guiFileFileList) {
        YmlUtil.guiFileFileList = guiFileFileList;
    }

    public static void setItemData(YamlConfiguration giveItemDataFileList) {
        YmlUtil.giveItemDataFileList = giveItemDataFileList;
    }
    public static void setCommands(YamlConfiguration commandsFileConfig) {
        YmlUtil.commandsFileConfig = commandsFileConfig;
    }


    public static void sendColorWarn(Player player, String cardName, Pokemon pokemon) {
        player.sendMessage(transToColor(YmlUtil.message.getString("Warn." + cardName).replace("%pokemon%", pokemon.getLocalizedName())));
    }

    public static void sendColorMessage(Player player, String cardName, Pokemon pokemon, String extra) {
        if(extra == null){
            player.sendMessage(transToColor(
                    YmlUtil.message.getString("Message." + cardName)
                            .replace("%pokemon%", pokemon.getLocalizedName())
            ));
            return;
        }
        player.sendMessage(transToColor(YmlUtil.message.getString("Message." + cardName)
                .replace("%pokemon%", pokemon.getLocalizedName())
                .replace("%growth%", extra)
                .replace("%nature%", extra)
                .replace("%form%", extra)
                .replace("%pokeball%", extra))
                .replace("%evs%", extra)
                .replace("%ivs%", extra)
        );
    }


    public static void sendColorWarn(Player player, String cardName, Pokemon pokemon, String extra) {
        player.sendMessage(transToColor(YmlUtil.message.getString("Warn." + cardName)
                .replace("%pokemon%", pokemon.getLocalizedName())
                .replace("%growth%", extra)
                .replace("%nature%", extra)
                .replace("%form%", extra)
                .replace("%pokeball%", extra)
                .replace("%evs%", extra)
                .replace("%ivs%", extra)
        ));
    }

    public static void sendColorItemMessage(Player player, String path) {
        player.sendMessage(transToColor(YmlUtil.message.getString("ItemMessage." + path)));
    }

    public static void sendColorItemMessage(CommandSender sender, String path, String playerName, String cardName, int num) {
        sender.sendMessage(transToColor(YmlUtil.message.getString("ItemMessage." + path)
                .replace("%player%", playerName)
                .replace("%cardNum%", String.valueOf(num))
                .replace("%cardName%", cardName)
        ));
    }

    public static void sendColorEventWarn(Player player, String path) {
        player.sendMessage(transToColor(YmlUtil.message.getString("EventWarn." + path)));
    }

    public static void sendColorEventWarn(Player player, String path, Pokemon pokemon) {
        player.sendMessage(transToColor(YmlUtil.message.getString("EventWarn." + path).replace("%pokemon%", pokemon.getLocalizedName())));
    }

    public static void sendColorNoItem(Player player) {
        player.sendMessage(transToColor(YmlUtil.message.getString("NoItem")));
    }

    public static String transToColor(String message) {
        message = message.replace("&", "§");
        return message;
    }

    public static void sendColorBlackListMessage(Player player, String cardName, Pokemon pokemon) {
        player.sendMessage(transToColor(YmlUtil.blacklist.getString("blackMessage." + cardName).replace("%pokemon%", pokemon.getLocalizedName())));
    }


    public static boolean blackListPokemon(Player player, Pokemon pokemon, String cardName) {
        String localizedName = pokemon.getLocalizedName();
        List<String> cardBlackList = blacklist.getStringList("blackList." + cardName);
        for (String mt : cardBlackList) {
            if (localizedName.equals(mt)) {
                sendColorBlackListMessage(player, cardName, pokemon);
                return false;
            }
        }
        return true;
    }

    public static String getNameConfigFromLore(String displayName) {
        if (displayName == null) {
            return null; // 如果 displayName 为 null，直接返回
        }

        ConfigurationSection config = Main.plugin.getConfig(); // 获取配置文件
        Set<String> keys = config.getKeys(false); // 获取所有顶级键

        for (String key : keys) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section != null) {
                String configName = section.getString("name"); // 获取配置中的 name 属性
                if (configName != null && displayName.equals(configName.replace("&", "§"))) {
                    return key; // 如果匹配，返回当前键
                }
            }
        }

        return null; // 未匹配到返回 null
    }


}
