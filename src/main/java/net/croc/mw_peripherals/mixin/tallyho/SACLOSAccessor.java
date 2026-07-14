package net.croc.mw_peripherals.mixin.tallyho;

import edn.stratodonut.tallyho.missile.guid.SACLOS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Pseudo
@Mixin({SACLOS.class})
public interface SACLOSAccessor {
    @Accessor(value = "properties", remap = false)
    SACLOS.SeekerProperties getProperties();

    @Accessor(value = "steerOrigin", remap = false)
    Vec3 steerOrigin();

    @Accessor(value = "steerDir", remap = false)
    Vec3 steerDir();
}