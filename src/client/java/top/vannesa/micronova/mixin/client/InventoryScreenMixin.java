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
        // Render the health silhouette and bars on the left side of the inventory
        int x = 10;
        int y = 20;
        InventoryHealthRenderer.render(ctx, x, y);
    }
}
