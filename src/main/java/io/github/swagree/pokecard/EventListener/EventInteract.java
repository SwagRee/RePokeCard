package io.github.swagree.pokecard.EventListener;

import io.github.swagree.pokecard.Main;
import io.github.swagree.pokecard.enums.EnumCardName;
import io.github.swagree.pokecard.gui.GuiMain;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    String metaDisplayName = itemMeta.getDisplayName();
                    String lore = YmlUtil.getNameConfigFromLore(metaDisplayName);
                    String name = getFormDetailCardName();
                    String displayName = itemMeta.getDisplayName();

                    try {
                        if (cardIsDetailCardToExecute(player, name, displayName)) return;
                    } catch (Exception ignored) {

                    }
                    ifExistCardThenOpenGui(player, lore);
                }
            }
        }
    }
    private String getFormDetailCardName() {
        String name = Main.plugin.getConfig().getString("formDetail.name").replace("&", "§");
        return name;
    }

    private boolean cardIsDetailCardToExecute(Player player, String name, String displayName) {
        if (name.contains(displayName)) {
            String cardNameCN = EnumCardName.FromDetail.getCardNameCN();
            GuiMain.Gui(player, cardNameCN);
            return true;
        }
        return false;
    }

    private void ifExistCardThenOpenGui(Player player, String lore) {
        for (EnumCardName e : EnumCardName.values()) {
            if (e.getCardName().equals(lore)) {
                GuiMain.Gui(player, e.getCardName());
            }
        }
    }


}
