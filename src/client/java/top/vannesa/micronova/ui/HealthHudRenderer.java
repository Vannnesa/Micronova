package top.vannesa.micronova.ui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import top.vannesa.micronova.health.BodyPart;

import java.util.Map;

public class HealthHudRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register(HealthHudRenderer::render);
    }

    private static void render(DrawContext ctx, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int x = 20;
        int y = 40;

        for (Map.Entry<BodyPart, Float> e : ClientHealthCache.get().entrySet()) {
            ctx.drawText(
                    client.textRenderer,
                    e.getKey().name() + ": " + e.getValue(),
                    x,
                    y,
                    0xFFFFFF,
                    false
            );
            y += 10;
        }
    }
}
