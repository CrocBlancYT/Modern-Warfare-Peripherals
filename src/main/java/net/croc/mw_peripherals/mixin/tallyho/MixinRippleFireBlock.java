package net.croc.mw_peripherals.mixin.tallyho;

import edn.stratodonut.tallyho.block.RippleFireBlock;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;
import static net.croc.mw_peripherals.RegistryBlockStateInfo.*;

@Mixin(RippleFireBlock.class)
public class MixinRippleFireBlock extends Block {

    @Unique
    private static final int MISSILES_REFRESH_TICKS = 200;

    @Unique
    private static final double SIZE = 20D;

    public MixinRippleFireBlock(Properties properties) { super(properties); }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInstance(CallbackInfo ci) {
        this.registerDefaultState(
                this.getStateDefinition().any().setValue(MISSILES, 0)
        );
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void addMissilesProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(MISSILES);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide) return;
        level.scheduleTick(pos, this, MISSILES_REFRESH_TICKS);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        Ship s = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (s == null) {
            Vec3 worldPos = VectorConversionsMCKt.toMinecraft(s.getShipToWorld().transformPosition(VectorConversionsMCKt.toJOML(Vec3.atCenterOf(pos))));

            List<Entity> entities = level.getEntities((Entity) null,
                    AABB.ofSize(worldPos, SIZE, SIZE, SIZE),
                    e -> (e instanceof MountedMissileEntity && e.isAlive()) && s.equals(VSGameUtilsKt.getShipManaging(e)));

            double mass_sum = 0D;

            for (Entity entity : entities) {
                if (entity instanceof MountedMissileEntity missile) {
                    mass_sum = mass_sum + getMassForMissile(missile.getMissileId());
                }
            }

            BlockState newState = state.setValue(MISSILES, encode(mass_sum));
            level.setBlock(pos, newState, 3);
        }

        level.scheduleTick(pos, this, MISSILES_REFRESH_TICKS);
    }
}