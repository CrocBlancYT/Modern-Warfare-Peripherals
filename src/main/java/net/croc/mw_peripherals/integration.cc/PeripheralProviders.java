package net.croc.mw_peripherals.integration.cc;

import net.croc.mw_peripherals.blocks.APSBlockEntity;
import net.croc.mw_peripherals.blocks.GyroBlockEntity;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import net.croc.mw_peripherals.Main;

public class PeripheralProviders {
    public static void register() {
        Main.LOGGER.info("Registering peripheral providers...");

        if (ModList.get().isLoaded("computercraft")) {
            ForgeComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
            Main.LOGGER.info("ComputerCraft found - peripheral provider registered");
        } else {
            Main.LOGGER.warn("ComputerCraft not found - skipping peripheral registration");
        }
    }

    public static class PeripheralProvider implements IPeripheralProvider {
        @Nonnull
        public LazyOptional<IPeripheral> getPeripheral(@Nonnull Level level, @Nonnull BlockPos blockPos, @Nonnull Direction direction) {
            BlockState state = level.getBlockState(blockPos);
            Block block = state.getBlock();
            String blockId = ForgeRegistries.BLOCKS.getKey(block).toString();

            // Main.LOGGER.debug("Checking peripheral at pos: {}", blockPos);

            BlockEntity be = level.getBlockEntity(blockPos);

            if (be instanceof GyroBlockEntity) {
                return LazyOptional.of(() -> new GyroscopePeripheral(level, blockPos, be));
            } else if (blockId.equals("mw_peripherals:raycaster")) {
                return LazyOptional.of(() -> new RaycasterPeripheral(level, blockPos));
            }else if (blockId.equals("mw_peripherals:maws")) {
                return LazyOptional.of(() -> new MAWSPeripheral(level, blockPos));
            }else if (blockId.equals("minecraft:lectern")) {
                return LazyOptional.of(() -> new DatalinkPeripheral(level, blockPos));
            }else if (blockId.equals("mw_peripherals:weapons_manager")) {
                return LazyOptional.of(() -> new WeaponsManagerPeripheral(level, blockPos));
            }else if (be instanceof APSBlockEntity) {
                return LazyOptional.of(() -> new APSPeripheral(level, blockPos));
            } else if (blockId.equals("createbigcannons:cannon_mount")) {
                return LazyOptional.of(() -> new CannonMountPeripheral(level, blockPos));
            } else if (blockId.equals("cbcmodernwarfare:compact_mount")) {
                return LazyOptional.of(() -> new CannonMountPeripheral(level, blockPos));
            } else if (blockId.equals("createbigcannons:fixed_cannon_mount")) {
                return LazyOptional.of(() -> new CannonMountPeripheral(level, blockPos));
            }

            return LazyOptional.empty();
        }
    }
}
