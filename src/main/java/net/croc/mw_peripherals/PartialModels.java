package net.croc.mw_peripherals;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class PartialModels {
    public static void init() {}

    public static final HashMap<String, PartialModel> APS_BASES = new HashMap<>();
    public static final HashMap<String, PartialModel> APS_CRADLES = new HashMap<>();
    public static final HashMap<String, PartialModel> APS_TURRETS = new HashMap<>();

    public static final PartialModel RADAR_BASE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar_base"));
    public static final PartialModel RADAR_DISH = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar_dish"));
    public static final PartialModel RADAR_CRADLE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar_cradle"));
    
    static {
        for (String variant : RegistryBlocks.APS_VARIANT_NAMES) {
            APS_BASES.put(variant, new PartialModel(new ResourceLocation(Main.MOD_ID+":other/aps_base/base_" + variant)));
            APS_CRADLES.put(variant, new PartialModel(new ResourceLocation(Main.MOD_ID+":other/aps_cradle/cradle_" + variant)));
            APS_TURRETS.put(variant, new PartialModel(new ResourceLocation(Main.MOD_ID+":other/aps_turret/turret_" + variant)));
        }
    }
}