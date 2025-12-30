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
        });
    }

    public static Map<BodyPart, Float> read(PacketByteBuf buf) {
        int size = buf.readVarInt();
        Map<BodyPart, Float> map = new EnumMap<>(BodyPart.class);
        for (int i = 0; i < size; i++) {
            map.put(buf.readEnumConstant(BodyPart.class), buf.readFloat());
        }
        return map;
    }
}
