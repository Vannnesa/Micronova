package top.vannesa.micronova.ui;

import top.vannesa.micronova.health.BodyPart;

import java.util.EnumMap;
import java.util.Map;

public class ClientHealthCache {

    private static final Map<BodyPart, Float> LAST = new EnumMap<>(BodyPart.class);

    public static void update(Map<BodyPart, Float> snapshot) {
        LAST.clear();
        LAST.putAll(snapshot);
    }

    public static Map<BodyPart, Float> get() {
        return LAST;
    }
}
