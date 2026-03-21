package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import javax.annotation.Nonnull;
import net.croc.mw_peripherals.blocks.APSBlockEntity;
import net.croc.mw_peripherals.blocks.GyroBlockEntity;
import net.croc.mw_peripherals.blocks.RadarBlockEntity;
import net.croc.mw_peripherals.integration.computercraft.peripherals.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

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
                case MOD_ID+":maws":
                    return LazyOptional.of(() -> new MAWSPeripheral(level, blockPos));
                case MOD_ID+":weapons_manager":
                    return LazyOptional.of(() -> new WeaponsManagerPeripheral(level, blockPos));
                case "createbigcannons:cannon_mount":
                case "createbigcannons:fixed_cannon_mount":
                case "cbcmodernwarfare:compact_mount":
                    return LazyOptional.of(() -> new CannonMountPeripheral(level, blockPos));
            }

            BlockEntity be = level.getBlockEntity(blockPos);

            if (be instanceof RadarBlockEntity)
                return LazyOptional.of(() -> new RadarPeripheral(level, blockPos));
            if (be instanceof GyroBlockEntity)
                return LazyOptional.of(() -> new GyroscopePeripheral(level, blockPos, be));
            if (be instanceof APSBlockEntity)
                return LazyOptional.of(() -> new APSPeripheral(level, blockPos));

            if (be instanceof LecternBlockEntity lectern) {
                if (lectern.getBook().getItem() instanceof WrittenBookItem) {
                    return LazyOptional.of(() -> new DatalinkPeripheral(level, blockPos));
                }
            }

            return LazyOptional.empty();
        }
    }
}
