package net.croc.mw_peripherals.integration.tallyho;

import edn.stratodonut.tallyho.missile.Target;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class BlockTarget {
    static final AABB DEFAULT_AABB;

    static {
        DEFAULT_AABB = AABB.unitCubeFromLowerCorner(Vec3.ZERO);
    }

    public static class BlockEntityTarget extends Target<BlockEntity> {
        WeakReference<BlockEntity> be;

        public BlockEntityTarget(BlockEntity be) {
            this.be = new WeakReference<>(be);
        }

        public Vec3 position() {
            return !this.isAlive() ? Vec3.ZERO : this.be.get().getBlockPos().getCenter();
        }

        public Vec3 velocity() {
            return Vec3.ZERO;
        }

        public AABB boundingBox() {
            return !this.isAlive() ? DEFAULT_AABB : this.be.get().getRenderBoundingBox();
        }

        @Nullable
        public BlockEntity get() {
            return this.be.get();
        }

        public boolean isAlive() {
            return this.be.get() != null;
        }
    }

    @Nullable
    public static Target<?> getTarget(Level level, BlockPos pos) {
        Ship s = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (s != null) {
            return new Target.ShipTarget(s);
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            return new BlockEntityTarget(be);
        }

        return null;
    }
}