package net.devscape.project.supremechat.listeners;

import net.devscape.project.supremechat.SupremeChat;
import net.devscape.project.supremechat.utils.FormatUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.devscape.project.supremechat.utils.Message.*;

public class Formatting implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) e.setCancelled(true);

        if (SupremeChat.getInstance().getConfig().getBoolean("mute-chat")) {
            if (!player.hasPermission(Objects.requireNonNull(SupremeChat.getInstance().getConfig().getString("bypass-mute-chat-permission")))) {
                msgPlayer(player, "&cChat is currently muted!");
                e.setCancelled(true);
                return;
            }
        }

        // BANNED WORD DETECTION
        if (SupremeChat.getInstance().getConfig().getBoolean("word-detect-enable")) {
            if (!player.hasPermission("sc.bypass")) {
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

        // ITEM IN CHAT
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean itemChat = SupremeChat.getInstance().getConfig().getBoolean("enable-chat-item");
        String replacement = SupremeChat.getInstance().getConfig().getString("chat-item-replace");
        assert replacement != null;

        if (item.getItemMeta() != null) {
            String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name();
            replacement = replacement.replaceAll("%item%", format("x" + item.getAmount() + " " + displayName));
        } else {
            replacement = replacement.replaceAll("%item%", format("x" + item.getAmount() + " " + item.getType().name()));
        }

        String message = e.getMessage();

        if (itemChat) {
            for (String itemString : SupremeChat.getInstance().getConfig().getStringList("chat-item-strings")) {
                if (message.contains(itemString)) {
                    e.setMessage(message.replace(itemString, format(replacement)));
                    break;
                }
            }
        }

        /// CHAT FORMATTING
        handleChatFormat(e);
    }

    private void handleChatFormat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        boolean enableChatFormat = SupremeChat.getInstance().getConfig().getBoolean("enable-chat-format");
        String originalMessage = e.getMessage();

        if (enableChatFormat) {
            boolean grouping = SupremeChat.getInstance().getConfig().getBoolean("group-formatting");
            String rank = grouping ? FormatUtil.getRank(player) : null;
            String chat = getChatFormat(rank);

            if (chat != null) {
                boolean hover = SupremeChat.getInstance().getConfig().getBoolean("hover.enable");
                boolean click = SupremeChat.getInstance().getConfig().getBoolean("click.enable");

                String permission = SupremeChat.getInstance().getConfig().getString("chat-color-permission");

                if (hover && click) {
                    for (Player onlinePlayer : e.getRecipients()) {
                        List<String[]> hoverMessages = new ArrayList<>();

                        for (String hoverMessage : SupremeChat.getInstance().getConfig().getStringList("hover.string")) {
                            hoverMessage = addOtherPlaceholders(hoverMessage, player);
                            TextComponent hoverComponent = new TextComponent(format(hoverMessage));
                            hoverMessages.add(hoverComponent.toLegacyText().split("\n"));
                        }

                        TextComponent msg = new TextComponent(format(chat));
                        String formatted = TextComponent.toLegacyText(msg);
                        formatted = addChatPlaceholders(formatted, player, originalMessage);
                        msg = new TextComponent(TextComponent.fromLegacyText(format(formatted)));

                        ComponentBuilder hoverBuilder = new ComponentBuilder("");
                        for (int i = 0; i < hoverMessages.size(); i++) {
                            String[] hoverMessage = hoverMessages.get(i);
                            hoverBuilder.append(Arrays.toString(hoverMessage).replace("[", "").replace("]", ""));

                            // Check if it's not the last line
                            if (i < hoverMessages.size() - 1) {
                                hoverBuilder.append("\n");
                            }
                        }

                        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.create()));

                        String clickMsg = SupremeChat.getInstance().getConfig().getString("click.string");
                        clickMsg = addOtherPlaceholders(clickMsg, player);
                        msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickMsg));

                        onlinePlayer.spigot().sendMessage(ChatMessageType.CHAT, msg);
                    }
                    e.getRecipients().clear();
                } else {
                    String formattedMessage = format(chat);

                    formattedMessage = addChatPlaceholders(formattedMessage, player, originalMessage);

                    assert permission != null;
                    e.setFormat(formattedMessage.replace("%message%", player.hasPermission(permission) ? format(originalMessage) : originalMessage).replace("%", "%%").replaceAll("%[^\\w\\s%]", ""));
                }
            }
        }
    }

    private String getChatFormat(String rank) {
        String chatFormat;

        if (rank != null) {
            chatFormat = getRankFormat(rank);
        } else {
            chatFormat = getGlobalFormat();
        }

        return chatFormat;
    }

    private static boolean isWordBlocked(String message, String blockedWord) {
        String pattern = "\\b" + blockedWord + "\\b";
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(message);

        return matcher.find();
    }
}