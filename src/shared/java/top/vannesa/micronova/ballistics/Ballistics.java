package top.vannesa.micronova.ballistics;

/**
 * Represents the physical properties of a ballistic projectile (bullet).
 * Used to calculate trajectories, velocity decay, and penetration capabilities.
 */
public class Ballistics {

    // Projectile physical properties
    public final float initialVelocity;      // m/s (e.g., 9mm Parabellum: 350-400)
    public final float mass;                 // grams (e.g., 9mm FMJ: 8g)
    public final float dragCoefficient;      // 0.001-0.01 (air resistance per unit mass)
    public final float minPenetrationSpeed;  // m/s (minimum speed to penetrate armor)
    public final float penetrationValue;     // Tarkov-style penetration power (0-100)
    public final float baseDamage;           // HP (base damage to apply)
    public final float range;                // meters (effective range before speed too low)

    public Ballistics(float initialVelocity, float mass, float dragCoefficient,
                      float minPenetrationSpeed, float penetrationValue, float baseDamage, float range) {
        this.initialVelocity = initialVelocity;
        this.mass = mass;
        this.dragCoefficient = dragCoefficient;
        this.minPenetrationSpeed = minPenetrationSpeed;
        this.penetrationValue = penetrationValue;
        this.baseDamage = baseDamage;
        this.range = range;
    }

    /**
     * Get velocity at time t, accounting for air resistance.
     * Exponential decay: v(t) = v₀ * e^(-k*t) where k = dragCoefficient / mass
     */
    public float getVelocityAt(float elapsedTime) {
        if (elapsedTime <= 0) return initialVelocity;
        float decayRate = dragCoefficient / mass;
        return initialVelocity * (float) Math.exp(-decayRate * elapsedTime);
    }

    /**
     * Get distance traveled without air resistance (theoretical max).
     */
    public float getTravelDistanceNoResistance(float elapsedTime) {
        return initialVelocity * elapsedTime;
    }

    /**
     * Get kinetic energy (1/2 * m * v²) for damage calculations.
     */
    public float getKineticEnergy(float velocity) {
        // KE = 0.5 * m * v²
        // m is in grams, convert to kg: m_kg = mass / 1000
        float m_kg = mass / 1000f;
        return 0.5f * m_kg * velocity * velocity;
    }

    /**
     * Check if velocity is sufficient to penetrate armor.
     */
    public boolean canPenetrate(float velocity) {
        return velocity >= minPenetrationSpeed;
    }

    /**
     * Get damage multiplier based on current velocity.
     * Higher velocity = more damage.
     */
    public float getDamageMultiplier(float velocity) {
        if (velocity >= initialVelocity) return 1.0f;
        if (velocity <= minPenetrationSpeed) return 0.0f;
        // Linear falloff from 100% at initialVelocity to 0% at minPenetrationSpeed
        return (velocity - minPenetrationSpeed) / (initialVelocity - minPenetrationSpeed);
    }

    /**
     * Factory methods for common ammunition types.
     */
    public static Ballistics pistol9mm() {
        // 9x19mm Parabellum
        return new Ballistics(
            375f,   // initialVelocity (m/s)
            8.0f,   // mass (g)
            0.005f, // dragCoefficient
            100f,   // minPenetrationSpeed (m/s)
            26f,    // penetrationValue (increased for realistic balance)
            40f,    // baseDamage (HP)
            50f     // range (meters)
        );
    }

    public static Ballistics rifle556() {
        // 5.56x45mm NATO
        return new Ballistics(
            940f,   // initialVelocity (m/s)
            4.0f,   // mass (g)
            0.003f, // dragCoefficient
            150f,   // minPenetrationSpeed (m/s)
            80f,    // penetrationValue (M855A1, realistic balance)
            60f,    // baseDamage (HP)
            300f    // range (meters)
        );
    }

    public static Ballistics rifle762x54() {
        // 7.62x54mm Mosin Nagant
        return new Ballistics(
            865f,   // initialVelocity (m/s)
            9.6f,   // mass (g)
            0.004f, // dragCoefficient
            140f,   // minPenetrationSpeed (m/s)
            58f,    // penetrationValue (increased for realistic balance)
            85f,    // baseDamage (HP)
            400f    // range (meters)
        );
    }
}
