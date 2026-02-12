package top.vannesa.micronova.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.vannesa.micronova.ui.InventoryHealthRenderer;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void afterRender(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Render the health silhouette and bars on the top-left corner, scaled to 80%
        // Create a custom scaled rendering by passing modified coordinates to InventoryHealthRenderer
        int x = 10;
        int y = 10;
        // For now, render at the adjusted position with 80% scaling applied manually in the renderer
        // by modifying the coordinates passed to InventoryHealthRenderer
        InventoryHealthRenderer.renderScaled(ctx, x, y, 0.8f);
    }
}
