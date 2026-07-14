package net.croc.mw_peripherals.integration.tallyho;

import com.tterrag.registrate.util.entry.ItemEntry;
import edn.stratodonut.tallyho.camera.entity.FlexibleSeatEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.item.MissileItem;
import net.croc.mw_peripherals.RegistryEntities;
import net.croc.mw_peripherals.entity.MountedPodEntity;
import net.croc.mw_peripherals.integration.tallyho.warhead.DummyWarhead;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static edn.stratodonut.tallyho.TallyhoMod.REGISTRATE;

public class ForeignPodEntry extends ForeignMissileEntry {
    private final IPodFactory<?> podFactory;

    public final MissileFactory<MountedPodEntity> factory;

    private final Supplier<EntityType<Entity>> entityType;

    private final ItemEntry<MissileItem> itemEntry;

    public ForeignPodEntry(String id, IPodFactory<?> podFactory) {
        super(id, new DummyWarhead());
        this.podFactory = podFactory;
        this.factory = MountedPodEntity::new;
        this.entityType = RegistryEntities.POD_ENTITY;
        this.itemEntry = REGISTRATE.item(id, p -> new MissileItem(p, id)).properties(p -> p.stacksTo(1)).register();
    }

    @Nullable
    public PodComponent getPod() {
        if (this.podFactory != null)
            return this.podFactory.create();
        return null;
    }

    @Override
    public void appendHoverText(List<Component> components) {
        if (this.podFactory != null)
            this.podFactory.appendHoverText(components);
    }

    @Nonnull
    @Override
    public MountedMissileEntity spawn(ServerLevel serverLevel, Vec3 pos, float base_yaw) {
        MountedPodEntity missile = this.factory.create(this.entityType.get(), serverLevel, this.id, this.itemEntry.get());
        missile.setPos(pos);
        serverLevel.addFreshEntity(missile);
        FlexibleSeatEntity.sitDown(serverLevel, pos, missile);
        missile.setYRot(base_yaw);
        missile.setXRot(0.0F);
        return missile;
    }

    public static interface MissileFactory<T extends MountedPodEntity> {
        T create(EntityType<?> entityType, Level level, String id, Item item);
    }
}