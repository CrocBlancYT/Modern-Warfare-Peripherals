package net.croc.mw_peripherals.integration.tallyho.pod;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import java.util.List;

import edn.stratodonut.tallyho.missile.MissileRegistry;
import net.croc.mw_peripherals.RegistrySounds;
import net.croc.mw_peripherals.entity.MountedPodEntity;
import net.croc.mw_peripherals.integration.tallyho.IPodFactory;
import net.croc.mw_peripherals.integration.tallyho.PodComponent;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class RocketPod extends PodComponent {
    private final Properties properties;
    private int rocketCount;

    private static final RandomSource random = RandomSource.create();

    public record Properties(String rocketId, int rocketCount) {
        public void appendHoverText(List<Component> components) {
            components.add(
                    Component.literal("Rocket: (")
                            .append(Component.translatable("item.tallyho."+this.rocketId))
                            .append(Component.literal(") x"+this.rocketCount))
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))
            );
        }
    }

    public static class Factory implements IPodFactory<RocketPod> {
        private final RocketPod.Properties properties;

        public Factory(MissileRegistry.MissileRegistryEntry missile, int rocketCount) {
            this.properties = new RocketPod.Properties(missile.id, rocketCount);
        }

        public RocketPod create() {
            return new RocketPod(this.properties);
        }

        public void appendHoverText(List<Component> list) {
            this.properties.appendHoverText(list);
        }
    }

    public RocketPod(Properties props) {
        this.properties = props;
        this.rocketCount = props.rocketCount;
    }

    @Override
    public void tick(MountedPodEntity e) { }

    @Override
    public boolean launch(MountedPodEntity e, Vec3 dir, float boost) {
        if (this.rocketCount <= 0) {
            e.drop();
            return false;
        }

        this.rocketCount--;

        MissileRegistry.MissileRegistryEntry entry = MissileRegistry.getEntry(this.properties.rocketId);
        if (entry == null) return false;

        Ship s = VSGameUtilsKt.getShipMountedTo(e);
        if (e.level() instanceof ServerLevel serverLevel && s != null) {
            Vec3 spawnPosition = e.getEyePosition();
            Vec3 lookInShip = e.getLookAngle();
            Vec3 lookInWorld = VSUtils.toWorldDirection(e.level(), e.getLookAngle(), e.position());

            Vec3 randomOffset = new Vec3(random.nextDouble(), random.nextDouble(), random.nextDouble()).scale(0.6);


            MountedMissileEntity missile = entry.spawn(serverLevel, spawnPosition.add(randomOffset),
                    Direction.getNearest(lookInShip.x, 0.0D, lookInShip.z).toYRot());

            missile.lookAt(EntityAnchorArgument.Anchor.FEET, missile.position().add(lookInWorld));
            missile.tick();
            missile.launch();

            CBCUtils.playBlastLikeSoundOnServer(serverLevel,
                    spawnPosition.x, spawnPosition.y, spawnPosition.z,
                    RegistrySounds.MISSILE_LAUNCH.get(),
                    SoundSource.BLOCKS,  12.0F, 1.0F, 5.0F);
        }

        return true;
    }
}