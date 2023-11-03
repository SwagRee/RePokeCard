package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.pokemon.EVsGainedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GuiModifyEvs implements Listener {
    private static final List<Integer> invSlots = Arrays.asList(11, 13, 15, 29, 31, 33);
    private static final String TITLE_PREFIX = "§b选择一项努力值修改吧";
    private static final String cardName = "anyEvs";

    static HashMap<String, Integer> hashmapPlayerPokemon = new HashMap<>();



    public static void Gui(Player player, int slot) {

        Inventory inv = Bukkit.createInventory(null, 45, TITLE_PREFIX);

        HashMap<Integer, StatsType> invSlotItem = new HashMap<>();
        for (StatsType s : StatsType.getStatValues()) {
            invSlotItem.put(invSlots.get(Math.abs(s.getStatIndex()) - 1), s);
        }

        for (Map.Entry<Integer, StatsType> s : invSlotItem.entrySet()) {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§e修改努力值" + s.getValue().getLocalizedName()+"为252");
            itemStack.setItemMeta(itemMeta);
            inv.setItem(s.getKey(), itemStack);
        }
        hashmapPlayerPokemon.put(player.getName(),slot);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase(TITLE_PREFIX)) {
            event.setCancelled(true);
            int clickSlot = event.getSlot();
            Player player = (Player) event.getWhoClicked();
            Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(hashmapPlayerPokemon.get(player.getName()));

            HashMap<Integer, StatsType> invSlotItem = new HashMap<>();
            for (StatsType s : StatsType.getStatValues()) {
                invSlotItem.put(invSlots.get(Math.abs(s.getStatIndex()) - 1), s);
            }
            int total = pokemon.getEVs().getTotal();
            player.closeInventory();
            if(total>258){
                YmlUtil.sendColorEventWarn(player,"No510");
                return;
            }
            for (Map.Entry<Integer, StatsType> s : invSlotItem.entrySet()) {

                if (clickSlot == s.getKey()) {
                    String evsName = s.getValue().getLocalizedName();
                    if(pokemon.getEVs().getStat(s.getValue())==252){
                        YmlUtil.sendColorWarn(player,cardName,pokemon,evsName);
                        return;
                    }
                    ItemStack item = ItemUtil.createItem(cardName);
                    ItemUtil.takeItem(player,item,cardName,pokemon,evsName);
                    pokemon.getEVs().setStat(s.getValue(), 252);

                }
            }
        }
    }
}