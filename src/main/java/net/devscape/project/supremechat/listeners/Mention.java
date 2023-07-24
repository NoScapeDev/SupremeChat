package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

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

        if (target == null) return;
        if (target.getName().equalsIgnoreCase(event.getPlayer().getName())) return;

        String replacement = SupremeChat.getInstance().getConfig().getString("mention-replacement");
        String sound = SupremeChat.getInstance().getConfig().getString("mention-sound.sound");

        if (replacement != null) {
            if (SupremeChat.getInstance().getConfig().getBoolean("mention-spaces")) {
                target.sendMessage("");

                replacement = replacement.replaceAll("%target%", target.getName());

                // send formatted message
                event.setMessage(event.getMessage().replaceAll(target.getName(), format(replacement)));

                Player finalTarget = target;
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        finalTarget.sendMessage("");
                    }
                }.runTaskLater(SupremeChat.getInstance(), 20L);

            } else {
                replacement = replacement.replaceAll("%target%", target.getName());

                // send formatted message
                event.setMessage(event.getMessage().replaceAll(target.getName(), format(replacement)));
            }
            if (SupremeChat.getInstance().getConfig().getBoolean("mention-sound.enable")) {
                target.playSound(target.getLocation(), Sound.valueOf(sound), 1, 1);
            }
        }
    }
}