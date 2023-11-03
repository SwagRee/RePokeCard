package io.github.swagree.pokecard.event;

import static org.bukkit.ChatColor.*;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.Main;
import io.github.swagree.pokecard.enums.EnumCardName;
import io.github.swagree.pokecard.gui.*;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EventMain implements Listener {

    /**
     * 监听右击事件，如果在副手就不打开了，不是副手打开gui
     *
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    List<String> lores = itemMeta.getLore();
                    String name = YmlUtil.getNameConfigFromLore(lores);

                    for (EnumCardName e : EnumCardName.values()) {
                        if (e.getCardName().equals(name)) {
                            GuiMain.Gui(player, e.getCardNameCN());
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());
        int slot = event.getRawSlot();
        String title = event.getView().getTitle();

        String cardName = null;

        for (EnumCardName guiTitle : EnumCardName.values()) {
            if (title.contains(guiTitle.getCardNameCN())) {
                event.setCancelled(true);
                if (title.equals("§b"+guiTitle.getCardNameCN() + "修改界面")) {
                    cardName = guiTitle.getCardName();

                    Map<Integer, Integer> itemSlotMap = new HashMap<>();
                    itemSlotMap.put(11, 0);
                    itemSlotMap.put(13, 1);
                    itemSlotMap.put(15, 2);
                    itemSlotMap.put(29, 3);
                    itemSlotMap.put(31, 4);
                    itemSlotMap.put(33, 5);

                    for (Map.Entry<Integer, Integer> entry : itemSlotMap.entrySet()) {
                        int invSlot = entry.getValue();
                        int itemSlot = entry.getKey(); // 键对应的值即为invSlot
                        modifySprite(party, player, slot, itemSlot, invSlot, cardName);
                    }
                    break;
                }
            }
        }
    }


    public void modifySprite(PlayerPartyStorage party, Player player, int slot, int invSlot, int spriteSlot, String cardName) {
        ItemStack newItemStack;

        if (slot == invSlot) {
            if (party.get(spriteSlot) == null)
                return;
            newItemStack = ItemUtil.createItem(cardName);
            switch (cardName) {
                case "mt":
                    mtModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "shiny":
                    shinyModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "maxLevel":
                    maxLevelModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "clearLevel":
                    clearLevelModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "clearEvs":
                    clearEvsModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "maxIvs":
                    maxIvsModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "gender":
                    genderModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "anyIvs":
                    GuiModifyIvs.Gui(player, spriteSlot);
                    break;
                case "anyEvs":
                    GuiModifyEvs.Gui(player, spriteSlot);
                    break;
                case "anyGrowth":
                    GuiModifyGrowth.Gui(player, spriteSlot);
                    break;
                case "rdGrowth":
                    rdGrowthModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "anyNature":
                    GuiModifyNature.Gui(player, spriteSlot);
                    break;
                case "rdNature":
                    rdNatureModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "formLegendary":
                case "formCommon":
                    GuiModifyFormLegendaryOrFormCommon.Gui(player, spriteSlot,cardName);
                    break;
                case "form":
                    GuiModifyForm.Gui(player, spriteSlot);
                    break;
                case "pokeBall":
                    GuiModifyPokeBall.Gui(player, spriteSlot);
                    break;
                case "move":
                    GuiModifyPokeMove.Gui(player, spriteSlot);
                    break;
                case "bind":
                    bindModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "unbind":
                    unbindModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "unBreed":
                    unBreedModify(party, player, spriteSlot, cardName, newItemStack);
                    break;
                case "hatch":
                    hatchModify(party,player,spriteSlot,cardName,newItemStack);
                    break;

            }
        }
    }

    private void unBreedModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if (party.get(spriteSlot).hasSpecFlag("unbreedable")) {
            ItemUtil.takeItem(player, newItemStack, cardName,party.get(spriteSlot));
            party.get(spriteSlot).removeSpecFlag("unbreedable");
        } else {
            YmlUtil.sendColorWarn(player, cardName,party.get(spriteSlot));
            return;
        }
        player.closeInventory();
    }

    private void clearLevelModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {

        if (party.get(spriteSlot).getLevel() != 1) {
            party.get(spriteSlot).setLevel(1);
            ItemUtil.takeItem(player, newItemStack, cardName,party.get(spriteSlot));
        } else {
            YmlUtil.sendColorWarn(player, cardName,party.get(spriteSlot));
        }
        player.closeInventory();
    }


    private void unbindModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if (party.get(spriteSlot).hasSpecFlag("untradeable")) {
            ItemUtil.takeItem(player, newItemStack, cardName,party.get(spriteSlot));
            party.get(spriteSlot).removeSpecFlag("untradeable");
        } else {
            YmlUtil.sendColorWarn(player, cardName,party.get(spriteSlot));
            return;
        }
        player.closeInventory();
    }

    private void bindModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if (!party.get(spriteSlot).hasSpecFlag("untradeable")) {
            ItemUtil.takeItem(player, newItemStack, cardName,party.get(spriteSlot));
            party.get(spriteSlot).addSpecFlag("untradeable");
        } else {
            YmlUtil.sendColorWarn(player, cardName,party.get(spriteSlot));
            return;
        }
        player.closeInventory();

    }

    private void rdNatureModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        EnumNature randomNature = EnumNature.getRandomNature();
        ItemUtil.takeItem(player, newItemStack, cardName, party.get(spriteSlot),randomNature.getLocalizedName());
        party.get(spriteSlot).setNature(randomNature);

    }

    private void rdGrowthModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        EnumGrowth randomGrowth = EnumGrowth.getRandomGrowth();
        ItemUtil.takeItem(player, newItemStack, cardName, party.get(spriteSlot),randomGrowth.getLocalizedName());
        party.get(spriteSlot).setGrowth(randomGrowth);
    }

    private void genderModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if (party.get(spriteSlot).getGender().equals(Gender.Male)) {
            ItemUtil.takeItem(player, newItemStack, cardName + "ToFemale", party.get(spriteSlot));
            party.get(spriteSlot).setGender(Gender.Female);
        } else if (party.get(spriteSlot).getGender().equals(Gender.Female)) {
            ItemUtil.takeItem(player, newItemStack, cardName + "ToMale", party.get(spriteSlot));
            party.get(spriteSlot).setGender(Gender.Male);
        } else {
            YmlUtil.sendColorWarn(player, cardName, party.get(spriteSlot));
        }
        player.closeInventory();
    }

    private void maxIvsModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if(!YmlUtil.blackListPokemon(player,party.get(spriteSlot),cardName)){
            player.closeInventory();
            return;
        }
        if (!(party.get(spriteSlot).getIVs().getStat(StatsType.Speed) == 31 &&
                party.get(spriteSlot).getIVs().getStat(StatsType.Attack) == 31 &&
                party.get(spriteSlot).getIVs().getStat(StatsType.SpecialDefence) == 31 &&
                party.get(spriteSlot).getIVs().getStat(StatsType.SpecialAttack) == 31 &&
                party.get(spriteSlot).getIVs().getStat(StatsType.Defence) == 31 &&
                party.get(spriteSlot).getIVs().getStat(StatsType.HP) == 31)
        ) {
            ItemUtil.takeItem(player, newItemStack, cardName, party.get(spriteSlot));
            party.get(spriteSlot).getIVs().setStat(StatsType.Speed, 31);
            party.get(spriteSlot).getIVs().setStat(StatsType.Attack, 31);
            party.get(spriteSlot).getIVs().setStat(StatsType.SpecialDefence, 31);
            party.get(spriteSlot).getIVs().setStat(StatsType.SpecialAttack, 31);
            party.get(spriteSlot).getIVs().setStat(StatsType.Defence, 31);
            party.get(spriteSlot).getIVs().setStat(StatsType.HP, 31);
        } else {
            YmlUtil.sendColorWarn(player, cardName, party.get(spriteSlot));
        }
        player.closeInventory();
    }

    private void clearEvsModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        HashMap<Integer, ItemStack> removedItems;
        if (party.get(spriteSlot).getEVs().getStat(StatsType.Speed) != 0 ||
                party.get(spriteSlot).getEVs().getStat(StatsType.Attack) != 0 ||
                party.get(spriteSlot).getEVs().getStat(StatsType.SpecialDefence) != 0 ||
                party.get(spriteSlot).getEVs().getStat(StatsType.SpecialAttack) != 0 ||
                party.get(spriteSlot).getEVs().getStat(StatsType.Defence) != 0 ||
                party.get(spriteSlot).getEVs().getStat(StatsType.HP) != 0
        ) {
            ItemUtil.takeItem(player, newItemStack, cardName, party.get(spriteSlot));
            party.get(spriteSlot).getEVs().setStat(StatsType.Speed, 0);
            party.get(spriteSlot).getEVs().setStat(StatsType.Attack, 0);
            party.get(spriteSlot).getEVs().setStat(StatsType.SpecialDefence, 0);
            party.get(spriteSlot).getEVs().setStat(StatsType.SpecialAttack, 0);
            party.get(spriteSlot).getEVs().setStat(StatsType.Defence, 0);
            party.get(spriteSlot).getEVs().setStat(StatsType.HP, 0);
        } else {
            YmlUtil.sendColorWarn(player, cardName, party.get(spriteSlot));
        }
        player.closeInventory();
    }

    private void maxLevelModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if (party.get(spriteSlot).getLevel() != 100) {
            ItemUtil.takeItem(player, newItemStack, cardName, party.get(spriteSlot));
            party.get(spriteSlot).setLevel(100);
        } else {
            YmlUtil.sendColorWarn(player, cardName, party.get(spriteSlot));
        }
        player.closeInventory();
    }

    private void shinyModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if (!party.get(spriteSlot).isShiny()) {
            ItemUtil.takeItem(player, newItemStack, cardName, party.get(spriteSlot));
            party.get(spriteSlot).setShiny(true);
        } else {
            YmlUtil.sendColorWarn(player, cardName, party.get(spriteSlot));
        }
        player.closeInventory();
    }

    private void mtModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if(!YmlUtil.blackListPokemon(player,party.get(spriteSlot),cardName)){
            player.closeInventory();
            return;
        }
        if (party.get(spriteSlot).getAbilitySlot() != 2) {
            party.get(spriteSlot).setAbilitySlot(2);
            if (party.get(spriteSlot).getAbilitySlot() == 2) {
                ItemUtil.takeItem(player, newItemStack, cardName,party.get(spriteSlot));
            } else {
                YmlUtil.sendColorEventWarn(player,"NoMt",party.get(spriteSlot));
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, party.get(spriteSlot));
        }
        player.closeInventory();
    }
    private void hatchModify(PlayerPartyStorage party, Player player, int spriteSlot, String cardName, ItemStack newItemStack) {
        if (party.get(spriteSlot).getEggCycles()>0) {
            ItemUtil.takeItem(player, newItemStack, cardName, party.get(spriteSlot));
            party.get(spriteSlot).setEggCycles(-1);
        } else {
            YmlUtil.sendColorWarn(player, cardName, party.get(spriteSlot));
        }
        player.closeInventory();
    }
}
