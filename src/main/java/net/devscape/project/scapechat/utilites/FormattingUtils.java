package net.devscape.project.scapechat.utilites;

import net.devscape.project.scapechat.ScapeChat;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.devscape.project.scapechat.utilites.Message.*;

public class FormattingUtils {

    // ==================================================
    // CHAT FORMAT
    // ==================================================

    public static void formatChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) return;
        if (isChatMuted())
            e.setCancelled(player.hasPermission(ScapeChat.getInstance().getConfig().getString("bypass-mute-chat-permission")));

        String rank = getRank(player);
        boolean grouping = ScapeChat.getInstance().getConfig().getBoolean("group-formatting");

        String chat;

        if (grouping) {
            chat = getRankFormat(rank);
        } else {
            chat = getGlobalFormat();
        }

        chat = addChatPlaceholers(chat, player, e.getMessage());

        String permission = ScapeChat.getInstance().getConfig().getString("chat-color-permission");
        boolean hover = ScapeChat.getInstance().getConfig().getBoolean("hover.enable");
        boolean click = ScapeChat.getInstance().getConfig().getBoolean("click.enable");

        assert permission != null;
        e.setCancelled(true);

        List<String> hover_m = new ArrayList<>();

        for (String hover_message : ScapeChat.getInstance().getConfig().getStringList("hover.string")) {
            hover_message = addOtherPlaceholers(hover_message, player);
            hover_m.add(hover_message);
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            TextComponent message = new TextComponent(TextComponent.fromLegacyText(format(chat)));
            if (hover) {
                setHoverBroadcastEvent(message, color(hover_m), player);
            }

            if (click) {
                setClickBroadcastEvent(message, ScapeChat.getInstance().getConfig().getString("click.string"), player);
            }

            all.spigot().sendMessage(message);
        }
    }

    // ==================================================
    // CHAT FILTER
    // ==================================================

    public static void messageFilter(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        // BANNED WORD DETECTION
        for (String word : ScapeChat.getInstance().getConfig().getStringList("banned-words")) {
            if (e.getMessage().contains(word)) {
                e.setCancelled(true);
                String detect = ScapeChat.getInstance().getConfig().getString("word-detect");
                detect = detect.replaceAll("%word%", word);

                msgPlayer(player, detect);

                // alert staff
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission(ScapeChat.getInstance().getConfig().getString("detect-alert-staff-permission"))) {
                        String detect_alert = ScapeChat.getInstance().getConfig().getString("word-detect-staff");
                        detect_alert = detect_alert.replaceAll("%message%", e.getMessage());
                        detect_alert = detect_alert.replaceAll("%name%", player.getName());

                        msgPlayer(staff, detect_alert);
                    }
                }
            }
        }

        // CHAT DELAY
        if (ScapeChat.getInstance().getConfig().getInt("chat-delay") >= 1) {
            if (!ScapeChat.getInstance().getChatDelayList().contains(player)) {
                ScapeChat.getInstance().getChatDelayList().add(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ScapeChat.getInstance().getChatDelayList().remove(player);
                    }
                }.runTaskLaterAsynchronously(ScapeChat.getInstance(), 20L * ScapeChat.getInstance().getConfig().getInt("chat-delay"));
            } else {
                e.setCancelled(true);
                msgPlayer(player, ScapeChat.getInstance().getConfig().getString("chat-warn"));
            }
        }

        // REPART FILTER
        if (ScapeChat.getInstance().getLastMessage().containsKey(player)) {
            String lastMessage = ScapeChat.getInstance().getLastMessage().get(player);
            String newMessage = e.getMessage();

            if (newMessage.equalsIgnoreCase(lastMessage)) {
                e.setCancelled(true);
                msgPlayer(player, ScapeChat.getInstance().getConfig().getString("repeat-warn"));
            } else {
                ScapeChat.getInstance().getLastMessage().remove(player);
                ScapeChat.getInstance().getLastMessage().put(player, newMessage);
            }
        } else {
            String newMessage = e.getMessage();
            ScapeChat.getInstance().getLastMessage().put(player, newMessage);
        }
    }

    public static void commandFilter(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) return;

        // command spam detect
        if (ScapeChat.getInstance().getConfig().getInt("command-delay") >= 1) {
            if (!ScapeChat.getInstance().getCommandDelayList().contains(player)) {
                ScapeChat.getInstance().getCommandDelayList().add(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ScapeChat.getInstance().getCommandDelayList().remove(player);
                    }
                }.runTaskLaterAsynchronously(ScapeChat.getInstance(), 20L * ScapeChat.getInstance().getConfig().getInt("command-delay"));
            } else {
                e.setCancelled(true);
                msgPlayer(player, ScapeChat.getInstance().getConfig().getString("command-warn"));
            }
        }
    }

    // ==================================================
    // JOIN & LEAVE
    // ==================================================

    public static void formatJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        String join = getJoin();
        join = addOtherPlaceholers(join, player);

        for (String motd : ScapeChat.getInstance().getConfig().getStringList("motd")) {
            motd = addOtherPlaceholers(motd, player);
            msgPlayer(player, motd);
        }

        if (!ScapeChat.getInstance().getConfig().getString("custom-join").isEmpty()) {
            e.setJoinMessage(format(join));
        } else {
            e.setJoinMessage(null);
        }
    }

    public static void formatLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        String leave = getLeave();
        leave = addOtherPlaceholers(leave, player);

        if (!ScapeChat.getInstance().getConfig().getString("custom-leave").isEmpty()) {
            e.setQuitMessage(format(leave));
        } else {
            e.setQuitMessage(null);
        }

        ScapeChat.getInstance().getLastMessage().remove(player);
        ScapeChat.getInstance().getChatDelayList().remove(player);
        ScapeChat.getInstance().getCommandDelayList().remove(player);
    }

    // ==================================================
    // CUSTOM COMMANDS
    // ==================================================

    public static void customCommands(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String cmd = e.getMessage();
        for (String commands : ScapeChat.getInstance().getConfig().getConfigurationSection("custom-commands").getKeys(false)) {
            String str = ScapeChat.getInstance().getConfig().getString("custom-commands." + commands + ".string");
            if (str != null) {
                if (cmd.equalsIgnoreCase(commands)) {
                    e.setCancelled(true);
                    msgPlayer(player, str);
                }
            }
        }
    }

    // ==================================================
    // IS MUTED
    // ==================================================

    public static boolean isChatMuted() {
        return ScapeChat.getInstance().getConfig().getBoolean("mute-chat");
    }

    // ==================================================
    // GET RANK
    // ==================================================
    public static String getRank(Player player) {
        String group = ScapeChat.getPermissions().getPrimaryGroup(player);
        List<String> ranks = new ArrayList<>(Arrays.asList(group));

        return group;
    }

    // ==================================================
    // IS STAFF
    // ==================================================

    public static boolean isStaff(Player player) {
        String[] group = ScapeChat.getPermissions().getPlayerGroups(player);
        List<String> ranks = new ArrayList<>(Arrays.asList(group));

        List<String> staff = ScapeChat.getInstance().getConfig().getStringList("staff");

        for (String s : staff) {
            if (ranks.contains(s)) return true;
        }

        return false;
    }

    // ==================================================
    // IS DONOR
    // ==================================================

    public static boolean isDonor(Player player) {
        String[] group = ScapeChat.getPermissions().getPlayerGroups(player);
        List<String> ranks = new ArrayList<>(Arrays.asList(group));

        List<String> staff = ScapeChat.getInstance().getConfig().getStringList("donors");

        for (String s : staff) {
            if (ranks.contains(s)) return true;
        }

        return false;
    }


    // ==================================================
    // SCAPECHAT HELP MESSAGE
    // ==================================================

    public static void sendHelp(Player player) {
        msgPlayer(player,
                PREFIX + " &7Help Message:",
                "",
                "&6/scapechat reload &7- reload the plugin config.",
                "&6/scapechat mutechat &7- mutes the chat for everyone.",
                "",
                "&#fdc269Author: &#fff2ccDevScape",
                "&#fdc269Plugin Version: &#fff2cc" + ScapeChat.getInstance().getDescription().getVersion(),
                "&#fdc269ScapeHelp Server: &#fff2ccfhttps://discord.gg/AnPwty8asP");
    }

}
