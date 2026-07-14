package net.croc.mw_peripherals.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ClientShip;

public interface IVSCamera {
    void setupWithShipMounted(
            BlockGetter level, Entity renderViewEntity,
            boolean thirdPerson, boolean thirdPersonReverse,
            float partialTicks, ClientShip shipMountedTo,
            Vector3dc inShipPlayerPosition);

    void setPositionVS(Vec3 pos);

    void setRotationVS(float yaw, float pitch, float roll);

    float getZrot();
}
