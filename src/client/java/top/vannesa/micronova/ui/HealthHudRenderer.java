package top.vannesa.micronova.ui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import top.vannesa.micronova.health.BodyPart;

import java.util.Map;
import java.util.EnumMap;

public class HealthHudRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register(HealthHudRenderer::render);
    }

    private static void render(DrawContext ctx, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int x = 20;
        int y = 40;
        int barWidth = 80;
        int barHeight = 8;

        // smoothing map: keep displayed values between frames
        // stored as percentage (0-100)
        if (DISPLAYED.isEmpty()) {
            // initialize with hp values
            ClientHealthCache.snapshot().forEach((p, v) -> DISPLAYED.put(p, v.hp));
        }

        Map<BodyPart, ClientHealthCache.PartState> snapshot = ClientHealthCache.snapshot();
        for (BodyPart part : BodyPart.values()) {
            float target = snapshot.containsKey(part) ? snapshot.get(part).hp : 0f;
            float prev = DISPLAYED.getOrDefault(part, target);
            float newVal = prev + (target - prev) * 0.12f; // lerp
            DISPLAYED.put(part, newVal);

            int bx = x;
            int by = y;

            // background
            ctx.fill(bx, by, bx + barWidth, by + barHeight, 0xFF333333);

            // foreground color gradient (green -> red)
            float ratio = Math.max(0f, Math.min(1f, newVal / 100f));
            int red = (int) (255 * (1f - ratio));
            int green = (int) (255 * ratio);
            int color = (red << 16) | (green << 8);

            int filled = (int) (barWidth * ratio);
            ctx.fill(bx, by, bx + filled, by + barHeight, 0xFF000000 | color);

            // cripple indicator: red flashing border if crippled
            var crippleLevel = snapshot.containsKey(part) ? snapshot.get(part).crippleLevel : null;
            if (crippleLevel != null && crippleLevel != top.vannesa.micronova.health.CrippleLevel.NONE) {
                // Flash effect: alternate between normal and bright red based on crippledTicks
                int crippledTicks = snapshot.get(part).crippledTicks;
                boolean flash = (crippledTicks / 5) % 2 == 0;  // blink every 5 ticks
                int borderColor = flash ? 0xFFFF3333 : 0xFF990000;  // bright red or dark red

                // Draw border (2px)
                ctx.fill(bx - 2, by - 2, bx + barWidth + 2, by - 1, borderColor);  // top
                ctx.fill(bx - 2, by + barHeight + 1, bx + barWidth + 2, by + barHeight + 2, borderColor);  // bottom
                ctx.fill(bx - 2, by - 2, bx - 1, by + barHeight + 2, borderColor);  // left
                ctx.fill(bx + barWidth + 1, by - 2, bx + barWidth + 2, by + barHeight + 2, borderColor);  // right
            }

            // text with cripple indicator
            String label = part.name();
            if (crippleLevel != null && crippleLevel != top.vannesa.micronova.health.CrippleLevel.NONE) {
                label += " [" + crippleLevel.name() + "]";
            }

            ctx.drawText(
                    client.textRenderer,
                    label + ": " + String.format("%.1f", newVal),
                    bx + barWidth + 6,
                    by - 2,
                    0xFFFFFF,
                    false
            );

            y += barHeight + 8;
        }
    }

    private static final Map<BodyPart, Float> DISPLAYED = new EnumMap<>(BodyPart.class);
}
