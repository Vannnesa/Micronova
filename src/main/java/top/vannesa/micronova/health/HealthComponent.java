package top.vannesa.micronova.health;

import java.util.EnumMap;
import java.util.Map;

public class HealthComponent {

    private final Map<BodyPart, Float> healthMap = new EnumMap<>(BodyPart.class);

    public HealthComponent() {
        for (BodyPart part : BodyPart.values()) {
            healthMap.put(part, 100.0f);
        }
    }

    public void damage(BodyPart part, float amount) {
        healthMap.computeIfPresent(part, (p, hp) -> Math.max(0f, hp - amount));
    }

    public Map<BodyPart, Float> snapshot() {
        return Map.copyOf(healthMap);
    }
}
