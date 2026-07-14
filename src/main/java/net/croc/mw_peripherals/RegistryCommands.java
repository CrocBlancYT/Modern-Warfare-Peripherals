package net.croc.mw_peripherals;

import com.mojang.brigadier.CommandDispatcher;
import net.croc.mw_peripherals.stuff.ShipHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.core.apigame.VSCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.command.VSCommands;

import javax.annotation.Nullable;
import java.util.HashMap;

public class RegistryCommands {

    private static void getConnectedShip(Level level, Ship start, HashMap<Ship, Ship> target) {
        for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, start.getWorldAABB())) {
            if (!target.containsKey(ship)) {
                target.put(ship, ship);
                getConnectedShip(level, start, target);
            }
        }
    }

    @Nullable
    private static Ship getShipAtHit(Level level, HitResult hit) {
        for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, new AABB(hit.getLocation(), hit.getLocation()).inflate(0.5D))) {
            return ship;
        }
        return null;
    }

    private static void teleportShips(Player player) {
        if (player == null) return;

        Level level = player.level();

        HitResult hit = player.pick(10.0f, 0.0f, false);

        HashMap<Ship, Ship> ships = new HashMap<>();

        Ship starting_ship = getShipAtHit(level, hit);
        if (starting_ship == null) return;

        ships.put(starting_ship, starting_ship);
        getConnectedShip(level, starting_ship, ships);

        Vector3dc start_pos = starting_ship.getTransform().getPositionInWorld();
        Vector3d teleport_pos = (Vector3d) start_pos;

        for (Ship ship : ships.values()) {
            AABBic box = ship.getShipAABB();

            if (box != null) {
                double height = (box.maxY() - box.minY()) + 3.5;

                ShipTeleportDataImpl teleportData = new ShipTeleportDataImpl(
                        teleport_pos,
                        new Quaterniond(),
                        new Vector3d(),
                        new Vector3d(),
                        ship.getChunkClaimDimension(),
                        1.0D
                );

                teleport_pos = teleport_pos.add(new Vector3d(0, height, 0));

                ServerShipWorld world = (ServerShipWorld) VSGameUtilsKt.getShipObjectWorld(level);

                ShipHandle.getOrCreate((ServerShip) ship).setStatic(true);
                VSGameUtilsKt.getVsCore().teleportShip(world, (ServerShip) ship, teleportData);
            }
        }
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("ships")
                .then(Commands.literal("split")
                        .executes(context -> {
                            Player player = context.getSource().getPlayer();
                            teleportShips(player);
                            return 1;
                        })));
    }
}