package top.vannesa.micronova.inventory;

/**
 * Example demonstrating how to use the grid-based inventory system.
 * This shows how to:
 * 1. Create and manage containers
 * 2. Place and move items
 * 3. Handle rotation and collision detection
 */
public class InventoryExample {
    
    public static void main(String[] args) {
        // Create an inventory manager with default containers
        InventoryManager manager = new InventoryManager();
        
        // Example 1: Place some items in the backpack
        GridItem rifle = new GridItem(3, 10); // 3 cells wide, 10 cells tall
        GridItem ammo = new GridItem(2, 2);
        GridItem medkit = new GridItem(2, 3);
        
        manager.placeItem("backpack", "rifle", rifle, 0, 0);
        manager.placeItem("backpack", "ammo", ammo, 4, 0);
        manager.placeItem("backpack", "medkit", medkit, 0, 10);
        
        // Example 2: Try to place item in pockets (limited space)
        GridItem lighter = new GridItem(1, 1);
        if (manager.placeItem("pockets", "lighter", lighter, 0, 0)) {
            System.out.println("✓ Lighter placed in pockets");
        }
        
        // Example 3: Move ammo to tactical vest
        if (manager.moveItem("backpack", "tactical_vest", "ammo", 0, 0)) {
            System.out.println("✓ Ammo moved to tactical vest");
        }
        
        // Example 4: Rotate an item
        rifle.toggleRotation();
        System.out.println("✓ Rifle rotated to " + 
            (rifle.isRotated() ? (rifle.getHeight() + "x" + rifle.getWidth()) : (rifle.getWidth() + "x" + rifle.getHeight())));
        
        // Example 5: Find container for item
        String containerWithRifle = manager.findContainerForItem("rifle");
        System.out.println("✓ Rifle is in: " + containerWithRifle);
        
        // Example 6: Display container stats
        for (String containerId : manager.getAllContainers().keySet()) {
            GridContainer container = manager.getContainer(containerId);
            System.out.println("Container: " + containerId + 
                " (" + container.getWidth() + "x" + container.getHeight() + 
                "), items: " + container.getItemCount());
        }
    }
}
