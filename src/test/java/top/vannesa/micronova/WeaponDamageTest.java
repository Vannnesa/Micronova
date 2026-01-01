package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.health.HealthComponent;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.weapon.WeaponConfig;

import static org.junit.jupiter.api.Assertions.*;

public class WeaponDamageTest {

    @Test
    public void pistolAppliesExpectedDamageWithPenetration() {
        HealthComponent hc = new HealthComponent();
        hc.setArmor(BodyPart.CHEST, 30f);
        WeaponConfig cfg = WeaponConfig.pistol9mm();

        hc.damage(BodyPart.CHEST, cfg.baseDamage, cfg.penetration, cfg.bleedChance);

        float expected = 100f - (cfg.baseDamage * (1f - Math.max(0f, (30f - cfg.penetration) / 100f)));
        assertEquals(expected, hc.snapshot().get(BodyPart.CHEST), 0.01f);
    }
}
