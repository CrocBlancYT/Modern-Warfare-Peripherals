package net.croc.mw_peripherals;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class PartialModels {
    public static void init() {}

    public static final PartialModel RADAR_BASE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar/base"));
    public static final PartialModel RADAR_DISH = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar/dish"));
    public static final PartialModel RADAR_CRADLE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar/cradle"));

    public static final PartialModel APS_2_BASE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/2_charge_aps/base"));
    public static final PartialModel APS_2_CHARGES = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/2_charge_aps/charges"));
    public static final PartialModel APS_2_CRADLE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/2_charge_aps/cradle"));
    public static final PartialModel APS_2_TUBES = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/2_charge_aps/tubes"));

    public static final PartialModel APS_TROPHY_BASE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/trophy_aps/base"));
    public static final PartialModel APS_TROPHY_CHARGE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/trophy_aps/charge"));
    public static final PartialModel APS_TROPHY_CRADLE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/trophy_aps/cradle"));

    public static final PartialModel ROCKET_POD_4_COLORED = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_4_colored"));
    public static final PartialModel ROCKET_POD_4_0 = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_4_state_0"));
    public static final PartialModel ROCKET_POD_4_1 = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_4_state_1"));
    public static final PartialModel ROCKET_POD_4_2 = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_4_state_2"));
    public static final PartialModel ROCKET_POD_4_3 = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_4_state_3"));
    public static final PartialModel ROCKET_POD_4_4 = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_4_state_4"));

    public static final PartialModel ROCKET_POD_7_COLORED = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_7_colored"));
    public static final PartialModel ROCKET_POD_7_UNCOLORED = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_7_uncolored"));
    public static final PartialModel ROCKET_POD_7_ROCKET = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_7_rocket"));

    public static final PartialModel ROCKET_POD_19_COLORED = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_19_colored"));
    public static final PartialModel ROCKET_POD_19_UNCOLORED = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_19_uncolored"));
    public static final PartialModel ROCKET_POD_19_ROCKET = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/launchers/rocket_pod_19_rocket"));

    public static final PartialModel JOYSTICK_UP = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick_up"));
    public static final PartialModel JOYSTICK_DOWN = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick_down"));
    public static final PartialModel JOYSTICK_RIGHT = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick_right"));
    public static final PartialModel JOYSTICK_LEFT = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick_left"));
    
    public static final PartialModel JOYSTICK_DEFAULT = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick"));
}