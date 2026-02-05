package top.vannesa.micronova.weapon;

/**
 * External ammunition configuration.
 * Defines ballistic properties for a specific ammo type.
 * Can be loaded from JSON files.
 */
public class AmmoConfig {

    // Unique identifier
    public final String id;

    // Display properties
    public final String displayName;
    public final String description;

    // Ballistic properties
    public final float initialVelocity;      // m/s
    public final float mass;                 // grams
    public final float dragCoefficient;      // air resistance
    public final float minPenetrationSpeed;  // m/s minimum to penetrate armor
    public final float penetrationValue;     // Tarkov-style (0-100+)
    public final float baseDamage;           // HP damage
    public final float effectiveRange;       // meters

    // Behavioral properties
    public final boolean tracerRound;        // visual tracer in flight
    public final float armorPenetrationBonus; // bonus to penetration chance (0-1)

    public AmmoConfig(
            String id, String displayName, String description,
            float initialVelocity, float mass, float dragCoefficient,
            float minPenetrationSpeed, float penetrationValue,
            float baseDamage, float effectiveRange,
            boolean tracerRound, float armorPenetrationBonus) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.initialVelocity = initialVelocity;
        this.mass = mass;
        this.dragCoefficient = dragCoefficient;
        this.minPenetrationSpeed = minPenetrationSpeed;
        this.penetrationValue = penetrationValue;
        this.baseDamage = baseDamage;
        this.effectiveRange = effectiveRange;
        this.tracerRound = tracerRound;
        this.armorPenetrationBonus = Math.max(-1f, Math.min(1f, armorPenetrationBonus));
    }

    /**
     * Factory methods for common ammo presets.
     * These match the current Ballistics hardcoded values.
     */

    public static AmmoConfig pistol9mmFMJ() {
        return new AmmoConfig(
            "9mm_fmj",
            "9x19mm Parabellum FMJ",
            "Standard full metal jacket round for 9mm pistols",
            375f,    // initialVelocity
            8.0f,    // mass
            0.005f,  // dragCoefficient
            100f,    // minPenetrationSpeed
            26f,     // penetrationValue
            40f,     // baseDamage
            50f,     // effectiveRange
            false,   // tracerRound
            0f       // armorPenetrationBonus
        );
    }

    public static AmmoConfig rifle556M855A1() {
        return new AmmoConfig(
            "5.56_m855a1",
            "5.56x45mm M855A1",
            "Advanced penetrator rifle round with steel core",
            940f,    // initialVelocity
            4.0f,    // mass
            0.003f,  // dragCoefficient
            150f,    // minPenetrationSpeed
            80f,     // penetrationValue
            60f,     // baseDamage
            300f,    // effectiveRange
            false,   // tracerRound
            0.1f     // armorPenetrationBonus
        );
    }

    public static AmmoConfig rifle762x54R() {
        return new AmmoConfig(
            "7.62x54r",
            "7.62x54mm R (Mosin)",
            "Full power rifle cartridge, high damage",
            865f,    // initialVelocity
            9.6f,    // mass
            0.004f,  // dragCoefficient
            140f,    // minPenetrationSpeed
            58f,     // penetrationValue
            85f,     // baseDamage
            400f,    // effectiveRange
            false,   // tracerRound
            0f       // armorPenetrationBonus
        );
    }

    public static AmmoConfig pistol9mmHP() {
        // High power hollow point variant
        return new AmmoConfig(
            "9mm_hp",
            "9x19mm Parabellum HP",
            "Hollow point round for increased stopping power against soft targets",
            375f,    // initialVelocity (same as FMJ)
            7.0f,    // mass (slightly lighter due to hollow cavity)
            0.006f,  // dragCoefficient (increased drag from expansion)
            100f,    // minPenetrationSpeed
            15f,     // penetrationValue (much lower - poor armor penetration)
            55f,     // baseDamage (higher damage on soft targets)
            50f,     // effectiveRange
            false,   // tracerRound
            -0.2f    // armorPenetrationBonus (penalty vs armor)
        );
    }

    public static AmmoConfig rifle556M193() {
        // Older, lighter penetrator
        return new AmmoConfig(
            "5.56_m193",
            "5.56x45mm M193",
            "Classic rifle round with lead core",
            940f,    // initialVelocity
            3.6f,    // mass (lighter than M855A1)
            0.004f,  // dragCoefficient (more drag)
            150f,    // minPenetrationSpeed
            50f,     // penetrationValue (less penetration than M855A1)
            55f,     // baseDamage
            280f,    // effectiveRange
            false,   // tracerRound
            0f       // armorPenetrationBonus
        );
    }

    /**
     * Convert this AmmoConfig to a Ballistics object for simulation.
     */
    public top.vannesa.micronova.ballistics.Ballistics toBallistics() {
        return new top.vannesa.micronova.ballistics.Ballistics(
            initialVelocity,
            mass,
            dragCoefficient,
            minPenetrationSpeed,
            penetrationValue,
            baseDamage,
            effectiveRange
        );
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %.0f m/s, penetration=%d, damage=%.0f",
            displayName, id, initialVelocity, (int)penetrationValue, baseDamage);
    }
}
