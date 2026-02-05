package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.ballistics.PenetrationCalculator;
import top.vannesa.micronova.ballistics.PenetrationCalculator.ArmorClass;

import static org.junit.jupiter.api.Assertions.*;

public class PenetrationTest {

    @Test
    public void armorClassProperties() {
        assertEquals(1, ArmorClass.CLASS_1.level);
        assertEquals(6, ArmorClass.CLASS_6.level);
        assertTrue(ArmorClass.CLASS_1.baseProtection < ArmorClass.CLASS_6.baseProtection);
    }

    @Test
    public void penetrationChanceLowPenetration() {
        // Low penetration ammo vs high armor = low chance
        float chance = PenetrationCalculator.calculatePenetrationChance(10f, ArmorClass.CLASS_6, 1.0f);
        assertTrue(chance >= 0 && chance <= 1f);
        assertTrue(chance < 0.3f, "Low pen ammo should have <30% chance vs class 6 armor");
    }

    @Test
    public void penetrationChanceHighPenetration() {
        // High penetration ammo vs low armor = high chance
        float chance = PenetrationCalculator.calculatePenetrationChance(80f, ArmorClass.CLASS_1, 1.0f);
        assertTrue(chance >= 0 && chance <= 1f);
        assertTrue(chance > 0.4f, "High pen ammo should have good chance vs class 1 armor");
    }

    @Test
    public void penetrationChanceDegradedArmor() {
        ArmorClass armor = ArmorClass.CLASS_4;
        
        // New armor
        float newChance = PenetrationCalculator.calculatePenetrationChance(30f, armor, 1.0f);
        
        // Degraded armor (50% durability)
        float degradedChance = PenetrationCalculator.calculatePenetrationChance(30f, armor, 0.5f);
        
        assertTrue(degradedChance > newChance, "Degraded armor should have higher penetration chance");
    }

    @Test
    public void damageAfterSuccessfulPenetration() {
        float baseDamage = 50f;
        ArmorClass armor = ArmorClass.CLASS_4;
        
        float damage = PenetrationCalculator.calculateDamageAfterArmor(baseDamage, armor, 0.8f, true);
        
        // Should be 60-80% of base damage
        assertTrue(damage > baseDamage * 0.6f, "Penetrated damage should be significant");
        assertTrue(damage < baseDamage, "Penetrated damage should be reduced");
    }

    @Test
    public void damageAfterFailedPenetration() {
        float baseDamage = 50f;
        ArmorClass armor = ArmorClass.CLASS_4;
        
        float damage = PenetrationCalculator.calculateDamageAfterArmor(baseDamage, armor, 0.2f, false);
        
        // Should be 10-30% of base damage (blunt damage)
        assertTrue(damage < baseDamage * 0.3f, "Blunt damage should be much lower");
        assertTrue(damage > 0, "Should still apply some damage");
    }

    @Test
    public void penetrationHigherChanceLessDamageReduction() {
        float baseDamage = 50f;
        ArmorClass armor = ArmorClass.CLASS_4;
        
        // High penetration chance = less damage reduction
        float highChance = PenetrationCalculator.calculateDamageAfterArmor(baseDamage, armor, 0.9f, true);
        
        // Low penetration chance = more damage reduction
        float lowChance = PenetrationCalculator.calculateDamageAfterArmor(baseDamage, armor, 0.2f, true);
        
        assertTrue(highChance > lowChance, "Higher penetration chance should result in more damage");
    }

    @Test
    public void velocityLossIncreaseWithArmorClass() {
        float initialVelocity = 400f;
        
        float loss1 = PenetrationCalculator.calculateVelocityLoss(initialVelocity, ArmorClass.CLASS_1);
        float loss6 = PenetrationCalculator.calculateVelocityLoss(initialVelocity, ArmorClass.CLASS_6);
        
        assertTrue(loss1 < loss6, "Stronger armor should cause more velocity loss");
        assertTrue(loss6 < initialVelocity, "Velocity loss should be less than initial velocity");
    }

    @Test
    public void armorDurabilityDamage() {
        float bulletPenetration = 40f;
        
        // Penetrated round
        float penetratedDamage = PenetrationCalculator.calculateArmorDurabilityDamage(
            bulletPenetration, ArmorClass.CLASS_4, true);
        
        // Failed penetration
        float bluntDamage = PenetrationCalculator.calculateArmorDurabilityDamage(
            bulletPenetration, ArmorClass.CLASS_4, false);
        
        assertTrue(bluntDamage > penetratedDamage, "Blunt impact should damage armor more");
    }

    @Test
    public void getArmorClassMapping() {
        assertEquals(ArmorClass.CLASS_1, PenetrationCalculator.getArmorClassFromMinecraftArmor("leather"));
        assertEquals(ArmorClass.CLASS_3, PenetrationCalculator.getArmorClassFromMinecraftArmor("iron"));
        assertEquals(ArmorClass.CLASS_5, PenetrationCalculator.getArmorClassFromMinecraftArmor("diamond"));
        assertEquals(ArmorClass.CLASS_6, PenetrationCalculator.getArmorClassFromMinecraftArmor("netherite"));
    }
}
