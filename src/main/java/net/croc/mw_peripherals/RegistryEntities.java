package net.croc.mw_peripherals;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.EntityBuilder;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import edn.stratodonut.tallyho.TallyhoMod;
import edn.stratodonut.tallyho.render.*;
import net.croc.mw_peripherals.entity.MountedMissileCameraEntity;
import net.croc.mw_peripherals.entity.MountedPodEntity;
import net.croc.mw_peripherals.integration.cbcmodernwarfare.munitions.barrel_launched_missile.MissileMediumcannonProjectile;
import net.croc.mw_peripherals.integration.cbcmodernwarfare.munitions.barrel_launched_missile.MissileMediumcannonRoundItem;
import net.croc.mw_peripherals.integration.createbigcannons.munitions.barrel_launched_missile.BarrelLaunchedMissileBlock;
import net.croc.mw_peripherals.integration.createbigcannons.munitions.barrel_launched_missile.BarrelLaunchedMissileProjectile;
import net.croc.mw_peripherals.utils.SimpleBlockEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.valkyrienskies.mod.client.EmptyRenderer;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.multiloader.EntityTypeConfigurator;
import rbasamoyai.createbigcannons.munitions.big_cannon.AbstractBigCannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonProjectileRenderer;
import rbasamoyai.createbigcannons.munitions.big_cannon.mortar_stone.MortarStonePropertiesHandler;
import rbasamoyai.createbigcannons.munitions.config.MunitionPropertiesHandler;
import rbasamoyai.createbigcannons.munitions.config.PropertiesTypeHandler;
import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonProjectileRenderer;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.InertMediumcannonProjectilePropertiesHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class RegistryEntities {
    private static final CreateRegistrate REGISTRATE = TallyhoMod.REGISTRATE;

    public static final EntityEntry<Entity> POD_ENTITY;
    public static final EntityEntry<Entity> MOUNTED_TV_CAMERA;

    private static <T> NonNullConsumer<T> configure(Consumer<EntityTypeConfigurator> cons) {
        return (b) -> cons.accept(EntityTypeConfigurator.of(b));
    }

    private static <T> NonNullConsumer<T> cannonProperties() {
        return configure((c) -> c.size(0.8F, 0.8F).fireImmune().updateInterval(1).updateVelocity(false).trackingRange(16));
    }

    private static <T> NonNullConsumer<T> autocannonProperties() {
        return configure((c) -> c.size(0.2F, 0.2F).fireImmune().updateInterval(1).updateVelocity(false).trackingRange(16));
    }

    private static <T extends AbstractBigCannonProjectile> EntityEntry<T> cannonProjectile(
            String id, EntityType.EntityFactory<T> factory, PropertiesTypeHandler<EntityType<?>, ?> handler) {
        return ((EntityBuilder) CreateBigCannons.REGISTRATE
                .entity(id, factory, MobCategory.MISC)
                .properties(cannonProperties())
                .renderer(() -> BigCannonProjectileRenderer::new)
                .tag()
                .onRegister((type) -> MunitionPropertiesHandler.registerProjectileHandler(type, handler)))
                .register();
    }

    private static <T extends AbstractMediumcannonProjectile> EntityEntry<T> mediumcannonProjectile(
            String id, EntityType.EntityFactory<T> factory, String enUSdiffLang, PropertiesTypeHandler<EntityType<?>, ?> handler) {
        return ((EntityBuilder) CBCModernWarfare.REGISTRATE
                .entity(id, factory, MobCategory.MISC)
                .properties(autocannonProperties())
                .renderer(() -> MediumcannonProjectileRenderer::new)
                .lang(enUSdiffLang)
                .tag()
                .onRegister((type) -> MunitionPropertiesHandler.registerProjectileHandler(type, handler)))
                .register();
    }

    public static final HashMap<String, EntityEntry<?>> missileId_to_entry = new HashMap<>();
    public static final HashMap<EntityEntry<?>, String> entry_to_missileId = new HashMap<>();

    public static EntityEntry<BarrelLaunchedMissileProjectile> registerBarrelLaunchedMissile(
            String missileId, SimpleBlockEntry<BarrelLaunchedMissileBlock> blockEntry) {

        EntityEntry<BarrelLaunchedMissileProjectile> missile = cannonProjectile("barrel_fired_"+missileId,
                BarrelLaunchedMissileProjectile::new,
                CBCMunitionPropertiesHandlers.MORTAR_STONE); // doesn't work (prob needs its own declaration)

        missileId_to_entry.put(missileId, missile);
        entry_to_missileId.put(missile, missileId);
        return missile;
    }

    public static EntityEntry<MissileMediumcannonProjectile> registerMediumcannonMissile(
            String missileId, ItemEntry<MissileMediumcannonRoundItem> itemEntry) {

        EntityEntry<MissileMediumcannonProjectile> missile = mediumcannonProjectile("barrel_fired_"+missileId,
                MissileMediumcannonProjectile::new,
                "Barrel Launched Mediumcannon Missile",
                CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE);

        missileId_to_entry.put(missileId, missile);
        entry_to_missileId.put(missile, missileId);

        return missile;
    }

    @Nullable
    public static String getMissileIdFromType(EntityType<?> type) {
        AtomicReference<String> missileId = new AtomicReference<>("");

        entry_to_missileId.forEach((entry, id) -> {
            if (type.getDescriptionId().equals(entry.get().getDescriptionId())) {
                missileId.set(id);
            }
        });

        return missileId.get();
    }

    @Nullable
    public static EntityType<?> getTypeFromMissileId(String missileId) {
        EntityEntry<?> entry = missileId_to_entry.get(missileId);
        if (entry == null) return null;
        return entry.get();
    }

    static {
        POD_ENTITY = REGISTRATE
                .entity("pod", MountedPodEntity::new, MobCategory.MISC)
                .renderer(() -> (context) -> new MissileEntityRenderer(context))
                .properties(configure(c -> c
                        .trackingRange(32)
                        //.clientTrackingRange(32)
                        .updateInterval(3)
                        .updateVelocity(false)
                        .fireImmune()
                        //.noSummon()
                        .size(1.0f, 1.0f))
                ).register();

        MOUNTED_TV_CAMERA = REGISTRATE
                .entity("tv_mounted_camera", MountedMissileCameraEntity::new, MobCategory.MISC)
                .renderer(() -> EmptyRenderer::new)
                .properties(configure(c -> c
                        .trackingRange(32)
                        .updateInterval(3)
                        .updateVelocity(false)
                        .fireImmune()
                        .size(1.0f, 1.0f))
                ).register();
    }

    public static void register() {}
}