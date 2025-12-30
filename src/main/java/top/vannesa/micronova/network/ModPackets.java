package top.vannesa.micronova.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import top.vannesa.micronova.MicronovaMod;

public class ModPackets {

    public static final Identifier HEALTH_SYNC = new Identifier("micronova", "health_sync");
    public static final Identifier DEBUG_HEALTH = new Identifier("micronova", "debug_health");

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(
                DEBUG_HEALTH,
                new DebugHealthC2SPacket()
        );
    }
}
