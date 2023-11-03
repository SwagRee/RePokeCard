package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.items.ItemPokeball;
import io.github.swagree.pokecard.util.ItemUtil;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.*;

public class GuiModifyPokeBall implements Listener {
    private static final String TITLE_PREFIX = "§b选择一项球种修改吧";
    private static final String cardName = "pokeBall";

    private static final Map<UUID, Integer> playerSlotMap = new HashMap<>();



    public static void Gui(Player player, int slot) {

        Inventory inv = Bukkit.createInventory(null, 45, TITLE_PREFIX);

        final ArrayList<ItemStack> itemStacks = new ArrayList<>();

        Class<?> enumClass = EnumPokeballs.class;
        List<String> list = new ArrayList<>();
        if (enumClass.isEnum()) {
            Field[] fields = enumClass.getFields(); // 获取枚举类的所有字段

            for (Field field : fields) {
                if (field.isEnumConstant()) {
                    try {
                        Object value = field.get(null); // 获取枚举常量的值
                        list.add(EnumPokeballs.valueOf(value.toString()).getLocalizedName());
                    } catch (IllegalAccessException ee) {
                        ee.printStackTrace();
                    }
                }
            }
        }
        HashMap<String, Integer> attributeLoreMap = new LinkedHashMap<>();
        for(int i = 0 ; i<list.size();i++){
            attributeLoreMap.put(list.get(i), i);
        }


        for (Map.Entry<String, Integer> entry : attributeLoreMap.entrySet()) {
            System.out.println(entry.getKey());
            ItemPokeball item = EnumPokeballs.getFromIndex(entry.getValue()).getItem();
            net.minecraft.item.ItemStack itemStack2 = new net.minecraft.item.ItemStack(item);

            ItemStack itemStack1 = CraftItemStack.asBukkitCopy( (net.minecraft.server.v1_12_R1.ItemStack)(Object)itemStack2);
            ItemStack itemStack = new ItemStack(itemStack1);

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§e修改球种为" + entry.getKey());

            ArrayList<String> lores = new ArrayList<>();
            lores.add("修改球种为"+entry.getKey());
            itemMeta.setLore(lores);

            itemStack.setItemMeta(itemMeta);
            itemStacks.add(itemStack);
        }

        for (int i = 0; i < itemStacks.size(); i++) {
            if (i < inv.getSize()) {
                inv.setItem(i, itemStacks.get(i));
            }
        }

        player.openInventory(inv);
        playerSlotMap.put(player.getUniqueId(), slot); // 保存玩家与slot的对应关系

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase(TITLE_PREFIX)) {
            Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
            event.setCancelled(true);
            player.closeInventory();


            Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(playerSlotMap.get(player.getUniqueId()));
            int slot = event.getSlot();

            if (pokemon.getCaughtBall().getLocalizedName() != EnumPokeballs.getFromIndex(slot).getLocalizedName()) {
                ItemStack newItemStack = ItemUtil.createItem(cardName);
                ItemUtil.takeItem(player,newItemStack,cardName,pokemon,EnumPokeballs.getFromIndex(slot).getLocalizedName());
                pokemon.setCaughtBall(EnumPokeballs.getFromIndex(slot));
            } else {
                YmlUtil.sendColorWarn(player,cardName,pokemon,pokemon.getCaughtBall().getLocalizedName());
            }
        }

    }




}