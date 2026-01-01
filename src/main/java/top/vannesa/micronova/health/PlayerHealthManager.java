package top.vannesa.micronova.health;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import top.vannesa.micronova.network.HealthSyncS2CPacket;
import top.vannesa.micronova.network.ModPackets;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerHealthManager {

    // map player UUID -> HealthComponent
    private static final Map<UUID, HealthComponent> MAP = new ConcurrentHashMap<>();
    // players currently being force-killed (to avoid recursion)
    private static final Set<UUID> FORCE_KILL = ConcurrentHashMap.newKeySet();

    private PlayerHealthManager() {}

    public static HealthComponent get(ServerPlayerEntity player) {
        return MAP.computeIfAbsent(player.getUuid(), k -> new HealthComponent());
    }

    public static void remove(ServerPlayerEntity player) {
        MAP.remove(player.getUuid());
    }

    public static void applyDamage(ServerPlayerEntity player, BodyPart part, float amount) {
        HealthComponent hc = get(player);
        // default: no penetration, small bleed chance
        hc.damage(part, amount, 0f, 0.05f);

        // broadcast updated health to the player
        broadcastHealth(player);

        // Check for death condition: any part <= 0
        boolean dead = hc.snapshot().values().stream().anyMatch(v -> v <= 0f);
        if (dead) {
            forceKill(player);
        }
    }

    /**
     * Tick per-player health (apply bleeding) and broadcast if changed.
     */
    public static void tick(ServerPlayerEntity player) {
        HealthComponent hc = get(player);
        boolean changed = hc.tick();
        if (changed) broadcastHealth(player);
    }

    private static void forceKill(ServerPlayerEntity player) {
        UUID id = player.getUuid();
        if (FORCE_KILL.contains(id)) return;
        FORCE_KILL.add(id);
        try {
            // Force the vanilla death path by invoking LivingEntity.onDeath via invoker mixin.
            ((top.vannesa.micronova.mixin.LivingEntityInvoker) player).callOnDeath(null);
        } finally {
            FORCE_KILL.remove(id);
        }
    }

    public static boolean isForceKill(UUID id) {
        return FORCE_KILL.contains(id);
    }

    public static void broadcastHealth(ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(io.netty.buffer.Unpooled.buffer());
        HealthSyncS2CPacket.write(buf, get(player).snapshot());
        ServerPlayNetworking.send(player, ModPackets.HEALTH_SYNC, buf);
    }
}
