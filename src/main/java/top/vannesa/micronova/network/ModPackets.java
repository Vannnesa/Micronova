package top.vannesa.micronova.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import top.vannesa.micronova.health.PlayerHealthManager;

public class ModPackets {

    public static final Identifier HEALTH_SYNC =
            new Identifier("micronova", "health_sync");
    public static final Identifier PISTOL_FIRE = new Identifier("micronova", "pistol_fire");

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
        // Server tick: apply bleeding to all online players and broadcast changes
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getPlayerManager().getPlayerList().forEach(p -> {
                server.execute(() -> PlayerHealthManager.tick(p));
            });
        });

        // Register server receiver for pistol fire (client -> server)
        ServerPlayNetworking.registerGlobalReceiver(PISTOL_FIRE, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // perform server-side hitscan from player's eye position
                net.minecraft.util.hit.HitResult hit = player.raycast(64.0D, 0f, false);
                if (hit instanceof net.minecraft.util.hit.EntityHitResult) {
                    net.minecraft.util.hit.EntityHitResult erh = (net.minecraft.util.hit.EntityHitResult) hit;
                    net.minecraft.entity.Entity entity = erh.getEntity();
                    // map hit to body part
                    top.vannesa.micronova.health.BodyPart part = mapHitToPart(entity, erh.getPos().y);
                    if (entity instanceof net.minecraft.server.network.ServerPlayerEntity) {
                        net.minecraft.server.network.ServerPlayerEntity target = (net.minecraft.server.network.ServerPlayerEntity) entity;
                        // Basic weapon parameters: pistol9mm
                        top.vannesa.micronova.weapon.WeaponConfig cfg = top.vannesa.micronova.weapon.WeaponConfig.pistol9mm();
                        top.vannesa.micronova.combat.HitscanService.applyHitscan(target, part, cfg.baseDamage, cfg.penetration, cfg.bleedChance);
                    }
                }
            });
        });
    }

    private static top.vannesa.micronova.health.BodyPart mapHitToPart(net.minecraft.entity.Entity entity, double hitY) {
        double min = entity.getBoundingBox().minY;
        double max = entity.getBoundingBox().maxY;
        double rel = (hitY - min) / (max - min);
        if (rel > 0.85) return top.vannesa.micronova.health.BodyPart.HEAD;
        if (rel > 0.6) return top.vannesa.micronova.health.BodyPart.CHEST;
        if (rel > 0.4) return top.vannesa.micronova.health.BodyPart.STOMACH;
        return top.vannesa.micronova.health.BodyPart.LEFT_LEG;
    }
}
