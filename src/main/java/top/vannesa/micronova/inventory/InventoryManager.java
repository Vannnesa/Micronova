package top.vannesa.micronova.inventory;

import java.util.*;

/**
 * Manages the player's inventory with multiple containers.
 * Each container has its own grid system.
 */
public class InventoryManager {
    private final Map<String, GridContainer> containers = new HashMap<>();
    private String selectedContainerId;
    
    public InventoryManager() {
        // Initialize with default containers
        containers.put("pockets", new GridContainer(2, 5));      // Small pocket
        containers.put("tactical_vest", new GridContainer(8, 8)); // Tactical vest
        containers.put("backpack", new GridContainer(12, 20));    // Main backpack
    }
    
    /**
     * Register a new container.
     */
    public void registerContainer(String id, GridContainer container) {
        containers.put(id, container);
    }
    
    /**
     * Get a container by ID.
     */
    public GridContainer getContainer(String id) {
        return containers.get(id);
    }
    
    /**
     * Get all containers.
     */
    public Map<String, GridContainer> getAllContainers() {
        return new HashMap<>(containers);
    }
    
    /**
     * Try to place an item in a specific container.
     * @return true if placement succeeded
     */
    public boolean placeItem(String containerId, String itemId, GridItem item, int x, int y) {
        GridContainer container = containers.get(containerId);
        if (container == null) {
            return false;
        }
        return container.tryPlace(itemId, item, x, y);
    }
    
    /**
     * Try to move an item from one container to another.
     * @return true if move succeeded
     */
    public boolean moveItem(String fromContainerId, String toContainerId, String itemId, int newX, int newY) {
        GridContainer fromContainer = containers.get(fromContainerId);
        GridContainer toContainer = containers.get(toContainerId);
        
        if (fromContainer == null || toContainer == null) {
            return false;
        }
        
        GridItem item = fromContainer.getItem(itemId);
        if (item == null) {
            return false;
        }
        
        // Try to place in new container
        if (toContainer.tryPlace(itemId, item, newX, newY)) {
            // Remove from old container
            fromContainer.remove(itemId);
            return true;
        }
        
        return false;
    }
    
    /**
     * Remove an item from a container.
     */
    public GridItem removeItem(String containerId, String itemId) {
        GridContainer container = containers.get(containerId);
        if (container == null) {
            return null;
        }
        return container.remove(itemId);
    }
    
    /**
     * Find which container holds a specific item.
     * @return container ID or null if not found
     */
    public String findContainerForItem(String itemId) {
        for (Map.Entry<String, GridContainer> entry : containers.entrySet()) {
            if (entry.getValue().getItem(itemId) != null) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Get the selected container ID.
     */
    public String getSelectedContainerId() {
        return selectedContainerId;
    }
    
    /**
     * Set the selected container.
     */
    public void setSelectedContainer(String containerId) {
        if (containers.containsKey(containerId)) {
            this.selectedContainerId = containerId;
        }
    }
    
    /**
     * Clear all containers.
     */
    public void clear() {
        for (GridContainer container : containers.values()) {
            container.clear();
        }
    }
}
