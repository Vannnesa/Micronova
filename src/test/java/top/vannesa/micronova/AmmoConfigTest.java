package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.weapon.AmmoConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test ammunition configuration system.
 */
public class AmmoConfigTest {

    @Test
    public void pistol9mmFMJBasicProperties() {
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        
        assertEquals("9mm_fmj", ammo.id);
        assertEquals(375f, ammo.initialVelocity, 0.01f);
        assertEquals(8.0f, ammo.mass, 0.01f);
        assertEquals(26f, ammo.penetrationValue, 0.01f);
        assertEquals(40f, ammo.baseDamage, 0.01f);
        assertEquals(50f, ammo.effectiveRange, 0.01f);
        assertFalse(ammo.tracerRound);
        assertEquals(0f, ammo.armorPenetrationBonus, 0.01f);
    }

    @Test
    public void rifle556M855A1Properties() {
        AmmoConfig ammo = AmmoConfig.rifle556M855A1();
        
        assertEquals("5.56_m855a1", ammo.id);
        assertEquals(940f, ammo.initialVelocity, 0.01f);
        assertEquals(4.0f, ammo.mass, 0.01f);
        assertEquals(80f, ammo.penetrationValue, 0.01f);
        assertEquals(60f, ammo.baseDamage, 0.01f);
        assertEquals(300f, ammo.effectiveRange, 0.01f);
        assertEquals(0.1f, ammo.armorPenetrationBonus, 0.01f);
    }

    @Test
    public void ammoConfigArmorPenetrationBonusIsNormalized() {
        // Create config with out-of-range bonus
        AmmoConfig ammo = new AmmoConfig(
            "test", "Test", "Test ammo",
            375f, 8f, 0.005f, 100f, 26f, 40f, 50f,
            false, 1.5f  // bonus > 1.0 should be clamped
        );
        
        assertEquals(1.0f, ammo.armorPenetrationBonus, 0.01f);
    }

    @Test
    public void ammoConfigNegativeBonusIsAllowed() {
        AmmoConfig ammo = new AmmoConfig(
            "test", "Test", "Test ammo",
            375f, 8f, 0.005f, 100f, 26f, 40f, 50f,
            false, -0.5f  // negative should be allowed (penalty)
        );
        
        assertEquals(-0.5f, ammo.armorPenetrationBonus, 0.01f);
    }

    @Test
    public void ammoConfigBonusExtremeValuesAreClipped() {
        AmmoConfig ammoHigh = new AmmoConfig(
            "test", "Test", "Test ammo",
            375f, 8f, 0.005f, 100f, 26f, 40f, 50f,
            false, 5.0f  // should be clipped to 1.0
        );
        assertEquals(1.0f, ammoHigh.armorPenetrationBonus, 0.01f);
        
        AmmoConfig ammoLow = new AmmoConfig(
            "test", "Test", "Test ammo",
            375f, 8f, 0.005f, 100f, 26f, 40f, 50f,
            false, -2.0f  // should be clipped to -1.0
        );
        assertEquals(-1.0f, ammoLow.armorPenetrationBonus, 0.01f);
    }

    @Test
    public void pistol9mmHPVariant() {
        AmmoConfig hp = AmmoConfig.pistol9mmHP();
        AmmoConfig fmj = AmmoConfig.pistol9mmFMJ();
        
        // HP has lower penetration
        assertTrue(hp.penetrationValue < fmj.penetrationValue);
        
        // HP has higher damage
        assertTrue(hp.baseDamage > fmj.baseDamage);
        
        // HP has penetration penalty
        assertEquals(-0.2f, hp.armorPenetrationBonus, 0.01f);
    }

    @Test
    public void rifle556Variants() {
        AmmoConfig m855a1 = AmmoConfig.rifle556M855A1();
        AmmoConfig m193 = AmmoConfig.rifle556M193();
        
        // M855A1 is heavier and better armor penetration
        assertTrue(m855a1.mass > m193.mass);
        assertTrue(m855a1.penetrationValue > m193.penetrationValue);
        
        // M193 has more drag (lighter, worse aerodynamics)
        assertTrue(m193.dragCoefficient > m855a1.dragCoefficient);
    }

    @Test
    public void toBallistics() {
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        var ballistics = ammo.toBallistics();
        
        assertEquals(ammo.initialVelocity, ballistics.initialVelocity, 0.01f);
        assertEquals(ammo.mass, ballistics.mass, 0.01f);
        assertEquals(ammo.dragCoefficient, ballistics.dragCoefficient, 0.0001f);
        assertEquals(ammo.penetrationValue, ballistics.penetrationValue, 0.01f);
        assertEquals(ammo.baseDamage, ballistics.baseDamage, 0.01f);
    }

    @Test
    public void ammoConfigToString() {
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        String str = ammo.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("9x19mm"));
        assertTrue(str.contains("375"));
        assertTrue(str.contains("9mm_fmj"));
    }

    @Test
    public void rifle762x54RProperties() {
        AmmoConfig ammo = AmmoConfig.rifle762x54R();
        
        assertEquals("7.62x54r", ammo.id);
        assertEquals(865f, ammo.initialVelocity, 0.01f);
        assertEquals(9.6f, ammo.mass, 0.01f);
        assertEquals(85f, ammo.baseDamage, 0.01f);
        assertEquals(400f, ammo.effectiveRange, 0.01f);
    }

    @Test
    public void tracerRoundProperty() {
        AmmoConfig normal = AmmoConfig.pistol9mmFMJ();
        assertFalse(normal.tracerRound);
        
        AmmoConfig tracer = new AmmoConfig(
            "tracer", "Tracer", "Tracer round",
            375f, 8f, 0.005f, 100f, 26f, 40f, 50f,
            true,  // tracerRound = true
            0f
        );
        assertTrue(tracer.tracerRound);
    }

    @Test
    public void allPresetsHaveUniqueIds() {
        var presets = new AmmoConfig[]{
            AmmoConfig.pistol9mmFMJ(),
            AmmoConfig.pistol9mmHP(),
            AmmoConfig.rifle556M855A1(),
            AmmoConfig.rifle556M193(),
            AmmoConfig.rifle762x54R()
        };
        
        for (int i = 0; i < presets.length; i++) {
            for (int j = i + 1; j < presets.length; j++) {
                assertNotEquals(presets[i].id, presets[j].id,
                    "Preset " + i + " and " + j + " have duplicate IDs");
            }
        }
    }

    @Test
    public void allPresetsHaveValidProperties() {
        var presets = new AmmoConfig[]{
            AmmoConfig.pistol9mmFMJ(),
            AmmoConfig.pistol9mmHP(),
            AmmoConfig.rifle556M855A1(),
            AmmoConfig.rifle556M193(),
            AmmoConfig.rifle762x54R()
        };
        
        for (AmmoConfig preset : presets) {
            assertTrue(preset.initialVelocity > 0, preset.id + ": velocity <= 0");
            assertTrue(preset.mass > 0, preset.id + ": mass <= 0");
            assertTrue(preset.baseDamage > 0, preset.id + ": damage <= 0");
            assertTrue(preset.penetrationValue >= 0, preset.id + ": penetration < 0");
            assertTrue(preset.armorPenetrationBonus >= -1 && preset.armorPenetrationBonus <= 1,
                preset.id + ": bonus out of range [-1,1]");
        }
    }
}
