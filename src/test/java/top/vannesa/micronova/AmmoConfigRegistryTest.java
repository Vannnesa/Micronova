package top.vannesa.micronova;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.vannesa.micronova.weapon.AmmoConfig;
import top.vannesa.micronova.weapon.AmmoConfigRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the ammunition configuration registry.
 */
public class AmmoConfigRegistryTest {

    private AmmoConfigRegistry registry;

    @BeforeEach
    public void setup() {
        // Get singleton and reset to clean state
        registry = AmmoConfigRegistry.getInstance();
        registry.resetToDefaults();
    }

    @Test
    public void registryContainsDefaultAmmo() {
        // Should have at least the 5 default presets
        assertTrue(registry.size() >= 5);
        assertTrue(registry.has("9mm_fmj"));
        assertTrue(registry.has("5.56_m855a1"));
        assertTrue(registry.has("7.62x54r"));
    }

    @Test
    public void registryLookupByid() {
        AmmoConfig ammo = registry.get("9mm_fmj");
        assertNotNull(ammo);
        assertEquals("9x19mm Parabellum FMJ", ammo.displayName);
    }

    @Test
    public void registryReturnNullForMissingId() {
        AmmoConfig ammo = registry.get("nonexistent_ammo");
        assertNull(ammo);
    }

    @Test
    public void registryCanRegisterNewAmmo() {
        AmmoConfig custom = new AmmoConfig(
            "custom_round", "Custom Round", "Test custom ammo",
            500f, 10f, 0.005f, 100f, 30f, 50f, 100f,
            false, 0.1f
        );
        
        registry.register(custom);
        
        assertTrue(registry.has("custom_round"));
        AmmoConfig retrieved = registry.get("custom_round");
        assertNotNull(retrieved);
        assertEquals("custom_round", retrieved.id);
    }

    @Test
    public void registryCanOverwriteExisting() {
        AmmoConfig original = registry.get("9mm_fmj");
        float originalDamage = original.baseDamage;
        
        AmmoConfig modified = new AmmoConfig(
            "9mm_fmj", "Modified 9mm", "Edited",
            original.initialVelocity, original.mass, original.dragCoefficient,
            original.minPenetrationSpeed, original.penetrationValue,
            100f,  // different damage
            original.effectiveRange,
            false, 0f
        );
        
        registry.register(modified);
        
        AmmoConfig retrieved = registry.get("9mm_fmj");
        assertEquals(100f, retrieved.baseDamage, 0.01f);
        assertNotEquals(originalDamage, retrieved.baseDamage, 0.01f);
    }

    @Test
    public void registryRejectsNullConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(null);
        });
    }

    @Test
    public void registryRejectsConfigWithNullId() {
        AmmoConfig config = new AmmoConfig(
            null, "Test", "Test",
            375f, 8f, 0.005f, 100f, 26f, 40f, 50f,
            false, 0f
        );
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(config);
        });
    }

    @Test
    public void registryGetAllIds() {
        String[] ids = registry.getAllIds();
        assertTrue(ids.length >= 5);
        
        // Check that specific presets are in the list
        boolean has9mm = false;
        boolean has556 = false;
        for (String id : ids) {
            if (id.equals("9mm_fmj")) has9mm = true;
            if (id.equals("5.56_m855a1")) has556 = true;
        }
        assertTrue(has9mm && has556);
    }

    @Test
    public void registryGetAll() {
        AmmoConfig[] all = registry.getAll();
        assertTrue(all.length >= 5);
        
        // All should have non-null IDs
        for (AmmoConfig ammo : all) {
            assertNotNull(ammo.id);
            assertNotNull(ammo.displayName);
        }
    }

    @Test
    public void registryCanClear() {
        registry.clear();
        assertEquals(0, registry.size());
        assertFalse(registry.has("9mm_fmj"));
    }

    @Test
    public void registryResetToDefaults() {
        registry.clear();
        assertEquals(0, registry.size());
        
        registry.resetToDefaults();
        assertTrue(registry.size() >= 5);
        assertTrue(registry.has("9mm_fmj"));
    }

    @Test
    public void registryIsSingleton() {
        AmmoConfigRegistry registry1 = AmmoConfigRegistry.getInstance();
        AmmoConfigRegistry registry2 = AmmoConfigRegistry.getInstance();
        assertSame(registry1, registry2);
    }

    @Test
    public void registryToString() {
        String str = registry.toString();
        assertNotNull(str);
        assertTrue(str.contains("AmmoConfigRegistry"));
        assertTrue(str.contains("registered"));
    }

    @Test
    public void registryPistolVariants() {
        assertTrue(registry.has("9mm_fmj"));
        assertTrue(registry.has("9mm_hp"));
        
        AmmoConfig fmj = registry.get("9mm_fmj");
        AmmoConfig hp = registry.get("9mm_hp");
        
        assertNotNull(fmj);
        assertNotNull(hp);
        
        // HP should have lower penetration and higher damage
        assertTrue(hp.penetrationValue < fmj.penetrationValue);
        assertTrue(hp.baseDamage > fmj.baseDamage);
    }

    @Test
    public void registryRifleVariants() {
        assertTrue(registry.has("5.56_m855a1"));
        assertTrue(registry.has("5.56_m193"));
        
        AmmoConfig m855a1 = registry.get("5.56_m855a1");
        AmmoConfig m193 = registry.get("5.56_m193");
        
        assertNotNull(m855a1);
        assertNotNull(m193);
        
        // M855A1 is better for armor penetration
        assertTrue(m855a1.penetrationValue > m193.penetrationValue);
    }
}
