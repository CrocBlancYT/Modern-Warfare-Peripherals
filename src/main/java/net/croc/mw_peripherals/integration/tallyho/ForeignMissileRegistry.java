package net.croc.mw_peripherals.integration.tallyho;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.*;
import edn.stratodonut.tallyho.missile.fuze.ProximityFuze;
import edn.stratodonut.tallyho.missile.guid.LaserSeeker;
import edn.stratodonut.tallyho.missile.guid.SACLOS;
import edn.stratodonut.tallyho.missile.motor.RocketMotor;
import edn.stratodonut.tallyho.missile.motor.RocketMotorNoLift;
import edn.stratodonut.tallyho.missile.warhead.*;
import net.croc.mw_peripherals.integration.tallyho.guid.*;
import net.croc.mw_peripherals.integration.tallyho.pod.RocketPod;
import net.croc.mw_peripherals.mixin.tallyho.SACLOSAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

import static edn.stratodonut.tallyho.missile.GuidanceComponent.tryLock;
import static edn.stratodonut.tallyho.missile.MissileRegistry.MissileRegistryEntry;
import static edn.stratodonut.tallyho.missile.MissileRegistry.register;

public class ForeignMissileRegistry {
    public static void init() { }

    public static abstract class SteerController {
        public abstract boolean isAlive();

        public abstract void attemptRelink(Level param1Level, Vec3 param1Vec3);

        public abstract Vec3 getSteerOrigin();

        public abstract Vec3 getSteerDir();

        public abstract void deserialiseNBT(@Nonnull CompoundTag param1CompoundTag);

        @Nonnull
        public abstract CompoundTag serialiseNBT();
    }

    public final static float G = 0.035f;
    public final static float invG = 1 / G;

    public final static float km = 50f;

    public static boolean isActiveIR(MountedMissileEntity e, int fov, int range) {
        return tryLock(e.level(), e.getLookAngle(), e.position(), null, fov, range).isPresent();
    }

    public static boolean isActiveSACLOS(SACLOS s) {
        SACLOSAccessor saclos = (SACLOSAccessor) s;
        return saclos.steerOrigin() != null && saclos.steerDir() != null;
    }

    static {
        MissileRegistry.AIM_9L = register((new ForeignMissileEntry("aim9lima", new FragWarhead(8.0F, 0.1F)))
                .withGuidance(new IRSeeker.Factory(5, 43, 0.0192D, 4, 35*G, (int) (5.5*km), 0.9F))
                .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
                .withFuze(new ProximityFuze.Factory(9.0F, 2))
                .withSize(0.2F));

        MissileRegistry.AGM_114B = register(new ForeignMissileEntry("agm114b", new ShapedChargeWarhead(84.0F, 5.0F))
                .withGuidance(new LaserSeeker.Factory(30, 45, 0.02D, 0.14715F, 420))
                .withMotor(new RocketMotor.Factory(0.13333334F, 30, true))
                .withSize(0.5F));

        MissileRegistry.AGM_88 = register(new ForeignMissileEntry("agm88", new HighExplosiveWarhead(14.0F))
                .withGuidance(new ARMGuidance.Factory(20, (int) (45*km), 3, 0.65F))
                .withMotor(new RocketMotor.Factory(0.13333334F, 120, true).offsetRender(2.0F))
                .withLongRange());
    }

    public static MissileRegistryEntry HVAR = register((new ForeignMissileEntry("hvar",
            new DualPurposeWarhead(
                    new ShapedChargeWarhead(36.0F, 2.0F),
                    new FragWarhead(8.0F, 0.3F)
            )))
            .withMotor(new RocketMotorNoLift.Factory(0.42F, 20, 0.01f, true, 0f))
            .withSize(0.2F));

    public static MissileRegistryEntry AN_M57 = register((new ForeignMissileEntry("m57", new HighExplosiveWarhead(8.0F)))
            .withSize(0.5F));

    public static MissileRegistryEntry AN_M65 = register((new ForeignMissileEntry("m65", new HighExplosiveWarhead(32.0F)))
            .withSize(0.5F));

    public static MissileRegistryEntry TYPE_23 = register((new ForeignMissileEntry("type23sneb", new ShapedChargeWarhead(45.0F, 3.0F)))
            .withMotor(new RocketMotorNoLift.Factory(0.56F, 20, 0.01f, true, 0f))
            .withSize(0.2F));

