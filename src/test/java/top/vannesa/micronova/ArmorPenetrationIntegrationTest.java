package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.ballistics.Ballistics;
import top.vannesa.micronova.ballistics.PenetrationCalculator;
import top.vannesa.micronova.health.ArmorComponent;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.HealthComponent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test full integration between armor, penetration, and health systems.
 */
public class ArmorPenetrationIntegrationTest {

    @Test
    public void armorClassMappingFromType() {
        assertEquals(PenetrationCalculator.ArmorClass.CLASS_1, 
            PenetrationCalculator.getArmorClassFromMinecraftArmor("leather"));
        assertEquals(PenetrationCalculator.ArmorClass.CLASS_3, 
            PenetrationCalculator.getArmorClassFromMinecraftArmor("iron"));
        assertEquals(PenetrationCalculator.ArmorClass.CLASS_5, 
            PenetrationCalculator.getArmorClassFromMinecraftArmor("diamond"));
        assertEquals(PenetrationCalculator.ArmorClass.CLASS_6, 
            PenetrationCalculator.getArmorClassFromMinecraftArmor("netherite"));
    }

    @Test
    public void pistolVsLeatherArmor() {
        Ballistics pistol = Ballistics.pistol9mm();
        PenetrationCalculator.ArmorClass leather = PenetrationCalculator.ArmorClass.CLASS_1;
        
        // 9mm has 26 penetration, leather is CLASS_1
        float chance = PenetrationCalculator.calculatePenetrationChance(
            pistol.penetrationValue, leather, 1.0f);
        
        // Should have some chance to penetrate cloth armor (roughly 14%)
        assertTrue(chance > 0.1f && chance < 0.2f, "9mm vs leather should be around 14-15%");
    }

    @Test
    public void rifleVsHighClassArmor() {
        Ballistics rifle = Ballistics.rifle556();
        PenetrationCalculator.ArmorClass plate = PenetrationCalculator.ArmorClass.CLASS_5;
        
        float chance = PenetrationCalculator.calculatePenetrationChance(
            rifle.penetrationValue, plate, 1.0f);
        
        // 5.56 M855A1 has ~80 penetration, CLASS_5 is very tough
        // Very low chance (realistic for steel plates)
        assertTrue(chance > 0.02f && chance < 0.08f, "5.56 vs Class 5 should be minimal but possible");
    }

    @Test
    public void degradedArmorLowersPenetrationThreshold() {
        Ballistics pistol = Ballistics.pistol9mm();
        PenetrationCalculator.ArmorClass leather = PenetrationCalculator.ArmorClass.CLASS_1;
        
        float newArmor = PenetrationCalculator.calculatePenetrationChance(
            pistol.penetrationValue, leather, 1.0f);
        float degraded = PenetrationCalculator.calculatePenetrationChance(
            pistol.penetrationValue, leather, 0.2f);  // 80% damage
        
        // Degraded armor should have higher penetration chance
        assertTrue(degraded > newArmor, "Degraded armor should be easier to penetrate");
    }

    @Test
    public void damageCalculationOnSuccess() {
        float baseDamage = 30f;
        PenetrationCalculator.ArmorClass leather = PenetrationCalculator.ArmorClass.CLASS_1;
        float chance = 0.8f;
        
        float damage = PenetrationCalculator.calculateDamageAfterArmor(
            baseDamage, leather, chance, true);  // penetrated = true
        
        // Should reduce by 20-40%
        assertTrue(damage < baseDamage, "Penetrated damage should be less than base");
        assertTrue(damage > baseDamage * 0.6f, "Penetrated damage should not be too low");
    }

    @Test
    public void damageCalculationOnFail() {
        float baseDamage = 30f;
        PenetrationCalculator.ArmorClass leather = PenetrationCalculator.ArmorClass.CLASS_1;
        float chance = 0.2f;
        
        float damage = PenetrationCalculator.calculateDamageAfterArmor(
            baseDamage, leather, chance, false);  // penetrated = false
        
        // Should apply blunt damage (10-30%)
        assertTrue(damage < baseDamage * 0.35f, "Blunt damage should be much less");
        assertTrue(damage > 0, "Blunt damage should exist");
    }

    @Test
    public void armorDurabilityDamageOnPenetration() {
        Ballistics rifle = Ballistics.rifle556();
        PenetrationCalculator.ArmorClass plate = PenetrationCalculator.ArmorClass.CLASS_5;
        
        float damage = PenetrationCalculator.calculateArmorDurabilityDamage(
            rifle.penetrationValue, plate, true);  // penetrated
        
        assertTrue(damage > 0, "Armor should take durability damage from penetration");
    }

    @Test
    public void armorDurabilityDamageGreaterOnFail() {
        Ballistics rifle = Ballistics.rifle556();
        PenetrationCalculator.ArmorClass plate = PenetrationCalculator.ArmorClass.CLASS_5;
        
        float penetrated = PenetrationCalculator.calculateArmorDurabilityDamage(
            rifle.penetrationValue, plate, true);
        float failed = PenetrationCalculator.calculateArmorDurabilityDamage(
            rifle.penetrationValue, plate, false);
        
        // Failed penetration should damage armor more (blunt impact)
        assertTrue(failed > penetrated, "Failed penetration should damage armor more");
    }

    @Test
    public void healthComponentWithArmorIntegration() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        
        // Equip armor
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        
        // Damage should reduce armor durability
        armor.damageArmor(BodyPart.CHEST, 0.1f);
        assertEquals(0.9f, armor.getArmorDurability(BodyPart.CHEST), 0.01f);
    }

    @Test
    public void multipleArmorPiecesOnSamePart() {
        ArmorComponent armor = new ArmorComponent();
        
        // Simulate wearing undershirt + chestplate
        armor.setArmor(BodyPart.CHEST, 10f);  // Undershirt
        
        float previous = armor.getArmor(BodyPart.CHEST);
        armor.setArmor(BodyPart.CHEST, 15f);  // Chestplate (replaces, not stacks)
        
        // With current implementation, armor value is replaced
        // Future: could support true stacking
        assertEquals(15f, armor.getArmor(BodyPart.CHEST), 0.01f);
    }

    @Test
    public void armorProtectsOnlyAssignedParts() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();
        
        // Helmet only protects head
        armor.setArmor(BodyPart.HEAD, 10f);
        armor.setArmorType(BodyPart.HEAD, "iron");
        
        // Chest should still be unprotected
        assertEquals(0f, armor.getArmor(BodyPart.CHEST), 0.01f);
        assertEquals(0f, armor.getArmor(BodyPart.STOMACH), 0.01f);
        assertEquals(0f, armor.getArmor(BodyPart.LEFT_LEG), 0.01f);
    }

    @Test
    public void velocityLossIncreaseWithArmorClass() {
        Ballistics rifle = Ballistics.rifle556();
        float velocity = 900f;
        
        float loss1 = PenetrationCalculator.calculateVelocityLoss(
            velocity, PenetrationCalculator.ArmorClass.CLASS_1);
        float loss6 = PenetrationCalculator.calculateVelocityLoss(
            velocity, PenetrationCalculator.ArmorClass.CLASS_6);
        
        // Stronger armor should cause more velocity loss
        assertTrue(loss6 > loss1, "Class 6 armor should cause more velocity loss");
        assertTrue(loss1 > 0, "Even light armor should cause some velocity loss");
    }
}
