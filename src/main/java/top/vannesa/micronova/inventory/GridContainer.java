package top.vannesa.micronova.inventory;

import java.util.*;

/**
 * Represents a 2D grid-based container (like a backpack) that can hold items.
 * Grid coordinates: (x, y) where x is width, y is height.
 */
public class GridContainer {
    private final int width;
    private final int height;
    private final Map<String, GridItem> items = new HashMap<>();
    
    public GridContainer(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Try to place an item at the specified position.
     * @return true if placement succeeded
     */
    public boolean tryPlace(String itemId, GridItem item, int x, int y) {
        if (!canPlace(item, x, y)) {
            return false;
        }
        
        items.put(itemId, item);
        item.setPosition(x, y);
        return true;
    }
    
    /**
     * Check if an item can be placed at the given position.
     */
    public boolean canPlace(GridItem item, int x, int y) {
        int itemWidth = item.isRotated() ? item.getHeight() : item.getWidth();
        int itemHeight = item.isRotated() ? item.getWidth() : item.getHeight();
        
        // Check bounds
        if (x < 0 || y < 0 || x + itemWidth > width || y + itemHeight > height) {
            return false;
        }
        
        // Check for overlap with existing items
        for (GridItem existing : items.values()) {
            if (overlaps(item, x, y, existing)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if two items overlap in grid space.
     */
    private boolean overlaps(GridItem item1, int x1, int y1, GridItem item2) {
        int w1 = item1.isRotated() ? item1.getHeight() : item1.getWidth();
        int h1 = item1.isRotated() ? item1.getWidth() : item1.getHeight();
        
        int x2 = item2.getX();
        int y2 = item2.getY();
        int w2 = item2.isRotated() ? item2.getHeight() : item2.getWidth();
        int h2 = item2.isRotated() ? item2.getWidth() : item2.getHeight();
        
        return !(x1 + w1 <= x2 || x2 + w2 <= x1 || y1 + h1 <= y2 || y2 + h2 <= y1);
    }
    
    /**
     * Remove an item from the container.
     */
    public GridItem remove(String itemId) {
        return items.remove(itemId);
    }
    
    /**
     * Get an item by its ID.
     */
    public GridItem getItem(String itemId) {
        return items.get(itemId);
    }
    
    /**
     * Get the item at the specified grid position (if any).
     */
    public GridItem getItemAt(int x, int y) {
        for (GridItem item : items.values()) {
            int itemWidth = item.isRotated() ? item.getHeight() : item.getWidth();
            int itemHeight = item.isRotated() ? item.getWidth() : item.getHeight();
            
            if (x >= item.getX() && x < item.getX() + itemWidth &&
                y >= item.getY() && y < item.getY() + itemHeight) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Get all items in this container.
     */
    public Collection<GridItem> getAllItems() {
        return items.values();
    }
    
    /**
     * Clear all items from the container.
     */
    public void clear() {
        items.clear();
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getItemCount() {
        return items.size();
    }
}
