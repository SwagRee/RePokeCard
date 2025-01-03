package io.github.swagree.pokecard.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokecard.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ItemUtil {

    /**
     * 为玩家创建并添加一个物品。
     *
     * @param num      数量
     * @param cardName 卡片名称
     * @return 生成的物品
     * @throws Exception 如果卡片配置无效或缺失
     */
    public static ItemStack addItemToPlayer(int num, String cardName) throws Exception {
        String data = YmlUtil.giveItemDataFileList.getBoolean(cardName + ".enableData")
                ? YmlUtil.giveItemDataFileList.getString(cardName + ".data")
                : Main.plugin.getConfig().getString("id");

        ItemStack itemStack = getItemStack(data);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemStack.setAmount(num);

        String displayName = getCardDisplayName(cardName);
        if (displayName == null) {
            throw new Exception("Invalid card type: " + cardName);
        }

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(getCardLore(cardName));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * 从配置中获取卡片显示名称。
     */
    private static String getCardDisplayName(String cardName) {
        return Optional.ofNullable(Main.plugin.getConfig().getString(cardName + ".name"))
                .map(name -> name.replace("&", "§"))
                .orElse(null);
    }

    /**
     * 从配置中获取卡片的 Lore 列表。
     */
    private static List<String> getCardLore(String cardName) {
        return Main.plugin.getConfig().getStringList(cardName + ".lore")
                .stream()
                .map(lore -> lore.replace("&", "§"))
                .collect(Collectors.toList());
    }

    /**
     * 根据配置数据生成一个物品。
     */
    private static ItemStack getItemStack(String data) {
        try {
            int materialId = Integer.parseInt(data);
            return new ItemStack(Material.getMaterial(materialId));
        } catch (NumberFormatException e) {
            return new ItemStack(Material.valueOf(data.toUpperCase()));
        }
    }

    /**
     * 从玩家背包中移除特定物品。
     *
     * @param player        玩家
     * @param newItemStack  新物品
     * @param cardName      卡片名称
     * @param pokemon       精灵
     * @param extra         附加信息
     * @return 是否成功移除物品
     */
    public static boolean takeItem(Player player, ItemStack newItemStack, String cardName, Pokemon pokemon, String extra) {
        PlayerInventory inventory = player.getInventory();
        ItemMeta newItemMeta = newItemStack.getItemMeta();

        if (newItemMeta == null || newItemMeta.getLore() == null || newItemMeta.getLore().isEmpty()) {
            return false;
        }

        String targetLore = newItemMeta.getLore().get(0);
        String displayName = newItemMeta.getDisplayName();

        for (ItemStack itemStack : inventory) {
            if (itemStack == null || !itemStack.hasItemMeta()) continue;

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null || itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) continue;

            if (targetLore.equals(itemMeta.getLore().get(0)) &&
                    displayName.equalsIgnoreCase(itemMeta.getDisplayName())) {

                if (removeSingleItem(inventory, itemStack)) {
                    logItemTransaction(player, cardName);
                    YmlUtil.sendColorMessage(player, cardName, pokemon, extra);
                    toExecuteCommand(player, cardName);
                    player.closeInventory();
                    return true;
                } else {
                    YmlUtil.sendColorNoItem(player);
                    return false;
                }
            }
        }
        return false;
    }

    private static void toExecuteCommand(Player player, String cardName) {
        if(YmlUtil.commandsFileConfig.getBoolean(cardName +".enable")){
            List<String> commands = YmlUtil.commandsFileConfig.getStringList(cardName + ".commands");
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command.replace("%player%", player.getName()));
            }
        }
    }

    /**
     * 从玩家的背包中移除单个物品。
     */
    private static boolean removeSingleItem(PlayerInventory inventory, ItemStack itemStack) {
        ItemStack singleItem = itemStack.clone();
        singleItem.setAmount(1);
        return inventory.removeItem(singleItem).isEmpty();
    }

    /**
     * 记录物品交易日志。
     */
    private static void logItemTransaction(Player player, String cardName) {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        String[] split = dateTime.split(" ");

        YmlUtil.logFileList.set(split[0] + "." + split[1] + "." + player.getName(), cardName);

        try {
            YmlUtil.logFileList.save(YmlUtil.logFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save log file", e);
        }
    }

    /**
     * 创建一个带有指定卡片名称的物品。
     */
    public static ItemStack createItem(String cardName) {
        String data = Main.plugin.getConfig().getString("id");
        ItemStack itemStack = getItemStack(data);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(getCardDisplayName(cardName));
        itemMeta.setLore(getCardLore(cardName));
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
