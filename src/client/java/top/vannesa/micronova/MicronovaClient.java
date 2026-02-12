package top.vannesa.micronova;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import top.vannesa.micronova.inventory.client.ClientInventoryCache;
import top.vannesa.micronova.network.ClientHealthReceiver;
import top.vannesa.micronova.network.ClientPackets;
import top.vannesa.micronova.ui.inventory.CustomInventoryScreen;
import net.minecraft.client.MinecraftClient;

public class MicronovaClient implements ClientModInitializer {

	private static KeyBinding openInventoryKey;

	@Override
	public void onInitializeClient() {
		ClientHealthReceiver.register();
		ClientPackets.register();
		top.vannesa.micronova.client.PistolClient.register();

		// Initialize client inventory cache
		ClientInventoryCache.init();

		// Handle E-key interception (when inventory screen would open)
		// We replace it with our custom screen via Mixin
		
		// Also support I-key as backup
		openInventoryKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.micronova.open_inventory",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_I,
			"category.micronova"
		));

		// Handle key press in client tick
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			while (openInventoryKey.wasPressed()) {
				client.setScreen(new CustomInventoryScreen(ClientInventoryCache.getInventory()));
			}
		});
	}
}



