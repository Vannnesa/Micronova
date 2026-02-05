package top.vannesa.micronova.weapon;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for ammunition configurations.
 * Manages caching and lookup of ammo presets and custom configurations.
 */
public class AmmoConfigRegistry {

    private static final AmmoConfigRegistry INSTANCE = new AmmoConfigRegistry();
    private final Map<String, AmmoConfig> ammoCache = new HashMap<>();

    private AmmoConfigRegistry() {
        // Load default presets
        registerDefaults();
    }

    /**
     * Get the global singleton instance.
     */
    public static AmmoConfigRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Register all default ammunition presets.
     */
    private void registerDefaults() {
        register(AmmoConfig.pistol9mmFMJ());
        register(AmmoConfig.pistol9mmHP());
        register(AmmoConfig.rifle556M855A1());
        register(AmmoConfig.rifle556M193());
        register(AmmoConfig.rifle762x54R());
    }

    /**
     * Register a new ammo configuration.
     * Overwrites existing entry with same ID.
     */
    public void register(AmmoConfig config) {
        if (config == null || config.id == null) {
            throw new IllegalArgumentException("AmmoConfig must have non-null id");
        }
        ammoCache.put(config.id, config);
    }

    /**
     * Get ammo configuration by ID.
     * Returns null if not found.
     */
    public AmmoConfig get(String id) {
        return ammoCache.get(id);
    }

    /**
     * Check if an ammo ID is registered.
     */
    public boolean has(String id) {
        return ammoCache.containsKey(id);
    }

    /**
     * Get all registered ammo IDs.
     */
    public String[] getAllIds() {
        return ammoCache.keySet().toArray(new String[0]);
    }

    /**
     * Get all registered ammo configurations.
     */
    public AmmoConfig[] getAll() {
        return ammoCache.values().toArray(new AmmoConfig[0]);
    }

    /**
     * Get the number of registered ammo types.
     */
    public int size() {
        return ammoCache.size();
    }

    /**
     * Clear the registry (for testing).
     */
    public void clear() {
        ammoCache.clear();
    }

    /**
     * Reset to defaults (for testing/reloading).
     */
    public void resetToDefaults() {
        clear();
        registerDefaults();
    }

    @Override
    public String toString() {
        return String.format("AmmoConfigRegistry(%d registered ammo types)", size());
    }
}
