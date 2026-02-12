package top.vannesa.micronova.inventory.client;

import top.vannesa.micronova.inventory.InventoryManager;

/**
 * Client-side inventory cache.
 * Maintains a copy of the player's inventory state.
 */
public class ClientInventoryCache {
    private static InventoryManager playerInventory;

    public static void init() {
        if (playerInventory == null) {
            playerInventory = new InventoryManager();
        }
    }

    public static InventoryManager getInventory() {
        if (playerInventory == null) {
            init();
        }
        return playerInventory;
    }

    public static void setInventory(InventoryManager inventory) {
        playerInventory = inventory;
    }

    public static void reset() {
        playerInventory = new InventoryManager();
    }
}
