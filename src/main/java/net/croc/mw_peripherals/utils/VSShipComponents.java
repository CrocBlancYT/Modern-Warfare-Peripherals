package net.croc.mw_peripherals.utils;

import net.croc.mw_peripherals.blocks.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class VSShipComponents {
    private static final HashMap<ServerShip, VSShipComponents> attachements = new HashMap<>();

    private final HashSet<Component> components = new HashSet<>();

    public void subscribe(Level level, Component component) {
        components.add(component);
        components.removeIf((c -> !c.isAlive(level)));
    }

    public List<Component> getComponents() {
        return components.stream().toList();
    }
    public List<Component> getComponentsOfType(Component.Type type) {
        return components.stream().filter((component) -> component.type == type).toList();
    }

    public static boolean subscribe(BlockEntity be) {
        Level level = be.getLevel();
        if (level == null) return false;

        Component component = Component.wrap(be);
        if (component == null) return false;

        BlockPos pos = be.getBlockPos();
        if (VSGameUtilsKt.getShipManagingPos(level, pos) instanceof ServerShip ship) {
            getOrCreate(ship).subscribe(be.getLevel(), component);
            return true;
        }

        return false;
    }

    public static class Component {
        private final @Nullable Class<?> clazz;
        public final BlockPos pos;
        public final Type type;

        private Component(BlockEntity blockEntity, Type type) {
            this.type = type;
            this.pos = blockEntity.getBlockPos();
            this.clazz = blockEntity.getClass();
        }

        public Component(FriendlyByteBuf buffer) {
            this.type = buffer.readEnum(Type.class);
            this.pos = buffer.readBlockPos();
            this.clazz = null;
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeEnum(this.type);
            buffer.writeBlockPos(this.pos);
        }

        public boolean isAlive(Level level) {
            BlockEntity be = level.getBlockEntity(pos);
            return be != null && be.getClass() == clazz && be.getBlockPos() == pos;
        }

        public @Nullable BlockEntity getBlockEntity(Level level) {
            if (!isAlive(level)) return null;
            return level.getBlockEntity(pos);
        }

        @Override
        public int hashCode() {
            return this.pos.hashCode();
        }

        public static @Nullable Component wrap(BlockEntity be) {
            Type type = getTypeFromBlockEntity(be);
            if (type == null) return null;
            return new Component(be, type);
        }

        private static final HashMap<Class<?>, Type> types = new HashMap<>();

        public static Type getTypeFromBlockEntity(BlockEntity be) {
            return types.get(be.getClass());
        }

        public static enum Type {
            Radar(new Class[]{RadarBlockEntity.class, RadarFixedBlockEntity.class, RadarPanelBlockEntity.class}),
            RWR(RWRBlockEntity.class),
            MAWs(MAWSBlockEntity.class),
            LWs(LWSBlockEntity.class),
            Flares(FlareDispenserBlockEntity.class),
            WPM(WPMBlockEntity.class);

            Type(Class<?> clazz) {
                types.put(clazz, this);
            }

            Type(Class<?>[] clazz) {
                for (Class<?> aClass : clazz) {
                    types.put(aClass, this);
                }
            }
        }
    }

    public static VSShipComponents getOrCreate(ServerShip ship) {
        VSShipComponents tracker = attachements.get(ship);

        if (tracker == null) {
            tracker = new VSShipComponents();
            attachements.put(ship, tracker);
        }

        return tracker;
    }
}