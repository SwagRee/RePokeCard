package io.github.swagree.pokecard.command;

import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.Main;
import io.github.swagree.pokecard.enums.EnumCardName;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.bukkit.util.Consumer;

import java.io.File;
import java.util.*;


public class CommandCard implements CommandExecutor, TabExecutor {

    public boolean onCommand(CommandSender sender, Command command1, String label, String[] args) {
        if (!sender.isOp()) {
            YmlUtil.sendColorItemMessage((Player) sender, "noOP");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reloadAllConfig(sender);
                return true;
            case "list":
                sendCardList(sender);
                return true;
            default:
                return handleCardCommands(sender, args);
        }
    }

    private boolean handleCardCommands(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return cardBooleanForMessage(sender, args);
        }
        if (args.length == 3) {
            return addItemToPlayer(sender, args);
        }
        return false;
    }

    private void sendCardList(CommandSender sender) {
        List<String> stringList = Main.plugin.getConfig().getStringList("cardListMessage");
        stringList.forEach(s -> sender.sendMessage(s.replace("&", "§")));
    }

    private boolean addItemToPlayer(CommandSender sender, String[] args) {
        String playername = args[0];
        String cardName = args[1];
        Player p = getPlayer(playername);
        int num = parseNumber(args[2], sender, playername, cardName);

        if (p == null) {
            YmlUtil.sendColorItemMessage(sender, "noPlayer", playername, cardName, num);
            return true;
        }
        try {
            p.getInventory().addItem(ItemUtil.addItemToPlayer(num, cardName));
            YmlUtil.sendColorItemMessage(sender, "success", playername, cardName, num);
        } catch (Exception e) {
            YmlUtil.sendColorItemMessage(sender, "error", playername, cardName, num);
        }
        return false;
    }

    private int parseNumber(String arg, CommandSender sender, String playername, String cardName) {
        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            YmlUtil.sendColorItemMessage(sender, "error", playername, cardName, 1);
            return 1;
        }
    }

    private static void reloadAllConfig(CommandSender sender) {
        Main.plugin.reloadConfig();
        loadConfigFile("blackList.yml", YmlUtil::setBlacklist);
        loadConfigFile("message.yml", YmlUtil::setMessage);
        loadConfigFile("afterbind.yml", YmlUtil::setBindList);
        loadConfigFile("afterunbreed.yml", YmlUtil::setUnBreedList);
        loadConfigFile("gui.yml", YmlUtil::setGui);
        loadConfigFile("giveItemData.yml", YmlUtil::setItemData);
        loadConfigFile("commands.yml", YmlUtil::setCommands);

        sender.sendMessage(Main.plugin.getConfig().getString("reloadMessage").replace("&", "§"));
    }

    private static void loadConfigFile(String fileName, Consumer<YamlConfiguration> consumer) {
        File file = new File(Main.plugin.getDataFolder(), fileName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        consumer.accept(config);
    }

    private static void sendHelp(CommandSender sender) {
        List<String> stringList = Main.plugin.getConfig().getStringList("helpMessage");
        stringList.forEach(s -> sender.sendMessage(s.replace("&", "§")));
    }

    private boolean cardBooleanForMessage(CommandSender sender, String[] args) {
        String playername = args[0];
        String cardName = args[1];
        Player p = getPlayer(playername);
        int num = 1;

        if (p == null) {
            YmlUtil.sendColorItemMessage(sender, "noPlayer", playername, cardName, num);
            return true;
        }
        try {
            p.getInventory().addItem(ItemUtil.addItemToPlayer(num, cardName));
            YmlUtil.sendColorItemMessage(sender, "success", playername, cardName, num);
        } catch (Exception e) {
            YmlUtil.sendColorItemMessage(sender, "error", playername, cardName, num);
        }
        return false;
    }

    private Player getPlayer(String playername) {
        return Bukkit.getPlayer(playername);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return getMatchingPlayers(args[0]);
        }
        if (args.length == 2) {
            return getMatchingCardNames(args[1]);
        }
        return Collections.emptyList();
    }

    private List<String> getMatchingPlayers(String prefix) {
        List<String> tablist = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
                tablist.add(name);
            }
        }
        return tablist;
    }

    private List<String> getMatchingCardNames(String prefix) {
        List<String> tablist = new ArrayList<>();
        for (EnumCardName e : EnumCardName.values()) {
            String cardName = e.getCardName();
            if (cardName.toLowerCase().startsWith(prefix.toLowerCase())) {
                tablist.add(cardName);
            }
        }
        return tablist;
    }
}