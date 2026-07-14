package net.croc.mw_peripherals;

import java.util.HashMap;
import java.util.List;

import edn.stratodonut.tallyho.TallyhoMod;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.valkyrienskies.core.apigame.world.chunks.BlockType;
import org.valkyrienskies.mod.common.BlockStateInfo;
import org.valkyrienskies.mod.common.BlockStateInfoProvider;
import org.valkyrienskies.physics_api.voxel.Lod1LiquidBlockState;
import org.valkyrienskies.physics_api.voxel.Lod1SolidBlockState;

public final class RegistryBlockStateInfo {
    public static final RegistryBlockStateInfo INSTANCE = new RegistryBlockStateInfo();


    private final static HashMap<String, Double> weights;
    private final static double DEFAULT_WEIGHT = 175;

    static {
        weights = new HashMap<>();
        weights.put("", 0D);
    }

    public static double getMassForMissile(String missileId) {
        return weights.getOrDefault(missileId, DEFAULT_WEIGHT);
    }

    private final static int BASE_WEIGHT = 100;

    private final static double invMultiplier = 1 / 175D;
    public static int encode(double mass) {
        mass = Math.sqrt(mass * invMultiplier);

        if (mass > 8) return 8;
        if (mass < 0) return 0;

        return (int) Math.ceil(mass);
    }

    public static double decode(int mass) {
        return mass * mass * 175D;
    }

    public static final IntegerProperty MISSILES = IntegerProperty.create("encoded_mass", 0, 8);

    public void register() {
        Registry.register(BlockStateInfo.INSTANCE.getREGISTRY(), new ResourceLocation(TallyhoMod.MOD_ID, "ripple_block"), MissilesMass.INSTANCE);
    }

    public static final class MissilesMass implements BlockStateInfoProvider {
        public int getPriority() {
            return 200;
        }

        @Nullable
        public Double getBlockStateMass(@NotNull BlockState blockState) {
            if (!blockState.hasProperty(MISSILES)) return null;

            return BASE_WEIGHT + decode(blockState.getValue(MISSILES));
        }

        @Nullable
        public BlockType getBlockStateType(@NotNull BlockState blockState) {
            Intrinsics.checkNotNullParameter(blockState, "blockState");
            return null;
        }

        @NotNull
        public List<Lod1SolidBlockState> getSolidBlockStates() {
            return solidBlockStates;
        }

        @NotNull
        public static final MissilesMass INSTANCE = new MissilesMass();

        @NotNull
        private static final List<Lod1SolidBlockState> solidBlockStates = List.of();

        @NotNull
        public List<Lod1LiquidBlockState> getLiquidBlockStates() {
            return liquidBlockStates;
        }

        @NotNull
        private static final List<Lod1LiquidBlockState> liquidBlockStates = List.of();

        @NotNull
        public List<Triple<Integer, Integer, Integer>> getBlockStateData() {
            return blockStateData;
        }

        @NotNull
        private static final List<Triple<Integer, Integer, Integer>> blockStateData = List.of();
    }
}
