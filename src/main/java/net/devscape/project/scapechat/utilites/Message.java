package net.devscape.project.scapechat.utilites;

import me.clip.placeholderapi.PlaceholderAPI;
import net.devscape.project.scapechat.ScapeChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Message {

    public static String PREFIX = "&#00ff94&lS&#20f78f&lC&#3ff089&lA&#5fe884&lP&#7fe17f&lE&#9ed979&lC&#bed174&lH&#ddca6e&lA&#fdc269&lT ➟ ";

    public static String format(String message) {
        message = message.replace(">>", "").replace("<<", "");
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]){6}");
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
            String before = message.substring(0, matcher.start());
            String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = hexPattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getGlobalFormat() {
        return ScapeChat.getInstance().getConfig().getString("format");
    }

    public static String getRankFormat(String rank) {
        return ScapeChat.getInstance().getConfig().getString("groups." + rank);
    }

    public static String getLeave() {
        return ScapeChat.getInstance().getConfig().getString("custom-leave");
    }

    public static String getJoin() {
        return ScapeChat.getInstance().getConfig().getString("custom-join");
    }

    public static String addChatPlaceholers(String string, Player player, String message) {
        string = string.replace("%name%", player.getName());
        string = string.replace("%message%", message);
        string = string.replace("%world%", player.getLocation().getWorld().getName());
        string = string.replace("%x%", String.valueOf(player.getLocation().getX()));
        string = string.replace("%y%", String.valueOf(player.getLocation().getY()));
        string = string.replace("%z%", String.valueOf(player.getLocation().getZ()));
        string = string.replace("%xp%", String.valueOf(player.getLevel()));
        string = string.replace("%gamemode%", player.getGameMode().name());
        string = string.replace("%flying%", String.valueOf(player.isFlying()));
        string = replacePlaceholders(player, string);

        return string;
    }

    public static String addOtherPlaceholers(String string, Player player) {
        string = string.replace("%name%", player.getName());
        string = string.replace("%world%", player.getLocation().getWorld().getName());
        string = string.replace("%x%", String.valueOf(player.getLocation().getX()));
        string = string.replace("%y%", String.valueOf(player.getLocation().getY()));
        string = string.replace("%z%", String.valueOf(player.getLocation().getZ()));
        string = string.replace("%xp%", String.valueOf(player.getLevel()));
        string = string.replace("%gamemode%", player.getGameMode().name());
        string = string.replace("%flying%", String.valueOf(player.isFlying()));
        string = replacePlaceholders(player, string);

        return string;
    }


    public static String deformat(String str) {
        return ChatColor.stripColor(format(str));
    }

    public static void msgPlayer(Player player, String... str) {
        for (String msg : str) {
            player.sendMessage(format(msg));
        }
    }

    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.setPlaceholders(user, base);
    }

    public static void msgPlayer(CommandSender player, String... str) {
        for (String msg : str) {
            player.sendMessage(format(msg));
        }
    }

    public static void titlePlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(format(title), format(subtitle), fadeIn, stay, fadeOut);
    }

    public static void soundPlayer(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static List<String> color(List<String> lore){
        return lore.stream().map(Message::format).collect(Collectors.toList());
    }

    public static TextComponent interactCreator(Player player, String str, String hover_message) {
        TextComponent mainComponent = new TextComponent(format(str));
        mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().create()));
        return mainComponent;
    }

    public static void setHoverBroadcastEvent(TextComponent component, List<String> hoverMessagesList, Player broadcastReceivers) {
        if (hoverMessagesList.size() == 0)
            return;
        ComponentBuilder hoverMessageBuilder = new ComponentBuilder();
        int hoverLine = 0;
        for (String hoverMessages : hoverMessagesList) {
            hoverMessages = addOtherPlaceholers(hoverMessages, broadcastReceivers);
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(format(hoverMessages)));
            hoverMessageBuilder.append(textComponent);
            if (hoverLine != hoverMessagesList.size() - 1)
                hoverMessageBuilder.append("\n");
            hoverLine++;
        }
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessageBuilder.create()));
    }

    public static void setClickBroadcastEvent(TextComponent component, String click, Player player) {
        if (click == null || click.length() == 0)
            return;
        switch (click.charAt(0)) {
            case '/':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click));
                return;
            case '*':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(1)));
                return;
        }

        click = addOtherPlaceholers(click, player);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
    }

    public static void setClickBroadcastEvent(TextComponent component, String click) {
        if (click == null || click.length() == 0)
            return;
        switch (click.charAt(0)) {
            case '/':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click));
                return;
            case '*':
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(1)));
                return;
        }

        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
    }
}
