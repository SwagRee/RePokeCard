package io.github.swagree.pokecard.command;

import com.google.common.base.Charsets;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.Main;
import io.github.swagree.pokecard.enums.EnumCardName;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandCard implements CommandExecutor,TabExecutor {
    public static Map<String, FileConfiguration> configData;
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command1, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("§b<§m*-----=======§b热宝可梦卡§b §m=======-----§b>");
            sender.sendMessage("§e/rpc 玩家 [类型] 数量 §f- 给予指定的宝可梦卡");
            sender.sendMessage("§e/rpc list §f- 查看卡的类型");
            sender.sendMessage("§e/rpc reload §f- 重载插件");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            Main.plugin.reloadConfig();

            File fileBlackList = new File(Main.plugin.getDataFolder(),"blackList.yml");
            YamlConfiguration blacklist = YamlConfiguration.loadConfiguration(fileBlackList);
            YmlUtil.setBlacklist(blacklist);

            File fileMessage = new File(Main.plugin.getDataFolder(),"message.yml");
            YamlConfiguration message = YamlConfiguration.loadConfiguration(fileMessage);
            YmlUtil.setMessage(message);
            sender.sendMessage("§b重载插件成功");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§7<§f-----======= §b热宝可梦卡§f =======-----§7>");
            String s = "";
            int count = 0;
        for(EnumCardName e:EnumCardName.values()){
            s = s+"§b"+e.getCardName() + "§f"+e.getCardNameCN()+"卡" +"    ";
            count++;
            if(count==2){
                sender.sendMessage(s);
                s = "";
                count=0;
            }
        }
        }
        if (args.length == 2) {
            if (!sender.isOp()) {
                YmlUtil.sendColorItemMessage((Player) sender,"noOP");
                return true;
            }
            String playername = args[0];
            String cardName = args[1];
            Player p = Bukkit.getPlayer(playername);
            int num = 1;
            if (p == null) {
                YmlUtil.sendColorItemMessage((Player) sender,"noPlayer",playername,cardName,num);
                return true;
            }
            try {
                p.getInventory().addItem(new ItemStack[]{ItemUtil.addItemToPlayer(num, cardName)});
                YmlUtil.sendColorItemMessage((Player) sender,"success",playername,cardName,num);
            } catch (Exception e) {
                YmlUtil.sendColorItemMessage((Player) sender,"error",playername,cardName,num);
            }
            return true;
        }
        if (args.length == 3) {
            if (!sender.isOp()) {
                YmlUtil.sendColorItemMessage((Player) sender,"noOP");
                return true;
            }
            String playername = args[0];
            String cardName = args[1];
            Player p = Bukkit.getPlayer(playername);
            int num = 1;
            try{
                num = Integer.parseInt(args[2]);
            }catch (Exception ee){
                YmlUtil.sendColorItemMessage((Player) sender,"error",playername,cardName,num);
                return true;
            }

            if (p == null) {
                YmlUtil.sendColorItemMessage((Player) sender,"noPlayer",playername,cardName,num);
                return true;
            }
            try {
                p.getInventory().addItem(new ItemStack[]{ItemUtil.addItemToPlayer(num, cardName)});
                YmlUtil.sendColorItemMessage((Player) sender,"success",playername,cardName,num);
            } catch (Exception e) {
                YmlUtil.sendColorItemMessage((Player) sender,"error",playername,cardName,num);
            }
            return true;
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tablist = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0];
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                if (name.toLowerCase().startsWith(prefix.toLowerCase())) { // 将前缀和玩家ID都转换为小写进行比较
                    tablist.add(name);
                }
            }
            return tablist;
        }
        if (args.length == 2) {
            String prefix = args[1]; // 获取输入的前缀
            for (EnumCardName e : EnumCardName.values()) {
                String cardName = e.getCardName();
                if (cardName.startsWith(prefix)) { // 判断是否与前缀匹配（忽略大小写）
                    tablist.add(cardName);
                }
            }
            return tablist;
        }
        return Collections.emptyList();
    }

}

