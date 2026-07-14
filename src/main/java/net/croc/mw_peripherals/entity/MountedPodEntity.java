package net.croc.mw_peripherals.entity;

import edn.stratodonut.tallyho.TallyhoMod;
import edn.stratodonut.tallyho.Utils;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.MissileRegistry;
import net.croc.mw_peripherals.RegistryEntities;
import net.croc.mw_peripherals.integration.tallyho.ForeignPodEntry;
import net.croc.mw_peripherals.integration.tallyho.PodComponent;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class MountedPodEntity extends MountedMissileEntity {
    private static final EntityDataAccessor<Boolean> DATA_DEPLOYED = SynchedEntityData.defineId(MountedMissileEntity.class, EntityDataSerializers.BOOLEAN);

    private PodComponent pod;
    public boolean isDropped = false;

    public MountedPodEntity(EntityType<?> type, Level level) { super(RegistryEntities.POD_ENTITY.get(), level); }

    public MountedPodEntity(EntityType<?> type, Level level, Item item) { super(RegistryEntities.POD_ENTITY.get(), level, item); }

    public MountedPodEntity(EntityType<?> type, Level level, String id, Item item) {
        super(RegistryEntities.POD_ENTITY.get(), level, id, item);
        loadDataFromID(id);
    }

    private void loadDataFromID(String id) {
        MissileRegistry.MissileRegistryEntry entry = MissileRegistry.getEntry(id);

        if (entry == null) {
            TallyhoMod.warn("Failed to load missile of id '{}'!", id);
            return;
        }

        if (entry instanceof ForeignPodEntry podEntry) {
            if (this.pod == null)
                this.pod = podEntry.getPod();
        }
        this.hitboxScale = entry.getHitboxScale();
        this.isLongRange = entry.isLongRange();
        refreshDimensions();
    }


    @Override
    public void tick() {
        super.tick();
        if (this.pod != null) {
            this.pod.tick(this);
        }
    }

    @Override
    public boolean launch(Vec3 dir, float boost) {
        if (this.pod != null) {
            this.pod.launch(this, dir, boost);
            this.entityData.set(DATA_DEPLOYED, true);
        }
        return true;
    }

    @Override
    public boolean isDeployed() {
        return this.entityData.get(DATA_DEPLOYED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DEPLOYED, false);
    }

    @Override
    public boolean launch() {
        return launch(getLookAngle(), 0.0F);
    }

    @Override
    public void detonate(Vec3 hit) { }

    @Override
    public void updateHeading() { }

    public void drop() {
        if (isDropped) return;
        isDropped = true;

        Ship s = VSGameUtilsKt.getShipMountedTo(this);
        this.stopRiding();
        if (s != null) {
            Vec3 look = VSUtils.toWorldDirection(s, this.getLookAngle());
            
            Utils.PosVel compensation = Utils.moveInWorld(this.position(), s, 3.0F);
            this.setPos(compensation.pos());
            this.addDeltaMovement(compensation.vel());
            this.lookAt(EntityAnchorArgument.Anchor.FEET, this.position().add(this.getDeltaMovement()).add(look) );
        }
    }
}