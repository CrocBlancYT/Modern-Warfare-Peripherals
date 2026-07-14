package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import javax.annotation.Nonnull;

import edn.stratodonut.tallyho.block.RippleFireBlock;
import edn.stratodonut.tallyho.camera.entity.RemoteStationEntity;
import net.croc.mw_peripherals.blocks.*;
import net.croc.mw_peripherals.blocks.aps.APS;
import net.croc.mw_peripherals.blocks.aps.YRotatedAPS;
import net.croc.mw_peripherals.blocks.aps.ZYRotatedAPS;
import net.croc.mw_peripherals.integration.computercraft.peripherals.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;
import riftyboi.cbcmodernwarfare.cannon_control.compact_mount.CompactCannonMountBlockEntity;

import java.util.List;

import static net.croc.mw_peripherals.Main.MOD_ID;

public class PeripheralProviders {
    public static void register() {
        if (!ModList.get().isLoaded("computercraft")) return;
        ForgeComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
    }

    public static class PeripheralProvider implements IPeripheralProvider {
        @Nonnull
        public LazyOptional<IPeripheral> getPeripheral(@Nonnull Level level, @Nonnull BlockPos blockPos, @Nonnull Direction direction) {
            BlockState state = level.getBlockState(blockPos);
            Block block = state.getBlock();

            String ID = ForgeRegistries.BLOCKS.getKey(block).toString();

            switch (ID) {
                case MOD_ID+":raycaster":
                    return LazyOptional.of(() -> new RaycasterPeripheral(level, blockPos));
                case MOD_ID+":rwr":
                    return LazyOptional.of(() -> new RWRPeripheral(level, blockPos));
                case MOD_ID+":weapons_manager":
                    return LazyOptional.of(() -> new WeaponsManagerPeripheral(level, blockPos));
                case MOD_ID+":lws":
                    return LazyOptional.of(() -> new LWSPeripheral(level, blockPos));
                case MOD_ID+":maws":
                    return LazyOptional.of(() -> new MAWSPeripheral(level, blockPos));
                case MOD_ID+":aesa_panel":
                    return LazyOptional.of(() -> new RadarPanelPeripheral(level, blockPos));
                case MOD_ID+":aesa_fixed_radar":
                    return LazyOptional.of(() -> new RadarFixedPeripheral(level, blockPos));
            }

            if (block instanceof RippleFireBlock)
                return LazyOptional.of(() -> new RippleFirePeripheral(level, blockPos));

            BlockEntity be = level.getBlockEntity(blockPos);

            if (be instanceof CompactCannonMountBlockEntity)
                return LazyOptional.of(() -> new CannonMountPeripheral(level, blockPos));
            if (be instanceof CannonMountBlockEntity)
                return LazyOptional.of(() -> new CannonMountPeripheral(level, blockPos));
            if (be instanceof FixedCannonMountBlockEntity)
                return LazyOptional.of(() -> new CannonMountPeripheral(level, blockPos));


            if (be instanceof FlareDispenserBlockEntity flareDispenser)
                return LazyOptional.of(() -> new FlareDispenserPeripheral(level, blockPos, flareDispenser));
            if (be instanceof RadarBlockEntity)
                return LazyOptional.of(() -> new RadarPeripheral(level, blockPos));
            if (be instanceof GyroBlockEntity)
                return LazyOptional.of(() -> new GyroscopePeripheral(level, blockPos, be));


            if (be instanceof ZYRotatedAPS)
                return LazyOptional.of(() -> new APSZYRotPeripheral(level, blockPos));
            if (be instanceof YRotatedAPS)
                return LazyOptional.of(() -> new APSYRotPeripheral(level, blockPos));
            if (be instanceof APS)
                return LazyOptional.of(() -> new APSFixedPeripheral(level, blockPos));

            if (be instanceof LecternBlockEntity lectern) {
                if (lectern.getBook().getItem() instanceof WrittenBookItem) {
                    return LazyOptional.of(() -> new DatalinkPeripheral(level, blockPos));
                }
            }

            return LazyOptional.empty();
        }
    }
}
