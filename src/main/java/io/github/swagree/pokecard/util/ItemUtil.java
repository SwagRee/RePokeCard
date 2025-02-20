package io.github.swagree.pokecard.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokecard.Main;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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

        itemStack.setAmount(num);




        String displayName = getCardDisplayName(cardName);
        if (displayName == null) {
            throw new Exception("Invalid card type: " + cardName);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(getCardLore(cardName));
        itemStack.setItemMeta(itemMeta);

        itemStack = addNBToItemStack(itemStack);
        return itemStack;
    }

    private static ItemStack addNBToItemStack(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = addNBT(itemStack);
        itemStack = CraftItemStack.asBukkitCopy(nmsItemStack);
        return itemStack;
    }

    private static net.minecraft.server.v1_12_R1.ItemStack addNBT(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
        nbtTagCompound.setString("sakura_auto_bind", "0");
        nmsItemStack.setTag(nbtTagCompound);
        return nmsItemStack;
    }

    /**
     * 从配置中获取卡片显示名称。
     */
    private static String getCardDisplayName(String cardName) {
        // 获取配置文件中所有键的集合
        Set<String> keys = Main.plugin.getConfig().getKeys(true);
        // 将传入的 cardName 转换为小写，以便进行大小写不敏感的匹配
        String lowerCardName = cardName.toLowerCase();
        // 找到匹配的键
        String matchedKey = null;
        for (String key : keys) {
            if (key.toLowerCase().equals(lowerCardName + ".name")) {
                matchedKey = key;
                break;
            }
        }
        // 如果找到了匹配的键，获取对应的值并进行替换
        if (matchedKey != null) {
            return Optional.ofNullable(Main.plugin.getConfig().getString(matchedKey))
                    .map(name -> name.replace("&", "§"))
                    .orElse(null);
        }
        // 如果没有找到匹配的键，返回null
        return null;
    }

    /**
     * 从配置中获取卡片的 Lore 列表。
     */
    private static List<String> getCardLore(String cardName) {
        Set<String> keys = Main.plugin.getConfig().getKeys(true);
        String lowerCardName = cardName.toLowerCase();
        String matchedKey = null;
        for (String key : keys) {
            if (key.toLowerCase().equals(lowerCardName + ".lore")) {
                matchedKey = key;
                break;
            }
        }
        if (matchedKey != null) {
            return Main.plugin.getConfig().getStringList(matchedKey)
                    .stream()
                    .map(lore -> lore.replace("&", "§"))
                    .collect(Collectors.toList());
        }
        return null;
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
                    afterBindPokemon(player, pokemon, cardName);
                    afterUnBreedPokemon(player, pokemon, cardName);
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

    public static void afterBindPokemon(Player player, Pokemon pokemon, String cardName) {
        boolean flag = YmlUtil.afterBindList.getBoolean(cardName);
        if (flag) {
            pokemon.addSpecFlag("untradeable");
            player.sendMessage(YmlUtil.afterBindList.getString("message").replace("&", "§"));
        }
    }


    public static void afterUnBreedPokemon(Player player, Pokemon pokemon, String cardName) {
        boolean flag = YmlUtil.afterUnBreedFileList.getBoolean(cardName);
        if (flag) {
            pokemon.addSpecFlag("unbreedable");
            player.sendMessage(YmlUtil.afterUnBreedFileList.getString("message").replace("&", "§"));
        }
    }
}
