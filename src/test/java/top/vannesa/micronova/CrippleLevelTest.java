package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.health.CrippleLevel;
import top.vannesa.micronova.health.HealthComponent;
import top.vannesa.micronova.health.BodyPart;

import static org.junit.jupiter.api.Assertions.*;

public class CrippleLevelTest {

    @Test
    public void cripleLevelFromHealthPercent() {
        assertEquals(CrippleLevel.NONE, CrippleLevel.fromHealthPercent(100f));
        assertEquals(CrippleLevel.NONE, CrippleLevel.fromHealthPercent(75f));
        assertEquals(CrippleLevel.LIGHT, CrippleLevel.fromHealthPercent(74f));
        assertEquals(CrippleLevel.LIGHT, CrippleLevel.fromHealthPercent(50f));
        assertEquals(CrippleLevel.MODERATE, CrippleLevel.fromHealthPercent(49f));
        assertEquals(CrippleLevel.MODERATE, CrippleLevel.fromHealthPercent(25f));
        assertEquals(CrippleLevel.SEVERE, CrippleLevel.fromHealthPercent(24f));
        assertEquals(CrippleLevel.SEVERE, CrippleLevel.fromHealthPercent(0f));
    }

    @Test
    public void cripleLevelPenaltyFactors() {
        assertEquals(0.0f, CrippleLevel.NONE.penaltyFactor);
        assertEquals(0.25f, CrippleLevel.LIGHT.penaltyFactor);
        assertEquals(0.50f, CrippleLevel.MODERATE.penaltyFactor);
        assertEquals(0.75f, CrippleLevel.SEVERE.penaltyFactor);
    }

    @Test
    public void updateCrippleStatusOnDamage() {
        HealthComponent hc = new HealthComponent();

        // Initially no crippling
        assertEquals(CrippleLevel.NONE, hc.getCrippleLevel(BodyPart.LEFT_LEG));

        // Damage to 74 HP -> LIGHT crippling
        hc.setHealth(BodyPart.LEFT_LEG, 74f);
        hc.updateCrippleStatus();
        assertEquals(CrippleLevel.LIGHT, hc.getCrippleLevel(BodyPart.LEFT_LEG));

        // Damage to 49 HP -> MODERATE crippling
        hc.setHealth(BodyPart.LEFT_LEG, 49f);
        hc.updateCrippleStatus();
        assertEquals(CrippleLevel.MODERATE, hc.getCrippleLevel(BodyPart.LEFT_LEG));

        // Damage to 24 HP -> SEVERE crippling
        hc.setHealth(BodyPart.LEFT_LEG, 24f);
        hc.updateCrippleStatus();
        assertEquals(CrippleLevel.SEVERE, hc.getCrippleLevel(BodyPart.LEFT_LEG));

        // Recovery to 75 HP -> NONE
        hc.setHealth(BodyPart.LEFT_LEG, 75f);
        hc.updateCrippleStatus();
        assertEquals(CrippleLevel.NONE, hc.getCrippleLevel(BodyPart.LEFT_LEG));
    }

    @Test
    public void crippledTicksDecrement() {
        HealthComponent hc = new HealthComponent();
        hc.setHealth(BodyPart.LEFT_ARM, 50f);
        hc.updateCrippleStatus();

        int ticksBefore = hc.crippledTicksSnapshot().get(BodyPart.LEFT_ARM);
        hc.tick();
        int ticksAfter = hc.crippledTicksSnapshot().get(BodyPart.LEFT_ARM);

        assertTrue(ticksBefore > 0, "Cripple duration should be > 0 after status update");
        assertEquals(ticksBefore - 1, ticksAfter, "Ticks should decrement by 1 each tick");
    }

    @Test
    public void crippleLevelRecoveryWhenHealthIncreases() {
        HealthComponent hc = new HealthComponent();

        // Severe damage
        hc.setHealth(BodyPart.RIGHT_LEG, 20f);
        hc.updateCrippleStatus();
        assertEquals(CrippleLevel.SEVERE, hc.getCrippleLevel(BodyPart.RIGHT_LEG));

        // Recovery to full health
        hc.setHealth(BodyPart.RIGHT_LEG, 100f);
        hc.updateCrippleStatus();
        assertEquals(CrippleLevel.NONE, hc.getCrippleLevel(BodyPart.RIGHT_LEG));
    }
}
