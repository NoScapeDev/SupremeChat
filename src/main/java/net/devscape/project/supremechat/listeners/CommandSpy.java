package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static net.devscape.project.supremechat.utils.Message.format;
import static net.devscape.project.supremechat.utils.Message.msgPlayer;

public class CommandSpy implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) return;

        Player player = e.getPlayer();

        if (SupremeChat.getInstance().getConfig().getBoolean("enable-command-spy")) {
            boolean isWhitelisted = false;
            for (String str : SupremeChat.getInstance().getConfig().getStringList("whitelist-spy-commands")) {
                if (e.getMessage().equalsIgnoreCase(str)) {
                    isWhitelisted = true;
                    break;
                }
            }

            if (!isWhitelisted) {
                String alert = SupremeChat.getInstance().getConfig().getString("cs-spy");
                alert = alert.replaceAll("%command%", e.getMessage());
                alert = alert.replaceAll("%name%", player.getName());

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("supremetags.commandspy.alert")) {
                        if (!staff.getName().equalsIgnoreCase(player.getName())) {
                            msgPlayer(staff, format(alert));
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }
}