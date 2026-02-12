package top.vannesa.micronova.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.PlayerHealthManager;

@Mixin(targets = "net.minecraft.entity.LivingEntity")
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Only override server-side player damage handling; other entities continue to use vanilla
        Entity self = (Entity) (Object) this;
        if (self.getWorld().isClient) return;
        if (!(self instanceof ServerPlayerEntity player)) return;

        // If we are performing a forced kill, allow vanilla to proceed
        if (PlayerHealthManager.isForceKill(player.getUuid())) return;

        // Determine body part mapping
        BodyPart part = mapDamageSourceToPart(source, amount, player);

        // Apply damage via PlayerHealthManager and cancel vanilla damage
        PlayerHealthManager.applyDamage(player, part, amount);
        
        // Return true to indicate damage was handled, preventing vanilla damage
        cir.setReturnValue(true);
    }

    private BodyPart mapDamageSourceToPart(DamageSource source, float amount, ServerPlayerEntity player) {
        // Priority: projectile with embedded target -> use that if possible
        if (source.getAttacker() instanceof ProjectileEntity) {
            // fallback: chance to be head, else chest
            return player.getRandom().nextFloat() < 0.12f ? BodyPart.HEAD : BodyPart.CHEST;
        }

        // Default mapping: heavy hits -> chest, light hits -> limbs
        if (amount > 8f) return BodyPart.CHEST;
        return player.getRandom().nextBoolean() ? BodyPart.LEFT_ARM : BodyPart.RIGHT_ARM;
    }
}

