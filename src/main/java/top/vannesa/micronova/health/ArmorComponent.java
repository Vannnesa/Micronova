package top.vannesa.micronova.health;

import java.util.EnumMap;
import java.util.Map;

/**
 * Tracks armor protection for each body part.
 * Maps Minecraft armor items to protection values and durability.
 */
public class ArmorComponent {

    // Armor value and durability for each body part
    private final Map<BodyPart, Float> armorValues = new EnumMap<>(BodyPart.class);
    private final Map<BodyPart, Float> armorDurability = new EnumMap<>(BodyPart.class);  // 0-1, 1=new
    private final Map<BodyPart, String> armorType = new EnumMap<>(BodyPart.class);       // e.g., "iron", "diamond"

    public ArmorComponent() {
        for (BodyPart part : BodyPart.values()) {
            armorValues.put(part, 0f);
            armorDurability.put(part, 1f);
            armorType.put(part, "none");
        }
    }

    /**
     * Set armor value for a body part.
     */
    public void setArmor(BodyPart part, float value) {
        armorValues.put(part, Math.max(0f, value));
    }

    /**
     * Get armor value for a body part.
     */
    public float getArmor(BodyPart part) {
        return armorValues.getOrDefault(part, 0f);
    }

    /**
     * Set armor durability (0-1, where 1 = new condition).
     */
    public void setArmorDurability(BodyPart part, float durability) {
        armorDurability.put(part, Math.max(0f, Math.min(1f, durability)));
    }

    /**
     * Get armor durability.
     */
    public float getArmorDurability(BodyPart part) {
        return armorDurability.getOrDefault(part, 1f);
    }

    /**
     * Apply durability damage to armor.
     */
    public void damageArmor(BodyPart part, float damageAmount) {
        float current = armorDurability.getOrDefault(part, 1f);
        float newDurability = Math.max(0f, current - damageAmount);
        armorDurability.put(part, newDurability);
    }

    /**
     * Set armor type (e.g., "leather", "iron", "diamond", "netherite").
     */
    public void setArmorType(BodyPart part, String type) {
        armorType.put(part, type != null ? type : "none");
    }

    /**
     * Get armor type for a body part.
     */
    public String getArmorType(BodyPart part) {
        return armorType.getOrDefault(part, "none");
    }

    /**
     * Get snapshot of all armor values.
     */
    public Map<BodyPart, Float> armorSnapshot() {
        return Map.copyOf(armorValues);
    }

    /**
     * Get snapshot of all armor durabilities.
     */
    public Map<BodyPart, Float> durabilitySnapshot() {
        return Map.copyOf(armorDurability);
    }

    /**
     * Reset all armor (used when player dies or removes gear).
     */
    public void resetArmor() {
        for (BodyPart part : BodyPart.values()) {
            armorValues.put(part, 0f);
            armorDurability.put(part, 1f);
            armorType.put(part, "none");
        }
    }

    /**
     * Determine which body parts are protected by a specific armor piece.
     * E.g., a chestplate protects HEAD, CHEST, STOMACH but not limbs.
     */
    public static boolean isBodyPartProtectedBy(BodyPart part, String armorSlot) {
        switch (armorSlot) {
            case "helmet":
            case "head":
                return part == BodyPart.HEAD;
                
            case "chestplate":
            case "chest":
                return part == BodyPart.HEAD || part == BodyPart.CHEST || part == BodyPart.STOMACH;
                
            case "leggings":
            case "legs":
                return part == BodyPart.LEFT_LEG || part == BodyPart.RIGHT_LEG || 
                       part == BodyPart.STOMACH;  // Leggings protect partial stomach
                
            case "boots":
            case "feet":
                return part == BodyPart.LEFT_LEG || part == BodyPart.RIGHT_LEG;
                
            default:
                return false;
        }
    }

    /**
     * Get armor class based on Minecraft armor type.
     * Maps to Tarkov-style protection levels.
     */
    public static int getArmorClassLevel(String minecraftArmorType) {
        switch (minecraftArmorType) {
            case "leather": return 1;
            case "chainmail": return 2;
            case "iron": return 3;
            case "diamond": return 5;
            case "netherite": return 6;
            default: return 0;
        }
    }

    /**
     * Estimate base armor value from Minecraft armor type.
     * Actual value depends on durability and layers.
     */
    public static float getBaseArmorValue(String minecraftArmorType) {
        switch (minecraftArmorType) {
            case "leather": return 4f;
            case "chainmail": return 12f;
            case "iron": return 15f;
            case "diamond": return 20f;
            case "netherite": return 25f;
            default: return 0f;
        }
    }
}
