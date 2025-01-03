package io.github.swagree.pokecard.gui.guiHolder;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.swagree.pokecard.gui.GuiMain;
import io.github.swagree.pokecard.util.YmlUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlyPageHolder implements InventoryHolder {
    public Inventory inv;
    public Player player;
    public String cardName;
    public Map<Integer, Pokemon> mapSlotToPokemon ;

    public OnlyPageHolder(Player player, String cardName) {
        this.player = player;
        this.cardName = cardName;
        setupInv(player,cardName);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void setupInv(Player player,String cardName) {
        String title = YmlUtil.guiFileFileList.getString("Gui.onlyGui." + cardName + ".title").replace("&","§");
        List<String> guiSlots = YmlUtil.guiFileFileList.getStringList("Gui.onlyGui." + cardName + ".slot");

        int guiSize = guiSlots.size() * 9;
        this.inv = Bukkit.createInventory(this, guiSize, title);

        mapSlotToPokemon = new HashMap<>();

        for (int i = 0; i < guiSlots.size(); i++) {
            byte[] bytes = guiSlots.get(i).getBytes();
            for (int i1 = 0; i1 < bytes.length; i1++) {
                for(int j = 1;j<=6;j++){
                    if(bytes[i1] == Character.forDigit(j, 10)){
                        Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(j - 1);
                        int invSlot = (i) * 9 + i1;
                        GuiMain.SpriteInGui(inv, player, pokemon, invSlot);
                        mapSlotToPokemon.put(invSlot, pokemon);
                    }
                }
            }
        }

    }
}