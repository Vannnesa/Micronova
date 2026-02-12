package top.vannesa.micronova.ui;

import net.minecraft.client.gui.DrawContext;
import top.vannesa.micronova.health.BodyPart;

import java.util.EnumMap;
import java.util.Map;

public final class InventoryHealthRenderer {

    private static final Map<BodyPart, Float> DISPLAYED = new EnumMap<>(BodyPart.class);

    private InventoryHealthRenderer() {}

    public static void render(DrawContext ctx, int x, int y) {
        // Render simple silhouette at (x,y) and bars to the right
        // silhouette box
        int sx = x;
        int sy = y;
        int sW = 24;
        int sH = 40;

        ctx.fill(sx, sy, sx + sW, sy + sH, 0xFF222222);

        Map<BodyPart, ClientHealthCache.PartState> snapshot = ClientHealthCache.snapshot();
        // initialize displayed with hp
        if (DISPLAYED.isEmpty()) snapshot.forEach((p, v) -> DISPLAYED.put(p, v.hp));

        int bx = sx + sW + 12;
        int by = sy;
        int barW = 80;
        int barH = 8;

        for (BodyPart part : BodyPart.values()) {
            float target = snapshot.containsKey(part) ? snapshot.get(part).hp : 0f;
            float prev = DISPLAYED.getOrDefault(part, target);
            float newVal = prev + (target - prev) * 0.12f;
            DISPLAYED.put(part, newVal);

            // draw line from silhouette to bar
            int lineStartX = sx + sW / 2;
            int lineStartY = sy + 4 + (part.ordinal() * 6);
            int lineEndX = bx;
            int lineEndY = by + (part.ordinal() * (barH + 6)) + 4;
            ctx.fill(lineStartX, lineStartY, lineEndX, lineEndY, 0xFF444444);

            // background
            int yPos = by + part.ordinal() * (barH + 6);
            ctx.fill(bx, yPos, bx + barW, yPos + barH, 0xFF333333);

            float ratio = Math.max(0f, Math.min(1f, newVal / 100f));
            int red = (int) (255 * (1f - ratio));
            int green = (int) (255 * ratio);
            int color = (red << 16) | (green << 8);
            int filled = (int) (barW * ratio);
            ctx.fill(bx, yPos, bx + filled, yPos + barH, 0xFF000000 | color);

            ctx.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    part.name() + ": " + String.format("%.0f", newVal), bx + barW + 6, yPos - 2, 0xFFFFFF, false);
        }
    }

    /**
     * Render with scaling factor applied to all coordinates.
     */
    public static void renderScaled(DrawContext ctx, int x, int y, float scale) {
        // Render simple silhouette at (x,y) and bars to the right, with scaling applied
        int sx = (int) (x);
        int sy = (int) (y);
        int sW = (int) (24 * scale);
        int sH = (int) (40 * scale);

        ctx.fill(sx, sy, sx + sW, sy + sH, 0xFF222222);

        Map<BodyPart, ClientHealthCache.PartState> snapshot = ClientHealthCache.snapshot();
        // initialize displayed with hp
        if (DISPLAYED.isEmpty()) snapshot.forEach((p, v) -> DISPLAYED.put(p, v.hp));

        int bx = (int) (sx + sW + 12 * scale);
        int by = sy;
        int barW = (int) (80 * scale);
        int barH = (int) (8 * scale);

        for (BodyPart part : BodyPart.values()) {
            float target = snapshot.containsKey(part) ? snapshot.get(part).hp : 0f;
            float prev = DISPLAYED.getOrDefault(part, target);
            float newVal = prev + (target - prev) * 0.12f;
            DISPLAYED.put(part, newVal);

            // draw line from silhouette to bar
            int lineStartX = (int) (sx + sW / 2);
            int lineStartY = (int) (sy + 4 * scale + (part.ordinal() * 6 * scale));
            int lineEndX = bx;
            int lineEndY = (int) (by + (part.ordinal() * (barH + 6 * scale)) + 4 * scale);
            ctx.fill(lineStartX, lineStartY, lineEndX, lineEndY, 0xFF444444);

            // background
            int yPos = (int) (by + part.ordinal() * (barH + 6 * scale));
            ctx.fill(bx, yPos, bx + barW, yPos + barH, 0xFF333333);

            float ratio = Math.max(0f, Math.min(1f, newVal / 100f));
            int red = (int) (255 * (1f - ratio));
            int green = (int) (255 * ratio);
            int color = (red << 16) | (green << 8);
            int filled = (int) (barW * ratio);
            ctx.fill(bx, yPos, bx + filled, yPos + barH, 0xFF000000 | color);

            ctx.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    part.name() + ": " + String.format("%.0f", newVal), (int) (bx + barW + 6 * scale), (int) (yPos - 2 * scale), 0xFFFFFF, false);
        }
    }
}
