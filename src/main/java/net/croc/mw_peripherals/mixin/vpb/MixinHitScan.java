package net.croc.mw_peripherals.mixin.vpb;

import com.vicmatskiv.pointblank.client.ClientEventHandler;
import com.vicmatskiv.pointblank.client.GunClientState;
import com.vicmatskiv.pointblank.item.GunItem;
import com.vicmatskiv.pointblank.util.HitScan;
import com.vicmatskiv.pointblank.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


@Mixin(value = HitScan.class, priority = 1100)
public class MixinHitScan {
    /*private static class LockableEntity extends Entity {
        public LockableEntity(Level level, Vec3 pos) {
            super(EntityType.ARMOR_STAND, level);
            this.setPos(pos);
        }

        @Override
        protected void defineSynchedData() { }

        @Override
        protected void readAdditionalSaveData(CompoundTag compoundTag) { }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) { }
    }

    @Inject(method = "getNearestObjectInCrosshair", at = @At("RETURN"), cancellable = true, remap = false)
    private static void vpbMissileLockOnShips(LivingEntity player,
                                              Vec3 startPos, Vec3 directionVector,
                                              float partialTicks, double maxDistance,
                                              Predicate<Block> isBreakableBlock,
                                              Predicate<Block> isPassThroughBlock,
                                              List<BlockPos> blockPosToBreakOutput,
                                              CallbackInfoReturnable<HitResult> cir) {


        Vec3 endVec = startPos.add(directionVector.x * maxDistance, directionVector.y * maxDistance, directionVector.z * maxDistance);
        AABB playerBox = player.getBoundingBox();
        AABB expandedBox = playerBox.expandTowards(directionVector.x * maxDistance, directionVector.y * maxDistance, directionVector.z * maxDistance);
        Entity closestEntity = null;
        double closestEntityDistance = maxDistance;
        Vec3 closestEntityHitVec = null;

        // RESUME RESULT
        HitResult res = cir.getReturnValue();
        if (res.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
            Entity hitScanEntity = ((EntityHitResult) res).getEntity();
            closestEntity = hitScanEntity;
            closestEntityHitVec = hitScanEntity.position();
            closestEntityDistance = startPos.distanceTo(closestEntityHitVec);
        } else {
            closestEntityHitVec = res.getLocation();
            closestEntityDistance = startPos.distanceTo(closestEntityHitVec);
        }

        // INTERSECT SHIPS
        for (Ship ship : VSGameUtilsKt.getShipsIntersecting(player.level(), expandedBox)) {
            AABBdc shipBox = ship.getWorldAABB();
            Vector3dc pos = ship.getTransform().getPositionInWorld();

            AABB entityBox = new AABB(
                    shipBox.minX(), shipBox.minY(), shipBox.minZ(),
                    shipBox.maxX(), shipBox.maxY(), shipBox.maxZ()
            ).inflate(0.3);

            Optional<Vec3> hitVec = entityBox.clip(startPos, endVec);
            if (hitVec.isPresent()) {
                double distanceToEntity = startPos.distanceTo((hitVec.get()));
                if (distanceToEntity < closestEntityDistance) {
                    closestEntity = new LockableEntity(player.level(), new Vec3(pos.x(), pos.y(), pos.z()));
                    closestEntityDistance = distanceToEntity;
                    closestEntityHitVec = hitVec.get();
                }
            }
        }

        cir.setReturnValue(((closestEntity != null ? new EntityHitResult(closestEntity, closestEntityHitVec) : res)));
    }*/
}