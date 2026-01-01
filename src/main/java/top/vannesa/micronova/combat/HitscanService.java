package top.vannesa.micronova.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.DummyHealthManager;
import top.vannesa.micronova.health.PlayerHealthManager;

public final class HitscanService {

    private HitscanService() {}

    /**
     * Apply direct hitscan damage to a specific body part of target player.
     * This method is server-side and will broadcast updated health.
     */
    public static void applyHitscan(ServerPlayerEntity target, BodyPart part, float amount, float penetration, float bleedChance) {
        // Provide weapon-level parameters (penetration, bleed chance)
        top.vannesa.micronova.health.HealthComponent hc = PlayerHealthManager.get(target);
        hc.damage(part, amount, penetration, bleedChance);
        PlayerHealthManager.broadcastHealth(target);
        // check death
        boolean dead = hc.snapshot().values().stream().anyMatch(v -> v <= 0f);
        if (dead) PlayerHealthManager.applyDamage(target, part, 0f); // ensure forced kill if needed
    }

    /**
     * Generic hitscan for any living entity (used by debug dummies and non-player entities).
     */
    public static void applyHitscan(LivingEntity target, BodyPart part, float amount, float penetration, float bleedChance) {
        if (target instanceof ServerPlayerEntity) {
            applyHitscan((ServerPlayerEntity) target, part, amount, penetration, bleedChance);
            return;
        }
        // For non-player entities use the DummyHealthManager (debug-only)
        DummyHealthManager.applyDamage(target, part, amount, penetration, bleedChance);
    }
}
