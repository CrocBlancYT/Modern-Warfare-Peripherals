package net.croc.mw_peripherals.blocks;

import java.util.List;
import javax.annotation.Nullable;

import com.mojang.math.Axis;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.stuff.APS_HardKill;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class APSBlockEntity extends BlockEntity {
    public static final int RANGE = 20;

    private int charges = 0;
    private int cooldown = 0;
    private int heightOffset = 0;

    private float yaw = 0.0F;
    private float pitch = 26.0F;

    private float client_yaw = 0.0F;
    private float client_pitch = 0.0F;

    private VoxelShape cachedShapeVoxel;
    private int cachedShapeHeight;

    public VoxelShape getCachedShape() {
        if ((this.cachedShapeHeight != this.heightOffset) || (this.cachedShapeVoxel == null)) {
            this.cachedShapeHeight = this.heightOffset;
            int height = this.getHeightOffset();
            this.cachedShapeVoxel = Block.box(1.0D, 0.0D, 1.0D, 15.0D, (13 + height), 15.0D);
        }

        return this.cachedShapeVoxel;
    }

    private float circular_lerp(float a, float b, float alpha) {
        float diff = (b - a);
        diff = ((diff + 180f) % 360f) - 180f;
        return (a + diff * alpha) % 360f;
    }

    private float lerp(float a, float b, float alpha) {
        return a + (b - a) * alpha;
    }

    public void onRender() {
        this.client_yaw = circular_lerp(this.client_yaw, this.yaw, 0.4F);
        this.client_pitch = lerp(this.client_pitch, this.pitch, 0.4F);
    }

    public float getClientYaw() {
        return this.client_yaw;
    }

    public float getClientPitch() {
        return this.client_pitch;
    }

    public void onWrenched(BlockHitResult hitResult) {
        this.heightOffset = (int) (((hitResult.getLocation()).y - this.getBlockPos().getY()) * 16.0D);
        setUpdated();
    }

    private void setUpdated() {
        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(), 2);
    }

    private float short_angle(float angle) {
        return ((angle + 180f) % 360f) - 180f;
    }

    public void setYaw(float yaw) {
        this.yaw = short_angle(yaw);
        setChanged();
        setUpdated();
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        setChanged();
        setUpdated();
    }

    public APSBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.APS_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, APSBlockEntity blockEntity) {
        if (blockEntity.cooldown > 0)
            blockEntity.cooldown--;
        int redstonePower = level.getBestNeighborSignal(pos);
        if (redstonePower > 0 && blockEntity.cooldown <= 0)
            autoIntercept(level, pos, blockEntity);
        blockEntity.setChanged();
    }

    private static void autoIntercept(Level level, BlockPos pos, APSBlockEntity APS) {
        List<Entity> incoming = APS_HardKill.detectProjectiles(APS, 20);
        incoming.forEach(entity -> {
            if (APS.charges > 0) {
                boolean success = APS_HardKill.tryKillProjectile(APS, entity);
                if (success) {
                    Vec3 targetPos = entity.getEyePosition();
                    APS.pointAt(new Vector3d(targetPos.x(), targetPos.y(), targetPos.z()));
                    APS.charges--;
                    APS.cooldown = 100;
                    APS.setChanged();
                }
            }
        });
    }

    public boolean addCharge() {
        if (this.charges < 2) {
            this.charges++;
            this.setChanged();
            return true;
        }
        return false;
    }

    public boolean useCharge() {
        if (this.charges > 0) {
            this.charges--;
            this.setChanged();
            return true;
        }
        return false;
    }

    public int getCharges() {
        return this.charges;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public int getHeightOffset() {
        int height = this.heightOffset;
        if (height > 16)
            height = 16;
        if (height < 0)
            height = 0;
        return height;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(APSBlock.FACING);
    }

    public void pointAt(Vector3d to) {
        BlockPos pos = this.getBlockPos();
        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipManagingPos(this.getLevel(), pos);

        if (ship != null) to = ship.getWorldToShip().transformPosition(to);

        Vector3d from = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.65D + this.heightOffset * 0.0625D, pos.getZ() + 0.5D);
        Vector3d dir = to.sub(from);

        Direction facing = this.getFacing();

        setYaw((float) Math.toDegrees(Math.atan2(dir.x, dir.z)) + facing.toYRot());
        setPitch((float)Math.toDegrees(Math.asin(dir.y / dir.length())));
        setUpdated();
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("charges", this.charges);
        tag.putInt("cooldown", this.cooldown);
        tag.putFloat("yaw", this.yaw);
        tag.putFloat("pitch", this.pitch);
        tag.putInt("offset", this.heightOffset);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("charges"))
            this.charges = tag.getInt("charges");
        if (tag.contains("cooldown"))
            this.cooldown = tag.getInt("cooldown");
        if (tag.contains("yaw"))
            this.yaw = tag.getFloat("yaw");
        if (tag.contains("pitch"))
            this.pitch = tag.getFloat("pitch");
        if (tag.contains("offset"))
            this.heightOffset = tag.getInt("offset");
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null)
            this.load(tag);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }
}
