package top.vannesa.micronova.health;

/**
 * Represents the severity level of limb crippling damage.
 * Each level corresponds to different gameplay penalties.
 */
public enum CrippleLevel {
    NONE(0, 0.0f),           // No crippling effect
    LIGHT(1, 0.25f),         // 25% penalty (e.g., 25% movement speed reduction)
    MODERATE(2, 0.50f),      // 50% penalty
    SEVERE(3, 0.75f);        // 75% penalty

    public final int level;
    public final float penaltyFactor;  // Fraction of effect applied (0.0 to 1.0)

    CrippleLevel(int level, float penaltyFactor) {
        this.level = level;
        this.penaltyFactor = penaltyFactor;
    }

    /**
     * Determine cripple level based on health percentage (0-100).
     * Thresholds: 75+ HP = NONE, 50-74 = LIGHT, 25-49 = MODERATE, 0-24 = SEVERE
     */
    public static CrippleLevel fromHealthPercent(float hpPercent) {
        if (hpPercent >= 75f) return NONE;
        if (hpPercent >= 50f) return LIGHT;
        if (hpPercent >= 25f) return MODERATE;
        return SEVERE;
    }
}
