package top.vannesa.micronova.ui;

import net.minecraft.network.PacketByteBuf;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.CrippleLevel;

import java.util.EnumMap;
import java.util.Map;

public final class ClientHealthCache {

    public static final class PartState {
        public float hp;
        public float armor;
        public int bleedTicks;
        public float bleedRate;
        public CrippleLevel crippleLevel;
        public int crippledTicks;

        public PartState(float hp, float armor, int bleedTicks, float bleedRate, CrippleLevel crippleLevel, int crippledTicks) {
            this.hp = hp;
            this.armor = armor;
            this.bleedTicks = bleedTicks;
            this.bleedRate = bleedRate;
            this.crippleLevel = crippleLevel;
            this.crippledTicks = crippledTicks;
        }
    }

    private static final Map<BodyPart, PartState> HEALTH = new EnumMap<>(BodyPart.class);

    public static void readFromPacket(PacketByteBuf buf) {
        HEALTH.clear();

        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            BodyPart part = buf.readEnumConstant(BodyPart.class);
            float value = buf.readFloat();
            float armor = buf.readFloat();
            int ticks = buf.readVarInt();
            float rate = buf.readFloat();
            CrippleLevel crippleLevel = buf.readEnumConstant(CrippleLevel.class);
            int crippledTicks = buf.readVarInt();
            HEALTH.put(part, new PartState(value, armor, ticks, rate, crippleLevel, crippledTicks));
        }
    }

    public static float getHealth(BodyPart part) {
        return HEALTH.containsKey(part) ? HEALTH.get(part).hp : 0f;
    }

    public static float getArmor(BodyPart part) {
        return HEALTH.containsKey(part) ? HEALTH.get(part).armor : 0f;
    }

    public static int getBleedTicks(BodyPart part) {
        return HEALTH.containsKey(part) ? HEALTH.get(part).bleedTicks : 0;
    }

    public static float getBleedRate(BodyPart part) {
        return HEALTH.containsKey(part) ? HEALTH.get(part).bleedRate : 0f;
    }

    public static CrippleLevel getCrippleLevel(BodyPart part) {
        return HEALTH.containsKey(part) ? HEALTH.get(part).crippleLevel : CrippleLevel.NONE;
    }

    public static int getCrippledTicks(BodyPart part) {
        return HEALTH.containsKey(part) ? HEALTH.get(part).crippledTicks : 0;
    }

    public static Map<BodyPart, PartState> snapshot() {
        return Map.copyOf(HEALTH);
    }

    private ClientHealthCache() {}
}

