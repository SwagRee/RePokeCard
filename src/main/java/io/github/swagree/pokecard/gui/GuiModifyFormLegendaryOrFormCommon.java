package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.util.YmlUtil;
import net.minecraft.client.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiModifyFormLegendaryOrFormCommon implements Listener {
    private static final Map<UUID, Integer> playerSlotMap = new HashMap<>();

    private static String TITLE_PREFIX = "§b选择一种普通宝可梦形态修改吧";
    public static String getTitlePrefix() {
        return TITLE_PREFIX;
    }

    public static void setTitlePrefix(String titlePrefix) {
        TITLE_PREFIX = titlePrefix;
    }
    private static String cardName = "formCommon";

    public static String getCardName() {
        return cardName;
    }

    public static void setCardName(String cardName) {
        GuiModifyFormLegendaryOrFormCommon.cardName = cardName;
    }

    public static void Gui(Player player, int slot,String cardName) {

        setTitlePrefix("§b选择一种普通宝可梦形态修改吧");

        if(cardName.equals("formLegendary")){
            setTitlePrefix("§b选择一种神兽形态修改吧");
            setCardName("formLegendary");
        }
        Inventory inv = Bukkit.createInventory(null, 54, getTitlePrefix());
        Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(slot);
        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);
        if(!YmlUtil.blackListPokemon(player,pokemon,"form")){
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

        if(cardName.equals("formCommon") && !pokemon.isLegendary()){
            player.openInventory(inv);
            playerSlotMap.put(player.getUniqueId(), slot);
            return;
        }
        if(cardName.equals("formLegendary") && pokemon.isLegendary()){
            player.openInventory(inv);
            playerSlotMap.put(player.getUniqueId(), slot);
            return;
        }
        YmlUtil.sendColorEventWarn(player,"NoMatchForm",pokemon);

    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase(getTitlePrefix())) {
            Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
            event.setCancelled(true);
            player.closeInventory();

            Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(playerSlotMap.get(player.getUniqueId()));

            changePokemonForm(event, player, pokemon,getCardName());
        }


    }

    private void changePokemonForm(InventoryClickEvent event, Player player, Pokemon pokemon,String cardName) {
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