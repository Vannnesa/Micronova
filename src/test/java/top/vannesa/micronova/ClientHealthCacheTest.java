package top.vannesa.micronova;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import org.junit.jupiter.api.Test;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.ui.ClientHealthCache;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClientHealthCacheTest {

    @Test
    public void readFromPacketUpdatesCache() {
        Map<BodyPart, Float> input = new EnumMap<>(BodyPart.class);
        input.put(BodyPart.CHEST, 33.3f);
        input.put(BodyPart.LEFT_ARM, 75.0f);

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(input.size());
        input.forEach((part, val) -> {
            buf.writeEnumConstant(part);
            buf.writeFloat(val);
        });

        // Apply to cache
        ClientHealthCache.readFromPacket(buf);

        assertEquals(33.3f, ClientHealthCache.getHealth(BodyPart.CHEST), 0.0001f);
        assertEquals(75.0f, ClientHealthCache.getHealth(BodyPart.LEFT_ARM), 0.0001f);
    }
}
