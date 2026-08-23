package net.croc.mw_peripherals;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;

import static net.croc.mw_peripherals.Main.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryIcon {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                changeWindowIcon();
            } catch (Exception e) {
                Main.LOGGER.error("Failed to change window icon", e);
            }
        });
    }

    public static void changeWindowIcon() {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        try {
            List<NativeImage> iconImages = new ArrayList<>();
            iconImages.add(loadNativeImage(new ResourceLocation(MOD_ID, "textures/icon/icon_16x16.png")));
            iconImages.add(loadNativeImage(new ResourceLocation(MOD_ID, "textures/icon/icon_32x32.png")));
            iconImages.add(loadNativeImage(new ResourceLocation(MOD_ID, "textures/icon/icon_64x64.png")));
            List<ByteBuffer> iconBuffers = new ArrayList<>();
            for (NativeImage image : iconImages) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int color = image.getPixelRGBA(x, y);
                        buffer.put((byte)(color & 0xFF));
                        buffer.put((byte)(color >> 8 & 0xFF));
                        buffer.put((byte)(color >> 16 & 0xFF));
                        buffer.put((byte)(color >> 24 & 0xFF));
                    }
                }
                buffer.flip();
                iconBuffers.add(buffer);
            }
            MemoryStack stack = MemoryStack.stackPush();
            try {
                GLFWImage.Buffer icons = GLFWImage.malloc(iconImages.size(), stack);
                for (int i = 0; i < iconImages.size(); i++) {
                    NativeImage image = iconImages.get(i);
                    ByteBuffer buffer = iconBuffers.get(i);
                    (icons.get(i))
                            .width(image.getWidth())
                            .height(image.getHeight())
                            .pixels(buffer);
                }
                GLFW.glfwSetWindowIcon(window.getWindow(), icons);
                if (stack != null)
                    stack.close();
            } catch (Throwable throwable) {
                if (stack != null)
                    try {
                        stack.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }
            for (NativeImage image : iconImages)
                image.close();
        } catch (IOException e) {
            Main.LOGGER.error("Could not load icon image", e);
        }
    }

    public static NativeImage loadNativeImage(ResourceLocation location) throws IOException {
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(location).orElseThrow(() -> new FileNotFoundException(location.toString()));
        InputStream stream = resource.open();
        try {
            NativeImage nativeImage = NativeImage.read(stream);
            if (stream != null)
                stream.close();
            return nativeImage;
        } catch (Throwable throwable) {
            if (stream != null)
                try {
                    stream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            throw throwable;
        }
    }
}
