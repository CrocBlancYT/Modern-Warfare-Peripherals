package net.croc.mw_peripherals.mixin.tallyho;

import edn.stratodonut.tallyho.entity.LaserPointEntity;
import edn.stratodonut.tallyho.missile.guid.LaserSeeker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.lang.ref.WeakReference;

@Pseudo
@Mixin({LaserSeeker.class})
public interface LaserSeekerAccessor {
    @Accessor(remap = false)
    WeakReference<LaserPointEntity> getTarget();

    @Accessor(remap = false)
    LaserSeeker.SeekerProperties getProperties();
}