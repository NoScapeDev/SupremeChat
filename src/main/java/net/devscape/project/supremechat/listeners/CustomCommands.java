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
        for (String commands : SupremeChat.getInstance().getConfig().getConfigurationSection("custom-commands").getKeys(false)) {
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
