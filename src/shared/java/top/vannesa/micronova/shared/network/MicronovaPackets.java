package top.vannesa.micronova.health;

import top.vannesa.micronova.shared.health.BodyPart;

import java.util.EnumMap;

public final class HealthComponent {

    private final EnumMap<BodyPart, Integer> health = new EnumMap<>(BodyPart.class);

    public HealthComponent() {
        for (BodyPart part : BodyPart.values()) {
            health.put(part, 100);
        }
    }

    public int getHealth(BodyPart part) {
        return health.get(part);
    }

    public void addHealth(BodyPart part, int delta) {
        health.put(part, Math.max(0, health.get(part) + delta));
    }

    public EnumMap<BodyPart, Integer> snapshot() {
        return new EnumMap<>(health);
    }
}
