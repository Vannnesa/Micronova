package top.vannesa.micronova.health;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import top.vannesa.micronova.network.HealthSyncS2CPacket;
import top.vannesa.micronova.network.ModPackets;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages HealthComponent instances for non-player entities used for debugging (dummies).
 * This is intentionally minimal and marked as debug-only; remove after debugging.
 */
public final class DummyHealthManager {

    private static final Map<UUID, HealthComponent> MAP = new ConcurrentHashMap<>();

    private DummyHealthManager() {}

    public static HealthComponent createFor(net.minecraft.entity.LivingEntity entity, HealthComponent source) {
        HealthComponent copy = new HealthComponent(source);
        MAP.put(entity.getUuid(), copy);
        return copy;
    }

    public static HealthComponent get(net.minecraft.entity.LivingEntity entity) {
        return MAP.computeIfAbsent(entity.getUuid(), k -> new HealthComponent());
    }

    public static void remove(net.minecraft.entity.LivingEntity entity) {
        MAP.remove(entity.getUuid());
    }

    public static void applyDamage(net.minecraft.entity.LivingEntity entity, BodyPart part, float amount, float penetration, float bleedChance) {
        HealthComponent hc = get(entity);
        hc.damage(part, amount, penetration, bleedChance);
        broadcastHealthToAll(entity, hc);

        boolean dead = hc.snapshot().values().stream().anyMatch(v -> v <= 0f);
        if (dead) {
            // Trigger vanilla death path for the entity
            try {
                // use null to trigger vanilla death path (invoker accepts nullable source)
                ((top.vannesa.micronova.mixin.LivingEntityInvoker) entity).callOnDeath(null);
            } catch (Exception ignored) {}
            remove(entity);
        }
    }

    private static void broadcastHealthToAll(net.minecraft.entity.LivingEntity entity, HealthComponent hc) {
        // Send the dummy snapshot to all online players (debug convenience)
        if (!(entity.getWorld() instanceof ServerWorld)) return;
        ServerWorld sw = (ServerWorld) entity.getWorld();
        sw.getServer().getPlayerManager().getPlayerList().forEach(p -> {
            PacketByteBuf buf = new PacketByteBuf(io.netty.buffer.Unpooled.buffer());
            HealthSyncS2CPacket.write(buf, hc.snapshot());
            ServerPlayNetworking.send(p, ModPackets.HEALTH_SYNC, buf);
        });
    }
}
