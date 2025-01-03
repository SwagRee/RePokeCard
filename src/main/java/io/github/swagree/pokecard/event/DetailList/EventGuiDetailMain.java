package io.github.swagree.pokecard.event.DetailList;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import io.github.swagree.pokecard.event.EventGuiMain;
import io.github.swagree.pokecard.gui.guiHolder.NeedNextHolder;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EventGuiDetailMain implements Listener {

    public static String cardName = "";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof NeedNextHolder)) {
            return;
        }
        event.setCancelled(true);

        NeedNextHolder needNextHolder = (NeedNextHolder) holder;
        int clickSlot = event.getRawSlot();

        if (clickSlot > needNextHolder.inv.getSize() - 1 || clickSlot < 0) {
            return;
        }

        cardName = needNextHolder.cardName;
        if (cardName.startsWith("form") || cardName.equals("move")) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);

        player.closeInventory();

        Integer selectedIndex = needNextHolder.mapSlotToField.get(clickSlot);

        if (selectedIndex == null) {
            return;
        }

        switch (cardName) {
            case "anyZeroIvs":
                handleStatChange(selectedIndex, player, pokemon, StatsType.getStatValues(), 0, pokemon.getIVs()::setStat, pokemon.getIVs()::getStat);
                break;
            case "anyIvs":
                handleStatChange(selectedIndex, player, pokemon, StatsType.getStatValues(), 31, pokemon.getIVs()::setStat, pokemon.getIVs()::getStat);
                break;
            case "anyEvs":
                handleStatChange(selectedIndex, player, pokemon, StatsType.getStatValues(), 252, pokemon.getEVs()::setStat, pokemon.getEVs()::getStat, 510);
                break;
            case "anyGrowth":
                handleChange(selectedIndex, player, pokemon, Arrays.asList(EnumGrowth.values()), EnumGrowth::getLocalizedName, pokemon::setGrowth, pokemon.getGrowth());
                break;
            case "pokeBall":
                handleChange(selectedIndex, player, pokemon, Arrays.asList(EnumPokeballs.values()), EnumPokeballs::getLocalizedName, pokemon::setCaughtBall, pokemon.getCaughtBall());
                break;
            case "anyNature":
                handleChange(selectedIndex, player, pokemon, Arrays.asList(EnumNature.values()), EnumNature::getLocalizedName, pokemon::setNature, pokemon.getNature());
                break;
            default:
                break;
        }
    }

    private <T> void handleChange(Integer index, Player player, Pokemon pokemon, List<T> options,
                                  Function<T, String> getLocalizedName,
                                  Consumer<T> changeHandler, T current) {
        if (index < 1 || index > options.size()) return;

        T selected = options.get(index - 1);
        if (selected.equals(current)) {
            YmlUtil.sendColorWarn(player, cardName, pokemon, getLocalizedName.apply(current));
            return;
        }

        ItemStack item = ItemUtil.createItem(cardName);
        if (ItemUtil.takeItem(player, item, cardName, pokemon, getLocalizedName.apply(selected))) {
            changeHandler.accept(selected);
            EventGuiMain.afterBindPokemon(player, pokemon, cardName);
            EventGuiMain.afterUnBreedPokemon(player, pokemon, cardName);
        }
    }

    private void handleStatChange(Integer index, Player player, Pokemon pokemon, StatsType[] statsTypes,
                                  int targetValue, BiConsumer<StatsType, Integer> setStat,
                                  Function<StatsType, Integer> getStat, int... maxTotal) {
        for (StatsType statType : statsTypes) {
            if (index == statType.getStatIndex() || index == Math.abs(statType.getStatIndex())) {
                if (maxTotal.length > 0 && pokemon.getEVs().getTotal() > maxTotal[0]) {
                    YmlUtil.sendColorEventWarn(player, "No510");
                    return;
                }
                if (getStat.apply(statType) == targetValue) {
                    YmlUtil.sendColorWarn(player, cardName, pokemon, statType.getLocalizedName());
                    return;
                }

                ItemStack item = ItemUtil.createItem(cardName);
                if (ItemUtil.takeItem(player, item, cardName, pokemon, statType.getLocalizedName())) {
                    setStat.accept(statType, targetValue);
                    EventGuiMain.afterBindPokemon(player, pokemon, cardName);
                    EventGuiMain.afterUnBreedPokemon(player, pokemon, cardName);
                }
            }
        }
    }
}
