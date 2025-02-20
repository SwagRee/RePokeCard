package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import io.github.swagree.pokecard.Main;
import io.github.swagree.pokecard.gui.guiHolder.OnlyPageHolder;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

import java.util.ArrayList;
import java.util.List;

public class GuiMain {

    public static void Gui(Player player, String cardName) {
        OnlyPageHolder onlyPageHolder = new OnlyPageHolder(player, cardName);
        player.openInventory(onlyPageHolder.getInventory());
    }

    public static void SpriteInGui(Inventory inv, Player player, Pokemon pokemon, int invSlot) {
        if (pokemon == null) {
            setEmptySlot(inv, invSlot);
            return;
        }

        ItemStack poke = getPokemonItemStack(pokemon);
        ItemMeta pmeta = poke.getItemMeta();
        pmeta.setDisplayName("§a" + (pokemon.isEgg() ? pokemon.getLocalizedName() + "的蛋" : pokemon.getLocalizedName()));

        if (pokemon.isEgg() && !Main.plugin.getConfig().getBoolean("eggDetail")) {
            setEggLore(pmeta, pokemon);
        } else {
            setPokemonLore(pmeta, player, pokemon);
        }

        poke.setItemMeta(pmeta);
        inv.setItem(invSlot, poke);
    }

    private static void setEmptySlot(Inventory inv, int invSlot) {
        ItemStack empty = new ItemStack(Material.BARRIER, 1);
        ItemMeta itemMeta = empty.getItemMeta();
        itemMeta.setDisplayName("§c无精灵");
        empty.setItemMeta(itemMeta);
        inv.setItem(invSlot, empty);
    }

    private static ItemStack getPokemonItemStack(Pokemon pokemon) {
        net.minecraft.item.ItemStack nmeitem = ItemPixelmonSprite.getPhoto(pokemon);
        return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_12_R1.ItemStack) (Object) nmeitem);
    }

    private static void setEggLore(ItemMeta pmeta, Pokemon pokemon) {
        int eggSteps = pokemon.getEggSteps();
        int allSteps = (pokemon.getBaseStats().getEggCycles() - 1) * 256;
        List<String> lores = new ArrayList<>();
        lores.add("§b当前步数:§f" + eggSteps + "   §b需要的总步数:§f" + allSteps);
        pmeta.setLore(lores);
    }

    private static void setPokemonLore(ItemMeta pmeta, Player player, Pokemon pokemon) {
        List<String> lores = Main.plugin.getConfig().getStringList("lore");
        List<String> formattedLores = new ArrayList<>();
        String formattedLore = "";

        for (String lore : lores) {
            formattedLore = lore
                    .replace("%LEVEL%", String.valueOf(pokemon.getLevel()))
                    .replace("%IVS_HP%", String.valueOf(pokemon.getIVs().getStat(StatsType.HP)))
                    .replace("%IVS_Attack%", String.valueOf(pokemon.getIVs().getStat(StatsType.Attack)))
                    .replace("%IVS_Speed%", String.valueOf(pokemon.getIVs().getStat(StatsType.Speed)))
                    .replace("%IVS_Defence%", String.valueOf(pokemon.getIVs().getStat(StatsType.Defence)))
                    .replace("%IVS_SpecialAttack%", String.valueOf(pokemon.getIVs().getStat(StatsType.SpecialAttack)))
                    .replace("%IVS_SpecialDefence%", String.valueOf(pokemon.getIVs().getStat(StatsType.SpecialDefence)))
                    .replace("%EVS_HP%", String.valueOf(pokemon.getEVs().getStat(StatsType.HP)))
                    .replace("%EVS_Attack%", String.valueOf(pokemon.getEVs().getStat(StatsType.Attack)))
                    .replace("%EVS_Speed%", String.valueOf(pokemon.getEVs().getStat(StatsType.Speed)))
                    .replace("%EVS_Defence%", String.valueOf(pokemon.getEVs().getStat(StatsType.Defence)))
                    .replace("%EVS_SpecialAttack%", String.valueOf(pokemon.getEVs().getStat(StatsType.SpecialAttack)))
                    .replace("%EVS_SpecialDefence%", String.valueOf(pokemon.getEVs().getStat(StatsType.SpecialDefence)))
                    .replace("%BIND%", pokemon.hasSpecFlag("untradeable") ? "已绑定" : "未绑定")
                    .replace("%Shiny%", pokemon.isShiny() ? "是" : "否")
                    .replace("%Ability%", pokemon.getAbility().getLocalizedName())
                    .replace("%Nature%", pokemon.getNature().getLocalizedName())
                    .replace("%Growth%", pokemon.getGrowth().getLocalizedName())
                    .replace("%Gender%", pokemon.getGender().getLocalizedName())
                    .replace("%Nick_Name%", pokemon.getNickname() != null ? pokemon.getNickname() : "无");

            formattedLores.add(formattedLore.replace("&","§"));
        }

        pmeta.setLore(formattedLores);
    }
}