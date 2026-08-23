package net.croc.mw_peripherals.mixin;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;

@Mixin(CannonMountBlockEntity.class)
public class MixinCannonMountBlockEntity {
    @Unique
    private BlockPos synced_pos = null;

    @Inject(method = "tick", at = @At("RETURN"), remap = false)
    private void injectTick(CallbackInfo ci) {
        if (synced_pos == null) return;
        CannonMountBlockEntity mount = (CannonMountBlockEntity) (Object) this;
        Level level = mount.getLevel();

        if (level != null && level.getBlockEntity(synced_pos) instanceof MechanicalBearingBlockEntity bearing) {
            BearingBlock block = (BearingBlock) level.getBlockState(synced_pos).getBlock();
            Direction.Axis axis = block.getRotationAxis(level.getBlockState(synced_pos));
            CannonMountAccessor accessor = (CannonMountAccessor) mount;

            if (axis.isHorizontal()) {
                bearing.setAngle(accessor.getCannonPitch());
            } else {
                bearing.setAngle(accessor.getCannonYaw());
            }
        }

    }

    @Inject(method = "lazyTick", at = @At("RETURN"), remap = false)
    private void injectLazyTick(CallbackInfo ci) {
        injectTick(ci);

    }

    @Inject(method = "write", at = @At("RETURN"), remap = false)
    private void injectWrite(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        tag.put("synced", NbtUtils.writeBlockPos(synced_pos != null ? synced_pos : BlockPos.ZERO));
    }

    @Inject(method = "read", at = @At("RETURN"), remap = false)
    private void injectRead(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        if (tag.contains("synced")) {
            synced_pos = NbtUtils.readBlockPos(tag.getCompound("synced"));
        }
    }
}