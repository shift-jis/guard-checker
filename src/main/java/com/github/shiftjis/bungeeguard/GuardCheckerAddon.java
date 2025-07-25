package com.github.shiftjis.bungeeguard;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;

import java.util.ArrayList;
import java.util.List;

public class GuardCheckerAddon extends MeteorAddon {
    private final List<BungeeguardToken> bungeeguardTokens = new ArrayList<>();

    @Override
    public void onInitialize() {
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public String getPackage() {
        return "com.github.shiftjis.bungeeguard";
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof LoginSuccessS2CPacket(com.mojang.authlib.GameProfile profile) && profile.getProperties().containsKey("bungeeguard-token")) {
            String tokenValue = profile.getProperties().get("bungeeguard-token").stream()
                    .map(com.mojang.authlib.properties.Property::value) // or .getValue() in older authlib versions
                    .findFirst()
                    .orElse("N/A");
            bungeeguardTokens.add(new BungeeguardToken(event.connection.getAddressAsString(true), tokenValue));
        }
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        bungeeguardTokens.removeIf(bungeeguardToken -> {
            ChatUtils.info("Detected BungeeGuard-protected server. Token: " + bungeeguardToken.value());
            MeteorClient.LOG.info("Detected BungeeGuard-protected server. Token: {}", bungeeguardToken.value());
            return true;
        });
    }
}
