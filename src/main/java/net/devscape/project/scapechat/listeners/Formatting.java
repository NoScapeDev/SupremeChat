package net.devscape.project.scapechat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.devscape.project.scapechat.utilites.FormattingUtils.*;

public class Formatting implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        messageFilter(e);
        onItem(e);
        formatChat(e);
    }
}
