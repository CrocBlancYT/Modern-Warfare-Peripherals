package net.croc.mw_peripherals.integration.tallyho;

import edn.stratodonut.tallyho.missile.*;
import edn.stratodonut.tallyho.missile.fuze.ProximityFuze;
import edn.stratodonut.tallyho.missile.guid.IRSeeker;
import edn.stratodonut.tallyho.missile.motor.RocketMotor;
import edn.stratodonut.tallyho.missile.motor.RocketMotorNoLift;
import edn.stratodonut.tallyho.missile.warhead.FragWarhead;
import edn.stratodonut.tallyho.missile.warhead.ShapedChargeWarhead;

import static edn.stratodonut.tallyho.missile.MissileRegistry.MissileRegistryEntry;
import static edn.stratodonut.tallyho.missile.MissileRegistry.register;

public class ForeignMissileRegistry {
    public static void init() { }

    public static MissileRegistryEntry AIM_9L_TAN = register((new ForeignMissileEntry("aim9lima_tan", new FragWarhead(8.0F, 0.1F)))
            .withGuidance(new IRSeeker.Factory(5, 43, 0.0192D, 4, 0.65F, 640, 0.9F))
            .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry R_73 = register((new ForeignMissileEntry("r73archer", new FragWarhead(12.0F, 0.1F)))
            .withGuidance(new IRSeeker.Factory(7, 55, 0.0192D, 4, 0.8F, 480, 0.8F))
            .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
            .withFuze(new ProximityFuze.Factory(9.0F, 2))
            .withSize(0.2F));

    public static MissileRegistryEntry TYPE_23 = register((new ForeignMissileEntry("type23sneb", new ShapedChargeWarhead(36.0F, 3.0F)))
            .withMotor(new RocketMotorNoLift.Factory(0.38F, 30, 0.05F, true, 100.0F))
            .withLongRange()
            .withSize(0.2F));

    static {
        MissileRegistry.AIM_9L = register((new ForeignMissileEntry("aim9lima", new FragWarhead(8.0F, 0.1F)))
                .withGuidance(new IRSeeker.Factory(5, 43, 0.0192D, 4, 0.65F, 640, 0.9F))
                .withMotor((new RocketMotor.Factory(1.0F, 5, false)) .offsetRender(1.5F))
                .withFuze(new ProximityFuze.Factory(9.0F, 2))
                .withSize(0.2F));
    }
}