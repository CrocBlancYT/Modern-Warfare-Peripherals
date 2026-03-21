package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class JetEngineBlockEntity extends BlockEntity {
    private float current_throttle_alpha = 0.0F;

    private float target_throttle_alpha = 0.0F;

    public JetEngineBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.JET_ENGINE_BLOCK_ENTITY.get(), pos, state);
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(JetEngineBlock.FACING);
    }

    public void setThrottle(float alpha) {
        this.target_throttle_alpha = alpha;
    }

    public float getThrust() {
        return this.current_throttle_alpha;
    }

    private static float lerp(float a, float b, float alpha) {
        return a + (b - a) * alpha;
    }

    public float getTargetThrottle() {
        int signal = this.level.getBestNeighborSignal(this.getBlockPos());
        return signal * 0.0625F;
    }

    private float getMaxFueledThrottle(int fuel) {
        if (fuel > 0)
            return 1.0F;
        return 0.0F;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, JetEngineBlockEntity blockEntity) {
        float maxThrottle = blockEntity.getMaxFueledThrottle(1);
        float targetThrottle = blockEntity.getTargetThrottle();
        blockEntity.current_throttle_alpha = lerp(
                blockEntity.current_throttle_alpha,
                Math.min(maxThrottle, targetThrottle),
                0.2F);
    }
}
