package net.devscape.project.supremechat.commands;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.devscape.project.supremechat.utilites.FormattingUtils.isChatMuted;
import static net.devscape.project.supremechat.utilites.FormattingUtils.sendHelp;
import static net.devscape.project.supremechat.utilites.Message.PREFIX;
import static net.devscape.project.supremechat.utilites.Message.msgPlayer;

public class SCCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {

            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("supremechat")) {
                if (player.hasPermission("supremechat.admin")) {
                    if (args.length == 0) {
                        sendHelp(player);
                    } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            SupremeChat.getInstance().reload();
                            msgPlayer(player, PREFIX + " &7Reloaded config files.");
                        } else if (args[0].equalsIgnoreCase("mutechat")) {
                            if (isChatMuted()) {
                                SupremeChat.getInstance().getConfig().set("mute-chat", false);
                                SupremeChat.getInstance().reload();
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    msgPlayer(player, "&c[CHAT] Chat no longer muted!");
                                }
                            } else {
                                SupremeChat.getInstance().getConfig().set("mute-chat", true);
                                SupremeChat.getInstance().reload();
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    msgPlayer(player, "&c[CHAT] Chat is now muted!");
                                }
                            }
                        }
                    }
                } else {
                    msgPlayer(player, "&cNo Permission!");
                }
            }
        }
        return false;
    }
}
