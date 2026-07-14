package net.croc.mw_peripherals.mixin;

import net.croc.mw_peripherals.integration.tallyho.tracker.AerialTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.tournament.blocks.ThrusterBlock;
import org.valkyrienskies.tournament.ship.TournamentShips;

@Mixin({ThrusterBlock.class})
public class MixinThrusterBlock {
    @Inject(method = {"getShipControl"}, at = {@At("HEAD")}, remap = false)
    public final void detectableThruster(Level level, BlockPos pos, CallbackInfoReturnable<TournamentShips> cir) {
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);

        if (ship != null) {
            AerialTracker.subscribe(ship);
        }
    }
}