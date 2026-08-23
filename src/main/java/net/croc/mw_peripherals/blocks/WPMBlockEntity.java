package net.croc.mw_peripherals.blocks;

import edn.stratodonut.tallyho.camera.entity.RemoteStationEntity;
import edn.stratodonut.tallyho.camera.entity.TargetingPodEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.integration.computercraft.LuaCROWS;
import net.croc.mw_peripherals.integration.computercraft.LuaOrdnance;
import net.croc.mw_peripherals.integration.computercraft.LuaTGP;
import net.croc.mw_peripherals.utils.VSShipComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WPMBlockEntity extends BlockEntity {
    public WPMBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.WPM_BLOCK_ENTITY.get(),pos, state);
    }

    public static abstract class Weapon<T extends Entity> {
        private final T entity;
        public Weapon(T entity) { this.entity = entity; }
        public T entity() { return this.entity; }
        public abstract Object asLua(Level level, BlockPos pos);
    }

    public static class OrdnanceWeapon extends Weapon<MountedMissileEntity> {
        public OrdnanceWeapon(MountedMissileEntity entity) { super(entity); }

        @Override
        public Object asLua(Level level, BlockPos pos) { return new LuaOrdnance(entity(), level, pos); }
    }

    public static class CROWSWeapon extends Weapon<RemoteStationEntity> {
        public CROWSWeapon(RemoteStationEntity entity) { super(entity); }

        @Override
        public Object asLua(Level level, BlockPos pos) { return new LuaCROWS(entity(), level, pos); }
    }

    public static class TGPWeapon extends Weapon<TargetingPodEntity> {
        public TGPWeapon(TargetingPodEntity entity) { super(entity); }

        @Override
        public Object asLua(Level level, BlockPos pos) { return new LuaTGP(entity(), level, pos); }
    }

    public List<Weapon<?>> scanForWeapons() {
        ArrayList<Weapon<?>> weapons = new ArrayList<>();
        if (level == null) return weapons;
        AABB area = (new AABB(this.getBlockPos())).inflate(7.0D);

        for (MountedMissileEntity missile : level.getEntitiesOfClass(MountedMissileEntity.class, area)) {
            weapons.add(new OrdnanceWeapon(missile));
        }

        for (RemoteStationEntity missile : level.getEntitiesOfClass(RemoteStationEntity.class, area)) {
            weapons.add(new CROWSWeapon(missile));
        }

        for (TargetingPodEntity missile : level.getEntitiesOfClass(TargetingPodEntity.class, area)) {
            weapons.add(new TGPWeapon(missile));
        }

        return weapons;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        VSShipComponents.subscribe(this);
    }
}