package net.croc.mw_peripherals;

import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import net.croc.mw_peripherals.network.UseItemOnPacket;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.lang.Math;

public class MouseclickHandler {
    private static MouseclickHandler instance;

    public static MouseclickHandler get() {
        if (instance == null)
            instance = new MouseclickHandler();
        return instance;
    }

    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public void onMouse(InputEvent.MouseButton.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT && event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
        if (event.getAction() != GLFW.GLFW_PRESS) return;
        if (!(MC.screen instanceof ChatScreen)) return;

        event.setCanceled(true);
        if (MC.player == null || MC.gameMode == null) return;

        InteractionHand hand = MC.player.getUsedItemHand();
        if (!(MC.player.getItemInHand(hand).getItem() instanceof AirItem)) return;

        float reach = MC.gameMode.getPickRange();
        HitResult hitResult = raycastFromMouse(MC.player, reach);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;

            MC.hitResult = hitResult;

            Level level = MC.player.level();
            Ship ship = VSGameUtilsKt.getShipManagingPos(level, blockHit.getBlockPos());

            if (ship != null) {
                blockHit = new BlockHitResult(
                        VSUtils.toShipPosition(ship, blockHit.getLocation()),
                        blockHit.getDirection(),
                        blockHit.getBlockPos(),
                        blockHit.isInside()
                );
            }

            UseItemOnPacket.useItemOn(MC.player, MC.player.getUsedItemHand(), blockHit);
        }
    }

    private HitResult raycastFromMouse(Player player, double reachDistance) {
        Camera camera = MC.gameRenderer.getMainCamera();

        double mouseX = MC.mouseHandler.xpos();
        double mouseY = MC.mouseHandler.ypos();

        double width = MC.getWindow().getScreenWidth();
        double height = MC.getWindow().getScreenHeight();
        double fov = MC.options.fov().get();

        float vFovRad = (float)Math.toRadians(fov);
        float aspect = (float)(width / height);

        float hFovRad = 2 * (float) Math.atan(Math.tan(vFovRad / 2) * aspect);

        float halfHFov = hFovRad / 2.0f;
        float halfVFov = vFovRad / 2.0f;

        float yaw = (float) Math.atan(Math.tan(halfHFov) * (2 * mouseX / width - 1));
        float pitch = (float) Math.atan(Math.tan(halfVFov) * (1 - 2 * mouseY / height));

        Vector3f right = camera.getLeftVector().mul(-1);
        Vector3f up = camera.getUpVector();

        Vector3f dir = camera.getLookVector()
                .rotateAxis(pitch, right.x, right.y, right.z)
                .rotateAxis(-yaw, up.x, up.y, up.z);

        return player.level().clip(new ClipContext(
                camera.getPosition(),
                camera.getPosition().add(new Vec3(dir.x, dir.y, dir.z).scale(reachDistance)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
    }
}