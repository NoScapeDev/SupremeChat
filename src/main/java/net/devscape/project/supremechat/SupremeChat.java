package net.devscape.project.supremechat;

import net.devscape.project.supremechat.commands.SCCommand;
import net.devscape.project.supremechat.hooks.Metrics;
import net.devscape.project.supremechat.listeners.*;
import net.devscape.project.supremechat.utils.FormatUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SupremeChat extends JavaPlugin {

    private static SupremeChat instance;
    private static Permission perms = null;
    private static Chat chat;

    private final List<Player> chatDelayList = new ArrayList<>();
    private final List<Player> commandDelayList = new ArrayList<>();
    private final Map<Player, String> lastMessage = new HashMap<>();
    private FormatUtil formattingUtils;

    public static SupremeChat getInstance() {
        return instance;
    }

    public static Chat getChat() {
        return chat;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        chatDelayList.clear();
        lastMessage.clear();
        commandDelayList.clear();
    }

    private void init() {
        instance = this;

        saveDefaultConfig();

        setupVault();

        getCommand("supremechat").setExecutor(new SCCommand());

        getServer().getPluginManager().registerEvents(new Formatting(), this);
        getServer().getPluginManager().registerEvents(new JoinLeave(), this);
        getServer().getPluginManager().registerEvents(new CommandFilter(), this);
        getServer().getPluginManager().registerEvents(new CustomCommands(), this);
        getServer().getPluginManager().registerEvents(new Mention(), this);
        getServer().getPluginManager().registerEvents(new CommandSpy(), this);

        callMetrics();
    }

    private boolean setupVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            // Vault plugin not found
            return false;
        }

        RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (permProvider == null) {
            // Permission service not found
            return false;
        }
        perms = permProvider.getProvider();

        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager()
                .getRegistration(Chat.class);
        if (chatProvider == null) {
            // Chat service not found
            return false;
        }
        chat = chatProvider.getProvider();

        return true;
    }

    public static Permission getPermissions() {
        return perms;
    }


    public List<Player> getChatDelayList() {
        return chatDelayList;
    }

    public void reload() {
        super.reloadConfig();
    }

    public Map<Player, String> getLastMessage() {
        return lastMessage;
    }

    public List<Player> getCommandDelayList() {
        return commandDelayList;
    }

    private void callMetrics() {
        int pluginId = 18329;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfig().getString("language", "en")));

        metrics.addCustomChart(new Metrics.DrilldownPie("java_version", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            String javaVersion = System.getProperty("java.version");
            Map<String, Integer> entry = new HashMap<>();
            entry.put(javaVersion, 1);
            if (javaVersion.startsWith("1.7")) {
                map.put("Java 1.7", entry);
            } else if (javaVersion.startsWith("1.8")) {
                map.put("Java 1.8", entry);
            } else if (javaVersion.startsWith("1.9")) {
                map.put("Java 1.9", entry);
            } else {
                map.put("Other", entry);
            }
            return map;
        }));
    }
}