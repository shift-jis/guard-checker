package org.wasabi.bungeeguard;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;

public class GuardCheckerAddon extends MeteorAddon {
    @Override
    public void onInitialize() {
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public String getPackage() {
        return "org.wasabi.bungeeguard";
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof LoginSuccessS2CPacket(com.mojang.authlib.GameProfile profile) && profile.getProperties().containsKey("bungeeguard-token")) {
            String tokenValue = profile.getProperties().get("bungeeguard-token").stream()
                    .map(com.mojang.authlib.properties.Property::value) // or .getValue() in older authlib versions
                    .findFirst()
                    .orElse("N/A");
            MeteorClient.LOG.info("Detected BungeeGuard-protected server. Token: {}", tokenValue);
        }
    }
}
