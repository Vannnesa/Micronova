package top.vannesa.micronova.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {

    public static final Identifier HEALTH_SYNC =
            new Identifier("micronova", "health_sync");

    public static void registerServer() {
        // 目前先留空
        // 后续 Ballistics / Health 同步会在这里注册
    }
}
