package net.croc.mw_peripherals.mixinducks;

import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

public interface CannonMountDuck {
    void Iassemble();
    void Idisassemble();
    void Itick();

    float getCannonYaw();
    float getCannonPitch();

    void setCannonPitch(float pitch);
    void setCannonYaw(float yaw);

    PitchOrientedContraptionEntity IgetContraption();
    void IonRedstoneUpdate(boolean a, boolean b, boolean c, boolean d, int e);
}