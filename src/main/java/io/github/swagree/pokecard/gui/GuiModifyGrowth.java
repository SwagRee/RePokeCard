package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.Main;
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

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class GuiModifyGrowth implements Listener {
    private static final List<Integer> invSlot = Arrays.asList(2, 4, 6, 20, 22, 24, 38, 40, 42);
    private static final String TITLE_PREFIX = "§b选择一项体型修改吧";
    private static final String cardName = "anyGrowth";

    static HashMap<String, Integer> hashmapPlayerPokemon = new HashMap<>();



    public static void Gui(Player player, int slot) {

        Inventory inv = Bukkit.createInventory(null, 45, TITLE_PREFIX);

        final ArrayList<ItemStack> itemStacks = new ArrayList<>();

        // 属性名称和对应的lore值
        HashMap<String, String> attributeLoreMap = new LinkedHashMap<>();
        attributeLoreMap.put("袖珍", "§b修改为袖珍");
        attributeLoreMap.put("迷你", "§b修改为迷你");
        attributeLoreMap.put("侏儒", "§b修改为侏儒");
        attributeLoreMap.put("较小", "§b修改为较小");
        attributeLoreMap.put("普通", "§b修改为普通");
        attributeLoreMap.put("高大", "§b修改为高大");
        attributeLoreMap.put("巨人", "§b修改为巨人");
        attributeLoreMap.put("庞大", "§b修改为庞大");
        attributeLoreMap.put("巨大", "§b修改为巨大");


        for (Map.Entry<String, String> entry : attributeLoreMap.entrySet()) {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§e修改体型为" + entry.getKey());

            ArrayList<String> lores = new ArrayList<>();
            lores.add(entry.getValue());
            itemMeta.setLore(lores);

            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);
        }

        for (int i = 0; i < itemStacks.size(); i++) {
            if (i < inv.getSize()) {
                inv.setItem(invSlot.get(i), itemStacks.get(i));
            }
        }
        hashmapPlayerPokemon.put(player.getName(),slot);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase(TITLE_PREFIX)) {
            Player player = (Player) event.getWhoClicked();
            PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

            int slot = event.getSlot();
            event.setCancelled(true);

            ItemStack newItemStack = ItemUtil.createItem(cardName);

            HashMap<Integer, EnumGrowth> clickSlotToChangeEnum = new HashMap<>();
            clickSlotToChangeEnum.put(2,EnumGrowth.Microscopic);
            clickSlotToChangeEnum.put(4,EnumGrowth.Pygmy);
            clickSlotToChangeEnum.put(6,EnumGrowth.Runt);
            clickSlotToChangeEnum.put(20,EnumGrowth.Small);
            clickSlotToChangeEnum.put(22,EnumGrowth.Ordinary);
            clickSlotToChangeEnum.put(24,EnumGrowth.Huge);
            clickSlotToChangeEnum.put(38,EnumGrowth.Giant);
            clickSlotToChangeEnum.put(40,EnumGrowth.Enormous);
            clickSlotToChangeEnum.put(42,EnumGrowth.Ginormous);

            Pokemon pokemon = party.get(hashmapPlayerPokemon.get(player.getName()));
            for(Map.Entry<Integer, EnumGrowth> s : clickSlotToChangeEnum.entrySet()){
                if(slot==s.getKey()){
                    if(pokemon.getGrowth()==s.getValue()){
                        YmlUtil.sendColorWarn(player,cardName,pokemon,pokemon.getGrowth().getLocalizedName());
                        return;
                    }
                    pokemon.setGrowth(s.getValue());
                    ItemUtil.takeItem(player,newItemStack,cardName,pokemon,s.getValue().getLocalizedName());
                }
            }

            player.closeInventory();
        }

    }




}