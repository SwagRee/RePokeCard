package io.github.swagree.pokecard.gui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.pokecard.util.ItemUtil;
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

import java.lang.reflect.Field;
import java.util.*;

public class GuiModifyNature implements Listener {
    private static final List<Integer> invSlot = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24);
    private static final String TITLE_PREFIX = "§b选择一项性格修改吧";
    private static final String cardName = "anyNature";

    static HashMap<String, Integer> hashmapPlayerPokemon = new HashMap<>();


    public static void Gui(Player player, int slot) {

        Inventory inv = Bukkit.createInventory(null, 45, TITLE_PREFIX);

        final ArrayList<ItemStack> itemStacks = new ArrayList<>();

        // 属性名称和对应的lore值
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

        Class<?> enumClass = EnumNature.class;
        List<String> list = new ArrayList<>();
        if (enumClass.isEnum()) {
            Field[] fields = enumClass.getFields(); // 获取枚举类的所有字段

            for (Field field : fields) {
                if (field.isEnumConstant()) {
                    try {
                        Object value = field.get(null); // 获取枚举常量的值
                        list.add(EnumNature.valueOf(value.toString()).getLocalizedName());
                    } catch (IllegalAccessException ee) {
                        ee.printStackTrace();
                    }
                }
            }
        }
        HashMap<String, String> attributeLoreMap = new LinkedHashMap<>();
        for(int i = 0 ; i<list.size();i++){
            attributeLoreMap.put(list.get(i), "§b修改为"+list.get(i));
        }


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
            Pokemon pokemon = party.get(hashmapPlayerPokemon.get(event.getWhoClicked().getName()));
            int slot = event.getSlot();
            event.setCancelled(true);

            ItemStack newItemStack = ItemUtil.createItem(cardName);

            Class<?> enumClass = EnumNature.class;
            List<String> list = new ArrayList<>();
            if (enumClass.isEnum()) {
                Field[] fields = enumClass.getFields(); // 获取枚举类的所有字段

                for (Field field : fields) {
                    if (field.isEnumConstant()) {
                        try {
                            Object value = field.get(null); // 获取枚举常量的值
                            list.add(value.toString());
                        } catch (IllegalAccessException ee) {
                            ee.printStackTrace();
                        }
                    }
                }
                String pokemonNature = pokemon.getNature().getLocalizedName();
                for(int i=0;i<list.size();i++){
                    if(slot==i){
                        if(pokemon.getNature()==EnumNature.getNatureFromIndex(i)){
                            YmlUtil.sendColorWarn(player,cardName,pokemon,pokemonNature);
                            return;
                        }
                        ItemUtil.takeItem(player,newItemStack,cardName,pokemon,EnumNature.getNatureFromIndex(i).getLocalizedName());
                        pokemon.setNature(EnumNature.getNatureFromIndex(i));

                    }
                }
            }
            player.closeInventory();

        }

    }




}