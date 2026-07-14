package net.croc.mw_peripherals.integration.createbigcannons.munitions.barrel_launched_missile;

import net.croc.mw_peripherals.RegistryEntities;
import net.minecraft.world.entity.EntityType;
import rbasamoyai.createbigcannons.munitions.big_cannon.ProjectileBlock;

public class BarrelLaunchedMissileBlock extends ProjectileBlock<BarrelLaunchedMissileProjectile> {
    public final String missileId;
    public BarrelLaunchedMissileBlock(Properties properties, String missileId) {
        super(properties);
        this.missileId = missileId;
    }

    @Override
	public EntityType<? extends BarrelLaunchedMissileProjectile> getAssociatedEntityType() {
		return (EntityType<? extends BarrelLaunchedMissileProjectile>) RegistryEntities.missileId_to_entry.get(missileId).get();
	}
}
