package net.croc.mw_peripherals.mixin.tallyho;

import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.integration.tallyho.guid.IRSeeker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin({IRSeeker.class})
public interface IRSeekerAccessor {
    @Accessor(remap = false)
    Target<?> getTarget();
    
    @Accessor(remap = false)
    IRSeeker.SeekerProperties getProperties();
}