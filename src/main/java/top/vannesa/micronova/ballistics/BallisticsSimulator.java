package top.vannesa.micronova.ballistics;

import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.util.List;

/**
 * Simulates ballistic trajectories with realistic physics.
 * - Gravity acceleration (9.8 m/s²)
 * - Air resistance (exponential velocity decay)
 * - Discrete time steps (50ms = 1 Minecraft tick)
 */
public class BallisticsSimulator {

    // Physics constants
    public static final float GRAVITY = 9.8f;           // m/s² (Earth gravity)
    public static final float TIME_STEP = 0.05f;        // seconds (1 Minecraft tick)
    public static final float BLOCKS_PER_METER = 1.0f;  // Minecraft coordinate scale

    private BallisticsSimulator() {}

    /**
     * Simulate a ballistic trajectory from start position with initial velocity.
     * @param startPos       starting position (Minecraft coordinates)
     * @param startVelocity  initial velocity (m/s)
     * @param ballistics     ammunition ballistics profile
     * @param maxDistance    maximum range before stopping simulation (meters)
     * @return list of trajectory points (including start and end)
     */
    public static List<TrajectoryPoint> simulate(Vec3d startPos, Vec3d startVelocity,
                                                  Ballistics ballistics, float maxDistance) {
        List<TrajectoryPoint> trajectory = new ArrayList<>();

        // Initial state (relative from start)
        double x = 0;
        double y = 0;
        double z = 0;
        
        // Normalize and scale velocity to ballistics initial velocity
        float speed = (float) startVelocity.length();
        float vx, vy, vz;
        if (speed > 0) {
            float scale = ballistics.initialVelocity / speed;
            vx = (float) startVelocity.x * scale;
            vy = (float) startVelocity.y * scale;
            vz = (float) startVelocity.z * scale;
        } else {
            vx = ballistics.initialVelocity;
            vy = 0;
            vz = 0;
        }
        
        float time = 0f;

        // Add initial point
        float velocity = ballistics.initialVelocity;
        float ke = ballistics.getKineticEnergy(velocity);
        trajectory.add(new TrajectoryPoint(x, y, z, vx, vy, vz, velocity, ke, time));

        // Simulate until projectile stops or exits bounds
        int maxIterations = 10000;
        int iteration = 0;
        while (iteration < maxIterations) {
            iteration++;
            time += TIME_STEP;

            // Get velocity at this time (with air resistance decay)
            velocity = ballistics.getVelocityAt(time);
            if (velocity < 0.5f) break;  // Stop if velocity too low

            // Check distance traveled
            double distanceTraveled = Math.sqrt(x * x + y * y + z * z);
            if (distanceTraveled > maxDistance) break;

            // Get current speed magnitude
            float currentSpeed = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
            
            // Normalize velocity direction and apply new speed
            if (currentSpeed > 0.01f) {
                float scale = velocity / currentSpeed;
                vx *= scale;
                vy *= scale;
                vz *= scale;
            }

            // Apply gravity to vertical velocity
            vy -= GRAVITY * TIME_STEP;

            // Update position
            x += vx * TIME_STEP;
            y += vy * TIME_STEP;
            z += vz * TIME_STEP;

            // Calculate kinetic energy
            ke = ballistics.getKineticEnergy(velocity);

            // Add trajectory point
            trajectory.add(new TrajectoryPoint(x, y, z, vx, vy, vz, velocity, ke, time));

            // Stop if projectile went below Y=-10
            if (y < -10) break;
        }

        return trajectory;
    }

    /**
     * Calculate distance at which projectile loses penetration capability.
     */
    public static float getMaxRangeForPenetration(Ballistics ballistics) {
        // Find time when velocity drops below minPenetrationSpeed
        // v(t) = v₀ * e^(-k*t)
        // minV = v₀ * e^(-k*t) => t = -ln(minV/v₀) / k
        float k = ballistics.dragCoefficient / ballistics.mass;
        if (k <= 0) return ballistics.range;
        
        float ratio = ballistics.minPenetrationSpeed / ballistics.initialVelocity;
        if (ratio >= 1) return 0f;
        if (ratio <= 0) return ballistics.range;
        
        float time = -(float) Math.log(ratio) / k;
        
        // Distance = integral of v(t) from 0 to time
        // For exponential decay: integral of v₀*e^(-k*t) = (v₀/k) * (1 - e^(-k*t))
        float distance = (ballistics.initialVelocity / k) * (1f - (float) Math.exp(-k * time));
        return Math.min(distance, ballistics.range);
    }
}
