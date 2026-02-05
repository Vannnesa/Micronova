package top.vannesa.micronova.ballistics;

/**
 * Represents a point along a projectile's trajectory.
 * Stores position, velocity, and energy at a specific time.
 */
public class TrajectoryPoint {

    public final double x, y, z;              // Position in world space (meters)
    public final float vx, vy, vz;            // Velocity components (m/s)
    public final float velocity;              // Speed magnitude (m/s)
    public final float kineticEnergy;         // Kinetic energy (Joules)
    public final float time;                  // Time from launch (seconds)

    public TrajectoryPoint(double x, double y, double z, float vx, float vy, float vz, 
                          float velocity, float kineticEnergy, float time) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.velocity = velocity;
        this.kineticEnergy = kineticEnergy;
        this.time = time;
    }

    /**
     * Get velocity magnitude (speed).
     */
    public float getSpeed() {
        return velocity;
    }

    /**
     * Distance to another point.
     */
    public double distanceTo(TrajectoryPoint other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Distance from launch origin.
     */
    public double getDistance() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public String toString() {
        return String.format("TrajectoryPoint(%.2f, %.2f, %.2f) v=%.1f m/s ke=%.1f J t=%.3f s",
                x, y, z, velocity, kineticEnergy, time);
    }
}
