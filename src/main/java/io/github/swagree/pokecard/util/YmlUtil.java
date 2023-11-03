package io.github.swagree.pokecard.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokecard.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class YmlUtil {
    public static File fileMessage = new File(Main.plugin.getDataFolder(),"message.yml");
    public static YamlConfiguration message = YamlConfiguration.loadConfiguration(fileMessage);

    public static File fileBlackList = new File(Main.plugin.getDataFolder(),"blackList.yml");
    public static YamlConfiguration blacklist = YamlConfiguration.loadConfiguration(fileBlackList);

    public static void setBlacklist(YamlConfiguration blacklist) {
        YmlUtil.blacklist = blacklist;
    }

    public static void setMessage(YamlConfiguration message) {
        YmlUtil.message = message;
    }

    public static void sendColorMessage(Player player, String cardName, Pokemon pokemon){
        player.sendMessage(transToColor(YmlUtil.message.getString("Message."+cardName).replace("%pokemon%",pokemon.getLocalizedName())));
    }
    public static void sendColorMessage(Player player, String cardName, Pokemon pokemon,String extra){
        player.sendMessage(transToColor(YmlUtil.message.getString("Message."+cardName)
                .replace("%pokemon%",pokemon.getLocalizedName())
                .replace("%growth%",extra)
                .replace("%nature%",extra)
                .replace("%form%",extra)
                .replace("%pokeball%",extra))
                .replace("%evs%",extra)
                .replace("%ivs%",extra)
);
    }
    public static void sendColorWarn(Player player,String cardName, Pokemon pokemon){
        player.sendMessage(transToColor(YmlUtil.message.getString("Warn."+cardName).replace("%pokemon%",pokemon.getLocalizedName())));
    }

    public static void sendColorWarn(Player player,String cardName, Pokemon pokemon,String extra){
        player.sendMessage(transToColor(YmlUtil.message.getString("Warn."+cardName)
                .replace("%pokemon%",pokemon.getLocalizedName())
                .replace("%growth%",extra)
                .replace("%nature%",extra)
                .replace("%form%",extra)
                .replace("%pokeball%",extra)
                .replace("%evs%",extra)
                .replace("%ivs%",extra)
        ));
    }
    public static void sendColorItemMessage(Player player,String path){
        player.sendMessage(transToColor(YmlUtil.message.getString("ItemMessage."+path)

        ));
    }
    public static void sendColorItemMessage(Player player,String path,String playerName,String cardName,int num){
        player.sendMessage(transToColor(YmlUtil.message.getString("ItemMessage."+path)
                .replace("%player%",playerName)
                .replace("%cardNum%",String.valueOf(num))
                .replace("%cardName%",cardName)
        ));
    }
    public static void sendColorEventWarn(Player player,String path){
        player.sendMessage(transToColor(YmlUtil.message.getString("EventWarn."+path)));
    }
    public static void sendColorEventWarn(Player player,String path,Pokemon pokemon){
        player.sendMessage(transToColor(YmlUtil.message.getString("EventWarn."+path).replace("%pokemon%",pokemon.getLocalizedName())));
    }
    public static void sendColorNoItem(Player player){
        player.sendMessage(transToColor(YmlUtil.message.getString("NoItem")));
    }
    public static String transToColor(String message){
        message = message.replace("&", "§");
        return message;
    }

    public static void sendColorBlackListMessage(Player player, String cardName, Pokemon pokemon){
        player.sendMessage(transToColor(YmlUtil.blacklist.getString("blackMessage."+cardName).replace("%pokemon%",pokemon.getLocalizedName())));
    }



    public static boolean blackListPokemon(Player player,Pokemon pokemon,String cardName){
        String localizedName = pokemon.getLocalizedName();
        List<String> cardBlackList = blacklist.getStringList("blackList."+cardName);
        for(String mt:cardBlackList){
            if(localizedName.equals(mt)){
                sendColorBlackListMessage(player,cardName,pokemon);
                return false;
            }
        }
        return true;
    }


    public static String getNameConfigFromLore(List<String> lores) {

        ConfigurationSection config = Main.plugin.getConfig(); // 获取配置文件的根部分
        Set<String> keys = config.getKeys(false); // 获取配置文件中的所有顶级键

        for (String key : keys) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section != null && section.contains("lore")) {
                List<String> loreList = Main.plugin.getConfig().getStringList(key + ".lore");
                List<String> loreColorList = new ArrayList<>();

                for (String lore : loreList) {
                    loreColorList.add(lore.replace("&", "§"));
                }
                if (loreColorList.equals(lores)) { // 比较 lore 内容是否相同
                    return key; // 返回匹配的键
                }
            }
        }
        return null;
    }
}
