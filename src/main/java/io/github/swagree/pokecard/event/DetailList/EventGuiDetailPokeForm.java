package io.github.swagree.pokecard.event.DetailList;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import io.github.swagree.pokecard.Main;
import io.github.swagree.pokecard.event.EventGuiMain;
import io.github.swagree.pokecard.gui.guiHolder.NeedNextHolder;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EventGuiDetailPokeForm implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof NeedNextHolder)) {
            return;
        }

        NeedNextHolder holder = (NeedNextHolder) event.getInventory().getHolder();

        if (!holder.cardName.startsWith("form")) {
            return;
        }

        int clickSlot = event.getRawSlot();

        if (clickSlot < 0 || clickSlot >= holder.inv.getSize()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);

        if (pokemon == null) {
            player.sendMessage(ChatColor.RED + "宝可梦没有！");
            return;
        }

        event.setCancelled(true);

        player.closeInventory();

        if ("formDetail".equals(holder.cardName)) {
            handleFormDetailChange(event, player, pokemon, holder.cardName);
        } else {
            handleFormChange(event, player, pokemon, holder.cardName);
        }
    }

    private void handleFormDetailChange(InventoryClickEvent event, Player player, Pokemon pokemon, String cardName) {
        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);
        int slot = event.getSlot();
        if (slot >= possibleForms.size()) {
            return;
        }

        IEnumForm selectedForm = possibleForms.get(slot);
        if (selectedForm.isTemporary() || selectedForm.isDefaultForm()) {
            YmlUtil.sendColorEventWarn(player, "NoTemporaryFrom");
            return;
        }

        ItemStack handItem = player.getItemInHand();
        if (handItem == null || !handItem.hasItemMeta() || !handItem.getItemMeta().hasDisplayName()) {
            player.sendMessage(ChatColor.RED + "You are not holding the correct item.");
            return;
        }

        ItemStack newItemStack = ItemUtil.createItem(cardName);
        if (!handItem.getItemMeta().getDisplayName().equals(newItemStack.getItemMeta().getDisplayName())) {
            return;
        }

        ItemMeta itemMeta = handItem.getItemMeta();
        String localNameForm = selectedForm.getLocalizedName();
        List<String> lores = Main.plugin.getConfig().getStringList("cardDetailDataLore");
        List<String> updatedLores = new ArrayList<>();

        for (String lore : lores) {
            updatedLores.add(lore
                    .replace("{pokemon}", pokemon.getLocalizedName())
                    .replace("{form}", localNameForm)
                    .replace("{slot}", String.valueOf(slot + 1))
                    .replace("&", "§"));
        }

        itemMeta.setLore(updatedLores);
        handItem.setItemMeta(itemMeta);

        String formDetailMessage = YmlUtil.message.getString("Message.formDetail")
                .replace("%pokemon%", pokemon.getLocalizedName())
                .replace("%form%", localNameForm)
                .replace("&", "§");
        player.sendMessage(formDetailMessage);
    }

    private void handleFormChange(InventoryClickEvent event, Player player, Pokemon pokemon, String cardName) {
        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);
        int slot = event.getSlot();
        if (slot >= possibleForms.size()) {
            return;
        }

        IEnumForm selectedForm = possibleForms.get(slot);
        if (selectedForm.getLocalizedName().equals(pokemon.getFormEnum().getLocalizedName())) {
            YmlUtil.sendColorWarn(player, cardName, pokemon, selectedForm.getLocalizedName());
            return;
        }

        if (selectedForm.isTemporary() || selectedForm.isDefaultForm()) {
            YmlUtil.sendColorEventWarn(player, "NoTemporaryFrom");
            return;
        }

        ItemStack newItemStack = ItemUtil.createItem(cardName);
        if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, selectedForm.getLocalizedName())) {
            pokemon.setForm(selectedForm);
            EventGuiMain.afterBindPokemon(player, pokemon, cardName);
            EventGuiMain.afterUnBreedPokemon(player, pokemon, cardName);
        }
    }
}
