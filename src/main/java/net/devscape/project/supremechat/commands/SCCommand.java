package net.devscape.project.supremechat.commands;

import net.devscape.project.supremechat.SupremeChat;
import net.devscape.project.supremechat.utils.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.devscape.project.supremechat.utils.Message.PREFIX;
import static net.devscape.project.supremechat.utils.Message.msgPlayer;

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
                        FormatUtil.sendHelp(player);
                    } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            SupremeChat.getInstance().reload();
                            msgPlayer(player, PREFIX + " &7Reloaded config files.");
                        } else if (args[0].equalsIgnoreCase("mutechat")) {
                            if (SupremeChat.getInstance().getConfig().getBoolean("mute-chat")) {
                                SupremeChat.getInstance().getConfig().set("mute-chat", false);
                                SupremeChat.getInstance().saveConfig();

                                SupremeChat.getInstance().reload();
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    msgPlayer(all, "&c[CHAT] Chat no longer muted!");
                                }
                            } else {
                                SupremeChat.getInstance().getConfig().set("mute-chat", true);
                                SupremeChat.getInstance().saveConfig();

                                SupremeChat.getInstance().reload();
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    msgPlayer(all, "&c[CHAT] Chat is now muted!");
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
