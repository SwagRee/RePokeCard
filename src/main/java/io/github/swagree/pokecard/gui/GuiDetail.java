package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokecard.event.EventGuiMain;
import io.github.swagree.pokecard.gui.guiHolder.NeedNextHolder;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class GuiDetail implements Listener {

    public static void showGui(Player player, String cardName) {
        try {
            NeedNextHolder needNextHolder = new NeedNextHolder(player, cardName);
            Inventory inv = needNextHolder.inv;

            if (isBlacklisted(player, cardName)) {
                player.closeInventory();
                return;
            }

            player.openInventory(inv);
        } catch (Exception e) {
            e.printStackTrace(); // Replace with proper logging if needed
        }
    }

    private static boolean isBlacklisted(Player player, String cardName) {
        Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);

        if (cardName.startsWith("form") && !YmlUtil.blackListPokemon(player, pokemon, "form")) {
            return true;
        }

        return cardName.equals("mt") || cardName.equals("anyIvs") || cardName.equals("maxIvs")
                ? !YmlUtil.blackListPokemon(player, pokemon, cardName)
                : false;
    }
}
