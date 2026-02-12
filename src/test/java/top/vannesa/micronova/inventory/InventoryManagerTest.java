package top.vannesa.micronova.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryManagerTest {
    
    private InventoryManager manager;
    
    @BeforeEach
    public void setUp() {
        manager = new InventoryManager();
    }
    
    @Test
    public void testDefaultContainersCreated() {
        assertNotNull(manager.getContainer("pockets"));
        assertNotNull(manager.getContainer("tactical_vest"));
        assertNotNull(manager.getContainer("backpack"));
    }
    
    @Test
    public void testRegisterCustomContainer() {
        GridContainer custom = new GridContainer(5, 5);
        manager.registerContainer("custom", custom);
        
        assertNotNull(manager.getContainer("custom"));
        assertEquals(custom, manager.getContainer("custom"));
    }
    
    @Test
    public void testPlaceItemInContainer() {
        GridItem item = new GridItem(2, 2);
        assertTrue(manager.placeItem("pockets", "item1", item, 0, 0));
        
        GridContainer pockets = manager.getContainer("pockets");
        assertNotNull(pockets.getItem("item1"));
    }
    
    @Test
    public void testMoveItemBetweenContainers() {
        GridItem item = new GridItem(2, 2);
        manager.placeItem("pockets", "item1", item, 0, 0);
        
        // Move to tactical vest
        assertTrue(manager.moveItem("pockets", "tactical_vest", "item1", 0, 0));
        
        assertNull(manager.getContainer("pockets").getItem("item1"));
        assertNotNull(manager.getContainer("tactical_vest").getItem("item1"));
    }
    
    @Test
    public void testFindContainerForItem() {
        GridItem item = new GridItem(2, 2);
        manager.placeItem("backpack", "item1", item, 0, 0);
        
        assertEquals("backpack", manager.findContainerForItem("item1"));
        assertNull(manager.findContainerForItem("nonexistent"));
    }
    
    @Test
    public void testRemoveItem() {
        GridItem item = new GridItem(2, 2);
        manager.placeItem("pockets", "item1", item, 0, 0);
        
        GridItem removed = manager.removeItem("pockets", "item1");
        assertNotNull(removed);
        assertNull(manager.findContainerForItem("item1"));
    }
    
    @Test
    public void testSelectedContainer() {
        manager.setSelectedContainer("backpack");
        assertEquals("backpack", manager.getSelectedContainerId());
        
        manager.setSelectedContainer("invalid");
        assertEquals("backpack", manager.getSelectedContainerId()); // Should not change
    }
}
