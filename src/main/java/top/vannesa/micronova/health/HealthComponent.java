package top.vannesa.micronova.health;

import java.util.EnumMap;
import java.util.Map;

public class HealthComponent {

    private final Map<BodyPart, Float> healthMap = new EnumMap<>(BodyPart.class);
    private final Map<BodyPart, Float> armorMap = new EnumMap<>(BodyPart.class);
    private final Map<BodyPart, Float> bleedRateMap = new EnumMap<>(BodyPart.class); // damage per second
    private final Map<BodyPart, Integer> bleedTicksLeft = new EnumMap<>(BodyPart.class);
    private final Map<BodyPart, CrippleLevel> crippleMap = new EnumMap<>(BodyPart.class); // limb crippling level
    private final Map<BodyPart, Integer> crippledTicksLeft = new EnumMap<>(BodyPart.class); // cripple duration ticks
    private final ArmorComponent armorComponent = new ArmorComponent();

    public HealthComponent() {
        for (BodyPart part : BodyPart.values()) {
            healthMap.put(part, 100.0f);
            armorMap.put(part, 0.0f);
            bleedRateMap.put(part, 0.0f);
            bleedTicksLeft.put(part, 0);
            crippleMap.put(part, CrippleLevel.NONE);
            crippledTicksLeft.put(part, 0);
        }
    }

    /**
     * Copy constructor used by debug utilities to clone a component.
     */
    public HealthComponent(HealthComponent other) {
        for (BodyPart part : BodyPart.values()) {
            this.healthMap.put(part, other.healthMap.getOrDefault(part, 100.0f));
            this.armorMap.put(part, other.armorMap.getOrDefault(part, 0.0f));
            this.bleedRateMap.put(part, other.bleedRateMap.getOrDefault(part, 0.0f));
            this.bleedTicksLeft.put(part, other.bleedTicksLeft.getOrDefault(part, 0));
            this.crippleMap.put(part, other.crippleMap.getOrDefault(part, CrippleLevel.NONE));
            this.crippledTicksLeft.put(part, other.crippledTicksLeft.getOrDefault(part, 0));
        }
    }

    public void setHealth(BodyPart part, float hp) {
        this.healthMap.put(part, Math.max(0f, Math.min(100f, hp)));
    }

    public float getHealth(BodyPart part) {
        return this.healthMap.getOrDefault(part, 100f);
    }

    public void setBleed(BodyPart part, float rate, int ticks) {
        this.bleedRateMap.put(part, Math.max(0f, rate));
        this.bleedTicksLeft.put(part, Math.max(0, ticks));
    }
    /**
     * Apply damage to a body part taking armor into account.
     * @param part body part
     * @param amount raw damage
     * @param penetration armor penetration amount (reduces effective armor)
     * @param causeBleedChance chance between 0..1 to cause bleeding; when successful applies bleed with given rate and duration
     */
    public void damage(BodyPart part, float amount, float penetration, float causeBleedChance) {
        // compute effective armor
        float armor = armorMap.getOrDefault(part, 0f);
        float effectiveArmor = Math.max(0f, armor - penetration);

        // simple armor model: damage is reduced proportionally by (armor / 100)
        float reduction = Math.max(0f, Math.min(1f, effectiveArmor / 100f));
        float effectiveDamage = amount * (1f - reduction);

        healthMap.computeIfPresent(part, (p, hp) -> Math.max(0f, hp - effectiveDamage));

        // bleeding: probabilistic
        if (causeBleedChance > 0f && Math.random() < causeBleedChance) {
            // apply bleed: small rate based on damage
            float rate = Math.max(0.5f, effectiveDamage * 0.1f); // damage per second
            int durationTicks = 20 * 6; // 6 seconds by default
            bleedRateMap.put(part, rate);
            bleedTicksLeft.put(part, durationTicks);
        }
    }

    public void setArmor(BodyPart part, float armor) {
        armorMap.put(part, Math.max(0f, armor));
    }

    public float getArmor(BodyPart part) {
        return armorMap.getOrDefault(part, 0f);
    }

    /**
     * Update cripple level based on current health.
     * Called each tick to recalculate cripple states.
     * @return true if cripple level changed
     */
    public boolean updateCrippleStatus() {
        boolean changed = false;
        for (BodyPart part : BodyPart.values()) {
            float hp = healthMap.getOrDefault(part, 100f);
            CrippleLevel newLevel = CrippleLevel.fromHealthPercent(hp);
            CrippleLevel oldLevel = crippleMap.get(part);
            if (newLevel != oldLevel) {
                crippleMap.put(part, newLevel);
                // Reset cripple duration when level changes
                if (newLevel != CrippleLevel.NONE) {
                    crippledTicksLeft.put(part, 20 * 5); // 5 seconds minimum
                } else {
                    crippledTicksLeft.put(part, 0);
                }
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Server tick handler: apply bleeding damage per tick and update cripple status.
     * @return true if any health value changed
     */
    public boolean tick() {
        boolean changed = false;
        for (BodyPart part : BodyPart.values()) {
            int ticks = bleedTicksLeft.getOrDefault(part, 0);
            if (ticks > 0) {
                float rate = bleedRateMap.getOrDefault(part, 0f);
                // apply per-tick damage = rate / 20
                float dmg = rate / 20f;
                float old = healthMap.get(part);
                float now = Math.max(0f, old - dmg);
                if (now != old) {
                    healthMap.put(part, now);
                    changed = true;
                }
                bleedTicksLeft.put(part, ticks - 1);
                if (bleedTicksLeft.get(part) <= 0) {
                    bleedRateMap.put(part, 0f);
                }
            }
            // Decrement cripple duration
            int crippledTicks = crippledTicksLeft.getOrDefault(part, 0);
            if (crippledTicks > 0) {
                crippledTicksLeft.put(part, crippledTicks - 1);
            }
        }
        // Update cripple levels based on health
        if (updateCrippleStatus()) {
            changed = true;
        }
        return changed;
    }

    public Map<BodyPart, Float> snapshot() {
        return Map.copyOf(healthMap);
    }

    public Map<BodyPart, Float> armorSnapshot() {
        return Map.copyOf(armorMap);
    }

    public Map<BodyPart, Float> bleedRateSnapshot() {
        return Map.copyOf(bleedRateMap);
    }

    public Map<BodyPart, Integer> bleedTicksSnapshot() {
        return Map.copyOf(bleedTicksLeft);
    }

    public CrippleLevel getCrippleLevel(BodyPart part) {
        return crippleMap.getOrDefault(part, CrippleLevel.NONE);
    }

    public Map<BodyPart, CrippleLevel> crippleSnapshot() {
        return Map.copyOf(crippleMap);
    }

    public Map<BodyPart, Integer> crippledTicksSnapshot() {
        return Map.copyOf(crippledTicksLeft);
    }

    /**
     * Get the ArmorComponent for armor management.
     */
    public ArmorComponent getArmorComponent() {
        return armorComponent;
    }
}
