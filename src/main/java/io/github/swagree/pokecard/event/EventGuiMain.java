package io.github.swagree.pokecard.event;


import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import io.github.swagree.pokecard.gui.guiHolder.OnlyPageHolder;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.Main;
import io.github.swagree.pokecard.enums.EnumCardName;
import io.github.swagree.pokecard.gui.*;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventGuiMain implements Listener {

    /**
     * 监听右击事件，如果在副手就不打开了，不是副手打开gui
     *
     * @param event
     */
    public static Map<Player, Pokemon> mapPlayerToPokemon = new HashMap<>();
    ;

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


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        InventoryHolder holder = event.getInventory().getHolder();

        if (!(event.getInventory().getHolder() instanceof OnlyPageHolder)) {
            return;
        }
        event.setCancelled(true);
        OnlyPageHolder onlyPageHolder = (OnlyPageHolder) holder;
        Player player = (Player) event.getWhoClicked();
        Pokemon pokemon = onlyPageHolder.mapSlotToPokemon.get(event.getRawSlot());
        mapPlayerToPokemon.put(player, pokemon);
        modifySprite(player, pokemon, onlyPageHolder.cardName);
    }


    public void modifySprite(Player player, Pokemon pokemon, String cardName) {
        ItemStack newItemStack;

        if (pokemon == null) return;

        newItemStack = ItemUtil.createItem(cardName);

        switch (cardName) {
            case "mt":
                mtModify(player, pokemon, cardName, newItemStack);
                break;
            case "shiny":
                shinyModify(player, pokemon, cardName, newItemStack);
                break;
            case "unShiny":
                unShinyModify(player, pokemon, cardName, newItemStack);
                break;
            case "maxLevel":
                maxLevelModify(player, pokemon, cardName, newItemStack);
                break;
            case "clearLevel":
                clearLevelModify(player, pokemon, cardName, newItemStack);
                break;
            case "clearEvs":
                clearEvsModify(player, pokemon, cardName, newItemStack);
                break;
            case "maxIvs":
                maxIvsModify(player, pokemon, cardName, newItemStack);
                break;
            case "gender":
                genderModify(player, pokemon, cardName, newItemStack);
                break;
            case "rdGrowth":
                rdGrowthModify(player, pokemon, cardName, newItemStack);
                break;
            case "bind":
                bindModify(player, pokemon, cardName, newItemStack);
                break;
            case "unbind":
                unbindModify(player, pokemon, cardName, newItemStack);
                break;
            case "breed":
                breedModify(player, pokemon, cardName, newItemStack);
                break;
            case "unBreed":
                unBreedModify(player, pokemon, cardName, newItemStack);
                break;
            case "hatch":
                hatchModify(player, pokemon, cardName, newItemStack);
                break;
            case "rdNature":
                rdNatureModify(player, pokemon, cardName, newItemStack);
                break;
            case "rdForm":
                rdFormModify(player, pokemon, cardName, newItemStack);
                break;
            case "formDetail":
                formDetailModify(player, cardName);
                break;
            case "ability":
                abilityDetailModify(player, cardName);


            case "anyNature":
            case "form":
            case "formLegendary":
            case "formCommon":
            case "pokeBall":
            case "move":
            case "anyIvs":
            case "anyZeroIvs":
            case "anyEvs":
            case "anyGrowth":
                GuiDetail.showGui(player, cardName);
                break;
        }

    }


    private void breedModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (!pokemon.hasSpecFlag("unbreedable")) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.addSpecFlag("unbreedable");
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void unShinyModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (pokemon.isShiny()) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.setShiny(false);
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void abilityDetailModify(Player player, String cardName) {
        if (player.isOp()) {
            GuiDetail.showGui(player, cardName);
            return;
        }
        Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);

        List<AbilityBase> allAbilities = pokemon.getBaseStats().getAllAbilities();
        for (AbilityBase allAbility : allAbilities) {
            System.out.println(allAbility);
        }


    }

    private void formDetailModify(Player player, String cardName) {
        if (player.isOp()) {
            GuiDetail.showGui(player, cardName);
            return;
        }
        Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);
        String pokemonName = pokemon.getLocalizedName();
        List<String> lores = player.getItemInHand().getItemMeta().getLore();
        boolean flag = false;
        for (String lore : lores) {
            if (lore.contains(pokemonName)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            player.sendMessage(YmlUtil.message.getString("Warn.formDetail").replace("&", "§"));
            player.closeInventory();
            return;
        }
        if (flag) {
            for (String lore : lores) {
                if (lore.contains("形态编号")) {
                    String[] parts = lore.split(":");
                    if (parts.length > 1) {
                        Integer number = Integer.valueOf(parts[1].trim());
                        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);
                        ItemStack itemInHand = player.getInventory().getItemInMainHand(); // 获取玩家手中的物品堆栈
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                        pokemon.setForm(possibleForms.get(number - 1));
                        player.sendMessage("§e修改" + pokemon.getLocalizedName() + "为" + pokemon.getFormEnum().getLocalizedName() + "形态成功！");
                        afterBindPokemon(player, pokemon, "form");
                        player.closeInventory();
                        return;
                    }
                }
            }
        }
    }

    public static void afterBindPokemon(Player player, Pokemon pokemon, String cardName) {
        boolean flag = YmlUtil.afterBindList.getBoolean(cardName);
        if (flag) {
            pokemon.addSpecFlag("untradeable");
            player.sendMessage(YmlUtil.afterBindList.getString("message").replace("&", "§"));
        }
    }


    public static void afterUnBreedPokemon(Player player, Pokemon pokemon, String cardName) {
        boolean flag = YmlUtil.afterUnBreedFileList.getBoolean(cardName);
        if (flag) {
            pokemon.addSpecFlag("unbreedable");
            player.sendMessage(YmlUtil.afterUnBreedFileList.getString("message").replace("&", "§"));
        }
    }


    private void rdFormModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {

        if (!YmlUtil.blackListPokemon(player, pokemon, "form")) {
            player.closeInventory();
            return;
        }

        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);
        int formSize = possibleForms.size();
        Random random = new Random();
        int randomNum = random.nextInt(formSize);


        int countSize = 0;
        if (formSize < 2) {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
            player.closeInventory();
            return;
        }
        for (int i = 0; i < possibleForms.size(); i++) {
            if (possibleForms.get(i).isDefaultForm() || possibleForms.get(i).isTemporary()) {
                countSize++;
            }
        }
        if (countSize == possibleForms.size()) {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
            player.closeInventory();
            return;
        }
        if ((possibleForms.get(randomNum).getLocalizedName().equals(pokemon.getFormEnum().getLocalizedName()))) {
            YmlUtil.sendColorWarn(player, "form", pokemon, pokemon.getFormEnum().getLocalizedName());
            player.closeInventory();
            return;
        }

        IEnumForm iEnumForm = possibleForms.get(randomNum);
        if (iEnumForm.isTemporary() || iEnumForm.isDefaultForm()) {
            YmlUtil.sendColorEventWarn(player, "NoTemporaryFrom");
            player.closeInventory();
            return;
        }
        if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, pokemon.getFormEnum().getLocalizedName())) {
            pokemon.setForm(iEnumForm);
            afterBindPokemon(player, pokemon, cardName);
            afterUnBreedPokemon(player, pokemon, cardName);

            player.closeInventory();
        }
    }

    private void unBreedModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (pokemon.hasSpecFlag("unbreedable")) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.removeSpecFlag("unbreedable");
                afterBindPokemon(player, pokemon, cardName);
                afterUnBreedPokemon(player, pokemon, cardName);
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void clearLevelModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {

        if (pokemon.getLevel() != 1) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.setLevel(1);
                afterBindPokemon(player, pokemon, cardName);
                afterUnBreedPokemon(player, pokemon, cardName);
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }


    private void unbindModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (pokemon.hasSpecFlag("untradeable")) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.removeSpecFlag("untradeable");
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void bindModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (!pokemon.hasSpecFlag("untradeable")) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.addSpecFlag("untradeable");
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void rdNatureModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        EnumNature randomNature = EnumNature.getRandomNature();
        if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, randomNature.getLocalizedName())) {
            pokemon.setNature(randomNature);
            afterBindPokemon(player, pokemon, cardName);
            afterUnBreedPokemon(player, pokemon, cardName);
        }
        player.closeInventory();

    }

    private void rdGrowthModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        EnumGrowth randomGrowth = EnumGrowth.getRandomGrowth();
        if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, randomGrowth.getLocalizedName())) {
            pokemon.setGrowth(randomGrowth);
            afterBindPokemon(player, pokemon, cardName);
            afterUnBreedPokemon(player, pokemon, cardName);

        }
        player.closeInventory();

    }

    private void genderModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        Gender currentGender = pokemon.getGender();
        Gender targetGender = currentGender.equals(Gender.Male) ? Gender.Female : Gender.Male;
        String genderChangeItem = cardName + (currentGender.equals(Gender.Male) ? "ToFemale" : "ToMale");

        if (currentGender.equals(Gender.Male) || currentGender.equals(Gender.Female)) {
            if (ItemUtil.takeItem(player, newItemStack, genderChangeItem, pokemon, null)) {
                pokemon.setGender(targetGender);
                afterBindPokemon(player, pokemon, cardName);
                afterUnBreedPokemon(player, pokemon, cardName);
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void maxIvsModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (!YmlUtil.blackListPokemon(player, pokemon, cardName)) {
            player.closeInventory();
            return;
        }
        if (!(pokemon.getIVs().getStat(StatsType.Speed) == 31 &&
                pokemon.getIVs().getStat(StatsType.Attack) == 31 &&
                pokemon.getIVs().getStat(StatsType.SpecialDefence) == 31 &&
                pokemon.getIVs().getStat(StatsType.SpecialAttack) == 31 &&
                pokemon.getIVs().getStat(StatsType.Defence) == 31 &&
                pokemon.getIVs().getStat(StatsType.HP) == 31)
        ) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.getIVs().setStat(StatsType.Speed, 31);
                pokemon.getIVs().setStat(StatsType.Attack, 31);
                pokemon.getIVs().setStat(StatsType.SpecialDefence, 31);
                pokemon.getIVs().setStat(StatsType.SpecialAttack, 31);
                pokemon.getIVs().setStat(StatsType.Defence, 31);
                pokemon.getIVs().setStat(StatsType.HP, 31);
                afterBindPokemon(player, pokemon, cardName);
                afterUnBreedPokemon(player, pokemon, cardName);

            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }


    private void clearEvsModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (pokemonHaveV(pokemon)
        ) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.getEVs().setStat(StatsType.Speed, 0);
                pokemon.getEVs().setStat(StatsType.Attack, 0);
                pokemon.getEVs().setStat(StatsType.SpecialDefence, 0);
                pokemon.getEVs().setStat(StatsType.SpecialAttack, 0);
                pokemon.getEVs().setStat(StatsType.Defence, 0);
                pokemon.getEVs().setStat(StatsType.HP, 0);
                afterBindPokemon(player, pokemon, cardName);
                afterUnBreedPokemon(player, pokemon, cardName);

            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private static boolean pokemonHaveV(Pokemon pokemon) {
        return pokemon.getEVs().getStat(StatsType.Speed) != 0 ||
                pokemon.getEVs().getStat(StatsType.Attack) != 0 ||
                pokemon.getEVs().getStat(StatsType.SpecialDefence) != 0 ||
                pokemon.getEVs().getStat(StatsType.SpecialAttack) != 0 ||
                pokemon.getEVs().getStat(StatsType.Defence) != 0 ||
                pokemon.getEVs().getStat(StatsType.HP) != 0;
    }

    private void maxLevelModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (pokemon.getLevel() != 100) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.setLevel(100);
                afterBindPokemon(player, pokemon, cardName);
                afterUnBreedPokemon(player, pokemon, cardName);

            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void shinyModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (!pokemon.isShiny()) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.setShiny(true);
                afterBindPokemon(player, pokemon, cardName);
                afterUnBreedPokemon(player, pokemon, cardName);

            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void mtModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (!YmlUtil.blackListPokemon(player, pokemon, cardName)) {
            player.closeInventory();
            return;
        }
        AbilityBase ability = pokemon.getAbility();
        if (pokemon.getAbilitySlot() != 2) {
            pokemon.setAbilitySlot(2);
            if (pokemon.getAbilitySlot() == 2 || pokemon.getLocalizedName().equals("基格尔德")) {
                pokemon.setAbility(ability);
                if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                    pokemon.setAbilitySlot(2);
                    afterBindPokemon(player, pokemon, cardName);
                    afterUnBreedPokemon(player, pokemon, cardName);

                }
            } else {
                YmlUtil.sendColorEventWarn(player, "NoMt", pokemon);
            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }

    private void hatchModify(Player player, Pokemon pokemon, String cardName, ItemStack newItemStack) {
        if (pokemon.getEggCycles() > 0) {
            if (ItemUtil.takeItem(player, newItemStack, cardName, pokemon, null)) {
                pokemon.setEggCycles(-1);
                afterBindPokemon(player, pokemon, cardName);
                afterUnBreedPokemon(player, pokemon, cardName);

            }
        } else {
            YmlUtil.sendColorWarn(player, cardName, pokemon);
        }
        player.closeInventory();
    }
//    private static void dragonGuiListener(InventoryClickEvent event, int slot, Player player) {
//        event.setCancelled(true);
//        if (slot > 53 || slot < 0) {
//            return;
//        }
//        if (event.getInventory().getItem(slot) == null) {
//            return;
//        }
//        if (!event.getInventory().getItem(slot).hasItemMeta()) {
//            return;
//        }
//        boolean flag = false;
//        ItemStack item = event.getInventory().getItem(slot);
//        String displayName = event.getInventory().getItem(slot).getItemMeta().getDisplayName();
//        for (ItemStack itemStack : player.getInventory()) {
//            if (itemStack == null) {
//                continue;
//            }
//            if (!itemStack.hasItemMeta()) {
//                continue;
//            }
//            if (itemStack.getItemMeta().getLore().get(0).equals(item.getItemMeta().getLore().get(0))) {
//                GuiMain.Gui(player, displayName);
//                flag = true;
//            }
//        }
//        if (!flag) {
//            YmlUtil.sendColorNoItem(player);
//            player.closeInventory();
//        }
//    }
}
