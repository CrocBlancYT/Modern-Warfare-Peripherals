package net.croc.mw_peripherals.blocks.launchers;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import edn.stratodonut.tallyho.AllEntities;
import edn.stratodonut.tallyho.camera.entity.FlexibleSeatEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.MissileRegistry;
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
import net.minecraft.world.item.Item;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RocketPod7BlockEntity extends BlockEntity implements IHaveGoggleInformation {
    private static final int DEFAULT_COLOR = 0xFFFFFF;
    private static final int MAX_ROCKETS = 7;
    private static final RandomSource random = RandomSource.create();

    private static final HashMap<Item, LoadedRocket> rocketItems = new HashMap<>();
    private static final HashMap<String, LoadedRocket> rocketFromNames = new HashMap<>();

    public static enum LoadedRocket {
        Hydra70(ForeignMissileRegistry.HYDRA_70),
        MK4FFAR(ForeignMissileRegistry.MK4_FFAR),
        APKWS(MissileRegistry.APKWS),
        None(null);

        public final @Nullable MissileRegistry.MissileRegistryEntry entry;

        LoadedRocket(@Nullable MissileRegistry.MissileRegistryEntry entry) {
            this.entry = entry;
            if (entry != null && entry.getItemEntry() != null) {
                rocketItems.put(entry.getItemEntry().get(), this);
            }
            rocketFromNames.put(this.getName(), this);
        }

        public String getName() {
            if (this.entry == null) return "none";
            return this.entry.id;
        }
    }

    public boolean addRocket(Item rocketItem) {
        LoadedRocket type = rocketItems.get(rocketItem);
        if (type == null) return false;
        if (type.entry == null) return false;

        if (loaded_rocket_type != LoadedRocket.None) { // needs to be the same type
            if (type != loaded_rocket_type) { // isn't the same type
                return false;
            }
        }

        if (loaded_rocket_count >= MAX_ROCKETS) return false;
        loaded_rocket_count++;
        this.loaded_rocket_type = rocketItems.get(rocketItem);
        refreshAll();
        return true;
    }

    private LoadedRocket loaded_rocket_type = LoadedRocket.None;
    private int loaded_rocket_count = 0;
    private int color = DEFAULT_COLOR;

    public boolean is_shape_cached = false;
    public Direction cached_shape_direction = null;
    public VoxelShape cached_shape = null;
    public int render_x_offset = 0;
    public int render_y_offset = 0;
    public int render_z_offset = 0;

    public RocketPod7BlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.ROCKET_POD_7_BLOCK_ENTITY.get(), pos, state);
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(RocketPod4Block.HORIZONTAL_FACING);
    }

    public int getLoadedRockets() {
        return this.loaded_rocket_count;
    }

    public LoadedRocket getLoadedRocketType() {
        return loaded_rocket_type;
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
        return cached_shape = Shapes.ROCKET_POD_7.get(direction);
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
        setChanged();
        assert this.level != null;
        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(), 3);
    }

    public boolean fire() {
        if (this.level == null) return false;
        if (this.level.isClientSide) return false;
        if (!(level instanceof ServerLevel slevel)) return false;
        if (loaded_rocket_count <= 0) {
            this.loaded_rocket_type = LoadedRocket.None;
            return false;
        };
        if (this.loaded_rocket_type == LoadedRocket.None) return false;

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

        MissileRegistry.MissileRegistryEntry entry = this.loaded_rocket_type.entry;
        if (entry == null) return false;

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

        loaded_rocket_count--;
        refreshAll();
        if (loaded_rocket_count == 0) this.loaded_rocket_type = LoadedRocket.None;

        return true;
    }

    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("rockets", this.loaded_rocket_count);
        tag.putString("rockets_type", this.loaded_rocket_type.getName());
        tag.putInt("color", this.color);
        tag.putInt("offset_x", this.render_x_offset);
        tag.putInt("offset_y", this.render_y_offset);
        tag.putInt("offset_z", this.render_z_offset);
    }

    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("rockets")) {
            this.loaded_rocket_count = tag.getInt("rockets");
            if (this.loaded_rocket_count > MAX_ROCKETS) this.loaded_rocket_count = MAX_ROCKETS;
        }
        if (tag.contains("rockets_type")) {
            this.loaded_rocket_type = Objects.requireNonNullElse(rocketFromNames.get(tag.getString("rockets_type")), LoadedRocket.None);
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
            cached_shape = Shapes.ROCKET_POD_7.get(getFacing());
            is_shape_cached = true;
        }
    }

    @Override
    public boolean  addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        String loadedRocketDisplayed = switch (loaded_rocket_type) {
            case Hydra70 -> "Hydra 70";
            case MK4FFAR -> "MK4 FFAR";
            case APKWS -> "APKWS";
            default ->  "None";
        };

        int amount = loadedRocketDisplayed.equals("None") ? 0 : loaded_rocket_count;
        if (amount > MAX_ROCKETS) amount = MAX_ROCKETS;
        if (amount < 0) amount = 0;

        tooltip.add(Component.literal("    Rocket Pod Information:"));
        if (amount == 0) {
            tooltip.add(Component.literal("Rockets Loaded: "+loadedRocketDisplayed).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("Rockets Loaded: x"+amount+"/"+MAX_ROCKETS+" "+loadedRocketDisplayed).withStyle(ChatFormatting.GRAY));
        }
        return true;
    }

    @Override
    public ItemStack getIcon(boolean isPlayerSneaking) {
        return switch (loaded_rocket_type) {
            case Hydra70 -> RocketPod19BlockEntity.LoadedRocket.Hydra70.entry.getItemEntry().asStack();
            case MK4FFAR -> RocketPod19BlockEntity.LoadedRocket.MK4FFAR.entry.getItemEntry().asStack();
            case APKWS -> RocketPod19BlockEntity.LoadedRocket.APKWS.entry.getItemEntry().asStack();
            default ->  AllItems.GOGGLES.asStack();
        };
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