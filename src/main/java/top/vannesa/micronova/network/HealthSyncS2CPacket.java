package top.vannesa.micronova.network;

import net.minecraft.network.PacketByteBuf;
import top.vannesa.micronova.health.BodyPart;

import java.util.EnumMap;
import java.util.Map;

public class HealthSyncS2CPacket {

    public static void write(PacketByteBuf buf, Map<BodyPart, Float> snapshot) {
        buf.writeVarInt(snapshot.size());
        snapshot.forEach((part, hp) -> {
            buf.writeEnumConstant(part);
            buf.writeFloat(hp);
            // NOTE: Armor and bleed info may be appended by callers using extended write methods
        });
    }

    // Extended write/read to include armor and bleeding state for each body part.
    public static void writeFull(PacketByteBuf buf, Map<BodyPart, Float> hpSnapshot, Map<BodyPart, Float> armorSnapshot, Map<BodyPart, Integer> bleedTicks, Map<BodyPart, Float> bleedRates) {
        buf.writeVarInt(hpSnapshot.size());
        for (BodyPart part : BodyPart.values()) {
            buf.writeEnumConstant(part);
            buf.writeFloat(hpSnapshot.getOrDefault(part, 0f));
            buf.writeFloat(armorSnapshot.getOrDefault(part, 0f));
            buf.writeVarInt(bleedTicks.getOrDefault(part, 0));
            buf.writeFloat(bleedRates.getOrDefault(part, 0f));
        }
    }

    public static FullSnapshot readFull(PacketByteBuf buf) {
        int size = buf.readVarInt();
        Map<BodyPart, Float> hp = new EnumMap<>(BodyPart.class);
        Map<BodyPart, Float> armor = new EnumMap<>(BodyPart.class);
        Map<BodyPart, Integer> bleedTicks = new EnumMap<>(BodyPart.class);
        Map<BodyPart, Float> bleedRates = new EnumMap<>(BodyPart.class);
        for (int i = 0; i < size; i++) {
            BodyPart part = buf.readEnumConstant(BodyPart.class);
            hp.put(part, buf.readFloat());
            armor.put(part, buf.readFloat());
            bleedTicks.put(part, buf.readVarInt());
            bleedRates.put(part, buf.readFloat());
        }
        return new FullSnapshot(hp, armor, bleedTicks, bleedRates);
    }

    public static final class FullSnapshot {
        public final Map<BodyPart, Float> hp;
        public final Map<BodyPart, Float> armor;
        public final Map<BodyPart, Integer> bleedTicks;
        public final Map<BodyPart, Float> bleedRates;

        public FullSnapshot(Map<BodyPart, Float> hp, Map<BodyPart, Float> armor, Map<BodyPart, Integer> bleedTicks, Map<BodyPart, Float> bleedRates) {
            this.hp = hp;
            this.armor = armor;
            this.bleedTicks = bleedTicks;
            this.bleedRates = bleedRates;
        }
    }
}
