package top.vannesa.micronova.inventory;

import net.minecraft.item.Item;

/**
 * Configuration for a container item (like backpack, vest, etc).
 * Defines what size grid the container uses and which items can be stored in it.
 */
public class ContainerItemConfig {
    
    public static final ContainerItemConfig POCKETS = new ContainerItemConfig(
        "pockets", 2, 5, "Player pockets"
    );
    
    public static final ContainerItemConfig TACTICAL_VEST = new ContainerItemConfig(
        "tactical_vest", 8, 8, "Tactical chest rig"
    );
    
    public static final ContainerItemConfig TACTICAL_BACKPACK = new ContainerItemConfig(
        "tactical_backpack", 12, 20, "Large tactical backpack"
    );
    
    public static final ContainerItemConfig ASSAULT_PACK = new ContainerItemConfig(
        "assault_pack", 10, 15, "Medium assault pack"
    );
    
    public static final ContainerItemConfig SECURE_CONTAINER = new ContainerItemConfig(
        "secure_container", 4, 4, "Secure container (high priority)"
    );
    
    private final String id;
    private final int gridWidth;
    private final int gridHeight;
    private final String displayName;
    
    public ContainerItemConfig(String id, int gridWidth, int gridHeight, String displayName) {
        this.id = id;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.displayName = displayName;
    }
    
    public String getId() {
        return id;
    }
    
    public int getGridWidth() {
        return gridWidth;
    }
    
    public int getGridHeight() {
        return gridHeight;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public GridContainer createContainer() {
        return new GridContainer(gridWidth, gridHeight);
    }
}
