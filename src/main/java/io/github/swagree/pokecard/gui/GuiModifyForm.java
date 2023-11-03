package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GuiModifyForm implements Listener {
    private static final String TITLE_PREFIX = "§b选择一种形态修改吧";
    private static final String cardName = "form";
    private static final Map<UUID, Integer> playerSlotMap = new HashMap<>();


    public static void Gui(Player player, int slot) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE_PREFIX);
        Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(slot);
        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);
        if(!YmlUtil.blackListPokemon(player,pokemon,cardName)){
            player.closeInventory();
            return;
        }
        for (int i = 0; i < possibleForms.size(); i++) {
            Pokemon pokemon1 = Pixelmon.pokemonFactory.create(pokemon.getSpecies());
            pokemon1.setForm(possibleForms.get(i));
            net.minecraft.item.ItemStack nmeitem = ItemPixelmonSprite.getPhoto(pokemon1);
            ItemStack poke = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_12_R1.ItemStack) (Object) nmeitem);
            ItemMeta itemMeta = poke.getItemMeta();
            itemMeta.setDisplayName((possibleForms.get(i)).getLocalizedName());
            poke.setItemMeta(itemMeta);
            inv.setItem(i, poke);
        }
        player.openInventory(inv);
        playerSlotMap.put(player.getUniqueId(), slot); // 保存玩家与slot的对应关系
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase(TITLE_PREFIX)) {
            Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
            event.setCancelled(true);
            player.closeInventory();

            Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(playerSlotMap.get(player.getUniqueId()));

            changePokemonForm(event, player, pokemon);
        }


    }

    private void changePokemonForm(InventoryClickEvent event, Player player, Pokemon pokemon) {
        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);
        if (event.getSlot() >= possibleForms.size())
            return;
        if (!(possibleForms.get(event.getSlot())).getLocalizedName().equals(pokemon.getFormEnum().getLocalizedName())) {
            ItemStack newItemStack = ItemUtil.createItem(cardName);
            ItemUtil.takeItem(player,newItemStack,cardName, pokemon,possibleForms.get(event.getSlot()).getLocalizedName());
            pokemon.setForm(possibleForms.get(event.getSlot()));
        } else {
            YmlUtil.sendColorWarn(player,cardName,pokemon, pokemon.getFormEnum().getLocalizedName());
        }
    }




}