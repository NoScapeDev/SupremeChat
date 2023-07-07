package net.devscape.project.supremechat.utils;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.entity.Player;

import static net.devscape.project.supremechat.utils.Message.PREFIX;
import static net.devscape.project.supremechat.utils.Message.msgPlayer;

public class FormatUtil {


    // ==================================================
    // GET RANK
    // ==================================================
    public static String getRank(Player player) {
        return SupremeChat.getChat().getPrimaryGroup(player);
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
