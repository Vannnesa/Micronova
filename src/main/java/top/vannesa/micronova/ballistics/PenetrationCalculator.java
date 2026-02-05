package top.vannesa.micronova.ballistics;

/**
 * Calculates penetration probability and damage values based on ballistics.
 * Models armor-piercing mechanics similar to Escape from Tarkov.
 */
public class PenetrationCalculator {

    private PenetrationCalculator() {}

    /**
     * Armor class rating (Tarkov-style).
     * Higher number = stronger protection.
     */
    public enum ArmorClass {
        CLASS_1(1, 0.0f),     // Cloth/soft armor
        CLASS_2(2, 0.12f),    // Light armor (PACA, etc.)
        CLASS_3(3, 0.25f),    // Medium armor
        CLASS_4(4, 0.40f),    // Heavy armor (6B43, etc.)
        CLASS_5(5, 0.60f),    // Very heavy (Redut-M, etc.)
        CLASS_6(6, 0.80f);    // Ceramic plates (Kappa, etc.)

        public final int level;
        public final float baseProtection;  // Base damage reduction (0-1)

        ArmorClass(int level, float baseProtection) {
            this.level = level;
            this.baseProtection = baseProtection;
        }
    }

    /**
     * Calculate penetration probability.
     * Higher penetrationValue and lower armorClass = higher chance.
     * @param bulletPenetration ammunition penetration value (0-100)
     * @param armorClass the armor class rating
     * @param armorDurabilityPercent current durability (0-1, 1 = new)
     * @return penetration probability (0-1)
     */
    public static float calculatePenetrationChance(float bulletPenetration, ArmorClass armorClass, 
                                                   float armorDurabilityPercent) {
        // Tarkov-inspired formula:
        // Higher penetration and lower armor class increases chance
        // Degraded armor is weaker
        
        float armorFactor = armorClass.level * 15f;  // Armor difficulty
        float basePenetrationChance = (bulletPenetration - armorFactor) / (bulletPenetration + 50f);
        basePenetrationChance = Math.max(0f, Math.min(1f, basePenetrationChance));
        
        // Durability factor: degraded armor = easier penetration
        float durabilityBoost = (1f - armorDurabilityPercent) * 0.3f;  // Up to 30% boost for broken armor
        
        // Final chance
        float finalChance = basePenetrationChance + durabilityBoost;
        return Math.max(0f, Math.min(1f, finalChance));
    }

    /**
     * Calculate damage after armor reduction.
     * If penetration fails, apply blunt damage.
     * If penetration succeeds, apply reduced damage.
     * @param bulletDamage base bullet damage
     * @param armorClass the armor class
     * @param penetrationChance chance of penetration (from calculatePenetrationChance)
     * @param penetrated true if penetration succeeded
     * @return effective damage to apply
     */
    public static float calculateDamageAfterArmor(float bulletDamage, ArmorClass armorClass,
                                                  float penetrationChance, boolean penetrated) {
        if (penetrated) {
            // On successful penetration, damage is reduced by 20-40%
            // Higher penetration chance = less reduction
            float reduction = 0.40f - (penetrationChance * 0.20f);
            return bulletDamage * (1f - reduction);
        } else {
            // On failed penetration, apply blunt damage (10-30% of original)
            // Higher penetration chance = more blunt damage
            float bluntPercent = 0.10f + (penetrationChance * 0.20f);
            return bulletDamage * bluntPercent;
        }
    }

    /**
     * Calculate velocity loss when penetrating a material.
     * Heavier materials cause more velocity loss.
     */
    public static float calculateVelocityLoss(float currentVelocity, ArmorClass armorClass) {
        // Loss as percentage: stronger armor = more loss
        float lossPercent = 0.10f + (armorClass.level * 0.08f);  // 10% to 58% loss
        return currentVelocity * lossPercent;
    }

    /**
     * Calculate armor durability damage.
     * Penetrating rounds do less durability damage than blunt impacts.
     */
    public static float calculateArmorDurabilityDamage(float bulletPenetration, ArmorClass armorClass,
                                                       boolean penetrated) {
        // Base damage based on armor class (stronger = more durable, but takes more damage)
        float baseDamage = bulletPenetration * 0.5f;
        
        if (penetrated) {
            // Penetrating rounds do 10-15% less durability damage
            return baseDamage * 0.85f;
        } else {
            // Failed penetration does more durability damage
            return baseDamage * 1.2f;
        }
    }

    /**
     * Determine armor class from Minecraft item.
     * Returns a reasonable default based on item type.
     */
    public static ArmorClass getArmorClassFromMinecraftArmor(String armorType) {
        // Simplified mapping: Minecraft armor -> Tarkov armor classes
        switch (armorType) {
            case "leather": return ArmorClass.CLASS_1;
            case "chainmail": return ArmorClass.CLASS_2;
            case "iron": return ArmorClass.CLASS_3;
            case "diamond": return ArmorClass.CLASS_5;
            case "netherite": return ArmorClass.CLASS_6;
            default: return ArmorClass.CLASS_1;
        }
    }
}
