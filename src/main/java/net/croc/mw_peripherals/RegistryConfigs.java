package net.croc.mw_peripherals;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RegistryConfigs {

    @SuppressWarnings("deprecation")
    public static void register(ModLoadingContext modLoadingContext) {
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, RegistryConfigs.Config.SERVER_CONFIG);

        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);

        RegistryConfigs.Config.loadConfig(RegistryConfigs.Config.SERVER_CONFIG, Paths.get("config/croc-peripherals-server.toml"));

        // MinecraftForge.EVENT_BUS.register(this::onClientSetup);
    }

    public static class Config {
        public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SERVER_CONFIG;

        public static final ForgeConfigSpec.ConfigValue<Double> APS_COUNTER_FORCE;
        public static final ForgeConfigSpec.ConfigValue<Double> APS_COUNTER_MULTIPLIER;

        public static final ForgeConfigSpec.ConfigValue<Double> GYRO_MAX_TORQUE;
        public static final ForgeConfigSpec.ConfigValue<Double> GYRO_MAX_OMEGA;
        public static final ForgeConfigSpec.ConfigValue<Double> GYRO_MULTIPLIER;

        static {
            GYRO_MAX_TORQUE = SERVER_BUILDER
                    .comment("\n Gyro maximum torque output (on each axis)")
                    .define("gyro_max_torque_output", 10000.0);

            GYRO_MAX_OMEGA = SERVER_BUILDER
                    .comment("\n Gyro maximum input omega (on each axis)")
                    .define("gyro_max_omega_input", 25.0);

            GYRO_MULTIPLIER = SERVER_BUILDER
                    .comment("\n Gyro mass multiplier for torque")
                    .define("gyro_multiplier", 10.0);

            APS_COUNTER_FORCE = SERVER_BUILDER
                    .comment("\n How much the KE projectile is punched by the APS (impulse in blocks/s)")
                    .define("aps_force", 10.0);

            APS_COUNTER_MULTIPLIER = SERVER_BUILDER
                    .comment("\n How much the KE projectile is slown by (multiplier of velocity)")
                    .define("aps_multiplier", 0.75);

            SERVER_CONFIG = SERVER_BUILDER.build();
        }

        public static void loadConfig(ForgeConfigSpec config, Path path) {
            final CommentedFileConfig file = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
            file.load();
            config.setConfig(file);
        }
    }
}