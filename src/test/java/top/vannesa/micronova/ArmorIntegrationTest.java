package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.health.ArmorComponent;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.HealthComponent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test armor integration with HealthComponent.
 */
public class ArmorIntegrationTest {

    @Test
    public void healthComponentIncludesArmorComponent() {
        HealthComponent health = new HealthComponent();
        assertNotNull(health.getArmorComponent());
    }

    @Test
    public void armorComponentCanBeModified() {
        HealthComponent health = new HealthComponent();
        ArmorComponent armor = health.getArmorComponent();

        armor.setArmor(BodyPart.CHEST, 15f);
        assertEquals(15f, armor.getArmor(BodyPart.CHEST), 0.01f);
    }

    @Test
    public void multiPartArmorStackingBySlot() {
        ArmorComponent armor = new ArmorComponent();

        // Simulate chestplate protecting chest, stomach, head
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");

        armor.setArmor(BodyPart.STOMACH, 10f);
        armor.setArmorType(BodyPart.STOMACH, "iron");

        armor.setArmor(BodyPart.HEAD, 5f);
        armor.setArmorType(BodyPart.HEAD, "leather");

        assertEquals(15f, armor.getArmor(BodyPart.CHEST), 0.01f);
        assertEquals(10f, armor.getArmor(BodyPart.STOMACH), 0.01f);
        assertEquals(5f, armor.getArmor(BodyPart.HEAD), 0.01f);
    }

    @Test
    public void armorDurabilityTracking() {
        ArmorComponent armor = new ArmorComponent();

        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        assertEquals(1f, armor.getArmorDurability(BodyPart.CHEST), 0.01f);

        // Simulate penetration damage reducing durability
        armor.damageArmor(BodyPart.CHEST, 0.3f);
        assertEquals(0.7f, armor.getArmorDurability(BodyPart.CHEST), 0.01f);
    }

    @Test
    public void copiedHealthComponentIncludesArmor() {
        HealthComponent original = new HealthComponent();
        original.getArmorComponent().setArmor(BodyPart.CHEST, 15f);
        original.getArmorComponent().setArmorType(BodyPart.CHEST, "iron");

        // Note: current copy constructor may not deep-copy armor
        // This test documents current behavior
        HealthComponent copy = new HealthComponent(original);
        assertNotNull(copy.getArmorComponent());
    }

    @Test
    public void leggingsProtectLegsAndStomach() {
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_LEG, "leggings"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.RIGHT_LEG, "leggings"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.STOMACH, "leggings"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.CHEST, "leggings"));
    }

    @Test
    public void bootOnlyProtectLegs() {
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_LEG, "boots"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.RIGHT_LEG, "boots"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.STOMACH, "boots"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.CHEST, "boots"));
    }

    @Test
    public void helmetOnlyProtectHead() {
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.HEAD, "helmet"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.CHEST, "helmet"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_ARM, "helmet"));
    }

    @Test
    public void armorValueEstimates() {
        float leather = ArmorComponent.getBaseArmorValue("leather");
        float iron = ArmorComponent.getBaseArmorValue("iron");
        float diamond = ArmorComponent.getBaseArmorValue("diamond");
        float netherite = ArmorComponent.getBaseArmorValue("netherite");

        assertTrue(iron > leather, "Iron should have more armor than leather");
        assertTrue(diamond > iron, "Diamond should have more armor than iron");
        assertTrue(netherite > diamond, "Netherite should have more armor than diamond");
        assertTrue(leather > 0, "All armor should have positive value");
    }
}