    public static MissileRegistryEntry TYPE23_ROCKET_POD = register((new ForeignPodEntry("type23_rocket_pod", new RocketPod.Factory(TYPE_23, 7)))
            .withSize(0.5F)
            .withDeployModel());

    public static MissileRegistryEntry AIM_9B = register((new ForeignMissileEntry("aim9b", new FragWarhead(5.2F, 0.1F)))
            .withGuidance(new IRSeekerRear.Factory(12, 12, 0.0192D, 3, 10*G, (int) (5.5*km), 0.9F, 80))
            .withMotor((new RocketMotor.Factory(0.7F, 6, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry AIM_9L_TAN = register((new ForeignMissileEntry("aim9lima_tan", new FragWarhead(8.0F, 0.1F)))
            .withGuidance(new IRSeeker.Factory(5, 43, 0.0192D, 4, 35*G, (int) (5.5*km), 0.9F))
            .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry AIM_9M = register((new ForeignMissileEntry("aim9mike", new FragWarhead(8.0F, 0.1F)))
            .withGuidance(new IRSeekerIRCCM.Factory(5, 43, 0.0192D, 4, 35*G, (int) (5.5*km), 0.9F, 40, 0.95F))
            .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry AIM_9X_BLOCK2 = register((new ForeignMissileEntry("aim9x", new FragWarhead(8.0F, 0.1F)))
            .withGuidance(
                    new MultiModeSeeker.Factory()
                            .addGuidance(new DataLink.Factory((int) (10*km), 5, 50*G, 120))
                            .addGuidance(new IIRSeeker.Factory(43, 5, 50*G, (int) (9*km), 0.93F))
                            .addGuidance(new IOG.Factory(5, 50*G, 120)))
            .withMotor((new RocketMotor.Factory(1.0F, 8, false)) .offsetRender(1.0F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry AIM7 = register((new ForeignMissileEntry("aim7e2", new FragWarhead(10.0F, 0.12F)))
            .withGuidance(new SARHSeeker.Factory(36, (int) (40*km), 4, 25*G))
            .withMotor((new RocketMotor.Factory(0.26F, 100, false)) .offsetRender(1F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.4F));

    public static MissileRegistryEntry AGM_65  = register((new ForeignMissileEntry("agm65", new ShapedChargeWarhead(52.0F, 5.0F)))
            .withGuidance(new TVGuidance.Factory(2.25f*G, (int) (12*km)))
            .withMotor(new RocketMotor.Factory(0.04F, 80, true))
            .withSize(0.5F));

    /*public static MissileRegistryEntry GBU_39 = register((new ForeignMissileEntry("gbu39", new HighExplosiveWarhead(12.0F)))
            .withGuidance(new GNSS.Factory(50, 0.1F))
            .withDeployModel()
            .withSize(0.2F));*/


    public static MissileRegistryEntry R_73 = register((new ForeignMissileEntry("r73archer", new FragWarhead(12.0F, 0.1F)))
            .withGuidance(new IRSeeker.Factory(7, 55, 0.0192D, 4, 40*G, (int) (3.4*km), 0.8F))
            .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry R_73E = register((new ForeignMissileEntry("r73e", new FragWarhead(12.0F, 0.1F)))
            .withGuidance(new IRSeekerIRCCM.Factory(7, 55, 0.0192D, 4, 40*G, (int) (3.4*km), 0.8F, 40, 0.9F))
            .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry R550_MAGIC1 = register((new ForeignMissileEntry("r550_magic1", new FragWarhead(10.0F, 0.1F)))
            .withGuidance(new IRSeekerRear.Factory(14, 35, 0.0192D, 5, 35*G, (int) (5.5*km), 0.80F, 90))
            .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry R550_MAGIC2 = register((new ForeignMissileEntry("r550_magic2", new FragWarhead(10.0F, 0.1F)))
            .withGuidance(new IRSeekerIRCCM.Factory(14, 35, 0.0192D, 5, 35*G, (int) (3.4*km), 0.80F, 60, 0.94F))
            .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry R511 = register((new ForeignMissileEntry("r511", new FragWarhead(10.0F, 0.12F)))
            .withGuidance(new SARHSeeker.Factory(14, (int) (8*km), 3, 10*G))
            .withMotor((new RocketMotor.Factory(0.1F, 140, false)) .offsetRender(1F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.5F));

    public static MissileRegistryEntry R530 = register((new ForeignMissileEntry("r530", new FragWarhead(10.0F, 0.12F)))
            .withGuidance(new SARHSeeker.Factory(44, (int) (12.5*km), 4, 15*G))
            .withMotor((new RocketMotor.Factory(0.2F, 100, false)) .offsetRender(1F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.5F));

    public static MissileRegistryEntry MICA_EM = register((new ForeignMissileEntry("mica_em", new FragWarhead(10.0F, 0.15F)))
            .withGuidance(
                    new MultiModeSeeker.Factory()
                            .addGuidance(new DataLink.Factory((int) (10*km), 6, 50*G, 120))
                            .addGuidance(new ARHSeeker.Factory(72, (int) (16*km), 6, 50*G))
                            .addGuidance(new IOG.Factory(6, 50*G, 120)))
            .withMotor((new RocketMotor.Factory(0.2F, 100, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.4F));

    public static MissileRegistryEntry AA20 = register((new ForeignMissileEntry("aa20", new FragWarhead(12.2F, 0.12F)))
            .withGuidance(new MCLOS.Factory(4*G, (int) (12*km)))
            .withMotor(new RocketMotor.Factory(0.15F, 50, true))
            .withFuze(new ProximityFuze.Factory(14.0F, 2))
            .withSize(0.5F));

    public static MissileRegistryEntry AS20 = register((new ForeignMissileEntry("as20", new ShapedChargeWarhead(52.0F, 5.0F)))
            .withGuidance(new MCLOS.Factory(4*G, (int) (12*km)))
            .withMotor(new RocketMotor.Factory(0.15F, 50, true))
            .withSize(0.5F));

    public static MissileRegistryEntry AS30 = register((new ForeignMissileEntry("as30", new ShapedChargeWarhead(125.0F, 5.0F)))
            .withGuidance(new MCLOS.Factory(2.5f*G, (int) (5*km)))
            .withMotor(new RocketMotorNoLift.Factory(0.15F, 70, 0.015f, true, 0.0F))
            .withSize(0.5F));

    public static MissileRegistryEntry AS30L = register((new ForeignMissileEntry("as30l", new ShapedChargeWarhead(125.0F, 5.0F)))
            .withGuidance(new LaserSeeker.Factory(15, 0, 7.5D, 2.5f*G, (int) (10*km)))
            .withMotor(new RocketMotorNoLift.Factory(0.15F, 70, 0.015f, true, 0.0F))
            .withLongRange()
            .withSize(0.5F));

    public static MissileRegistryEntry SPIKE_LR2 = register((new ForeignMissileEntry("spike_lr2", new ShapedChargeWarhead(85.0F, 5.0F)))
            .withGuidance(
                    new MultiModeSeeker.Factory()
                            .addGuidance(new SACLOS.Factory(SACLOS.ControlMedium.WIRE, 0.049F, 500))
                            .addGuidance(new IRSeekerGround.Factory(5, 20, 0.0192D, 4, 0.049F, (int) (5*km), 1.0F)))
            .withMotor(new RocketMotor.Factory(0.13333334F, 30, false))
            .withSize(0.2F));

    public static MissileRegistryEntry KOBRA_9K112 = register((new ForeignMissileEntry("9k112_kobra", new ShapedChargeWarhead(81.0F, 4.0F)))
            .withGuidance(new SACLOS.Factory(SACLOS.ControlMedium.WIRE, 0.039F, 600))
            .withMotor(new RocketMotor.Factory(0.1F, 20, true))
            .withSize(0.25F));

    public static MissileRegistryEntry IRIS_T = register((new ForeignMissileEntry("iris_t", new FragWarhead(10.0F, 0.15F)))
            .withGuidance(
                    new MultiModeSeeker.Factory()
                            .addGuidance(new DataLink.Factory((int) (10*km), 4, 35*G, 120))
                            .addGuidance(new IIRSeeker.Factory(43, 6, 35*G,  (int) (25*km), 0.95F))
                            .addGuidance(new IOG.Factory(4, 35*G, 120)))
            .withMotor((new RocketMotor.Factory(0.2F, 100, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.4F));
}