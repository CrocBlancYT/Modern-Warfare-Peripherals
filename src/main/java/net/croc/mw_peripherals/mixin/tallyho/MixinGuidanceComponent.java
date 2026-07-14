package net.croc.mw_peripherals.mixin.tallyho;

import edn.stratodonut.tallyho.missile.GuidanceComponent;
import net.croc.mw_peripherals.Main;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.croc.mw_peripherals.integration.tallyho.ForeignMissileRegistry.G;
@Mixin(GuidanceComponent.class)
public class MixinGuidanceComponent {
    @Unique
    private static final double G_per_mps = 5*G / 100;

    @Inject(method = {"ProportionalGuidance"}, at = {@At("HEAD")}, remap = false, cancellable = true)
    private static void betterGuidance(Vec3 targetPos, Vec3 targetVel,
                         Vec3 missilePos, Vec3 missileVel,
                         float coefficient, float maxBearableG,
                         CallbackInfoReturnable<Vec3> cir) {

        Vec3 rel_pos = targetPos.subtract(missilePos);
        Vec3 v_r = targetVel.scale(0.05).subtract(missileVel);
        Vec3 omega_r = rel_pos.cross(v_r).scale((double) 1.0F / rel_pos.dot(rel_pos));
        Vec3 accel = missileVel.normalize().cross(omega_r).scale((double)(-coefficient) * v_r.length());

        double maxTurningG = G_per_mps * (missileVel.length() * 20);
        double effective_maxG = Math.min(maxTurningG, maxBearableG);
        double target_G = accel.length();

        Vec3 guid = missileVel.add(accel.normalize().scale(Math.clamp(0.0F, effective_maxG, target_G)));

        cir.setReturnValue(guid);
        cir.cancel();
    }
}