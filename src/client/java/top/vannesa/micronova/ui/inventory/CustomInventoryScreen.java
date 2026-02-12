package top.vannesa.micronova.ui.inventory;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import top.vannesa.micronova.inventory.GridContainer;
import top.vannesa.micronova.inventory.GridItem;
import top.vannesa.micronova.inventory.InventoryManager;
import top.vannesa.micronova.ui.ClientHealthCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom inventory screen replacing Minecraft's default inventory.
 * Layout: [Status Panel Left] [Scrollable Inventory Grid Middle] [Reserved Right]
 */
public class CustomInventoryScreen extends Screen {
    
    private final InventoryManager inventoryManager;
    private static final int STATUS_PANEL_WIDTH = 150;
    private static final int STATUS_PANEL_X = 10;
    private static final int STATUS_PANEL_Y = 10;
    
    private int containerDisplayX;
    private int containerDisplayY;
    private int inventoryScrollOffset = 0;
    private static final int SCROLL_SPEED = 10;
    
    private String selectedContainerId;
    private GridItem selectedItem;
    private int dragOffsetX;
    private int dragOffsetY;
    
    public CustomInventoryScreen(InventoryManager inventoryManager) {
        super(Text.literal("Inventory"));
        this.inventoryManager = inventoryManager;
        this.selectedContainerId = "backpack"; // Default selection
        this.containerDisplayX = STATUS_PANEL_WIDTH + 30;
        this.containerDisplayY = 20;
    }
    
