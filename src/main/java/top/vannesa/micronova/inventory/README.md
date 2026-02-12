# Tarkov-Style Grid Inventory System

This module implements a Tarkov-inspired grid-based inventory system for Micronova, replacing Minecraft's default cubic inventory.

## Features

### Core System
- **Grid-based Containers**: Each container has a 2D grid where items occupy space
- **Item Rotation**: Items can be rotated to optimize space usage
- **Collision Detection**: Prevents overlapping items in the same container
- **Multi-Container Support**: Multiple containers (pockets, vest, backpack, etc.)
- **Drag & Drop**: Intuitive mouse-based item movement between containers

### Default Containers
| Container | Size | Purpose |
|-----------|------|---------|
| Pockets | 2x5 (10 cells) | Quick access, limited space |
| Tactical Vest | 8x8 (64 cells) | Chest equipment |
| Backpack | 12x20 (240 cells) | Main storage |
| Custom | Configurable | Extended capacity items |

### Planned Features (Phase 2)
- Network synchronization with server
- Nested containers (backpack within backpack)
- Container locking and security
- Item weight and encumbrance system
- Container restrictions (ammo boxes only hold ammo, etc.)

## Architecture

### Data Structures (Server & Shared)

**GridContainer**
- Manages a 2D grid of cells
- Handles collision detection and item placement
- Methods: `tryPlace()`, `canPlace()`, `getItemAt()`, `remove()`

**GridItem**
- Represents an item in the grid
- Stores dimensions (width x height) and position
- Supports rotation: `toggleRotation()`, `isRotated()`

**InventoryManager**
- Manages all containers for a player
- Handles inter-container item movement
- Tracks selected container
- Methods: `placeItem()`, `moveItem()`, `removeItem()`, `findContainerForItem()`

**ContainerItemConfig**
- Defines container templates (pockets, backpack, etc.)
- Pre-configured: POCKETS, TACTICAL_VEST, TACTICAL_BACKPACK, ASSAULT_PACK, SECURE_CONTAINER

### UI Components (Client)

**GridRenderer**
- Renders grid cells and items
- Handles coordinate conversion (screen -> grid)
- Visual feedback for selected cells
- Methods: `render()`, `getGridCoordinates()`

**CustomInventoryScreen**
- Replaces Minecraft's default inventory screen
- Displays all containers side by side
- Handles drag & drop interactions
- Methods: `mouseClicked()`, `mouseDragged()`, `mouseReleased()`

## Usage Example

```java
// Create manager with default containers
InventoryManager manager = new InventoryManager();

// Place an item (rifle: 3 wide x 10 tall)
GridItem rifle = new GridItem(3, 10);
manager.placeItem("backpack", "rifle", rifle, 0, 0);

// Rotate if needed
rifle.toggleRotation();

// Move between containers
manager.moveItem("backpack", "tactical_vest", "rifle", 0, 0);

// Find where an item is stored
String container = manager.findContainerForItem("rifle");

// Remove item
GridItem removed = manager.removeItem("backpack", "rifle");
```

## Configuration

### Creating Custom Containers

```java
// Define a custom container config
ContainerItemConfig customBag = new ContainerItemConfig(
    "custom_bag",    // id
    8,              // width in cells
    12,             // height in cells
    "Custom Storage" // display name
);

// Create the container
GridContainer container = customBag.createContainer();

// Register with manager
manager.registerContainer("custom_bag", container);
```

### Defining Item Dimensions

Common item sizes (in grid cells):
- **Small ammo magazine**: 1x2
- **Rifle**: 2x8 to 4x12 (depending on type)
- **Pistol**: 1x3
- **First aid kit**: 2x3
- **Helmet**: 2x2
- **Body armor**: 3x4

## Testing

The system includes comprehensive unit tests:

- `GridContainerTest` (8 test cases)
  - Placement success/failure
  - Boundary checking
  - Overlap detection
  - Rotation effects
  
- `InventoryManagerTest` (7 test cases)
  - Container creation and registration
  - Item placement and movement
  - Inter-container transfers
  - Container lookup

Run tests with:
```bash
./gradlew test
```

## File Structure

```
src/main/java/top/vannesa/micronova/inventory/
├── GridContainer.java           (core grid logic)
├── GridItem.java               (item representation)
├── InventoryManager.java       (container management)
├── ContainerItemConfig.java    (predefined configs)
└── InventoryExample.java       (usage examples)

src/client/java/top/vannesa/micronova/ui/inventory/
├── GridRenderer.java           (grid visualization)
└── CustomInventoryScreen.java  (main UI screen)

src/test/java/top/vannesa/micronova/inventory/
├── GridContainerTest.java
└── InventoryManagerTest.java
```

## Integration Points

### For Network Sync (Phase 2)
1. Create `InventorySyncS2CPacket` similar to `HealthSyncS2CPacket`
2. Serialize `InventoryManager` state on server
3. Deserialize on client and sync with `ClientInventoryCache`
4. Hook into player tick events for sync triggers

### For Item System (Phase 3)
1. Extend Minecraft's `Item` class with dimension metadata
2. Load item configs from JSON
3. Validate placements against item restrictions
4. Implement drop/pickup mechanics

## Performance Considerations

- **Collision Detection**: O(n) where n = items in container
- **Item Lookup**: O(n) with hashmap fallback for optimization
- **Rendering**: Only renders visible containers (configurable via screen size)
- **Memory**: Static containers, items are references (minimal overhead)

## Future Enhancements

1. **Container Nesting**: Allow items to contain other containers
2. **Weight System**: Track total weight and apply movement penalties
3. **Persistence**: Save/load inventory state to disk
4. **Sorting**: Auto-arrange items to optimize space
5. **Filters**: Container-specific item type restrictions
6. **Quick Slots**: Favorite containers for rapid access
7. **Animations**: Smooth item movement between containers
8. **Sounds**: Item pickup/drop audio feedback
