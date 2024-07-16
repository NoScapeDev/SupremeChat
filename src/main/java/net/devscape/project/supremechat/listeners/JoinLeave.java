package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.devscape.project.supremechat.utils.Message.*;

public class JoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        String join = getJoin();
        join = addOtherPlaceholders(join, player);

        if (SupremeChat.getInstance().getConfig().getBoolean("enable-join-motd")) {
            for (String motd : SupremeChat.getInstance().getConfig().getStringList("motd")) {
                motd = addOtherPlaceholders(motd, player);
                msgPlayer(player, motd);
            }
        }

        if (!SupremeChat.getInstance().getConfig().getString("custom-join").isEmpty()) {
            e.setJoinMessage(format(join));
        } else {
            e.setJoinMessage(null);
        }

        SupremeChat.getInstance().getPrevention().add(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        String leave = getLeave();
        leave = addOtherPlaceholders(leave, player);

        if (!SupremeChat.getInstance().getConfig().getString("custom-leave").isEmpty()) {
            e.setQuitMessage(format(leave));
        } else {
            e.setQuitMessage(null);
        }

        SupremeChat.getInstance().getLastMessage().remove(player);
        SupremeChat.getInstance().getChatDelayList().remove(player);
        SupremeChat.getInstance().getCommandDelayList().remove(player);
        SupremeChat.getInstance().getPrevention().remove(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        boolean anti_bot = SupremeChat.getInstance().getConfig().getBoolean("anti-bot.commands") || SupremeChat.getInstance().getConfig().getBoolean("anti-bot.chat");

        if (anti_bot) {
            SupremeChat.getInstance().getPrevention().remove(e.getPlayer());
        }
    }
}