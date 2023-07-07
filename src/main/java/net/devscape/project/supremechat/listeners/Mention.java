package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.devscape.project.supremechat.utils.Message.format;

public class Mention implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onMention(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        if (event.getMessage().startsWith("/")) return;

        String message = event.getMessage();

        Player target = null;

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (message.contains(all.getName())) {
                target = all.getPlayer();
                break;
            }
        }

        String replacement = SupremeChat.getInstance().getConfig().getString("mention-replacement");
        String sound = SupremeChat.getInstance().getConfig().getString("mention-sound.sound");

        if (replacement != null) {
            if (SupremeChat.getInstance().getConfig().getBoolean("mention-spaces")) {
                if (target != null) {
                    target.sendMessage("");

                    replacement = replacement.replaceAll("%targer%", target.getName());

                    // send formatted message
                    event.setMessage(event.getMessage().replaceAll(target.getName(), format(replacement)));
                }

                if (target != null) {
                    target.sendMessage("");

                    if (SupremeChat.getInstance().getConfig().getBoolean("mention-sound.enable")) {
                        target.playSound(target.getLocation(), Sound.valueOf(sound), 1, 1);
                    }
                }
            } else {
                if (target != null) {
                    replacement = replacement.replaceAll("%targer%", target.getName());

                    // send formatted message
                    event.setMessage(event.getMessage().replaceAll(target.getName(), format(replacement)));
                    if (SupremeChat.getInstance().getConfig().getBoolean("mention-sound.enable")) {
                        target.playSound(target.getLocation(), Sound.valueOf(sound), 1, 1);
                    }
                }
            }
        }
    }
}