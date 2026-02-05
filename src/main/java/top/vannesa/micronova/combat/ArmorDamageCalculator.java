package top.vannesa.micronova.combat;

import net.minecraft.entity.player.PlayerEntity;
import top.vannesa.micronova.ballistics.PenetrationCalculator;
import top.vannesa.micronova.health.ArmorComponent;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.HealthComponent;
import top.vannesa.micronova.weapon.AmmoConfig;

/**
 * Integrates armor penetration with damage calculation.
 * Handles the complete damage chain: penetration → armor reduction → health damage.
 */
public class ArmorDamageCalculator {

    /**
     * Calculate actual damage after considering armor penetration.
     * 
     * @param ammo the ammunition configuration
     * @param targetHealth the target's health component
     * @param bodyPart the body part being hit
     * @param targetPlayer the target player (for context)
     * @return damage to apply to health, -1 if armor breaks (no health damage)
     */
    public static DamageResult calculateDamageWithArmor(
            AmmoConfig ammo,
            HealthComponent targetHealth,
            BodyPart bodyPart,
            PlayerEntity targetPlayer) {

        ArmorComponent armor = targetHealth.getArmorComponent();
        float armorValue = armor.getArmor(bodyPart);
        float armorDurability = armor.getArmorDurability(bodyPart);

        // No armor = full damage
        if (armorValue <= 0) {
            return new DamageResult(ammo.baseDamage, false, null);
        }

        // Determine armor class from current armor type
        String armorType = armor.getArmorType(bodyPart);
        PenetrationCalculator.ArmorClass armorClass = 
            PenetrationCalculator.getArmorClassFromMinecraftArmor(armorType);

        // Calculate penetration chance
        float penetrationChance = PenetrationCalculator.calculatePenetrationChance(
            ammo.penetrationValue + (ammo.armorPenetrationBonus * 20f), // Scale bonus to percentage
            armorClass,
            armorDurability
        );

        // Determine if penetration succeeds (random roll)
        boolean penetrated = Math.random() < penetrationChance;

        // Calculate damage after armor
        float finalDamage = PenetrationCalculator.calculateDamageAfterArmor(
            ammo.baseDamage,
            armorClass,
            penetrationChance,
            penetrated
        );

        // Calculate armor durability damage
        float durabilityDamage = PenetrationCalculator.calculateArmorDurabilityDamage(
            ammo.penetrationValue,
            armorClass,
            penetrated
        );

        // Apply durability damage
        armor.damageArmor(bodyPart, durabilityDamage / 100f); // Normalize to 0-1 range

        // Return result with metadata
        String armorStatus = penetrated ? "PENETRATED" : "BLOCKED";
        return new DamageResult(finalDamage, penetrated, armorStatus);
    }

    /**
     * Represents the result of damage calculation after armor considerations.
     */
    public static class DamageResult {
        public final float damage;           // Damage to apply to health
        public final boolean penetrated;     // Whether penetration succeeded
        public final String armorStatus;     // "PENETRATED", "BLOCKED", or null

        public DamageResult(float damage, boolean penetrated, String armorStatus) {
            this.damage = damage;
            this.penetrated = penetrated;
            this.armorStatus = armorStatus;
        }

        @Override
        public String toString() {
            return String.format("DamageResult(damage=%.1f, penetrated=%s, status=%s)",
                damage, penetrated, armorStatus);
        }
    }

    /**
     * Calculate velocity loss from armor penetration.
     * Used for multi-layer penetration scenarios.
     */
    public static float calculateVelocityLossFromArmor(
            float currentVelocity,
            BodyPart bodyPart,
            HealthComponent targetHealth) {

        ArmorComponent armor = targetHealth.getArmorComponent();
        String armorType = armor.getArmorType(bodyPart);
        PenetrationCalculator.ArmorClass armorClass = 
            PenetrationCalculator.getArmorClassFromMinecraftArmor(armorType);

        return PenetrationCalculator.calculateVelocityLoss(currentVelocity, armorClass);
    }

    /**
     * Check if armor is critically damaged (should provide warning).
     */
    public static boolean isArmorCritical(BodyPart bodyPart, HealthComponent targetHealth) {
        ArmorComponent armor = targetHealth.getArmorComponent();
        float durability = armor.getArmorDurability(bodyPart);
        return durability < 0.3f && armor.getArmor(bodyPart) > 0;
    }

    /**
     * Check if armor is fully destroyed.
     */
    public static boolean isArmorDestroyed(BodyPart bodyPart, HealthComponent targetHealth) {
        ArmorComponent armor = targetHealth.getArmorComponent();
        return armor.getArmorDurability(bodyPart) <= 0;
    }
}
