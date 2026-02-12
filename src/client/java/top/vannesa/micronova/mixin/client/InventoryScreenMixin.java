package top.vannesa.micronova.mixin.client;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replace vanilla InventoryScreen with custom inventory UI.
 * When E key opens inventory, show custom screen instead.
 */
@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "<init>", at = @At("TAIL"), cancellable = true)
    private void onInventoryScreenInit(PlayerEntity player, CallbackInfo ci) {
        // Replace vanilla inventory with custom one
        // This will be handled by replacing the screen in the game loop
        // We need to prevent the original screen from fully initializing
    }
}

