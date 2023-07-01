package net.devscape.project.supremechat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.devscape.project.supremechat.utilites.FormattingUtils.*;

public class Formatting implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        messageFilter(e);
        onItem(e);
        formatChat(e);
    }
}