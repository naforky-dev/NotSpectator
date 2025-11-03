package io.codejava.mc.notspectator;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotSpectator extends JavaPlugin {

    private Set<String> whitelist = new HashSet<>();
    private boolean showGamemodeMessages;
    private PacketListener packetListener;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();
        loadWhitelist();

        // Register command
        getCommand("notspectator").setExecutor(new NotSpectatorCommand(this));

        // Get ProtocolLib and register packet listener
        try {
            protocolManager = ProtocolLibrary.getProtocolManager();
            packetListener = new PacketListener(this);
            protocolManager.addPacketListener(packetListener);
        } catch (Exception e) {
            getLogger().severe("Could not initialize ProtocolLib listener!");
            getLogger().severe("Is ProtocolLib installed on the server?");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("[NotSpectator] v1.0-java");
        getLogger().info("[NotSpectator] NotSpectator has been enabled!");
    }

    @Override
    public void onDisable() {
        // Unregister listener
        if (protocolManager != null && packetListener != null) {
            protocolManager.removePacketListener(packetListener);
        }
        getLogger().info("[NotSpectator] NotSpectator has been disabled.");
    }

    public void loadWhitelist() {
        // Reloads config from disk
        reloadConfig();
        // Load the set of names, converting them all to lowercase
        this.whitelist = getConfig().getStringList("whitelist")
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        // Load the gamemode message setting
        this.showGamemodeMessages = getConfig().getBoolean("show-gamemode-messages", true);
        
        getLogger().info("Loaded " + whitelist.size() + " players into the spectator whitelist.");
    }

    public void saveWhitelist() {
        // Save the current Java set back to the config file
        getConfig().set("whitelist", List.copyOf(this.whitelist));
        getConfig().set("show-gamemode-messages", this.showGamemodeMessages);
        saveConfig();
    }

    // --- Whitelist Methods ---
    
    public boolean isWhitelisted(String playerName) {
        return whitelist.contains(playerName.toLowerCase());
    }

    public void addToWhitelist(String playerName) {
        whitelist.add(playerName.toLowerCase());
        saveWhitelist();
    }

    public void setWhitelist(Set<String> newWhitelist) {
        this.whitelist = newWhitelist;
        saveWhitelist();
    }

    public void removeFromWhitelist(String playerName) {
        whitelist.remove(playerName.toLowerCase());
        saveWhitelist();
    }

    public Set<String> getWhitelist() {
        return new HashSet<>(whitelist); // Return a copy to prevent modification
    }

    // --- Gamemode Message Method ---

    public boolean isShowGamemodeMessages() {
        return showGamemodeMessages;
    }

    public void setShowGamemodeMessages(boolean show) {
        this.showGamemodeMessages = show;
        // This will be saved when the whitelist is next saved, or on disable.
        // For immediate save, uncomment the line below:
        // saveWhitelist();
    }
}