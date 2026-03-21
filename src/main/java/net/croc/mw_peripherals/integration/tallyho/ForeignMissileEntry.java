package net.croc.mw_peripherals.integration.tallyho;

import edn.stratodonut.tallyho.missile.IFuzeFactory;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import edn.stratodonut.tallyho.missile.IMotorFactory;
import edn.stratodonut.tallyho.missile.IWarhead;

import static edn.stratodonut.tallyho.missile.MissileRegistry.*;

public class ForeignMissileEntry extends MissileRegistryEntry {

    public ForeignMissileEntry(String id, IWarhead warhead) {
        super(id, warhead);
    }

    public ForeignMissileEntry withGuidance(IGuidanceFactory<?> g) {
        return (ForeignMissileEntry) super.withGuidance(g);
    }

    protected ForeignMissileEntry withMotor(IMotorFactory<?> m) {
        return (ForeignMissileEntry) super.withMotor(m);
    }

    protected ForeignMissileEntry withFuze(IFuzeFactory<?> f) {
        return (ForeignMissileEntry) super.withFuze(f);
    }

    protected ForeignMissileEntry withLongRange() {
        return (ForeignMissileEntry) super.withLongRange();
    }

    protected ForeignMissileEntry withSize(float s) {
        return (ForeignMissileEntry) super.withSize(s);
    }

    protected ForeignMissileEntry withDeployModel() {
        return (ForeignMissileEntry) super.withDeployModel();
    }
}