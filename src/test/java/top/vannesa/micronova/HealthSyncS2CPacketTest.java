package top.vannesa.micronova;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import org.junit.jupiter.api.Test;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.network.HealthSyncS2CPacket;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HealthSyncS2CPacketTest {

    @Test
    public void roundTripSerialization() {
        Map<BodyPart, Float> snap = new EnumMap<>(BodyPart.class);
        snap.put(BodyPart.CHEST, 50.0f);
        snap.put(BodyPart.HEAD, 10.5f);

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        HealthSyncS2CPacket.write(buf, snap);

        // reset reader index is not needed; reading starts at readerIndex 0
        Map<BodyPart, Float> read = HealthSyncS2CPacket.read(buf);

        assertEquals(snap.size(), read.size());
        for (BodyPart p : snap.keySet()) {
            assertTrue(read.containsKey(p));
            assertEquals(snap.get(p), read.get(p), 0.0001f);
        }
    }
}
