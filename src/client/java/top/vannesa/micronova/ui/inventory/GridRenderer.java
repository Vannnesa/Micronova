package top.vannesa.micronova.ui.inventory;

import net.minecraft.client.gui.DrawContext;
import top.vannesa.micronova.inventory.GridContainer;
import top.vannesa.micronova.inventory.GridItem;

/**
 * Renders a grid-based inventory container to the screen.
 * Handles drawing grid cells, items, and selection highlighting.
 */
public class GridRenderer {
    
    private static final int CELL_SIZE = 20; // pixels per grid cell
    private static final int GRID_COLOR = 0xFF444444;
    private static final int SELECTED_CELL_COLOR = 0xFF6666FF;
    private static final int ITEM_BG_COLOR = 0xFF333333;
    private static final int ITEM_BORDER_COLOR = 0xFF999999;
    
    /**
     * Draw a grid container at the specified screen position.
     * @param ctx drawing context
     * @param container the grid container to render
     * @param screenX starting X coordinate on screen
     * @param screenY starting Y coordinate on screen
     * @param selectedX selected grid X (-1 if none selected)
     * @param selectedY selected grid Y (-1 if none selected)
     */
    public static void render(DrawContext ctx, GridContainer container, int screenX, int screenY, int selectedX, int selectedY) {
        int width = container.getWidth();
        int height = container.getHeight();
        
        // Draw grid background
        ctx.fill(screenX, screenY, screenX + width * CELL_SIZE, screenY + height * CELL_SIZE, 0xFF222222);
        
        // Draw grid lines
        for (int x = 0; x <= width; x++) {
            int px = screenX + x * CELL_SIZE;
            ctx.fill(px - 1, screenY, px, screenY + height * CELL_SIZE, GRID_COLOR);
        }
        
        for (int y = 0; y <= height; y++) {
            int py = screenY + y * CELL_SIZE;
            ctx.fill(screenX, py - 1, screenX + width * CELL_SIZE, py, GRID_COLOR);
        }
        
        // Highlight selected cell
        if (selectedX >= 0 && selectedY >= 0 && selectedX < width && selectedY < height) {
            int sx = screenX + selectedX * CELL_SIZE;
            int sy = screenY + selectedY * CELL_SIZE;
            ctx.fill(sx, sy, sx + CELL_SIZE, sy + CELL_SIZE, SELECTED_CELL_COLOR);
        }
        
        // Draw items
        for (GridItem item : container.getAllItems()) {
            drawItem(ctx, item, screenX, screenY);
        }
    }
    
    /**
     * Draw a single item on the grid.
     */
    private static void drawItem(DrawContext ctx, GridItem item, int screenX, int screenY) {
        int itemWidth = item.getEffectiveWidth() * CELL_SIZE;
        int itemHeight = item.getEffectiveHeight() * CELL_SIZE;
        int px = screenX + item.getX() * CELL_SIZE;
        int py = screenY + item.getY() * CELL_SIZE;
        
        // Draw item background
        ctx.fill(px, py, px + itemWidth, py + itemHeight, ITEM_BG_COLOR);
        
        // Draw item border
        ctx.fill(px, py, px + itemWidth, py + 2, ITEM_BORDER_COLOR);           // top
        ctx.fill(px, py + itemHeight - 2, px + itemWidth, py + itemHeight, ITEM_BORDER_COLOR); // bottom
        ctx.fill(px, py, px + 2, py + itemHeight, ITEM_BORDER_COLOR);           // left
        ctx.fill(px + itemWidth - 2, py, px + itemWidth, py + itemHeight, ITEM_BORDER_COLOR); // right
        
        // Draw item info (width x height)
        String sizeLabel = item.getWidth() + "x" + item.getHeight();
        if (item.isRotated()) {
            sizeLabel += " R";
        }
        
        ctx.drawText(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            sizeLabel,
            px + 4, py + 4,
            0xFFFFFF,
            false
        );
    }
    
    /**
     * Convert screen coordinates to grid coordinates.
     * @return array [gridX, gridY] or null if outside grid
     */
    public static int[] getGridCoordinates(GridContainer container, int screenX, int screenY, int mouseX, int mouseY) {
        int width = container.getWidth();
        int height = container.getHeight();
        
        int relX = mouseX - screenX;
        int relY = mouseY - screenY;
        
        int gridX = relX / CELL_SIZE;
        int gridY = relY / CELL_SIZE;
        
        if (gridX < 0 || gridX >= width || gridY < 0 || gridY >= height) {
            return null;
        }
        
        return new int[]{gridX, gridY};
    }
    
    public static int getCellSize() {
        return CELL_SIZE;
    }
}
