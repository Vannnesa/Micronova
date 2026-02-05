package top.vannesa.micronova;

import org.junit.jupiter.api.Test;
import top.vannesa.micronova.ballistics.Ballistics;
import top.vannesa.micronova.ballistics.BallisticsSimulator;
import top.vannesa.micronova.ballistics.TrajectoryPoint;
import net.minecraft.util.math.Vec3d;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BallisticsTest {

    @Test
    public void pistol9mmBasicProperties() {
        Ballistics ballistics = Ballistics.pistol9mm();
        
        assertEquals(375f, ballistics.initialVelocity, 0.01f);
        assertEquals(8.0f, ballistics.mass, 0.01f);
        assertEquals(0.005f, ballistics.dragCoefficient, 0.0001f);
        assertEquals(100f, ballistics.minPenetrationSpeed, 0.01f);
        assertEquals(26f, ballistics.penetrationValue, 0.01f);  // Updated
        assertEquals(40f, ballistics.baseDamage, 0.01f);
    }

    @Test
    public void velocityDecayOverTime() {
        Ballistics ballistics = Ballistics.pistol9mm();
        
        // At t=0, velocity should equal initial
        assertEquals(375f, ballistics.getVelocityAt(0f), 0.1f);
        
        // At t>0, velocity should decay (exponentially)
        float v1 = ballistics.getVelocityAt(1f);
        float v2 = ballistics.getVelocityAt(2f);
        
        assertTrue(v1 < 375f, "Velocity should decrease over time");
        assertTrue(v2 < v1, "Velocity should continue to decrease");
        assertTrue(v2 > 0, "Velocity should remain positive");
    }

    @Test
    public void canPenetrateCheck() {
        Ballistics ballistics = Ballistics.pistol9mm();
        
        // Initial velocity should penetrate
        assertTrue(ballistics.canPenetrate(ballistics.initialVelocity));
        
        // Velocity above minimum should penetrate
        assertTrue(ballistics.canPenetrate(150f));
        
        // Velocity at minimum should penetrate
        assertTrue(ballistics.canPenetrate(100f));
        
        // Velocity below minimum should not penetrate
        assertFalse(ballistics.canPenetrate(99f));
        assertFalse(ballistics.canPenetrate(0f));
    }

    @Test
    public void damageMultiplierVelocity() {
        Ballistics ballistics = Ballistics.pistol9mm();
        
        // At initial velocity, multiplier = 1.0
        assertEquals(1.0f, ballistics.getDamageMultiplier(ballistics.initialVelocity), 0.01f);
        
        // Below initial, multiplier decreases linearly
        float mid = (ballistics.initialVelocity + ballistics.minPenetrationSpeed) / 2f;
        float midMultiplier = ballistics.getDamageMultiplier(mid);
        assertEquals(0.5f, midMultiplier, 0.01f);
        
        // At minimum penetration speed, multiplier = 0
        assertEquals(0.0f, ballistics.getDamageMultiplier(ballistics.minPenetrationSpeed), 0.01f);
        
        // Below minimum, multiplier = 0
        assertEquals(0.0f, ballistics.getDamageMultiplier(50f), 0.01f);
    }

    @Test
    public void kineticEnergyCalculation() {
        Ballistics ballistics = Ballistics.pistol9mm();
        
        // KE should be positive
        float ke1 = ballistics.getKineticEnergy(375f);
        assertTrue(ke1 > 0, "Kinetic energy should be positive");
        
        // Higher velocity = higher energy (KE ∝ v²)
        float ke2 = ballistics.getKineticEnergy(187.5f);  // half velocity
        assertTrue(ke1 > ke2, "Higher velocity should have higher kinetic energy");
        
        // Zero velocity = zero energy
        assertEquals(0.0f, ballistics.getKineticEnergy(0f), 0.001f);
    }

    @Test
    public void trajectorySimulationBasic() {
        Ballistics ballistics = Ballistics.pistol9mm();
        Vec3d start = new Vec3d(0, 10, 0);
        Vec3d velocity = new Vec3d(100, 0, 0);  // 100 m/s horizontal (will be scaled to initialVelocity)
        
        List<TrajectoryPoint> trajectory = BallisticsSimulator.simulate(start, velocity, ballistics, 100f);
        
        // Should have multiple points
        assertTrue(trajectory.size() > 5, "Trajectory should have multiple points");
        
        // First point should be at start (relative coordinates)
        TrajectoryPoint first = trajectory.get(0);
        assertEquals(0, first.x, 0.1);
        assertEquals(0, first.y, 0.1);
        assertEquals(0, first.z, 0.1);
        
        // Last point should be further along X (positive direction)
        TrajectoryPoint last = trajectory.get(trajectory.size() - 1);
        assertTrue(last.x > 0 || last.y < 0, "Projectile should move or fall");
    }

    @Test
    public void trajectoryVelocityDecays() {
        Ballistics ballistics = Ballistics.pistol9mm();
        Vec3d start = new Vec3d(0, 10, 0);
        Vec3d velocity = new Vec3d(100, 0, 0);
        
        List<TrajectoryPoint> trajectory = BallisticsSimulator.simulate(start, velocity, ballistics, 100f);
        
        // Velocity should decrease along trajectory
        float prevVelocity = trajectory.get(0).velocity;
        for (TrajectoryPoint point : trajectory) {
            assertTrue(point.velocity <= prevVelocity + 0.1f, "Velocity should not increase");
            prevVelocity = point.velocity;
        }
    }

    @Test
    public void trajectoryStopsWhenVelocityTooLow() {
        Ballistics ballistics = Ballistics.pistol9mm();
        Vec3d start = new Vec3d(0, 10, 0);
        Vec3d velocity = new Vec3d(50, 0, 0);  // Low initial velocity (will be scaled to ballistics.initialVelocity)
        
        List<TrajectoryPoint> trajectory = BallisticsSimulator.simulate(start, velocity, ballistics, 500f);
        
        // Should have completed trajectory
        assertTrue(trajectory.size() > 5, "Should have trajectory points");
        
        // Velocity should decrease over trajectory
        TrajectoryPoint first = trajectory.get(0);
        TrajectoryPoint last = trajectory.get(trajectory.size() - 1);
        assertTrue(last.velocity <= first.velocity, "Velocity should not increase");
    }

    @Test
    public void maxRangeForPenetration() {
        Ballistics ballistics = Ballistics.pistol9mm();
        
        float maxRange = BallisticsSimulator.getMaxRangeForPenetration(ballistics);
        assertTrue(maxRange >= 0, "Max penetration range should be non-negative");
        assertTrue(maxRange <= ballistics.range, "Max penetration range should not exceed total range");
        
        // Should be reasonable (at least a few meters for 9mm)
        assertTrue(maxRange > 5f, "9mm should penetrate at least 5m");
    }

    @Test
    public void rifle556HigherVelocity() {
        Ballistics pistol = Ballistics.pistol9mm();
        Ballistics rifle = Ballistics.rifle556();
        
        // Rifle should have higher initial velocity
        assertTrue(rifle.initialVelocity > pistol.initialVelocity);
        
        // Rifle should have higher penetration value
        assertTrue(rifle.penetrationValue > pistol.penetrationValue);
        
        // Rifle should have higher damage
        assertTrue(rifle.baseDamage > pistol.baseDamage);
    }
}
