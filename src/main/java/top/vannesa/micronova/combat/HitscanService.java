package top.vannesa.micronova.combat;

import net.minecraft.server.network.ServerPlayerEntity;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.PlayerHealthManager;

public final class HitscanService {

    private HitscanService() {}

    /**
     * Apply direct hitscan damage to a specific body part of target player.
     * This method is server-side and will broadcast updated health.
     */
    public static void applyHitscan(ServerPlayerEntity target, BodyPart part, float amount) {
        PlayerHealthManager.applyDamage(target, part, amount);
    }
}
