package net.croc.mw_peripherals.client.hmd;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static net.croc.mw_peripherals.client.hmd.HMDRenderer.*;

public class DrawUtils {
    private static int cached_width = -1;
    private static int cached_height = -1;
    private static ScaledDisplay cached_display = null;

    public static record ScaledDisplay(
            double cx, double cy,
            double radar_x, double radar_y, double radar_size,
            double scope_x, double scope_y,
            double cscope_w, double cscope_h,
            double bscope_size,
            double weapons_x, double weapons_y, double weapons_spacing,
            double iff_list_x, double iff_list_y, double iff_list_spacing,
            double maws_alert_y, double maws_chevron_x, double maws_el_scale,
            double flare_x, double flare_y,
            double dot_x1, double dot_y1,
            double dot_x2, double dot_y2,
            double speed_label_x, double speed_label_y,
            double speed_value_x, double speed_value_y,
            double alt_label_x, double alt_label_y,
            double alt_value_x, double alt_value_y,

            double g_label_x, double g_label_y,
            double g_value_x, double g_value_y,
            double over_g_x, double over_g_y,

            double az_baseline, double az_pixels_per_deg,
            double az_pointer_y, double az_heading_y,
            double az_tick_major_h, double az_tick_minor_h,
            double az_label_off_y,

            double aoa_left, double aoa_px_per_deg,
            double aoa_tick_major_h, double aoa_tick_minor_h,
            double aoa_val_y_offset, double aoa_lbl_x,

            double banner_w,
            double banner_h_top, double banner_h_bottom,

            double maws_upper_limit, double maws_lower_limit,
            double maws_rx1, double maws_rx2
    ) { }

    private static GuiGraphics gfx;
    public static void setGuiGraphics(GuiGraphics gfx) {
        DrawUtils.gfx = gfx;
    }

    private static double scale_x = 1;
    private static double scale_y = 1;

    private static double sx(double x) { // scale on X
        return Math.round(scale_x * x);
    }

    private static double sy(double y) { // scale on Y
        return Math.round(scale_y * y);
    }

    private static double sxy(double v) { // scale on the XY diagonal
        return Math.round(Math.sqrt(scale_x * scale_y) * v);
    }

    public static ScaledDisplay values(HMDRenderContext ctx) {
        int width = ctx.screenWidth();
        int height = ctx.screenHeight();

        if (width == cached_width && height == cached_height) return cached_display;

        scale_x = (double) width / designate_width;
        scale_y = (double) height / designate_height;

        cached_width = width;
        cached_height = height;
        cached_display = new ScaledDisplay(
                sx(427), sy(229),
                sx(radar_x), sy(radar_y), sxy(radar_size),
                sx(bscope_x), sy(bscope_y),
                sx(cscope_x), sy(cscope_y),
                sxy(bscope_size),
                sx(weaponsmanager_x), sy(weaponsmanager_y), sy(12),
                sx(iff_list_x), sy(iff_list_y), sy(iff_list_spacing),
                sy(maws_alert_y), sx(maws_chevron_x), maws_elevation_scale * scale_y,
                sx(flare_x), sy(flare_y),

                sx(426), sy(228),
                sx(428), sy(230),
                sx(282), sy(195),
                sx(282), sy(209),
                sx(542), sy(195),
                sx(542), sy(209),
                sx(427), sy(365),
                sx(427), sy(379),
                 sx(427), sy(62),

                 sy(27), 2.3 * scale_x,
                sy(2), sy(44),
                sy(8), sy(4),
                sy(2),

                sx(30), 3 * scale_y,
                sy(10), sy(5),
                sy(14), sx(42),

                sx(150),
                sy(8), sy(10),

                sy(20), sy(height - 40),
                sx(854), sx(24)
        );

        return cached_display;
    }

    public static void drawText(String id, int color, double x, double y, String text, boolean centered) {
        if (text == null) return;
        gfx.pose().pushPose();
        if (centered) {
            gfx.drawCenteredString(Minecraft.getInstance().font, text, (int) x, (int) y, color);
        } else {
            gfx.drawString(Minecraft.getInstance().font, text, (int) x, (int) y, color);
        }
        gfx.pose().popPose();
    }

    public static void drawLine(String id, int color, int thickness, double x1, double y1, double x2, double y2) {
        gfx.pose().pushPose();
        int dx = (int) (x2 - x1);
        int dy = (int) (y2 - y1);
        Vec2 perp = (new Vec2(-dy, dx)).normalized().scale(thickness / 2.0F);
        VertexConsumer consumer = gfx.bufferSource().getBuffer(RenderType.gui());
        PoseStack stack = gfx.pose();
        consumer.vertex(stack.last().pose(), (float) (x1 + perp.x), (float) (y1 + perp.y), 0.0F).color(color).normal(stack.last().normal(), 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(stack.last().pose(), (float) (x2 + perp.x), (float) (y2 + perp.y), 0.0F).color(color).normal(stack.last().normal(), 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(stack.last().pose(), (float) (x2 - perp.x), (float) (y2 - perp.y), 0.0F).color(color).normal(stack.last().normal(), 0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(stack.last().pose(), (float) (x1 - perp.x), (float) (y1 - perp.y), 0.0F).color(color).normal(stack.last().normal(), 0.0F, 0.0F, 1.0F).endVertex();
        gfx.flush();
        gfx.pose().popPose();
    }

    public static void drawRect(String id, int color, double x1, double y1, double x2, double y2) {
        int left = (int) Math.round(Math.min(x1, x2));
        int right = (int) Math.round(Math.max(x1, x2));
        int top = (int) Math.round(Math.min(y1, y2));
        int bottom = (int) Math.round(Math.max(y1, y2));

        gfx.fill(left, top, right, bottom, color);
    }

    public static void drawLine3D(String id, int color, Vec3 a, Vec3 b) {

    }

    public static void drawCircle(String id, int color, int thickness, double radius, double center_x, double center_y) {
        double step = Math.toRadians(360D / 32D);
        for (int s = 0; s < 31; s++) {
            double a1 = s * step, a2 = (s + 1) * step;
            drawLine(id + "_" + s, color, thickness,
                    center_x + radius * Math.cos(a1), center_y + radius * Math.sin(a1),
                    center_x + radius * Math.cos(a2), center_y + radius * Math.sin(a2)
            );
        }
    }
}