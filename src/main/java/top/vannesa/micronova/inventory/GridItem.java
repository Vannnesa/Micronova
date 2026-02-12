package top.vannesa.micronova.inventory;

/**
 * Represents an item placed in a grid container.
 * Stores the item's dimensions and position within the grid.
 */
public class GridItem {
    private final int width;
    private final int height;
    private int x;
    private int y;
    private boolean rotated;
    
    /**
     * Create a new grid item with the specified dimensions.
     * @param width item width in grid units
     * @param height item height in grid units
     */
    public GridItem(int width, int height) {
        this.width = width;
        this.height = height;
        this.x = 0;
        this.y = 0;
        this.rotated = false;
    }
    
    /**
     * Set the position of the item in the grid.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Toggle rotation of the item (swaps width and height).
     */
    public void toggleRotation() {
        this.rotated = !rotated;
    }
    
    /**
     * Set rotation state.
     */
    public void setRotated(boolean rotated) {
        this.rotated = rotated;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public boolean isRotated() {
        return rotated;
    }
    
    /**
     * Get the effective width considering rotation.
     */
    public int getEffectiveWidth() {
        return rotated ? height : width;
    }
    
    /**
     * Get the effective height considering rotation.
     */
    public int getEffectiveHeight() {
        return rotated ? width : height;
    }
}
