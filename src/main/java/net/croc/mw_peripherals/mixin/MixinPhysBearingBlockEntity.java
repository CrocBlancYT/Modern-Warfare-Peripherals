package net.croc.mw_peripherals.mixin;

import net.croc.mw_peripherals.integration.tallyho.tracker.AerialTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.clockwork.content.contraptions.phys.bearing.PhysBearingBlockEntity;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin({PhysBearingBlockEntity.class})
public class MixinPhysBearingBlockEntity {
    @Inject(method = {"assemble"}, at = {@At("HEAD")}, remap = false)
    private final void detectablePhysBearing(CallbackInfo ci) {
        PhysBearingBlockEntity be = (PhysBearingBlockEntity) (Object) this;
        Ship ship = VSGameUtilsKt.getShipManagingPos(be.getLevel(), be.getBlockPos());

        if (ship != null) {
            AerialTracker.subscribe(ship);
        }
    }
}