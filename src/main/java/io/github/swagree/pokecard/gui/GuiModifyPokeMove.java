package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.Pixelmon;

import com.pixelmonmod.pixelmon.api.pokemon.LearnMoveController;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import io.github.swagree.pokecard.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;



import java.util.*;

public class GuiModifyPokeMove implements Listener {
    private static final String TITLE_PREFIX = "§b选择一项技能修改吧 当前页面:";
    private static final String cardName = "move";
    private static final Map<UUID, Integer> playerSlotMap = new HashMap<>();

    public static void Gui(Player player, int slot) {

        Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(slot);

        List<Attack> allMoves = pokemon.getBaseStats().getAllMoves();
        int page = allMoves.size() / 45;

        Inventory inv = Bukkit.createInventory(null, 54, TITLE_PREFIX + "1");
        int invSlotSize = allMoves.size() >= 45 ? 45 : allMoves.size();
        for (int j = 0; j < invSlotSize; j++) {

            ItemStack itemStack = new ItemStack(Material.BOOK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(allMoves.get(j).getActualMove().getLocalizedName());
            itemStack.setItemMeta(itemMeta);
            inv.setItem(j, itemStack);
        }
        if (allMoves.size() >45 ) {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.AQUA + "下一页");
            itemStack.setItemMeta(itemMeta);
            inv.setItem(53, itemStack);
        }


        player.openInventory(inv);
        playerSlotMap.put(player.getUniqueId(), slot); // 保存玩家与slot的对应关系

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if(title.contains(TITLE_PREFIX)){
            Pokemon pokemon = Pixelmon.storageManager.getParty(event.getWhoClicked().getUniqueId()).get(playerSlotMap.get(event.getWhoClicked().getUniqueId()));
            List<Attack> allMoves = pokemon.getBaseStats().getAllMoves();
            for (int i = 1; i <= allMoves.size() / 45+1; i++) {
                if (title.equalsIgnoreCase(TITLE_PREFIX + i)) {
                    title = event.getView().getTitle();
                    backPage(event,title,allMoves);
                    nextPage(event, title, allMoves);
                }
            }
            int page = Integer.valueOf(title.substring(title.length() - 1));

            Player player = (Player) event.getWhoClicked();
            CraftPlayer craftPlayer = (CraftPlayer) event.getWhoClicked();
            EntityPlayer handle = craftPlayer.getHandle();
            int slot = event.getSlot();
            HumanEntity whoClicked = event.getWhoClicked();
            if(slot>53||slot<0){
                return;
            }else if(slot<45 && slot>=0){

                if((page-1)*45+slot+1>allMoves.size()){
                    return;
                }
                ItemStack newItemStack = ItemUtil.createItem(cardName);
                if(pokemon.getMoveset().size()>3){
                    ItemUtil.takeItem(player,newItemStack,cardName,pokemon);
                    LearnMoveController.sendLearnMove((EntityPlayerMP)(Object) handle,pokemon.getUUID(),pokemon.getBaseStats().getAllMoves().get(slot+(page-1)*45).getActualMove());

                }else{
                    ItemUtil.takeItem(player,newItemStack,cardName,pokemon);
                    pokemon.getMoveset().add(pokemon.getBaseStats().getAllMoves().get(slot+(page-1)*45));

                }
            }
        }
    }

    private void nextPage(InventoryClickEvent event, String title, List<Attack> allMoves) {
        if (event.getSlot() == 53) {
            int page = Integer.valueOf(title.substring(title.length() - 1))+1;
            if(page>allMoves.size()/45+1){
                return;
            }
            Inventory inv = Bukkit.createInventory(null, 54, TITLE_PREFIX + page);
            int invSlotSize = allMoves.size() > 45*page ? 45 : allMoves.size()-45*(page-1);

                for (int j = 0; j < invSlotSize; j++) {

                    ItemStack itemStack = new ItemStack(Material.BOOK);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(allMoves.get(45 * (page-1) + j).getActualMove().getLocalizedName());
                    itemStack.setItemMeta(itemMeta);
                    inv.setItem(j, itemStack);
                }
            if (allMoves.size() > (page-1)*45 ) {
                ItemStack itemStack = new ItemStack(Material.PAPER);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.AQUA + "上一页");
                itemStack.setItemMeta(itemMeta);
                inv.setItem(52, itemStack);
            }
            if (allMoves.size() > page*45) {
                ItemStack itemStack = new ItemStack(Material.PAPER);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.AQUA + "下一页");
                itemStack.setItemMeta(itemMeta);
                inv.setItem(53, itemStack);
            }


            event.getWhoClicked().openInventory(inv);
        }
    }

    private void backPage(InventoryClickEvent event, String title, List<Attack> allMoves) {
        if (event.getSlot() == 52) {
            int currentPage = Integer.parseInt(title.replace(TITLE_PREFIX, ""));
            if (currentPage <= 1) {
                return; // 已经是第一页了
            }
            int prevPage = currentPage - 1;
            int startIndex = (prevPage - 1) * 45;
            int endIndex = Math.min(startIndex + 45, allMoves.size());

            Inventory inv = Bukkit.createInventory(null, 54, TITLE_PREFIX + prevPage);
            for (int j = startIndex; j < endIndex; j++) {
                ItemStack itemStack = new ItemStack(Material.BOOK);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(allMoves.get(j).getActualMove().getLocalizedName());
                itemStack.setItemMeta(itemMeta);
                inv.setItem(j - startIndex, itemStack);
            }

            if (currentPage > 2) {
                ItemStack prevItemStack = new ItemStack(Material.PAPER);
                ItemMeta prevItemMeta = prevItemStack.getItemMeta();
                prevItemMeta.setDisplayName(ChatColor.AQUA + "上一页");
                prevItemStack.setItemMeta(prevItemMeta);
                inv.setItem(52, prevItemStack);
            }

            ItemStack nextItemStack = new ItemStack(Material.PAPER);
            ItemMeta nextItemMeta = nextItemStack.getItemMeta();
            nextItemMeta.setDisplayName(ChatColor.AQUA + "下一页");
            nextItemStack.setItemMeta(nextItemMeta);
            inv.setItem(53, nextItemStack);

            event.getWhoClicked().openInventory(inv);
        }
    }



}