package io.github.swagree.pokecard.event;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.swagree.pokecard.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

/**
 * 一些事件，主要是为了拦截白灯pokemoninfo能对绑定精灵进行转换
 * 以及对pokemoninfo中的pokeegg命令进行拦截
 * 当然这个写在宝可梦卡里很抽象 之后会写在另一个插件里
 */
public class EventExtra implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command1 = event.getMessage().split(" ")[0].substring(1); // 获取玩家输入的指令

        List<String> eggCommand = Main.plugin.getConfig().getStringList("eggCommand");
        for(int i=0;i<eggCommand.size();i++){
            if (command1.equalsIgnoreCase(eggCommand.get(i)) && event.getMessage().contains("create")) { // 目标指令名
                for(int j = 1 ;j<=6 ; j++){
                    if(event.getMessage().contains(String.valueOf(j))){
                        PlayerPartyStorage party = Pixelmon.storageManager.getParty(event.getPlayer().getUniqueId());
                        if(party.get(j-1).hasSpecFlag("untradeable")){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("对不起 这只精灵已经绑定了！");
                        }
                    }
                }
            }
        }


    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClickPokemonInfo(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        String title = event.getView().getTitle();
        String guiTitle = Main.plugin.getConfig().getString("guiTitle");
        if (title.contains(guiTitle)) {
            List<Integer> invSlots = Main.plugin.getConfig().getIntegerList("invSlot");
            for (int i = 0; i < invSlots.size(); i++) {
                int invSlot = invSlots.get(i);
                int spriteSlot = getSpriteSlot(invSlot);

                if (spriteSlot == -1) {
                    continue;
                }

                if (slot == invSlot) {
                    if (!isTrade(player, spriteSlot)) {
                        event.setCancelled(true); // 取消事件
                        player.closeInventory();
                        player.sendMessage("您这只精灵绑定了，不能转换！");
                    }
                }

            }
        }
    }

    public boolean isTrade(Player player, int spriteSlot) {
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

        if (party.get(spriteSlot) == null) {
            return false;
        }

        if (party.get(spriteSlot).hasSpecFlag("untradeable")) {
            return false;
        }
        return true;
    }
    public int getSpriteSlot(int invSlot) {
        switch (invSlot) {
            case 10:
                return 0;
            case 13:
                return 1;
            case 16:
                return 2;
            case 28:
                return 3;
            case 31:
                return 4;
            case 34:
                return 5;
            default:
                return -1; // 表示无效的位置
        }
    }
}
