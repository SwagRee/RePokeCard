package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
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

public class GuiModifyIvs implements Listener {
    private static final List<Integer> invSlots = Arrays.asList(11, 13, 15, 29, 31, 33);
    private static final String TITLE_PREFIX = "§b选择一项个体修改吧";
    private static final String cardName = "anyIvs";

    private static GuiModifyIvs guiModifyIvs; // 声明为静态变量
    private int slot;

    public void setSlot(int value) {
        this.slot = value;
    }

    public int getSlot() {
        return slot;
    }


    public static void Gui(Player player, int slot) {
        guiModifyIvs = new GuiModifyIvs();

        Inventory inv = Bukkit.createInventory(null, 45, TITLE_PREFIX);

        HashMap<Integer, StatsType> invSlotItem = new HashMap<>();
        for (StatsType s : StatsType.getStatValues()) {
            invSlotItem.put(invSlots.get(Math.abs(s.getStatIndex()) - 1), s);
        }

        for (Map.Entry<Integer, StatsType> s : invSlotItem.entrySet()) {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§e修改" + s.getValue().getLocalizedName()+"个体为31");
            itemStack.setItemMeta(itemMeta);
            inv.setItem(s.getKey(), itemStack);
        }
        guiModifyIvs.setSlot(slot);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase(TITLE_PREFIX)) {
            event.setCancelled(true);

            int clickSlot = event.getSlot();
            Player player = (Player) event.getWhoClicked();
            Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(guiModifyIvs.getSlot());
            if(!YmlUtil.blackListPokemon(player,pokemon,cardName)){
                player.closeInventory();
                return;
            }
            HashMap<Integer, StatsType> invSlotItem = new LinkedHashMap<>();
            for (StatsType s : StatsType.getStatValues()) {
                invSlotItem.put(invSlots.get(Math.abs(s.getStatIndex()) - 1), s);
            }
            for (Map.Entry<Integer, StatsType> s : invSlotItem.entrySet()) {
                if (clickSlot == s.getKey()) {
                    player.closeInventory();
                    String evsName = s.getValue().getLocalizedName();
                    if(pokemon.getIVs().getStat(s.getValue())==31){
                        YmlUtil.sendColorWarn(player,cardName,pokemon,evsName);
                        return;
                    }
                    ItemStack item = ItemUtil.createItem(cardName);
                    ItemUtil.takeItem(player,item,cardName,pokemon,evsName);
                    pokemon.getIVs().setStat(s.getValue(), 31);
                }
            }
        }
    }



}