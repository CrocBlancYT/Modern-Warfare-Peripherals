package net.croc.mw_peripherals.integration.createbigcannons.munitions.barrel_launched_missile;

import javax.annotation.Nonnull;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.MissileRegistry;
import net.croc.mw_peripherals.Main;
import net.croc.mw_peripherals.RegistryBlocks;
import net.croc.mw_peripherals.RegistryEntities;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.munitions.big_cannon.AbstractBigCannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.config.BigCannonProjectilePropertiesComponent;
import rbasamoyai.createbigcannons.munitions.big_cannon.mortar_stone.MortarStoneProperties;
import rbasamoyai.createbigcannons.munitions.config.components.BallisticPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.components.EntityDamagePropertiesComponent;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class BarrelLaunchedMissileProjectile extends AbstractBigCannonProjectile {

    private boolean tooManyCharges = false;
    private final String missileId;

    public BarrelLaunchedMissileProjectile(EntityType<? extends BarrelLaunchedMissileProjectile> type, Level level) {
        super(type, level);
        this.missileId = RegistryEntities.getMissileIdFromType(this.getType());
    }

    @Override
    public void tick() {
        if (this.tooManyCharges && this.level() instanceof ServerLevel slevel) {
            CBCUtils.playBlastLikeSoundOnServer(slevel,
                    this.getX(), this.getY(), this.getZ(),
                    CBCSoundEvents.SHELL_EXPLOSION.getMainEvent(),
                    SoundSource.BLOCKS, 12.0F, 1.0F, 5.0F);

            this.discard();
            return;
        } else if (missileId != null && this.level() instanceof ServerLevel slevel) {
            MissileRegistry.MissileRegistryEntry entry = MissileRegistry.getEntry(this.missileId);
            if (entry == null) return;

            Vec3 spawnPos = this.position().add(this.getLookAngle().scale(2f));

            MountedMissileEntity missile = entry.spawn(slevel, spawnPos, this.getYRot());

            missile.setDeltaMovement(this.getDeltaMovement());
            missile.setYRot(this.getYRot());
            missile.setXRot(this.getXRot());
            missile.tick();
            missile.launch();

            this.discard();
            return;
        }
        super.tick();
    }

	@Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tooManyCharges = tag.getBoolean("TooManyCharges");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("TooManyCharges", this.tooManyCharges);
    }

    @Override
    public BlockState getRenderedBlockState() {
        return RegistryBlocks.BARREL_LAUNCHED_MISSILES.get(missileId).getBlock().get().defaultBlockState();
    }

    @Override
    public void setChargePower(float power) {
        float maxCharges = this.getAllProperties().maxCharges();
        this.tooManyCharges = maxCharges >= 0 && power > maxCharges;
    }

	@Nonnull
	@Override
	protected BigCannonProjectilePropertiesComponent getBigCannonProjectileProperties() {
		return this.getAllProperties().bigCannonProperties();
	}

    @Nonnull
    @Override
    public EntityDamagePropertiesComponent getDamageProperties() {
        return this.getAllProperties().damage();
    }

	@Nonnull
	@Override
	protected BallisticPropertiesComponent getBallisticProperties() {
		return this.getAllProperties().ballistics();
	}

	protected MortarStoneProperties getAllProperties() {
		return CBCMunitionPropertiesHandlers.MORTAR_STONE.getPropertiesOf(this);
	}

}