package net.croc.mw_peripherals.utils;

import edn.stratodonut.tallyho.render.MissileEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MissileEntityRendererGetter {
    @OnlyIn(Dist.CLIENT)
    public static EntityRenderer<Entity> create(EntityRendererProvider.Context context) {
        return new MissileEntityRenderer(context);
    }
}