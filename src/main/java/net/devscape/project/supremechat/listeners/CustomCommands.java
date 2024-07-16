package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static net.devscape.project.supremechat.utils.Message.msgPlayer;

public class CustomCommands implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String cmd = e.getMessage();

        if (e.isCancelled()) return;

        boolean anti_bot = SupremeChat.getInstance().getConfig().getBoolean("anti-bot.commands");

        if (anti_bot) {
            if (SupremeChat.getInstance().getPrevention().contains(player)) {
                e.setCancelled(true);
                String detect_alert = SupremeChat.getInstance().getConfig().getString("anti-bot.message");
                detect_alert = detect_alert.replaceAll("%name%", player.getName());

                msgPlayer(player, detect_alert);
                return;
            }
        }

        if (SupremeChat.getInstance().getConfig().getConfigurationSection("custom-commands") != null) {
            for (String commands : SupremeChat.getInstance().getConfig().getConfigurationSection("custom-commands").getKeys(false)) {
                if (commands != null) {
                    String str = SupremeChat.getInstance().getConfig().getString("custom-commands." + commands + ".string");
                    if (str != null) {
                        if (cmd.equalsIgnoreCase(commands)) {
                            e.setCancelled(true);
                            msgPlayer(player, str);
                        }
                    }
                }
            }
        }
    }
}