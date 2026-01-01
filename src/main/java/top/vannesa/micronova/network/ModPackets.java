package top.vannesa.micronova.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import top.vannesa.micronova.health.PlayerHealthManager;

public class ModPackets {

    public static final Identifier HEALTH_SYNC =
            new Identifier("micronova", "health_sync");

    public static void registerServer() {
        // 在玩家加入时发送初始 health 快照
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.execute(() -> {
                PlayerHealthManager.broadcastHealth(handler.player);
            });
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            server.execute(() -> {
                PlayerHealthManager.remove(handler.player);
            });
        });
    }
}
