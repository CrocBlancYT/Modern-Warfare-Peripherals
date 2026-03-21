package net.croc.mw_peripherals.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

import static net.croc.mw_peripherals.Main.MOD_ID;

public class RenderUtils {
    public static ResourceLocation getLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static final HashMap<String, BakedModel> cachedModels = new HashMap<>();

    public static BakedModel fetchModel(ResourceLocation modelLocation) {
        return Minecraft.getInstance().getModelManager().getModel(modelLocation);
    }

    public static BakedModel getModel(String path) {
        BakedModel model = cachedModels.get(path);
        if (model == null) {
            model = fetchModel(getLocation(path));
            cachedModels.put(path, model);
        }
        return model;
    }
}
