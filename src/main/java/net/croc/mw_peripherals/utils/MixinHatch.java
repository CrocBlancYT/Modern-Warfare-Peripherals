package net.croc.mw_peripherals.utils;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface MixinHatch {
    int getHeight();
    void setHeight(int height);
    void onWrenched(BlockHitResult hitResult);
}