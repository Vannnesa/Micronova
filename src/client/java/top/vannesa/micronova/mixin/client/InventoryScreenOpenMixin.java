package top.vannesa.micronova.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.vannesa.micronova.inventory.client.ClientInventoryCache;
import top.vannesa.micronova.ui.inventory.CustomInventoryScreen;

/**
 * Intercept vanilla InventoryScreen opening and replace with custom inventory.
 */
@Mixin(InventoryScreen.class)
public class InventoryScreenOpenMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void replaceWithCustomInventory(CallbackInfo ci) {
        // Replace the screen being set
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof InventoryScreen && 
            !(client.currentScreen instanceof CustomInventoryScreen)) {
            // Immediately replace with custom screen
            client.setScreen(new CustomInventoryScreen(ClientInventoryCache.getInventory()));
        }
    }
}
