package top.vannesa.micronova.mixin.client;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Removed E-key inventory screen rendering.
 * The custom inventory is now controlled via I-key binding.
 */
@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    // Intentionally empty - E key inventory screen is no longer rendered
}
