package net.croc.mw_peripherals.integration.tallyho.pod;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import java.util.List;
import java.util.function.Supplier;

import net.croc.mw_peripherals.entity.MountedPodEntity;
import net.croc.mw_peripherals.integration.tallyho.IPodFactory;
import net.croc.mw_peripherals.integration.tallyho.PodComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AutocannonBreechBlock;
import rbasamoyai.createbigcannons.cannons.autocannon.material.AutocannonMaterialProperties;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.index.CBCItems;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonRoundItem;
import rbasamoyai.createbigcannons.munitions.autocannon.flak.FlakAutocannonRoundItem;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class GunPod extends PodComponent {
    private final GunProperties properties;
    private final SoundEvent FIRING_SOUND;

    private final AutocannonRoundItem AMMO;
    private final AutocannonMaterialProperties AUTOCANNON;

    // CBCItems.FLAK_AUTOCANNON_ROUND.get();
    // CBCBlocks.STEEL_AUTOCANNON_BREECH.get().getAutocannonMaterial().properties();

    int belt;
    int firingCooldown;

    public int getBelt() {
        return this.belt;
    }

    public int getMaxBelt() {
        return this.properties.belt_size;
    }

    public record GunProperties(Supplier<AutocannonRoundItem> ammoSupplier, Supplier<AutocannonMaterialProperties> propertiesSupplier,
                                int FIRING_COOLDOWN_TICKS,
                                int belt_size, ItemStack FUZE, float power, boolean hasTracer, float spread) {
        public void appendHoverText(List<Component> components) {


            components.add(Component.literal("Belt Size: "+this.belt_size)
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            components.add(Component.literal("RPM: " + (int) (60 / (this.FIRING_COOLDOWN_TICKS * 0.05)))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

            if (FUZE != null) {
                components.add(Component.literal("Fuze: ("+this.FUZE.getHoverName()+")")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            }

            if (hasTracer) {
                components.add(Component.literal("has Tracer: Yes")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            }
        }
    }

    public static class Factory implements IPodFactory<GunPod> {
        private final GunPod.GunProperties properties;
        private Supplier<SoundEvent> fireSound = () -> CBCSoundEvents.FIRE_AUTOCANNON.getMainEvent();

        public Factory(Supplier<AutocannonRoundItem> ammoSupplier, Supplier<AutocannonMaterialProperties> propertiesSupplier,
                       int firing_cooldown_ticks, int belt_size, ItemStack fuze, float power, boolean hasTracer, float spread) {
            this.properties = new GunPod.GunProperties(ammoSupplier, propertiesSupplier, firing_cooldown_ticks, belt_size, fuze, power, hasTracer, spread);
        }

        public Factory withCustomFiringSound(Supplier<SoundEvent> fireSound) {
            this.fireSound = fireSound;
            return this;
        }

        public GunPod create() {
            return new GunPod(this.properties, this.fireSound);
        }

        public void appendHoverText(List<Component> list) {
            this.properties.appendHoverText(list);
        }
    }

    public GunPod(GunProperties props, Supplier<SoundEvent> firing_sound) {
        this.properties = props;

        this.AMMO = props.ammoSupplier.get();
        this.AUTOCANNON = props.propertiesSupplier.get();

        this.FIRING_SOUND = firing_sound.get();
        this.belt = props.belt_size;
        this.firingCooldown = props.FIRING_COOLDOWN_TICKS;
    }

    @Override
    public void tick(MountedPodEntity e) {
        Ship launchShip = VSGameUtilsKt.getShipMountedTo(e);
        
        if (e.isPassenger()) {
            BlockPos virtualPos = e.blockPosition();

            if (launchShip != null && e.getVehicle() != null) {
                virtualPos = e.getVehicle().blockPosition();
            }

            if (e.level().isLoaded(virtualPos) && e.level().hasNeighborSignal(virtualPos)) {
                handleShoot(e);
            }
        }

        this.firingCooldown--;
    }

    @Override
    public boolean launch(MountedPodEntity e, Vec3 dir, float boost) {
        return true;
    }

    public void handleShoot(MountedPodEntity shooter) {
        ServerLevel serverLevel = (ServerLevel) shooter.level();
        Vec3 lookVector = shooter.getViewVector(1.0F);
        Vec3 spawnPos = shooter.position().add(lookVector.scale(2.0D));

        if (this.firingCooldown >= 0) return;
        this.firingCooldown = this.properties.FIRING_COOLDOWN_TICKS;

        if (this.belt == 0) {
            serverLevel.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0F, 0.0F);
        } else {
            Vec3 direction = spawnPos.subtract(shooter.position()).normalize();
            ItemStack ammoStack = new ItemStack(CBCItems.FLAK_AUTOCANNON_ROUND.get(), 1);

            if (this.properties.FUZE != null) {
                ammoStack.getOrCreateTag().put("Fuze", this.properties.FUZE.serializeNBT());
            }

            AbstractAutocannonProjectile projectile = this.AMMO.getAutocannonProjectile(ammoStack, serverLevel);
            float speed = this.AUTOCANNON.baseSpeed() +
                    this.AUTOCANNON.speedIncreasePerBarrel() * 7.0F;
            float spread = this.properties.spread;

            if (projectile != null) {
                projectile.setPos(spawnPos);
                projectile.setChargePower(this.properties.power);
                projectile.setTracer(this.properties.hasTracer);
                projectile.setLifetime(this.AUTOCANNON.projectileLifetime() * 3);
                projectile.shoot(direction.x, direction.y, direction.z, speed, spread);
                projectile.xRotO = projectile.getXRot();
                projectile.yRotO = projectile.getYRot();
                serverLevel.addFreshEntity(projectile);
            }

            CBCUtils.playBlastLikeSoundOnServer(serverLevel,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    this.FIRING_SOUND,
                    SoundSource.BLOCKS, 12.0F, 1.0F, 5.0F);

            this.belt--;
        }
    }
}