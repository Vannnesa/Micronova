package top.vannesa.micronova.ui;

import net.minecraft.network.PacketByteBuf;
import top.vannesa.micronova.health.BodyPart;

import java.util.EnumMap;
import java.util.Map;

public final class ClientHealthCache {

    private static final Map<BodyPart, Float> HEALTH = new EnumMap<>(BodyPart.class);

    public static void readFromPacket(PacketByteBuf buf) {
        HEALTH.clear();

        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            BodyPart part = buf.readEnumConstant(BodyPart.class);
            float value = buf.readFloat();
            HEALTH.put(part, value);
        }
    }

    public static float getHealth(BodyPart part) {
        return HEALTH.getOrDefault(part, 0f);
    }

    public static Map<BodyPart, Float> snapshot() {
        return Map.copyOf(HEALTH);
    }

    private ClientHealthCache() {}
}
