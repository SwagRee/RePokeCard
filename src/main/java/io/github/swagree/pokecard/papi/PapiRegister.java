package io.github.swagree.pokecard.papi;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * 有关变量的一些注册
 * 目前计划为：绑定 绝育 所属世代
 * 已完成绑定变量
 */
public class PapiRegister extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "SwagRee";
    }

    @Override
    public String getIdentifier() {
        return "repokemoncard";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }


    @Override
    public String onRequest(OfflinePlayer player, String params) {
        int slot = 0;
        if (params.startsWith("bind")) {
            slot = Integer.parseInt(params.substring(5));
            String playerName = player.getName();
            Player onlinePlayer = Bukkit.getPlayer(playerName);
            if (onlinePlayer != null) {
                String s = PokemonBind(onlinePlayer, slot);
                return s.equals("yes") ? "已绑定" : "未绑定";
            }
        }
        return null;
    }

    public String PokemonBind(Player player, int slot) {
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());
        if (party != null) {
            if (party.get(slot).hasSpecFlag("untradeable")) {
                return "yes";
            }
        }
        return "no";
    }

}