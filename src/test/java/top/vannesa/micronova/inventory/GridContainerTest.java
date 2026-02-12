package top.vannesa.micronova.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class GridContainerTest {
    
    private GridContainer container;
    
    @BeforeEach
    public void setUp() {
        container = new GridContainer(10, 10);
    }
    
    @Test
    public void testPlaceItemSuccess() {
        GridItem item = new GridItem(2, 3);
        assertTrue(container.tryPlace("item1", item, 0, 0));
        assertEquals(0, item.getX());
        assertEquals(0, item.getY());
    }
    
    @Test
    public void testCannotPlaceOutOfBounds() {
        GridItem item = new GridItem(5, 5);
        assertFalse(container.canPlace(item, 7, 0)); // 7 + 5 > 10
        assertFalse(container.canPlace(item, 0, 7)); // 7 + 5 > 10
    }
    
    @Test
    public void testCannotPlaceOverlapingItems() {
        GridItem item1 = new GridItem(3, 3);
        GridItem item2 = new GridItem(3, 3);
        
        assertTrue(container.tryPlace("item1", item1, 0, 0));
        assertFalse(container.tryPlace("item2", item2, 2, 1)); // Overlaps with item1
        assertTrue(container.tryPlace("item2", item2, 4, 0));  // No overlap
    }
    
    @Test
    public void testRemoveItem() {
        GridItem item = new GridItem(2, 2);
        container.tryPlace("item1", item, 0, 0);
        
        GridItem removed = container.remove("item1");
        assertNotNull(removed);
        assertNull(container.getItem("item1"));
    }
    
    @Test
    public void testGetItemAt() {
        GridItem item = new GridItem(2, 3);
        container.tryPlace("item1", item, 2, 2);
        
        assertNotNull(container.getItemAt(2, 2));
        assertNotNull(container.getItemAt(3, 3));
        assertNull(container.getItemAt(4, 2));
    }
    
    @Test
    public void testRotationAffectsPlacement() {
        GridItem item = new GridItem(1, 5);
        
        // Place without rotation (1x5)
        assertTrue(container.tryPlace("item1", item, 0, 0));
        assertFalse(container.canPlace(new GridItem(1, 5), 0, 6)); // Only 5 units left vertically
        
        // Rotate to 5x1
        item.toggleRotation();
        assertFalse(container.canPlace(new GridItem(5, 1), 6, 0)); // 6 + 5 > 10
    }
    
    @Test
    public void testItemCount() {
        assertEquals(0, container.getItemCount());
        
        container.tryPlace("item1", new GridItem(2, 2), 0, 0);
        assertEquals(1, container.getItemCount());
        
        container.tryPlace("item2", new GridItem(2, 2), 4, 0);
        assertEquals(2, container.getItemCount());
    }
}
