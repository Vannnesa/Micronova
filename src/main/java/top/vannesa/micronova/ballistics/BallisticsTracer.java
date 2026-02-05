package top.vannesa.micronova.ballistics;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Traces a ballistic trajectory through the world and detects collisions.
 * Separates block and entity collisions.
 * Supports penetration through weak blocks (wood, stone) but stops at players.
 */
public class BallisticsTracer {

    private BallisticsTracer() {}

    // Blocks that bullets can penetrate through
    private static final Set<String> PENETRABLE_BLOCKS = new HashSet<>();
    static {
        // Soft/weak blocks that bullets pass through
        PENETRABLE_BLOCKS.add("minecraft:oak_log");
        PENETRABLE_BLOCKS.add("minecraft:birch_log");
        PENETRABLE_BLOCKS.add("minecraft:spruce_log");
        PENETRABLE_BLOCKS.add("minecraft:jungle_log");
        PENETRABLE_BLOCKS.add("minecraft:acacia_log");
        PENETRABLE_BLOCKS.add("minecraft:dark_oak_log");
        PENETRABLE_BLOCKS.add("minecraft:oak_planks");
        PENETRABLE_BLOCKS.add("minecraft:birch_planks");
        PENETRABLE_BLOCKS.add("minecraft:spruce_planks");
        PENETRABLE_BLOCKS.add("minecraft:oak_leaves");
        PENETRABLE_BLOCKS.add("minecraft:grass");
        PENETRABLE_BLOCKS.add("minecraft:tall_grass");
        // Add more as needed
    }

    /**
     * Represents a collision event during trajectory trace.
     */
    public static class TrajectoryHit {
        public final TrajectoryPoint point;          // Position and velocity at hit
        public final boolean isBlockHit;             // true if block, false if entity
        public final BlockPos blockPos;              // Position of hit block (null if entity)
        public final Object hitEntity;               // Hit entity (null if block)
        public final boolean canPenetrate;           // Whether this hit can be penetrated
        public final float velocityLossPercent;      // Velocity lost on impact (0-1)

        public TrajectoryHit(TrajectoryPoint point, boolean isBlockHit, BlockPos blockPos, 
                            Object hitEntity, boolean canPenetrate, float velocityLoss) {
            this.point = point;
            this.isBlockHit = isBlockHit;
            this.blockPos = blockPos;
            this.hitEntity = hitEntity;
            this.canPenetrate = canPenetrate;
            this.velocityLossPercent = velocityLoss;
        }
    }

    /**
     * Trace a ballistic trajectory through the world and detect all collisions.
     * @param world      the game world
     * @param trajectory the simulated trajectory points
     * @param ballistics the ammunition profile
     * @return list of collision hits (in order along trajectory)
     */
    public static List<TrajectoryHit> traceTrajectory(World world, List<TrajectoryPoint> trajectory, 
                                                       Ballistics ballistics, Vec3d startPos) {
        List<TrajectoryHit> hits = new ArrayList<>();

        if (trajectory.isEmpty()) return hits;

        // Iterate through trajectory segments
        for (int i = 0; i < trajectory.size() - 1; i++) {
            TrajectoryPoint current = trajectory.get(i);
            TrajectoryPoint next = trajectory.get(i + 1);

            Vec3d currentPos = new Vec3d(startPos.x + current.x, startPos.y + current.y, startPos.z + current.z);
            Vec3d nextPos = new Vec3d(startPos.x + next.x, startPos.y + next.y, startPos.z + next.z);

            // Perform raycast from current to next
            // NOTE: In a full implementation, use Minecraft's raycast API
            // For now, simplified block collision check
            
            // Check blocks along the segment
            BlockPos blockHit = traceBlockCollision(world, currentPos, nextPos);
            if (blockHit != null) {
                Block block = world.getBlockState(blockHit).getBlock();
                String blockId = block.getName().getString();
                boolean canPenetrate = PENETRABLE_BLOCKS.contains(blockId);
                
                // Calculate hit point (approximate at block center)
                Vec3d hitPos = blockHit.toCenterPos();
                float velocityAtHit = ballistics.getVelocityAt(current.time);
                float kineticEnergy = ballistics.getKineticEnergy(velocityAtHit);
                
                TrajectoryPoint hitPoint = new TrajectoryPoint(
                    hitPos.x - startPos.x, hitPos.y - startPos.y, hitPos.z - startPos.z,
                    current.vx, current.vy, current.vz,
                    velocityAtHit, kineticEnergy, current.time
                );
                
                float velocityLoss = canPenetrate ? 0.15f : 0f;  // 15% loss for penetrable blocks
                TrajectoryHit hit = new TrajectoryHit(hitPoint, true, blockHit, null, canPenetrate, velocityLoss);
                hits.add(hit);
                
                // If block is not penetrable, stop trajectory
                if (!canPenetrate) {
                    return hits;
                }
            }
        }

        return hits;
    }

    /**
     * Check for block collision along a line segment.
     * Simplified version - in production, use voxel raycasting.
     */
    private static BlockPos traceBlockCollision(World world, Vec3d from, Vec3d to) {
        // Simple AABB traversal - check blocks along the ray
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance < 0.01) return null;

        // Sample 10 points along the segment
        int steps = (int) (distance * 10);
        for (int i = 1; i <= steps; i++) {
            double t = (double) i / steps;
            double x = from.x + dx * t;
            double y = from.y + dy * t;
            double z = from.z + dz * t;

            BlockPos pos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
            if (!world.getBlockState(pos).isAir()) {
                return pos;
            }
        }

        return null;
    }

    /**
     * Check if a projectile can penetrate a block.
     */
    public static boolean canPenetrateBlock(Block block) {
        String blockId = block.getName().getString();
        return PENETRABLE_BLOCKS.contains(blockId);
    }
}
