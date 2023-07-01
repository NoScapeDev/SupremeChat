package net.devscape.project.supremechat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.devscape.project.supremechat.utilites.FormattingUtils.formatJoin;
import static net.devscape.project.supremechat.utilites.FormattingUtils.formatLeave;

public class JoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        formatJoin(e);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        formatLeave(e);
    }
}
