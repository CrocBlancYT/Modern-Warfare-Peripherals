package net.croc.mw_peripherals.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.util.HashMap;

import net.mcreator.rha.block.*;
import net.minecraftforge.registries.ForgeRegistries;

import static net.croc.mw_peripherals.RegistryIcon.loadNativeImage;

public class RHAColors {
    private record Color(int r, int g, int b) {
        public int asInt() {
            return r << 16 | g << 8 | b;
        }
    }

    private static Color getAverageColor(String namespace, String path) throws IOException {
        NativeImage image = loadNativeImage(new ResourceLocation(namespace, path));

        int sum_r = 0;
        int sum_g = 0;
        int sum_b = 0;

        int skippedPixels = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getPixelRGBA(x, y);

                int a = (color >> 24) & 0xFF;

                if (a > 10) {
                    sum_r += color & 0xFF;
                    sum_g += (color >> 8) & 0xFF;
                    sum_b += (color >> 16) & 0xFF;
                } else {
                    skippedPixels++;
                }
            }
        }

        double pixels = ((double) image.getHeight() * (double) image.getWidth()) - skippedPixels;
        double invPixels = 1 / pixels;
        return new Color((int) (sum_r * invPixels), (int) (sum_g * invPixels), (int) (sum_b * invPixels));
    }


    private static final HashMap<String, Color> cached_colors = new HashMap<>();

    public static int getColor(Item item) {
        ResourceLocation resource = ForgeRegistries.ITEMS.getKey(item);
        if (resource == null) return -1;

        String id = resource.toString();
        if (!id.contains("rha")) return -1;
        if (!id.contains("steel")) {
            if (!id.contains("hatch")) {
                return -1;
            }

            id = id.replace("hatch", "hardsteel");
        };

        id = id.replace("rha:", "");
        id = id.replace("rhaplus:", "");
        id = id.replaceAll(":", "");
        id = id.replaceAll("_", "");

        Color color = cached_colors.get(id);

        if (color != null) {
            return color.asInt();
        } else {
            try {
                color = getAverageColor("rha", "textures/block/"+id+".png");
            } catch (IOException e) {
                try {
                    color = getAverageColor("rha", "textures/block/"+id+"tex.png");
                } catch (IOException ignored) { }
            }
        }

        if (color != null) {
            cached_colors.put(id, color);
            return color.asInt();
        }

        return -1;
    }
}