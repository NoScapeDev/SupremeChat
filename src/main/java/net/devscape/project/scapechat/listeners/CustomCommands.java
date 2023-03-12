package net.devscape.project.scapechat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static net.devscape.project.scapechat.utilites.FormattingUtils.customCommands;

public class CustomCommands implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        customCommands(e);
    }

}
