package top.vannesa.micronova.health;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;

/**
 * Applies gameplay effects based on limb crippling status.
 * This includes movement speed reduction for leg injuries and attack speed reduction for arm injuries.
 */
public final class LimbEffectManager {

    private LimbEffectManager() {}

    // Modifier IDs for tracking our applied modifiers
    private static final UUID LEG_CRIPPLE_MODIFIER_ID = UUID.fromString("12345678-1234-1234-1234-123456789012");
    private static final UUID ARM_CRIPPLE_MODIFIER_ID = UUID.fromString("87654321-4321-4321-4321-210987654321");

    /**
     * Apply limb effects to a player based on their cripple status.
     * Called each tick from PlayerHealthManager.tick().
     */
    public static void applyLimbEffects(ServerPlayerEntity player) {
        HealthComponent hc = PlayerHealthManager.get(player);

        // Handle leg crippling (affects movement speed)
        CrippleLevel legLeft = hc.getCrippleLevel(BodyPart.LEFT_LEG);
        CrippleLevel legRight = hc.getCrippleLevel(BodyPart.RIGHT_LEG);
        CrippleLevel legCripple = maxCrippleLevel(legLeft, legRight);
        applyMovementPenalty(player, legCripple);

        // Handle arm crippling (affects attack speed)
        CrippleLevel armLeft = hc.getCrippleLevel(BodyPart.LEFT_ARM);
        CrippleLevel armRight = hc.getCrippleLevel(BodyPart.RIGHT_ARM);
        CrippleLevel armCripple = maxCrippleLevel(armLeft, armRight);
        applyAttackPenalty(player, armCripple);
    }

    /**
     * Apply movement speed penalty based on leg crippling.
     * Uses EntityAttributeInstance for Minecraft 1.20.1.
     */
    private static void applyMovementPenalty(ServerPlayerEntity player, CrippleLevel crippleLevel) {
        var instance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (instance == null) return;

        // Remove existing modifier if any
        if (instance.getModifier(LEG_CRIPPLE_MODIFIER_ID) != null) {
            instance.removeModifier(LEG_CRIPPLE_MODIFIER_ID);
        }

        // Apply new modifier based on cripple level
        if (crippleLevel != CrippleLevel.NONE) {
            // Reduce movement speed by the penalty factor (25%, 50%, 75%)
            float reduction = crippleLevel.penaltyFactor;
            var modifier = new EntityAttributeModifier(
                LEG_CRIPPLE_MODIFIER_ID,
                "leg_cripple_penalty",
                -reduction,  // Negative value to reduce
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL  // Multiplicative penalty
            );
            instance.addTemporaryModifier(modifier);
        }
    }

    /**
     * Apply attack speed penalty based on arm crippling.
     * Uses EntityAttributeInstance for Minecraft 1.20.1.
     */
    private static void applyAttackPenalty(ServerPlayerEntity player, CrippleLevel crippleLevel) {
        var instance = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
        if (instance == null) return;

        // Remove existing modifier if any
        if (instance.getModifier(ARM_CRIPPLE_MODIFIER_ID) != null) {
            instance.removeModifier(ARM_CRIPPLE_MODIFIER_ID);
        }

        // Apply new modifier based on cripple level
        if (crippleLevel != CrippleLevel.NONE) {
            // Reduce attack speed by the penalty factor (25%, 50%, 75%)
            float reduction = crippleLevel.penaltyFactor;
            var modifier = new EntityAttributeModifier(
                ARM_CRIPPLE_MODIFIER_ID,
                "arm_cripple_penalty",
                -reduction,  // Negative value to reduce
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL  // Multiplicative penalty
            );
            instance.addTemporaryModifier(modifier);
        }
    }

    /**
     * Helper to get the maximum cripple level from two enum values.
     */
    private static CrippleLevel maxCrippleLevel(CrippleLevel a, CrippleLevel b) {
        return a.level > b.level ? a : b;
    }
}
