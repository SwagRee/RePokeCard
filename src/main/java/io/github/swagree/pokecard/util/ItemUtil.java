package io.github.swagree.pokecard.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokecard.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * 工具类 包含一些处理的方法
 */
public class ItemUtil {

    public static ItemStack addItemToPlayer(int num, String cardName) throws Exception {
        ItemStack itemStack = new ItemStack(new ItemStack(Material.getMaterial(Main.plugin.getConfig().getInt("id")), num, (short) Main.plugin.getConfig().getInt("data")));
        ItemMeta id = itemStack.getItemMeta();

        Boolean havaFlag = false;
        for (String configCardName : Main.plugin.getConfig().getKeys(false)) {
            if (configCardName.equalsIgnoreCase(cardName)) {
                id.setDisplayName(Main.plugin.getConfig().getString(configCardName + ".name").replace("&", "§"));

                ArrayList<String> lore = new ArrayList();
                List<String> stringList = Main.plugin.getConfig().getStringList(configCardName + ".lore");
                for (String s : stringList) {
                    lore.add(s.replace("&", "§"));
                }
                id.setLore(lore);
                havaFlag = true;
                break;
            }
        }
        if(havaFlag == false){
            throw new Exception("没有这个类型");
        }

        itemStack.setItemMeta(id);
        return itemStack;
    }

    public static void takeItem(Player player, ItemStack newItemStack, String cardName, Pokemon pokemon) {
        PlayerInventory inventory = player.getInventory();
        if(inventory.getItemInOffHand().isSimilar(newItemStack)){
            return;
        }else{
            HashMap<Integer, ItemStack> removedItems;
            removedItems = inventory.removeItem(newItemStack);
            if (removedItems.isEmpty()) {
                YmlUtil.sendColorMessage(player,cardName,pokemon);
            } else {
                YmlUtil.sendColorNoItem(player);
            }
        }
    }
    public static void takeItem(Player player, ItemStack newItemStack, String cardName, Pokemon pokemon,String extra) {
        PlayerInventory inventory = player.getInventory();
        if(inventory.getItemInOffHand().isSimilar(newItemStack)){
            return;
        }else{
            HashMap<Integer, ItemStack> removedItems;
            removedItems = inventory.removeItem(newItemStack);
            if (removedItems.isEmpty()) {
                YmlUtil.sendColorMessage(player,cardName,pokemon,extra);
            } else {
                YmlUtil.sendColorNoItem(player);
            }
        }
    }

    public static ItemStack createItem(String cardName) {
        ItemStack itemStack = new ItemStack(new ItemStack(Material.getMaterial(Main.plugin.getConfig().getInt("id"))));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Main.plugin.getConfig().getString(cardName+".name").replace("&", "§"));
        ArrayList<String> lore = new ArrayList();
        List<String> stringList = Main.plugin.getConfig().getStringList(cardName + ".lore");
        for(String s : stringList){
            lore.add(s.replace("&", "§"));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


}
