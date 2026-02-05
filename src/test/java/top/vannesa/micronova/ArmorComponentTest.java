package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.health.ArmorComponent;
import top.vannesa.micronova.health.BodyPart;

import static org.junit.jupiter.api.Assertions.*;

public class ArmorComponentTest {

    @Test
    public void defaultArmorIsZero() {
        ArmorComponent armor = new ArmorComponent();
        
        for (BodyPart part : BodyPart.values()) {
            assertEquals(0f, armor.getArmor(part), 0.01f);
            assertEquals(1f, armor.getArmorDurability(part), 0.01f);
        }
    }

    @Test
    public void setAndGetArmor() {
        ArmorComponent armor = new ArmorComponent();
        
        armor.setArmor(BodyPart.CHEST, 15f);
        assertEquals(15f, armor.getArmor(BodyPart.CHEST), 0.01f);
        assertEquals(0f, armor.getArmor(BodyPart.HEAD), 0.01f);
    }

    @Test
    public void armorNeverNegative() {
        ArmorComponent armor = new ArmorComponent();
        
        armor.setArmor(BodyPart.CHEST, -10f);
        assertEquals(0f, armor.getArmor(BodyPart.CHEST), 0.01f);
    }

    @Test
    public void armorDurabilityDamage() {
        ArmorComponent armor = new ArmorComponent();
        
        assertEquals(1f, armor.getArmorDurability(BodyPart.CHEST), 0.01f);
        
        // Damage 20% durability
        armor.damageArmor(BodyPart.CHEST, 0.20f);
        assertEquals(0.80f, armor.getArmorDurability(BodyPart.CHEST), 0.01f);
        
        // Damage another 80% (should cap at 0)
        armor.damageArmor(BodyPart.CHEST, 0.80f);
        assertEquals(0f, armor.getArmorDurability(BodyPart.CHEST), 0.01f);
    }

    @Test
    public void setArmorType() {
        ArmorComponent armor = new ArmorComponent();
        
        armor.setArmorType(BodyPart.CHEST, "iron");
        assertEquals("iron", armor.getArmorType(BodyPart.CHEST));
        
        armor.setArmorType(BodyPart.CHEST, null);
        assertEquals("none", armor.getArmorType(BodyPart.CHEST));
    }

    @Test
    public void bodyPartProtectedByHelmet() {
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.HEAD, "helmet"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.CHEST, "helmet"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_LEG, "helmet"));
    }

    @Test
    public void bodyPartProtectedByChestplate() {
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.HEAD, "chestplate"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.CHEST, "chestplate"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.STOMACH, "chestplate"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_LEG, "chestplate"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_ARM, "chestplate"));
    }

    @Test
    public void bodyPartProtectedByLeggings() {
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.CHEST, "leggings"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_LEG, "leggings"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.RIGHT_LEG, "leggings"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.STOMACH, "leggings"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_ARM, "leggings"));
    }

    @Test
    public void bodyPartProtectedByBoots() {
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.CHEST, "boots"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.LEFT_LEG, "boots"));
        assertTrue(ArmorComponent.isBodyPartProtectedBy(BodyPart.RIGHT_LEG, "boots"));
        assertFalse(ArmorComponent.isBodyPartProtectedBy(BodyPart.STOMACH, "boots"));
    }

    @Test
    public void armorClassMapping() {
        assertEquals(1, ArmorComponent.getArmorClassLevel("leather"));
        assertEquals(2, ArmorComponent.getArmorClassLevel("chainmail"));
        assertEquals(3, ArmorComponent.getArmorClassLevel("iron"));
        assertEquals(5, ArmorComponent.getArmorClassLevel("diamond"));
        assertEquals(6, ArmorComponent.getArmorClassLevel("netherite"));
        assertEquals(0, ArmorComponent.getArmorClassLevel("unknown"));
    }

    @Test
    public void baseArmorValues() {
        assertTrue(ArmorComponent.getBaseArmorValue("leather") > 0);
        assertTrue(ArmorComponent.getBaseArmorValue("iron") > ArmorComponent.getBaseArmorValue("leather"));
        assertTrue(ArmorComponent.getBaseArmorValue("diamond") > ArmorComponent.getBaseArmorValue("iron"));
        assertTrue(ArmorComponent.getBaseArmorValue("netherite") > ArmorComponent.getBaseArmorValue("diamond"));
    }

    @Test
    public void resetArmor() {
        ArmorComponent armor = new ArmorComponent();
        
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmorType(BodyPart.CHEST, "iron");
        armor.damageArmor(BodyPart.CHEST, 0.5f);
        
        armor.resetArmor();
        
        assertEquals(0f, armor.getArmor(BodyPart.CHEST), 0.01f);
        assertEquals(1f, armor.getArmorDurability(BodyPart.CHEST), 0.01f);
        assertEquals("none", armor.getArmorType(BodyPart.CHEST));
    }

    @Test
    public void armorSnapshot() {
        ArmorComponent armor = new ArmorComponent();
        
        armor.setArmor(BodyPart.CHEST, 15f);
        armor.setArmor(BodyPart.HEAD, 5f);
        
        var snapshot = armor.armorSnapshot();
        assertEquals(15f, snapshot.get(BodyPart.CHEST), 0.01f);
        assertEquals(5f, snapshot.get(BodyPart.HEAD), 0.01f);
        assertEquals(0f, snapshot.get(BodyPart.LEFT_ARM), 0.01f);
    }
}
