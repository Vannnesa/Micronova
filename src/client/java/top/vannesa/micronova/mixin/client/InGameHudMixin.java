package top.vannesa.micronova.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hide vanilla health hearts and food bar.
 * Health is managed by our custom HealthComponent system.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void cancelHealthBar(DrawContext ctx, CallbackInfo ci) {
        // Cancel vanilla health bar rendering (red hearts)
        ci.cancel();
    }
}

