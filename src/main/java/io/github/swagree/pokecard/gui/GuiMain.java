package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.pokecard.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.*;
import org.bukkit.*;
import com.pixelmonmod.pixelmon.*;
import com.pixelmonmod.pixelmon.items.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

import java.util.*;

/**
 * 绘制一个gui 显示背包的六只宝可梦，标题按照传入的cardName进行设置，并且点击能够跳转到对应的修改gui
 * Question:留有一个问题，宝可梦的属性变量替换太过低效
 */
public class GuiMain {

    private static final List<Integer> invSlot = Arrays.asList(11, 13, 15, 29, 31, 33);
    private static final String TITLE_PREFIX = "修改界面";

    public static void Gui(Player player, String guiName) {
        Inventory inv = Bukkit.createInventory(null, 45, "§b" + guiName + TITLE_PREFIX);
        UUID uuid = player.getUniqueId();
        PlayerPartyStorage pokemon = Pixelmon.storageManager.getParty(uuid);

        for (int i = 0; i < 6; i++) {
            int item = invSlot.get(i);
            SpriteInGui(pokemon, inv, i, item,player);
        }

        player.openInventory(inv);
    }

    private static void SpriteInGui(PlayerPartyStorage pokemon, Inventory inv, int playerSlot, int invSlot,Player player) {
        if (pokemon.get(playerSlot) == null) {
            ItemStack kong = new ItemStack(Material.BARRIER, 1);
            ItemMeta itemMeta = kong.getItemMeta();
            itemMeta.setDisplayName("§c无精灵");
            kong.setItemMeta(itemMeta);
            inv.setItem(invSlot, kong);
        } else {

            net.minecraft.item.ItemStack nmeitem = ItemPixelmonSprite.getPhoto(pokemon.get(playerSlot));
            ItemStack poke = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_12_R1.ItemStack) (Object) nmeitem);

            ItemMeta pmeta = poke.getItemMeta();
            pmeta.setDisplayName("§a" + pokemon.get(playerSlot).getLocalizedName());
            if(pokemon.get(playerSlot).isEgg()){
                pmeta.setDisplayName("§a" + pokemon.get(playerSlot).getLocalizedName()+"的蛋");
            }

            int level = pokemon.get(playerSlot).getLevel();
            int HP = pokemon.get(playerSlot).getIVs().getStat(StatsType.HP);
            int Speed = pokemon.get(playerSlot).getIVs().getStat(StatsType.Speed);
            int Attack = pokemon.get(playerSlot).getIVs().getStat(StatsType.Attack);
            int SpecialAttack = pokemon.get(playerSlot).getIVs().getStat(StatsType.SpecialAttack);
            int SpecialDefence = pokemon.get(playerSlot).getIVs().getStat(StatsType.SpecialDefence);
            int Defence = pokemon.get(playerSlot).getIVs().getStat(StatsType.Defence);
            int evsHP = pokemon.get(playerSlot).getEVs().getStat(StatsType.HP);
            int evsSpeed = pokemon.get(playerSlot).getEVs().getStat(StatsType.Speed);
            int evsAttack = pokemon.get(playerSlot).getEVs().getStat(StatsType.Attack);
            int evsSpecialAttack = pokemon.get(playerSlot).getEVs().getStat(StatsType.SpecialAttack);
            int evsSpecialDefense = pokemon.get(playerSlot).getEVs().getStat(StatsType.SpecialDefence);
            int evsDefence = pokemon.get(playerSlot).getEVs().getStat(StatsType.Defence);
            String ability = pokemon.get(playerSlot).getAbility().getLocalizedName();
            String nature = pokemon.get(playerSlot).getNature().getLocalizedName();
            String growth = pokemon.get(playerSlot).getGrowth().getLocalizedName();
            String gender = pokemon.get(playerSlot).getGender().getLocalizedName();

            String isShiny = "否";
            if(pokemon.get(playerSlot).isShiny()){
                isShiny = "是";
            }

            String isBind = "未绑定";
            if (pokemon.get(playerSlot).hasSpecFlag("untradeable")) {
                isBind = "已绑定";
            }

            String nickname = "无";
            if(pokemon.get(playerSlot).getNickname()!=null){
                nickname = pokemon.get(playerSlot).getNickname();
            }

            List<String> list = new ArrayList<>();
            List<String> lores = Main.plugin.getConfig().getStringList("lore");

            for (String lore : lores) {
                String playerNamePlaceholder = PlaceholderAPI.setPlaceholders(player, lore);
                playerNamePlaceholder = ChatColor.translateAlternateColorCodes('&', playerNamePlaceholder)
                        .replace("%LEVEL%", String.valueOf(level))
                        .replace("%IVS_HP%",String.valueOf(HP))
                        .replace("%IVS_Attack%",String.valueOf(HP))
                        .replace("%IVS_Speed%",String.valueOf(Speed))
                        .replace("%IVS_Defence%",String.valueOf(Attack))
                        .replace("%IVS_SpecialAttack%",String.valueOf(SpecialAttack))
                        .replace("%IVS_SpecialDefence%",String.valueOf(SpecialDefence))
                        .replace("%IVS_Defence%",String.valueOf(Defence))
                        .replace("%EVS_HP%",String.valueOf(evsHP))
                        .replace("%EVS_Attack%",String.valueOf(evsAttack))
                        .replace("%EVS_Speed%",String.valueOf(evsSpeed))
                        .replace("%EVS_Defence%",String.valueOf(evsDefence))
                        .replace("%EVS_SpecialAttack%",String.valueOf(evsSpecialAttack))
                        .replace("%EVS_SpecialDefence%",String.valueOf(evsSpecialDefense))
                        .replace("%EVS_Defence%",String.valueOf(evsDefence))
                        .replace("%BIND%",isBind)
                        .replace("%Shiny%",isShiny)
                        .replace("%Ability%",ability)
                        .replace("%Nature%",nature)
                        .replace("%Growth%",growth)
                        .replace("%Gender%",gender)
                        .replace("%Nick_Name%",nickname);

                list.add(playerNamePlaceholder);
            }

            pmeta.setLore(list); // 设置 Lore
            poke.setItemMeta(pmeta); // 更新物品的元数据
            inv.setItem(invSlot, poke); // 更新物品栏里的相应物品
        }
    }
}