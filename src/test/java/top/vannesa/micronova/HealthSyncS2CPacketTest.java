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
        // write full snapshot with armor/bleed fields
        java.util.Map<BodyPart, Float> armor = new java.util.EnumMap<>(BodyPart.class);
        java.util.Map<BodyPart, Integer> ticks = new java.util.EnumMap<>(BodyPart.class);
        java.util.Map<BodyPart, Float> rates = new java.util.EnumMap<>(BodyPart.class);
        for (BodyPart p : snap.keySet()) {
            armor.put(p, 0f);
            ticks.put(p, 0);
            rates.put(p, 0f);
        }
        HealthSyncS2CPacket.writeFull(buf, snap, armor, ticks, rates);

        HealthSyncS2CPacket.FullSnapshot fs = HealthSyncS2CPacket.readFull(buf);

        assertEquals(snap.size(), fs.hp.size());
        for (BodyPart p : snap.keySet()) {
            assertTrue(fs.hp.containsKey(p));
            assertEquals(snap.get(p), fs.hp.get(p), 0.0001f);
            assertEquals(0f, fs.armor.get(p), 0.0001f);
            assertEquals(0, fs.bleedTicks.get(p).intValue());
            assertEquals(0f, fs.bleedRates.get(p), 0.0001f);
        }
    }
}
