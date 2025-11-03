package io.codejava.mc.notspectator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import java.util.ArrayList;
import java.util.List;

public class PacketListener extends PacketAdapter {

    private final NotSpectator plugin;

    public PacketListener(NotSpectator plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO_UPDATE);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            return;
        }

        // Get the list of player data entries from the packet
        List<PlayerInfoData> dataList = event.getPacket().getPlayerInfoDataLists().read(0);
        List<PlayerInfoData> modifiedDataList = new ArrayList<>();

        for (PlayerInfoData data : dataList) {
            WrappedGameProfile profile = data.getProfile();
            if (profile == null) {
                modifiedDataList.add(data);
                continue;
            }

            String playerName = profile.getName();

            // Check if the player is whitelisted AND is in spectator mode
            if (plugin.isWhitelisted(playerName) && data.getGameMode() == EnumWrappers.NativeGameMode.SPECTATOR) {
                
                // This is the player we want to hide.
                // We create a NEW PlayerInfoData object, copying everything...
                // ...but we lie and say their gamemode is SURVIVAL.
                PlayerInfoData newData = new PlayerInfoData(
                        data.getProfile(),
                        data.getLatency(),
                        EnumWrappers.NativeGameMode.SURVIVAL, // The "lie"
                        data.getDisplayName(),
                        data.getSignature()
                );
                modifiedDataList.add(newData);
            } else {
                // This player is not whitelisted or not spectator,
                // so we add their data to the list unmodified.
                modifiedDataList.add(data);
            }
        }

        // Write the new (potentially modified) list back to the packet
        event.getPacket().getPlayerInfoDataLists().write(0, modifiedDataList);
    }
}