package io.github.swagree.pokecard.event.DetailList;

import com.pixelmonmod.pixelmon.api.pokemon.LearnMoveController;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import io.github.swagree.pokecard.event.EventGuiMain;
import io.github.swagree.pokecard.gui.guiHolder.NeedNextHolder;
import io.github.swagree.pokecard.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EventGuiDetailPokeMove implements Listener {
    private static final String cardName = "move";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof NeedNextHolder) {
            NeedNextHolder inventory = (NeedNextHolder) event.getInventory().getHolder();
            if (!inventory.cardName.equals("move")) {
                return;
            }
            Player player = (Player) event.getWhoClicked();

            Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);
            List<Attack> allMoves = pokemon.getBaseStats().getAllMoves();
            int page = inventory.page;

            if (event.getSlot() == 52) {
                backPage(event, page, allMoves);
            } else if (event.getSlot() == 53) {
                nextPage(event, page, allMoves);
            } else {
                handleMoveSelection(event, player, pokemon, allMoves, page);
            }
        }
    }

    private void handleMoveSelection(InventoryClickEvent event, Player player, Pokemon pokemon, List<Attack> allMoves, int page) {
        int slot = event.getSlot();
        if (slot < 0 || slot >= 45) {
            return;
        }
        int moveIndex = (page - 1) * 45 + slot;
        if (moveIndex >= allMoves.size()) {
            return;
        }
        ItemStack newItemStack = ItemUtil.createItem(cardName);
        CraftPlayer craftPlayer = (CraftPlayer) event.getWhoClicked();
        EntityPlayer handlePlayer = craftPlayer.getHandle();
        if (pokemon.getMoveset().size() > 3) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {

                LearnMoveController.sendLearnMove((EntityPlayerMP) (Object) handlePlayer, pokemon.getUUID(), allMoves.get(moveIndex).getActualMove());
                EventGuiMain.afterBindPokemon(player, pokemon, cardName);
            }
        } else {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.getMoveset().add(allMoves.get(moveIndex));
                EventGuiMain.afterBindPokemon(player, pokemon, cardName);
                EventGuiMain.afterUnBreedPokemon(player, pokemon, cardName);
            }
        }
    }

    private void nextPage(InventoryClickEvent event, int page, List<Attack> allMoves) {
        if (event.getSlot() == 53) {
            if (page >= (allMoves.size() + 44) / 45) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            NeedNextHolder needNextHolder = new NeedNextHolder(player, cardName,page+1);
            needNextHolder.page = page + 1;
            updateInventory(needNextHolder, allMoves);
            event.getWhoClicked().openInventory(needNextHolder.inv);
        }
    }

    private void backPage(InventoryClickEvent event, int page, List<Attack> allMoves) {
        if (event.getSlot() == 52) {
            if (page <= 1) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            NeedNextHolder needNextHolder = new NeedNextHolder(player, cardName,page-1);
            needNextHolder.page = page - 1;
            updateInventory(needNextHolder, allMoves);
            event.getWhoClicked().openInventory(needNextHolder.inv);
        }
    }

    private void updateInventory(NeedNextHolder needNextHolder, List<Attack> allMoves) {
        Inventory inv = needNextHolder.inv;
        int startIndex = (needNextHolder.page - 1) * 45;
        int endIndex = Math.min(startIndex + 45, allMoves.size());

        // Clear inventory slots first
        for (int i = 0; i < 45; i++) {
            inv.setItem(i, new ItemStack(Material.AIR));
        }

        for (int j = startIndex; j < endIndex; j++) {
            ItemStack itemStack = new ItemStack(Material.BOOK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            String localizedName = allMoves.get(j).getActualMove().getLocalizedName();
            itemMeta.setDisplayName(localizedName);
            itemStack.setItemMeta(itemMeta);
            inv.setItem(j - startIndex, itemStack);
        }

        addNavigationButtons(inv, needNextHolder.page, allMoves.size());
    }

    private void addNavigationButtons(Inventory inv, int currentPage, int totalMoves) {
        if (currentPage > 1) {
            ItemStack prevItemStack = new ItemStack(Material.PAPER);
            ItemMeta prevItemMeta = prevItemStack.getItemMeta();
            prevItemMeta.setDisplayName(ChatColor.AQUA + "上一页");
            prevItemStack.setItemMeta(prevItemMeta);
            inv.setItem(52, prevItemStack);
        } else {
            inv.setItem(52, new ItemStack(Material.AIR));
        }

        if (currentPage < (totalMoves + 44) / 45) {
            ItemStack nextItemStack = new ItemStack(Material.PAPER);
            ItemMeta nextItemMeta = nextItemStack.getItemMeta();
            nextItemMeta.setDisplayName(ChatColor.AQUA + "下一页");
            nextItemStack.setItemMeta(nextItemMeta);
            inv.setItem(53, nextItemStack);
        } else {
            inv.setItem(53, new ItemStack(Material.AIR));
        }
    }
}