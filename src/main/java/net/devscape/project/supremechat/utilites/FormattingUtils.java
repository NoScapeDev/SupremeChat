package net.devscape.project.supremechat.utilites;

import net.devscape.project.supremechat.SupremeChat;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.devscape.project.supremechat.utilites.Message.*;

public class FormattingUtils {

    // ==================================================
    // CHAT FORMAT
    // ==================================================

    public static void formatChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) return;
        if (isChatMuted()) e.setCancelled(player.hasPermission(SupremeChat.getInstance().getConfig().getString("bypass-mute-chat-permission")));

        if (SupremeChat.getInstance().getConfig().getBoolean("enable-chat-format")) {

            String rank = getRank(player);
            boolean grouping = SupremeChat.getInstance().getConfig().getBoolean("group-formatting");

            String chat;

            if (grouping) {
                chat = getRankFormat(rank);
            } else {
                chat = getGlobalFormat();
            }

            chat = addChatPlaceholers(chat, player, e.getMessage());

            String permission = SupremeChat.getInstance().getConfig().getString("chat-color-permission");
            boolean hover = SupremeChat.getInstance().getConfig().getBoolean("hover.enable");
            boolean click = SupremeChat.getInstance().getConfig().getBoolean("click.enable");

            assert permission != null;
            e.setCancelled(true);

            List<String> hover_m = new ArrayList<>();

            for (String hover_message : SupremeChat.getInstance().getConfig().getStringList("hover.string")) {
                hover_message = addOtherPlaceholers(hover_message, player);
                hover_m.add(hover_message);
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                TextComponent message = new TextComponent(TextComponent.fromLegacyText(format(chat)));
                if (hover) {
                    setHoverBroadcastEvent(message, color(hover_m), player);
                }

                if (click) {
                    setClickBroadcastEvent(message, SupremeChat.getInstance().getConfig().getString("click.string"), player);
                }

                all.spigot().sendMessage(message);
            }
        }
    }

    // ==================================================
    // CHAT FILTER
    // ==================================================

    public static void messageFilter(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        // BANNED WORD DETECTION
        if (player.hasPermission("sc.bypass")) {

            for (String word : SupremeChat.getInstance().getConfig().getStringList("banned-words")) {
                if (isWordBlocked(e.getMessage(), word)) {
                    e.setCancelled(true);
                    String detect = SupremeChat.getInstance().getConfig().getString("word-detect");
                    detect = detect.replaceAll("%word%", word);

                    msgPlayer(player, detect);

                    // alert staff
                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission(SupremeChat.getInstance().getConfig().getString("detect-alert-staff-permission"))) {
                            String detect_alert = SupremeChat.getInstance().getConfig().getString("word-detect-staff");
                            detect_alert = detect_alert.replaceAll("%message%", e.getMessage());
                            detect_alert = detect_alert.replaceAll("%name%", player.getName());

                            msgPlayer(staff, detect_alert);
                            break;
                        }
                    }
                }
            }

            // CHAT DELAY
            if (SupremeChat.getInstance().getConfig().getInt("chat-delay") >= 1) {
                if (!SupremeChat.getInstance().getChatDelayList().contains(player)) {
                    SupremeChat.getInstance().getChatDelayList().add(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            SupremeChat.getInstance().getChatDelayList().remove(player);
                        }
                    }.runTaskLaterAsynchronously(SupremeChat.getInstance(), 20L * SupremeChat.getInstance().getConfig().getInt("chat-delay"));
                } else {
                    e.setCancelled(true);
                    msgPlayer(player, SupremeChat.getInstance().getConfig().getString("chat-warn"));
                }
            }

            // REPEAT FILTER
            if (SupremeChat.getInstance().getConfig().getBoolean("repeat-enable")) {
                if (SupremeChat.getInstance().getLastMessage().containsKey(player)) {
                    String lastMessage = SupremeChat.getInstance().getLastMessage().get(player);
                    String newMessage = e.getMessage();

                    if (newMessage.contains(lastMessage)) {
                        e.setCancelled(true);
                        msgPlayer(player, SupremeChat.getInstance().getConfig().getString("repeat-warn"));
                    } else {
                        SupremeChat.getInstance().getLastMessage().remove(player);
                        SupremeChat.getInstance().getLastMessage().put(player, newMessage);
                    }
                } else {
                    String newMessage = e.getMessage();
                    SupremeChat.getInstance().getLastMessage().put(player, newMessage);
                }
            }

            // CAPS FILTER
            if (SupremeChat.getInstance().getConfig().getBoolean("caps-lowercase")) {
                if (e.getMessage().chars().filter(Character::isUpperCase).count() >= SupremeChat.getInstance().getConfig().getInt("caps-limit")) {
                    for (final char c : e.getMessage().toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            if (!SupremeChat.getInstance().getConfig().getBoolean("disable-caps-warn")) {
                                msgPlayer(player, SupremeChat.getInstance().getConfig().getString("caps-warn"));
                            }
                            e.setMessage(format(e.getMessage().toLowerCase()));
                            break;
                        }
                    }
                }
            }
        }
    }

    private static boolean isWordBlocked(String message, String blockedWord) {
        String pattern = "\\b" + blockedWord + "\\b";
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(message);

        return matcher.find();
    }

    public static void commandFilter(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) return;

        // command spam detect
        if (SupremeChat.getInstance().getConfig().getInt("command-delay") >= 1) {
            if (!SupremeChat.getInstance().getCommandDelayList().contains(player)) {
                SupremeChat.getInstance().getCommandDelayList().add(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        SupremeChat.getInstance().getCommandDelayList().remove(player);
                    }
                }.runTaskLaterAsynchronously(SupremeChat.getInstance(), 20L * SupremeChat.getInstance().getConfig().getInt("command-delay"));
            } else {
                e.setCancelled(true);
                msgPlayer(player, SupremeChat.getInstance().getConfig().getString("command-warn"));
            }
        }
    }

    // ==================================================
    // ITEM IN CHAT
    // ==================================================

    public static void onItem(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) return;
        if (isChatMuted())
            e.setCancelled(player.hasPermission(SupremeChat.getInstance().getConfig().getString("bypass-mute-chat-permission")));

        ItemStack i = player.getInventory().getItemInMainHand();
        boolean itemChat = SupremeChat.getInstance().getConfig().getBoolean("enable-chat-item");
        String replacement = SupremeChat.getInstance().getConfig().getString("chat-item-replace");
        assert replacement != null;

        if (i.getItemMeta() != null) {
            replacement = replacement.replaceAll("%item%", format("x" + i.getAmount() + " " + i.getItemMeta().getDisplayName()));
        } else {
            replacement = replacement.replaceAll("%item%", format("x" + i.getAmount() + " " + i.getType().name()));
        }

        String message = e.getMessage();

        if (itemChat) {
            for (String i_strings : SupremeChat.getInstance().getConfig().getStringList("chat-item-strings")) {
                if (message.contains(i_strings)) {
                    e.setMessage(message.replace(i_strings, format(replacement)));
                    break;
                }
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

        for (String motd : SupremeChat.getInstance().getConfig().getStringList("motd")) {
            motd = addOtherPlaceholers(motd, player);
            msgPlayer(player, motd);
        }

        if (!SupremeChat.getInstance().getConfig().getString("custom-join").isEmpty()) {
            e.setJoinMessage(format(join));
        } else {
            e.setJoinMessage(null);
        }
    }

    public static void formatLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        String leave = getLeave();
        leave = addOtherPlaceholers(leave, player);

        if (!SupremeChat.getInstance().getConfig().getString("custom-leave").isEmpty()) {
            e.setQuitMessage(format(leave));
        } else {
            e.setQuitMessage(null);
        }

        SupremeChat.getInstance().getLastMessage().remove(player);
        SupremeChat.getInstance().getChatDelayList().remove(player);
        SupremeChat.getInstance().getCommandDelayList().remove(player);
    }

    // ==================================================
    // CUSTOM COMMANDS
    // ==================================================

    public static void customCommands(PlayerCommandPreprocessEvent e) {
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

    // ==================================================
    // IS MUTED
    // ==================================================

    public static boolean isChatMuted() {
        return SupremeChat.getInstance().getConfig().getBoolean("mute-chat");
    }

    // ==================================================
    // GET RANK
    // ==================================================
    public static String getRank(Player player) {
        String group = SupremeChat.getPermissions().getPrimaryGroup(player);
        List<String> ranks = new ArrayList<>(Arrays.asList(group));

        return group;
    }

    // ==================================================
    // IS STAFF
    // ==================================================

    public static boolean isStaff(Player player) {
        String[] group = SupremeChat.getPermissions().getPlayerGroups(player);
        List<String> ranks = new ArrayList<>(Arrays.asList(group));

        List<String> staff = SupremeChat.getInstance().getConfig().getStringList("staff");

        for (String s : staff) {
            if (ranks.contains(s)) return true;
        }

        return false;
    }

    // ==================================================
    // IS DONOR
    // ==================================================

    public static boolean isDonor(Player player) {
        String[] group = SupremeChat.getPermissions().getPlayerGroups(player);
        List<String> ranks = new ArrayList<>(Arrays.asList(group));

        List<String> staff = SupremeChat.getInstance().getConfig().getStringList("donors");

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
                "&6/supremechat reload &7- reload the plugin config.",
                "&6/supremechat mutechat &7- mutes the chat for everyone.",
                "",
                "&#fdc269Author: &#fff2ccDevScape",
                "&#fdc269Plugin Version: &#fff2cc" + SupremeChat.getInstance().getDescription().getVersion(),
                "&#fdc269ScapeHelp Server: &#fff2ccfhttps://discord.gg/AnPwty8asP");
    }

}
