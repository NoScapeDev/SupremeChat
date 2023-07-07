package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.devscape.project.supremechat.utils.Message.*;

public class JoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        String join = getJoin();
        join = addOtherPlaceholers(join, player);

        for (String motd : SupremeChat.getInstance().getConfig().getStringList("motd")) {
            motd = addOtherPlaceholers(motd, player);
            msgPlayer(player, motd);
        }

        if (!SupremeChat.getInstance().getConfig().getString("custom-join").isEmpty()) {
            e.setJoinMessage(format(join));
        } else {
            e.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        String leave = getLeave();
        leave = addOtherPlaceholers(leave, player);

        if (!SupremeChat.getInstance().getConfig().getString("custom-leave").isEmpty()) {
            e.setQuitMessage(format(leave));
        } else {
            e.setQuitMessage(null);
        }

        SupremeChat.getInstance().getLastMessage().remove(player);
        SupremeChat.getInstance().getChatDelayList().remove(player);
        SupremeChat.getInstance().getCommandDelayList().remove(player);
    }
}
