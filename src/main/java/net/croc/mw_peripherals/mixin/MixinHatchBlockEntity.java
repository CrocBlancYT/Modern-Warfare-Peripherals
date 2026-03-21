package net.croc.mw_peripherals.mixin;

import net.croc.mw_peripherals.utils.MixinHatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import net.mcreator.rha.block.entity.*;

@Mixin({
        HatchalgaeBlockEntity.class, HatcholiveBlockEntity.class, Hatch4boBlockEntity.class, HatchardenneBlockEntity.class, HatchazureBlockEntity.class,
        HatchhorizonBlockEntity.class, HatchpattonBlockEntity.class, HatchcactusBlockEntity.class, HatchcamelBlockEntity.class, HatchcharcoalBlockEntity.class,
        HatchdesertBlockEntity.class, HatchdustBlockEntity.class, HatchgelbBlockEntity.class, HatchginkBlockEntity.class, HatchgorgeBlockEntity.class,
        HatchgravelBlockEntity.class, HatchgrizzlyBlockEntity.class, HatchhideBlockEntity.class, HatchjetBlockEntity.class, HatchkampfgrauBlockEntity.class,
        HatchkatBlockEntity.class, HatchpanzergrauBlockEntity.class, HatchpineBlockEntity.class, HatchslateBlockEntity.class, HatchsnowBlockEntity.class,
        HatchscaleBlockEntity.class, HatchleyBlockEntity.class, HatchrotaBlockEntity.class, HatchtypeBlockEntity.class, HatchcoralBlockEntity.class,
        HatchcherenkovBlockEntity.class, HatchparadeBlockEntity.class
})
public abstract class MixinHatchBlockEntity implements MixinHatch {
    @Unique private int height = 0;

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    private void injectSave(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("height", height);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void injectLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("height")) {
            this.height = tag.getInt("height");
        }
    }

    private final int OFFSET = -13;

    @Override
    public void onWrenched(BlockHitResult hitResult) {
        setHeight((int) ((hitResult.getLocation().y - ((BlockEntity)(Object)this).getBlockPos().getY()) * 16 + OFFSET));
    }

    private final int MIN = -15;
    private final int MAX = 0;

    @Override
    public int getHeight() {
        if (this.height >= MAX) return MAX;
        if (this.height <= MIN) return MIN;
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
        ((BlockEntity)(Object)this).setChanged();
    }
}