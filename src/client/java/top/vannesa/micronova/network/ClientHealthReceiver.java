package top.vannesa.micronova.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import top.vannesa.micronova.ui.ClientHealthCache;

public final class ClientHealthReceiver {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                ModPackets.HEALTH_SYNC,
                (client, handler, buf, responseSender) -> {
                    ClientHealthCache.readFromPacket(buf);
                }
        );
    }

    private ClientHealthReceiver() {}
}
