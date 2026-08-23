package net.croc.mw_peripherals.blocks.launchers;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import edn.stratodonut.tallyho.AllEntities;
import edn.stratodonut.tallyho.camera.entity.FlexibleSeatEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.MissileRegistry;
import net.croc.mw_peripherals.PartialModels;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.RegistrySounds;
import net.croc.mw_peripherals.Shapes;
import net.croc.mw_peripherals.integration.tallyho.ForeignMissileRegistry;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import rbasamoyai.createbigcannons.utils.CBCUtils;

import javax.annotation.Nullable;
import java.util.List;

public class RocketPod4BlockEntity extends BlockEntity implements IHaveGoggleInformation {
    private static final int DEFAULT_COLOR = 0xFFFFFF;
    private static final RandomSource random = RandomSource.create();

    public static enum RocketState {
        Zero(PartialModels.ROCKET_POD_4_0),
        One(PartialModels.ROCKET_POD_4_1),
        Two(PartialModels.ROCKET_POD_4_2),
        Three(PartialModels.ROCKET_POD_4_3),
        Four(PartialModels.ROCKET_POD_4_4);


        public final PartialModel model;

        RocketState(PartialModel model) {
            this.model = model;
        }

        public static RocketState fromNumber(int state) {
            if (state == 0) return Zero;
            if (state == 1) return One;
            if (state == 2) return Two;
            if (state == 3) return Three;
            return Four;
        }
    }

    private RocketState rocketState = RocketState.Four;
    private int loaded_rockets = 0;
    private int color = DEFAULT_COLOR;

    public boolean is_shape_cached = false;
    public Direction cached_shape_direction = null;
    public VoxelShape cached_shape = null;
    public int render_x_offset = 0;
    public int render_y_offset = 0;
    public int render_z_offset = 0;

