package net.croc.mw_peripherals.mixin.tallyho;

import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.integration.tallyho.guid.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin({ARHSeeker.class, SARHSeeker.class, ARMGuidance.class, IIRSeeker.class, IRSeekerRear.class, IRSeekerIRCCM.class, IRSeekerGround.class})
public interface TargetAccessor {
    @Accessor(remap = false)
    Target<?> getTarget();

    @Accessor(remap = false)
    void setTarget(Target<?> target);
}