package net.devscape.project.scapechat.commands;

import net.devscape.project.scapechat.ScapeChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.devscape.project.scapechat.utilites.FormattingUtils.isChatMuted;
import static net.devscape.project.scapechat.utilites.FormattingUtils.sendHelp;
import static net.devscape.project.scapechat.utilites.Message.PREFIX;
import static net.devscape.project.scapechat.utilites.Message.msgPlayer;

public class SCCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {

            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("scapechat")) {
                if (player.hasPermission("scapechat.command")) {
                    if (args.length == 0) {
                        sendHelp(player);
                    } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            ScapeChat.getInstance().reload();
                            msgPlayer(player, PREFIX + " &7Reloaded config files.");
                        } else if (args[0].equalsIgnoreCase("mutechat")) {
                            if (isChatMuted()) {
                                ScapeChat.getInstance().getConfig().set("mute-chat", false);
                                ScapeChat.getInstance().reload();
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    msgPlayer(player, "&c[CHAT] Chat no longer muted!");
                                }
                            } else {
                                ScapeChat.getInstance().getConfig().set("mute-chat", true);
                                ScapeChat.getInstance().reload();
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
