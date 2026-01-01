package top.vannesa.micronova.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import top.vannesa.micronova.network.ModPackets;

public final class PistolClient {
    private static KeyBinding fireKey;

    private PistolClient() {}

    public static void register() {
        fireKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.micronova.pistol_fire", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.micronova.controls"));

        // client tick handler
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (fireKey.wasPressed()) {
                // send fire packet to server if holding pistol
                if (client.player != null && client.player.getMainHandStack().getItem() == top.vannesa.micronova.ModItems.PISTOL_9MM) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    ClientPlayNetworking.send(ModPackets.PISTOL_FIRE, buf);
                }
            }
        });
    }
}
