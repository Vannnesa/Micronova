package top.vannesa.micronova.ui.inventory;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import top.vannesa.micronova.inventory.GridContainer;
import top.vannesa.micronova.inventory.GridItem;
import top.vannesa.micronova.inventory.InventoryManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom inventory screen replacing Minecraft's default inventory.
 * Displays grid-based containers with drag-and-drop support.
 */
public class CustomInventoryScreen extends Screen {
    
    private final InventoryManager inventoryManager;
    private int containerDisplayStartX = 20;
    private int containerDisplayStartY = 20;
    private int containerSpacingX = 300;
    
    private String selectedContainerId;
    private GridItem selectedItem;
    private int dragOffsetX;
    private int dragOffsetY;
    
    public CustomInventoryScreen(InventoryManager inventoryManager) {
        super(Text.literal("Inventory"));
        this.inventoryManager = inventoryManager;
        this.selectedContainerId = "backpack"; // Default selection
    }
    
    @Override
    protected void init() {
        super.init();
    }
    
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx);
        
        // Draw all containers side by side
        int displayX = containerDisplayStartX;
        int displayY = containerDisplayStartY;
        
        for (String containerId : inventoryManager.getAllContainers().keySet()) {
            GridContainer container = inventoryManager.getContainer(containerId);
            
            // Draw container label
            ctx.drawText(
                this.textRenderer,
                Text.literal(containerId.replace('_', ' ')),
                displayX, displayY - 15,
                0xFFFFFF, false
            );
            
            // Draw container grid
            GridRenderer.render(ctx, container, displayX, displayY, -1, -1);
            
            displayX += containerSpacingX;
        }
        
        super.render(ctx, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false; // Only handle left click
        
        // Check which container was clicked
        int displayX = containerDisplayStartX;
        int displayY = containerDisplayStartY;
        
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
            
            displayX += containerSpacingX;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button != 0 || selectedItem == null) return false;
        
        // Update selection based on current mouse position
        // This could be extended for visual feedback during drag
        
        return true;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0 || selectedItem == null) return false;
        
        // Find which container the mouse is over
        int displayX = containerDisplayStartX;
        int displayY = containerDisplayStartY;
        
        for (String containerId : inventoryManager.getAllContainers().keySet()) {
            GridContainer container = inventoryManager.getContainer(containerId);
            
            int[] gridCoords = GridRenderer.getGridCoordinates(container, displayX, displayY, (int) mouseX, (int) mouseY);
            if (gridCoords != null) {
                // Try to place the item
                int targetX = gridCoords[0] - dragOffsetX;
                int targetY = gridCoords[1] - dragOffsetY;
                
                if (!selectedContainerId.equals(containerId)) {
                    // Moving to a different container
                    inventoryManager.moveItem(selectedContainerId, containerId, 
                        getItemIdInContainer(selectedContainerId, selectedItem), targetX, targetY);
                } else {
                    // Moving within the same container
                    selectedItem.setPosition(targetX, targetY);
                }
                
                selectedItem = null;
                return true;
            }
            
            displayX += containerSpacingX;
        }
        
        selectedItem = null;
        return false;
    }
    
    /**
     * Find the item ID for a given GridItem in a container.
     */
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
