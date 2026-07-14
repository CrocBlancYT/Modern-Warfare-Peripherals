package net.croc.mw_peripherals.integration.tallyho;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import javax.annotation.Nonnull;

import net.croc.mw_peripherals.entity.MountedPodEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public abstract class PodComponent {
    public abstract void tick(MountedPodEntity missile);

    public abstract boolean launch(MountedPodEntity missile, Vec3 dir, float boost);

    public void deserialiseNBT(@Nonnull CompoundTag nbt) {}

    @Nonnull
    public CompoundTag serialiseNBT() {
        return new CompoundTag();
    }
}