    public RocketPod4BlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.ROCKET_POD_4_BLOCK_ENTITY.get(), pos, state);
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(RocketPod4Block.HORIZONTAL_FACING);
    }

    public int getLoadedRockets() {
        return this.loaded_rockets;
    }

    public RocketState getRocketState() {
        return rocketState;
    }

    private void refreshRocketState() {
        this.rocketState = RocketState.fromNumber(loaded_rockets);
    }

    public boolean addRocket() {
        if (loaded_rockets >= 4) return false;
        loaded_rockets++;
        refreshAll();
        return true;
    }

    public int getColor() {
        return this.color;
    }

    public boolean trySetColor(int newColor) {
        if (newColor > 0xFFFFFF) return false;
        if (newColor < 0) return false;

        this.color = newColor;
        refreshAll();

        return true;
    }

    private VoxelShape getRawShape(Direction direction) {
        return cached_shape = Shapes.ROCKET_POD_4.get(direction);
    }

    public VoxelShape getCachedShape() {
        Direction direction = this.getFacing();

        if (direction != cached_shape_direction) {
            is_shape_cached = false;
            cached_shape_direction = direction;
        }

        if (!is_shape_cached) {
            cached_shape = getRawShape(direction);
            is_shape_cached = true;
        }

        return cached_shape;
    }

    private final static int OFFSET_X = 5;
    private final static int OFFSET_Y = 5;
    private final static int OFFSET_Z = 5;

    public void updateFromClick(BlockHitResult hit, BlockPos pos) {
        is_shape_cached = false;
        switch (hit.getDirection()) {
            case DOWN, UP -> {
                render_x_offset = (int) (((hit.getLocation()).x - pos.getX()) * 16.0D) - OFFSET_X;
                render_z_offset = (int) (((hit.getLocation()).z - pos.getZ()) * 16.0D) - OFFSET_Z;
                break;
            }
            case NORTH, SOUTH -> {
                render_x_offset = (int) (((hit.getLocation()).x - pos.getX()) * 16.0D) - OFFSET_X;
                render_y_offset = (int) (((hit.getLocation()).y - pos.getY()) * 16.0D) - OFFSET_Y;
                break;
            }
            case WEST, EAST -> {
                render_z_offset = (int) (((hit.getLocation()).z - pos.getZ()) * 16.0D) - OFFSET_Z;
                render_y_offset = (int) (((hit.getLocation()).y - pos.getY()) * 16.0D) - OFFSET_Y;
                break;
            }
        }

        if (this.level == null) return;
        refreshAll();
    }

    public void refreshAll() {
        refreshRocketState();
        setChanged();
        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(), 3);
    }

    public boolean fire() {
        if (this.level == null) return false;
        if (this.level.isClientSide) return false;
        if (!(level instanceof ServerLevel slevel)) return false;
        if (loaded_rockets <= 0) return false;

        Direction direction = getFacing();
        Vec3 spawnPosition = this.getBlockPos().relative(direction).getCenter();
        Vec3 randomOffset = new Vec3(random.nextDouble(), random.nextDouble(), random.nextDouble()).scale(0.25);

        Vector3fc d = direction.step();
        Vec3 look = new Vec3(d.x(), d.y(), d.z());

        Ship s = VSGameUtilsKt.getShipManagingPos(this.level, this.getBlockPos());

        if (s != null) {
            look = VSUtils.toWorldDirection(level, look, spawnPosition);
            spawnPosition = VSUtils.toWorldPosition(level, spawnPosition);
        }

        MissileRegistry.MissileRegistryEntry entry = ForeignMissileRegistry.ZUNI_MK32;

        MountedMissileEntity missile = entry.factory.create(AllEntities.MISSILE_ENTITY.get(), slevel, entry.id, entry.getItemEntry().get());
        missile.setPos(spawnPosition.add(randomOffset));
        missile.lookAt(EntityAnchorArgument.Anchor.FEET, missile.position().add(look));
        missile.setXRot(missile.xRotO);
        missile.setYRot(missile.yRotO);
        slevel.addFreshEntity(missile);
        FlexibleSeatEntity.sitDown(slevel, spawnPosition, missile);
        missile.tick();
        missile.launch();
        if (missile.getGuidance() != null) {
            missile.getGuidance().activateSeeker();
        }

        CBCUtils.playBlastLikeSoundOnServer(slevel,
                spawnPosition.x, spawnPosition.y, spawnPosition.z,
                RegistrySounds.MISSILE_LAUNCH.getMainEvent(),
                SoundSource.BLOCKS,  12.0F, 1.0F, 5.0F);

        loaded_rockets--;
        refreshAll();

        return true;
    }

    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("rockets", this.loaded_rockets);
        tag.putInt("color", this.color);
        tag.putInt("offset_x", this.render_x_offset);
        tag.putInt("offset_y", this.render_y_offset);
        tag.putInt("offset_z", this.render_z_offset);
    }

    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("rockets")) {
            this.loaded_rockets = tag.getInt("rockets");
            refreshRocketState();
        }
        if (tag.contains("color")) {
            this.color = tag.getInt("color");
        } else {
            this.color = DEFAULT_COLOR;
        }
        if (tag.contains("offset_x")) {
            this.render_x_offset = tag.getInt("offset_x");
        }
        if (tag.contains("offset_y")) {
            this.render_y_offset = tag.getInt("offset_y");
        }
        if (tag.contains("offset_z")) {
            this.render_z_offset = tag.getInt("offset_z");
        }
        if (tag.contains("offset_x") && tag.contains("offset_y") && tag.contains("offset_z")) {
            cached_shape = Shapes.ROCKET_POD_4.get(getFacing());
            is_shape_cached = true;
        }
    }

    @Override
    public boolean  addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        String loadedRocketDisplayed = "None";
        if (loaded_rockets > 0) {
            loadedRocketDisplayed = "Zuni Mk32";
        }

        int amount = loadedRocketDisplayed.equals("None") ? 0 : loaded_rockets;
        if (amount > 4) amount = 4;
        if (amount < 0) amount = 0;

        tooltip.add(Component.literal("    Rocket Pod Information:"));
        if (amount == 0) {
            tooltip.add(Component.literal("Rockets Loaded: "+loadedRocketDisplayed).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("Rockets Loaded: x"+amount+"/"+4+" "+loadedRocketDisplayed).withStyle(ChatFormatting.GRAY));
        }
        return true;
    }

    @Override
    public ItemStack getIcon(boolean isPlayerSneaking) {
        if (loaded_rockets <= 0) return AllItems.GOGGLES.asStack();
        return ForeignMissileRegistry.ZUNI_MK32.getItemEntry().asStack();
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.load(tag);
        }
    }

    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }
}