    @Override
    protected void init() {
        super.init();
    }
    
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx);
        
        // Render left status panel
        renderStatusPanel(ctx);
        
        // Render inventory in center
        renderInventoryPanel(ctx, mouseX, mouseY);
        
        super.render(ctx, mouseX, mouseY, delta);
    }
    
    private void renderStatusPanel(DrawContext ctx) {
        // Background
        ctx.fill(STATUS_PANEL_X, STATUS_PANEL_Y, STATUS_PANEL_X + STATUS_PANEL_WIDTH, 
                STATUS_PANEL_Y + 300, 0xFF1a1a1a);
        
        // Border
        ctx.fill(STATUS_PANEL_X, STATUS_PANEL_Y, STATUS_PANEL_X + STATUS_PANEL_WIDTH, 
                STATUS_PANEL_Y + 2, 0xFF888888);
        
        // Title
        ctx.drawText(
            this.textRenderer,
            Text.literal("Status"),
            STATUS_PANEL_X + 10, STATUS_PANEL_Y + 10,
            0xFFFFFF, false
        );
        
        // Health information from cache
        var healthSnapshot = ClientHealthCache.snapshot();
        int yOffset = STATUS_PANEL_Y + 35;
        
        for (var entry : healthSnapshot.entrySet()) {
            String partName = entry.getKey().name().toLowerCase();
            float hp = entry.getValue().hp;
            
            ctx.drawText(
                this.textRenderer,
                Text.literal(partName + ": " + String.format("%.0f", hp)),
                STATUS_PANEL_X + 10, yOffset,
                0xFFFFFF, false
            );
            
            // Health bar
            int barX = STATUS_PANEL_X + 10;
            int barY = yOffset + 12;
            int barWidth = STATUS_PANEL_WIDTH - 20;
            int barHeight = 6;
            
            ctx.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
            
            float ratio = Math.max(0f, Math.min(1f, hp / 100f));
            int red = (int) (255 * (1f - ratio));
            int green = (int) (255 * ratio);
            int color = (red << 16) | (green << 8);
            int filled = (int) (barWidth * ratio);
            
            ctx.fill(barX, barY, barX + filled, barY + barHeight, 0xFF000000 | color);
            
            yOffset += 25;
        }
    }
    
    private void renderInventoryPanel(DrawContext ctx, int mouseX, int mouseY) {
        // Background
        int inventoryWidth = 280;
        int inventoryHeight = 300;
        ctx.fill(containerDisplayX, containerDisplayY, containerDisplayX + inventoryWidth, 
                containerDisplayY + inventoryHeight, 0xFF222222);
        
        // Title
        ctx.drawText(
            this.textRenderer,
            Text.literal("Inventory"),
            containerDisplayX + 10, containerDisplayY + 10,
            0xFFFFFF, false
        );
        
        // Render all containers with scroll support
        int displayX = containerDisplayX + 10;
        int displayY = containerDisplayY + 30 - inventoryScrollOffset;
        int containerSpacing = 140;
        
        for (String containerId : inventoryManager.getAllContainers().keySet()) {
            GridContainer container = inventoryManager.getContainer(containerId);
            
            // Container label
            ctx.drawText(
                this.textRenderer,
                Text.literal(containerId.replace('_', ' ')),
                displayX, displayY - 15,
                0xFFFFFF, false
            );
            
            // Render grid
            if (displayY >= containerDisplayY && displayY <= containerDisplayY + inventoryHeight) {
                GridRenderer.render(ctx, container, displayX, displayY, -1, -1);
            }
            
            displayY += containerSpacing;
        }
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int inventoryWidth = 280;
        int inventoryHeight = 300;
        
        if (mouseX > containerDisplayX && mouseX < containerDisplayX + inventoryWidth &&
            mouseY > containerDisplayY && mouseY < containerDisplayY + inventoryHeight) {
            
            inventoryScrollOffset -= (int) (verticalAmount * SCROLL_SPEED);
            inventoryScrollOffset = Math.max(0, Math.min(inventoryScrollOffset, 200));
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false; // Only handle left click
        
        // Check which container was clicked
        int displayX = containerDisplayX + 10;
        int displayY = containerDisplayY + 30 - inventoryScrollOffset;
        int containerSpacing = 140;
        
        for (String containerId : inventoryManager.getAllContainers().keySet()) {
            GridContainer container = inventoryManager.getContainer(containerId);
            
            int[] gridCoords = GridRenderer.getGridCoordinates(container, displayX, displayY, (int) mouseX, (int) mouseY);
            if (gridCoords != null) {
                GridItem item = container.getItemAt(gridCoords[0], gridCoords[1]);
                
                if (item != null) {
                    selectedContainerId = containerId;
                    selectedItem = item;
                    dragOffsetX = gridCoords[0] - item.getX();
                    dragOffsetY = gridCoords[1] - item.getY();
                    return true;
                }
            }
            
            displayX += containerSpacing;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button != 0 || selectedItem == null) return false;
        return true;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0 || selectedItem == null) return false;
        
        // Find which container the mouse is over
        int displayX = containerDisplayX + 10;
        int displayY = containerDisplayY + 30 - inventoryScrollOffset;
        int containerSpacing = 140;
        
        for (String containerId : inventoryManager.getAllContainers().keySet()) {
            GridContainer container = inventoryManager.getContainer(containerId);
            
            int[] gridCoords = GridRenderer.getGridCoordinates(container, displayX, displayY, (int) mouseX, (int) mouseY);
            if (gridCoords != null) {
                int targetX = gridCoords[0] - dragOffsetX;
                int targetY = gridCoords[1] - dragOffsetY;
                
                if (!selectedContainerId.equals(containerId)) {
                    inventoryManager.moveItem(selectedContainerId, containerId, 
                        getItemIdInContainer(selectedContainerId, selectedItem), targetX, targetY);
                } else {
                    selectedItem.setPosition(targetX, targetY);
                }
                
                selectedItem = null;
                return true;
            }
            
            displayY += containerSpacing;
        }
        
        selectedItem = null;
        return false;
    }
    
    private String getItemIdInContainer(String containerId, GridItem item) {
        GridContainer container = inventoryManager.getContainer(containerId);
        for (GridItem containerItem : container.getAllItems()) {
            if (containerItem == item) {
                return "item_" + item.hashCode();
            }
        }
        return "unknown";
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
