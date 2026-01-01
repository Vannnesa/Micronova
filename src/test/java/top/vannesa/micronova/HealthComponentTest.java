package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.HealthComponent;

import static org.junit.jupiter.api.Assertions.*;

public class HealthComponentTest {

    @Test
    public void damageRespectsArmorAndAppliesBleed() {
        HealthComponent hc = new HealthComponent();
        hc.setArmor(BodyPart.CHEST, 50f); // 50% reduction
        hc.damage(BodyPart.CHEST, 40f, 0f, 1.0f); // guaranteed bleed

        float hpAfter = hc.snapshot().get(BodyPart.CHEST);
        // effective damage = 40 * (1 - 0.5) = 20
        assertEquals(80f, hpAfter, 0.001f);
        assertTrue(hc.bleedRateSnapshot().get(BodyPart.CHEST) > 0f);
        assertTrue(hc.bleedTicksSnapshot().get(BodyPart.CHEST) > 0);
    }

    @Test
    public void tickAppliesBleedDamage() {
        HealthComponent hc = new HealthComponent();
        hc.damage(BodyPart.LEFT_ARM, 10f, 0f, 1.0f);
        float before = hc.snapshot().get(BodyPart.LEFT_ARM);
        boolean changed = hc.tick();
        assertTrue(changed);
        float after = hc.snapshot().get(BodyPart.LEFT_ARM);
        assertTrue(after < before);
    }
}
