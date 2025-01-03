package io.github.swagree.pokecard.gui.guiHolder;


import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import io.github.swagree.pokecard.event.DetailList.EventGuiDetailMain;
import io.github.swagree.pokecard.event.EventGuiMain;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeedNextHolder implements InventoryHolder {
    public Inventory inv;
    public Player player;
    public String cardName;
    public Map<Integer, Integer> mapSlotToField;
    public Integer page = 1;

    public NeedNextHolder(Player player, String cardName) {

        this.player = player;
        this.cardName = cardName;
        setupInv();

    }

    public NeedNextHolder(Player player, String cardName, Integer page) {
        this.player = player;
        this.cardName = cardName;
        this.page = page;
        setupInv();
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void setupInv() {


        if (cardName.equals("move")) {
            ifMoveToSetGui();
            return;
        }

        Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);

        if(cardName.startsWith("form")){
            ifMoveToSetGui(pokemon);
            return;
        }



        String title = YmlUtil.guiFileFileList.getString("Gui.needNextHolder." + cardName + ".title").replace("&", "§");

        List<String> guiSlots = YmlUtil.guiFileFileList.getStringList("Gui.needNextHolder." + cardName + ".slot");

        int guiSize = guiSlots.size() * 9;
        this.inv = Bukkit.createInventory(this, guiSize, title);

        mapSlotToField = new HashMap<>();

        int countItem = 1;
        for (int i = 0; i < guiSlots.size(); i++) {
            byte[] bytes = guiSlots.get(i).getBytes();
            for (int i1 = 0; i1 < bytes.length; i1++) {
                char byteValue = (char) bytes[i1];

                // 跳过空字符或空白字符
                if (Character.isWhitespace(byteValue)) {
                    continue;
                }

                // 遍历 guiSize，以便处理数字和字符
                for (int j = 1; j <= guiSize; j++) {
                    // 支持的字符包括数字和字母
                    char expectedChar = Character.forDigit(j, 10);
                    if (expectedChar == '\0') {
                        expectedChar = (char) ('A' + j - 10); // 从 A 开始
                    }

                    if (byteValue == expectedChar) {
                        int invSlot = (i) * 9 + i1;
                        String data = YmlUtil.guiFileFileList.getString("Gui.needNextHolder." + cardName + ".item." + byteValue + ".data");
                        // 如果未找到数据，则跳过该循环
                        if (data.isEmpty()) {
                            continue;
                        }
                        ItemStack itemStack = null;
                        try {
                            Integer dataToNum = Integer.valueOf(data);
                            itemStack = new ItemStack(Material.getMaterial(dataToNum));
                        } catch (Exception e) {
                            itemStack = new ItemStack(Material.valueOf(data.toUpperCase()));
                        }

                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(
                                YmlUtil.guiFileFileList.getString("Gui.needNextHolder." +
                                        cardName + ".item." + byteValue + ".name").replace("&", "§")
                        );

                        List<String> lores = YmlUtil.guiFileFileList.getStringList("Gui.needNextHolder." +
                                cardName + ".item." + byteValue + ".lore");
                        List<String> toColorLores = new ArrayList<>();
                        for (String lore : lores) {
                            toColorLores.add(lore.replace("&", "§"));
                        }

                        itemMeta.setLore(toColorLores);

                        itemStack.setItemMeta(itemMeta);
                        inv.setItem(invSlot, itemStack);
                        mapSlotToField.put(invSlot, countItem);
                        countItem++;
                    }
                }
            }
        }

    }

    private void ifMoveToSetGui(Pokemon pokemon) {
        if(cardName.equals("form")){
            ifFromToSetGui();
            return;
        }
        if (cardName.equals("formDetail")) {
            ifFromToSetGui();
            return;
        }

        if (cardName.equals("formCommon") && !pokemon.isLegendary()) {
            ifFromToSetGui();
        }
        else if (cardName.equals("formLegendary") && pokemon.isLegendary()) {
            ifFromToSetGui();
        }else{
            player.closeInventory();
            YmlUtil.sendColorEventWarn(player, "NoMatchForm", pokemon);
        }
    }

    private void ifFromToSetGui() {
        String title = YmlUtil.guiFileFileList.getString("Gui.needNextHolder." + cardName + ".title").replace("&", "§");
        int guiSize = 54;
        this.inv = Bukkit.createInventory(this, guiSize, title);

        Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);
        List<IEnumForm> possibleForms = pokemon.getSpecies().getPossibleForms(true);
        if (!YmlUtil.blackListPokemon(player, pokemon, cardName)) {
            player.closeInventory();
            return;
        }
        for (int i = 0; i < possibleForms.size(); i++) {
            Pokemon pokemon1 = Pixelmon.pokemonFactory.create(pokemon.getSpecies());
            pokemon1.setForm(possibleForms.get(i));
            net.minecraft.item.ItemStack nmeitem = ItemPixelmonSprite.getPhoto(pokemon1);
            ItemStack poke = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_12_R1.ItemStack) (Object) nmeitem);

            ItemMeta itemMeta = poke.getItemMeta();
            String name = YmlUtil.guiFileFileList.getString("Gui.needNextHolder." + cardName + ".name").replace("&", "§").replace("{form}", (possibleForms.get(i)).getLocalizedName());
            itemMeta.setDisplayName(name);
            List<String> lores = YmlUtil.guiFileFileList.getStringList("Gui.needNextHolder." + cardName + ".lore");
            List<String> colorLores = new ArrayList<>();
            for (String lore : lores) {
                colorLores.add(lore.replace("&", "§").replace("{form}", (possibleForms.get(i)).getLocalizedName()));
            }
            itemMeta.setLore(colorLores);
            poke.setItemMeta(itemMeta);
            inv.setItem(i, poke);
        }
    }

    private void ifMoveToSetGui() {
        Pokemon pokemon = EventGuiMain.mapPlayerToPokemon.get(player);

        List<Attack> allMoves = pokemon.getBaseStats().getAllMoves();
        String title = YmlUtil.guiFileFileList.getString("Gui.needNextHolder." + cardName + ".title").replace("&", "§")
                .replace("{page}", String.valueOf(this.page));

        inv = Bukkit.createInventory(this, 54, title);
        int invSlotSize = Math.min(allMoves.size(), 45);
        for (int j = 0; j < invSlotSize; j++) {

            ItemStack itemStack = new ItemStack(Material.BOOK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            String name = YmlUtil.guiFileFileList.getString("Gui.needNextHolder." + cardName + ".name").replace("&", "§").replace("{move}", allMoves.get(j).getActualMove().getLocalizedName());
            itemMeta.setDisplayName(name);
            List<String> lores = YmlUtil.guiFileFileList.getStringList("Gui.needNextHolder." + cardName + ".lore");
            List<String> colorLores = new ArrayList<>();
            for (String lore : lores) {
                colorLores.add(lore.replace("&", "§").replace("{move}",allMoves.get(j).getActualMove().getLocalizedName()));
            }
            itemMeta.setLore(colorLores);
            itemStack.setItemMeta(itemMeta);
            inv.setItem(j, itemStack);
        }
        if (allMoves.size() > 45) {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.AQUA + "下一页");
            itemStack.setItemMeta(itemMeta);
            inv.setItem(53, itemStack);
        }
    }


}
