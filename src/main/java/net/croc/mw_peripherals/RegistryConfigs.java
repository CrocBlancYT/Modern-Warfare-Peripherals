package net.croc.mw_peripherals;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class RegistryConfigs {
    public static void register(ModLoadingContext modLoadingContext) {
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, Config.SERVER_CONFIG);
        Config.loadConfig(Config.SERVER_CONFIG, Paths.get("config/mw-peripherals-server.toml", new String[0]));
    }

    public static class Config {
        public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        public static final ForgeConfigSpec SERVER_CONFIG;

        public static final ForgeConfigSpec.ConfigValue<Double> APS_COUNTER_FORCE = SERVER_BUILDER
                .comment("\n How much the KE projectile is punched by the APS (impulse in blocks/s)")
                .define("aps_force", 10.0D);

        public static final ForgeConfigSpec.ConfigValue<Double> APS_COUNTER_MULTIPLIER = SERVER_BUILDER
                .comment("\n How much the KE projectile is slown by (multiplier of velocity)")
                .define("aps_multiplier", 0.75D);

        public static final ForgeConfigSpec.ConfigValue<Double> GYRO_MAX_TORQUE = SERVER_BUILDER.comment("\n Gyro maximum torque output (on each axis)").define("gyro_max_torque_output", Double.valueOf(10000.0D));

        public static final ForgeConfigSpec.ConfigValue<Double> GYRO_MAX_OMEGA = SERVER_BUILDER.comment("\n Gyro maximum input omega (on each axis)").define("gyro_max_omega_input", Double.valueOf(25.0D));

        public static final ForgeConfigSpec.ConfigValue<Double> RAYCASTER_START_DISTANCE = SERVER_BUILDER
                .comment("\n How many blocks forward, the ray from the raycaster starts from")
                .define("raycast_start_distance", 1.0D);

        static {
            SERVER_CONFIG = SERVER_BUILDER.build();
        }

        public static void loadConfig(ForgeConfigSpec config, Path path) {
            CommentedFileConfig file = (CommentedFileConfig)CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
            file.load();
            config.setConfig((CommentedConfig)file);
        }
    }
}
