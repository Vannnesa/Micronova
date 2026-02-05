package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.combat.ArmorDamageCalculator;
import top.vannesa.micronova.health.ArmorComponent;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.HealthComponent;
import top.vannesa.micronova.weapon.AmmoConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test armor and damage integration.
 */
public class ArmorDamageCalculatorTest {

    @Test
    public void noArmorFullDamage() {
        HealthComponent health = new HealthComponent();
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        
        // No armor equipped
        ArmorDamageCalculator.DamageResult result = 
            ArmorDamageCalculator.calculateDamageWithArmor(ammo, health, BodyPart.CHEST, null);
        
        // Should deal full damage
        assertEquals(ammo.baseDamage, result.damage, 0.01f);
        assertFalse(result.penetrated);  // No armor to penetrate
    }

    @Test
    public void armorReducesDamage() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        
        ArmorDamageCalculator.DamageResult result = 
            ArmorDamageCalculator.calculateDamageWithArmor(ammo, health, BodyPart.CHEST, null);
        
        // Damage should be reduced (either penetrated with reduction or blocked)
        assertTrue(result.damage < ammo.baseDamage, 
            "Armor should reduce damage (result=" + result + ")");
    }

    @Test
    public void armorDamageRecordsPenetrationStatus() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        
        ArmorDamageCalculator.DamageResult result = 
            ArmorDamageCalculator.calculateDamageWithArmor(ammo, health, BodyPart.CHEST, null);
        
        // Should have status
        assertNotNull(result.armorStatus);
        assertTrue(result.armorStatus.equals("PENETRATED") || result.armorStatus.equals("BLOCKED"));
    }

    @Test
    public void armorDurabilityDamagedByShot() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        
        float durabilityBefore = armor.getArmorDurability(BodyPart.CHEST);
        
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        ArmorDamageCalculator.calculateDamageWithArmor(ammo, health, BodyPart.CHEST, null);
        
        float durabilityAfter = armor.getArmorDurability(BodyPart.CHEST);
        
        // Durability should be reduced
        assertTrue(durabilityAfter < durabilityBefore, "Armor durability should decrease from shot");
    }

    @Test
    public void degradedArmorHasHigherDamage() {
        HealthComponent health1 = new HealthComponent();
        ArmorComponent armor1 = health1.getArmorComponent();
        armor1.setArmor(BodyPart.CHEST, 15f);
        armor1.setArmorType(BodyPart.CHEST, "iron");
        // armor1 is at 100% durability
        
        HealthComponent health2 = new HealthComponent();
        ArmorComponent armor2 = health2.getArmorComponent();
        armor2.setArmor(BodyPart.CHEST, 15f);
        armor2.setArmorType(BodyPart.CHEST, "iron");
        armor2.damageArmor(BodyPart.CHEST, 0.8f);  // Degrade to 20%
        
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        
        // Run multiple times to average out randomness
        float totalDamage1 = 0, totalDamage2 = 0;
        int iterations = 100;
        
        for (int i = 0; i < iterations; i++) {
            HealthComponent h1 = new HealthComponent();
            ArmorComponent a1 = h1.getArmorComponent();
            a1.setArmor(BodyPart.CHEST, 15f);
            a1.setArmorType(BodyPart.CHEST, "iron");
            
            HealthComponent h2 = new HealthComponent();
            ArmorComponent a2 = h2.getArmorComponent();
            a2.setArmor(BodyPart.CHEST, 15f);
            a2.setArmorType(BodyPart.CHEST, "iron");
            a2.damageArmor(BodyPart.CHEST, 0.8f);
            
            totalDamage1 += ArmorDamageCalculator.calculateDamageWithArmor(ammo, h1, BodyPart.CHEST, null).damage;
            totalDamage2 += ArmorDamageCalculator.calculateDamageWithArmor(ammo, h2, BodyPart.CHEST, null).damage;
        }
        
        float avgDamage1 = totalDamage1 / iterations;
        float avgDamage2 = totalDamage2 / iterations;
        
        // Degraded armor should allow more damage on average (higher penetration chance)
        assertTrue(avgDamage2 > avgDamage1,
            String.format("Degraded armor should have higher average damage (new=%.1f, degraded=%.1f)",
                avgDamage1, avgDamage2));
    }

    @Test
    public void isArmorCriticalStatus() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        
        // New armor not critical
        assertFalse(ArmorDamageCalculator.isArmorCritical(BodyPart.CHEST, health));
        
        // Damage to 25% durability
        armor.damageArmor(BodyPart.CHEST, 0.75f);
        assertTrue(ArmorDamageCalculator.isArmorCritical(BodyPart.CHEST, health));
    }

    @Test
    public void isArmorDestroyed() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        
        // Not destroyed
        assertFalse(ArmorDamageCalculator.isArmorDestroyed(BodyPart.CHEST, health));
        
        // Destroy armor
        armor.damageArmor(BodyPart.CHEST, 1.0f);
        assertTrue(ArmorDamageCalculator.isArmorDestroyed(BodyPart.CHEST, health));
    }

    @Test
    public void velocityLossFromArmor() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "diamond");  // CLASS_5
        
        float velocity = 500f;
        float loss = ArmorDamageCalculator.calculateVelocityLossFromArmor(
            velocity, BodyPart.CHEST, health);
        
        // Should lose some velocity
        assertTrue(loss > 0 && loss < velocity, "Velocity loss should be positive and less than initial");
    }

    @Test
    public void rifleVsLightArmor() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        armor.setArmor(BodyPart.CHEST, 4f);  // Leather
        armor.setArmorType(BodyPart.CHEST, "leather");
        
        AmmoConfig ammo = AmmoConfig.rifle556M855A1();
        
        // Calculate single shot result
        ArmorDamageCalculator.DamageResult result = 
            ArmorDamageCalculator.calculateDamageWithArmor(ammo, health, BodyPart.CHEST, null);
        
        // Rifle should deal reasonable damage through light armor (either penetrate or partial block)
        // Most importantly, damage should be > 0
        assertTrue(result.damage > 0, "Rifle should deal some damage through light armor");
        
        // Armor durability should be affected
        assertTrue(armor.getArmorDurability(BodyPart.CHEST) < 1.0f,
            "Armor durability should decrease from rifle shot");
    }

    @Test
    public void damageResultToString() {
        ArmorDamageCalculator.DamageResult result = 
            new ArmorDamageCalculator.DamageResult(25.5f, true, "PENETRATED");
        
        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("25.5"));
        assertTrue(str.contains("PENETRATED"));
    }

    @Test
    public void multipleBodyPartArmorIndependent() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        
        armor.setArmor(BodyPart.HEAD, 5f);
        armor.setArmorType(BodyPart.HEAD, "leather");
        
        // Each body part should have independent durability
        float chestDurBefore = armor.getArmorDurability(BodyPart.CHEST);
        float headDurBefore = armor.getArmorDurability(BodyPart.HEAD);
        
        AmmoConfig ammo = AmmoConfig.pistol9mmFMJ();
        
        // Damage chest
        ArmorDamageCalculator.calculateDamageWithArmor(ammo, health, BodyPart.CHEST, null);
        
        // Chest durability should decrease, head should not change
        float chestDurAfter = armor.getArmorDurability(BodyPart.CHEST);
        float headDurAfter = armor.getArmorDurability(BodyPart.HEAD);
        
        assertTrue(chestDurAfter < chestDurBefore);
        assertEquals(headDurBefore, headDurAfter, 0.01f);
    }
}
