package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static net.devscape.project.supremechat.utils.Message.msgPlayer;

public class CommandFilter implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) return;

        // command spam detect
        if (!player.hasPermission("sc.bypass")) {
            if (SupremeChat.getInstance().getConfig().getInt("command-delay") >= 1) {
                if (!SupremeChat.getInstance().getCommandDelayList().contains(player)) {
                    SupremeChat.getInstance().getCommandDelayList().add(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            SupremeChat.getInstance().getCommandDelayList().remove(player);
                        }
                    }.runTaskLater(SupremeChat.getInstance(), 20L * SupremeChat.getInstance().getConfig().getInt("command-delay"));
                } else {
                    e.setCancelled(true);
                    msgPlayer(player, SupremeChat.getInstance().getConfig().getString("command-warn"));
                }
            }
        }
    }
}
