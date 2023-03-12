package net.devscape.project.scapechat;

import net.devscape.project.scapechat.commands.SCCommand;
import net.devscape.project.scapechat.listeners.CommandFilter;
import net.devscape.project.scapechat.listeners.CustomCommands;
import net.devscape.project.scapechat.listeners.Formatting;
import net.devscape.project.scapechat.listeners.JoinLeave;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScapeChat extends JavaPlugin {

    private static ScapeChat instance;
    private static Permission perms = null;

    private final List<Player> chatDelayList = new ArrayList<>();
    private final List<Player> commandDelayList = new ArrayList<>();
    private final Map<Player, String> lastMessage = new HashMap<>();

    public static ScapeChat getInstance() {
        return instance;
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

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            setupPermissions();
        }

        getCommand("scapechat").setExecutor(new SCCommand());

        getServer().getPluginManager().registerEvents(new Formatting(), this);
        getServer().getPluginManager().registerEvents(new JoinLeave(), this);
        getServer().getPluginManager().registerEvents(new CommandFilter(), this);
        getServer().getPluginManager().registerEvents(new CustomCommands(), this);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public List<Player> getChatDelayList() {
        return chatDelayList;
    }

    public void reload() {
        super.reloadConfig();
        saveDefaultConfig();
    }

    public Map<Player, String> getLastMessage() {
        return lastMessage;
    }

    public List<Player> getCommandDelayList() {
        return commandDelayList;
    }
}
