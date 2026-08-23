package net.croc.mw_peripherals.client.hmd;

import kotlin.Pair;
import net.croc.mw_peripherals.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.croc.mw_peripherals.client.hmd.DrawUtils.*;
import static net.croc.mw_peripherals.client.hmd.DrawUtils.drawLine;

public class HMDRenderer {
    private static final int Z_INDEX = 101;

    // colors
    public static int
            color_readout = 0xFF00FF00,
            color_boresight = 0xFF00FF00,
            color_fpv = 0xFF00FF00,
            color_alert = 0xFFFF0000,
            color_aoa = 0xFF00FF00,
            color_radar = 0xFF00FF00,
            color_radar_dim = 0xAA00FF00,
            color_radar_friendly = 0xFF00FFFF,
            color_radar_friendly_dim = 0x4400FFFF,
            color_datalink = 0x6600FF00,
            color_datalink_friend = 0x6600FFFF,
            color_maws = 0xFFFF0000,
            color_maws_bg = 0xAA440000,
            color_iff_list = 0xFF00FFFF,
            color_panel_bg = 0x99002200,
            color_scope_bg = 0x99002200,
            color_bscope = 0xFF00FF00,
            color_bscope_dim_grid = 0xAA00FF00,
            color_cscope_dim_grid = 0xAA00FF00,
            color_cscope_axis_grid = 0xCC00FF00,
            color_weaponsmanager = 0xFF00FF00,
            color_weaponsmanager_selected = 0xFF44FF44,
            color_flare_idle = 0xFFFFFF00,
            color_flare_empty = 0xFFFF0000,
            color_flare_active = 0xFF44FF44;


    // configs
    public static int
            radar_x = 14,
            radar_y = 323,
            radar_size = 120,
            radar_range = 3000,
            radar_cone = 90;

    public static int
            bscope_x = 674,
            bscope_y = 14,
            bscope_size = 170,
            bscope_azimuth_range = 60;

    public static int
            cscope_x = 170,
            cscope_y = 130,
            cscope_azimuth_range = 75,
            cscope_elevation_up = 50,
            cscope_elevation_down = 30;

    public static int
            weaponsmanager_x = 770,
            weaponsmanager_y = 340;

    public static int
            flare_x = 770,
            flare_y = 325;

    public static double
            iff_list_x = 10,
            iff_list_y = 80,
            iff_list_spacing = 14,
            iff_list_max = 5,
            iff_proximity = 10;

    public static double
            maws_scan_interval = 0.15,
            maws_alert_y = 90,
            maws_chevron_x = 8,
            maws_elevation_scale = 3;

    public static int
            designate_width = 854,
            designate_height = 480;

    // misc
    public static double
            flare_load = 0, // todo: get from flare dispensers
            flare_auto_range = 500,
            flare_auto_cooldown = 2.0,
            flare_pulse_width = 0.05,
            flare_pulse_gap = 0.05;

    public static int flare_auto_salvo = 3;

    public static double over_g_limit = 10;
    public static double update_seconds = 0.05;
    public static double radar_scan_interval = 0.5;
    public static double boot_splash_duration = 4.0;
    public static Vec3 gravity = new Vec3(0, -10.0, 0);

    public static double
            boresight_distance = 50,
            boresight_radius = 1.1;

    public static double
            fpv_distance = 40,
            fpv_radius = 1.1,
            fpv_min_speed = 2;

    // todo: move all variables to a state-machine for the renderer
    public static class PlaneData {
        public Vec3 position;
        public Vec3 velocity;
        public Vec3 orientation;
        public double g;
        public boolean has_velocity;
        Quaternionf rotation;

        public PlaneData(Vec3 position, Vec3 velocity, Vec3 orientation,
                int g, boolean has_velocity, Quaternionf rotation) {
            this.position = position;
            this.velocity = velocity;
            this.orientation = orientation;
            this.g = g;
            this.has_velocity = has_velocity;
            this.rotation = rotation;
        }
    }

    public static final PlaneData shipdata = new PlaneData(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 1, false, null);
    public static ArrayList<Vec3> radar_contacts = new ArrayList<>();
    public static boolean radar_ok = false;
    public static boolean is_linked = false;

    public static ArrayList<WeaponGroup> weapon_cache = new ArrayList<>();
    public static double weapon_scan_timer = 0;
    public static int weapon_selected = 1;
    public static int weapon_cycle_prev = 0;
    public static int weapon_fire_prev = 0;

    public static int scope_mode = 0;
    public static int scope_toggle_prev = 0;

    public static boolean maws_ok = false;
    public static ArrayList<Vec3> maws_missiles = new ArrayList<>();
    public static int maws_scan_timer = 0;

    public static int flares = (int) flare_load;
    public static int flare_prev = 0;
    public static double current_flare_auto_cooldown = 0;
    public static double flare_own_launch = 0;
    public static int flare_pulse_count = 0;
    public static double flare_pulse_timer = 0;
    public static boolean flare_pulse_high = false;

    public static ArrayList<ContactEntry> contact_cache = new ArrayList<>();

    public static boolean iff_enabled = false;
    public static int
            iff_key = 0,
            iff_callsign = 0,
            iff_session_id = 0,
            iff_my_key = 0;

    public static ArrayList<IFF_Friendly> iff_friendlies = new ArrayList<>();
    public static ArrayList<IFF_DatalinkShared> iff_shared_contacts = new ArrayList<>();

    public static Vec3 toVec3(Vector3d vec) {
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public static Vec3 toVec3(Vector3dc vec) {
        return new Vec3(vec.x(), vec.y(), vec.z());
    }

    public static String formatNumber(double value, int decimals) {
        return String.format("%." + decimals + "f", value);
    }

    public static record IFF_Friendly(String callsign, int cid, Vec3 pos) { }

    public static record IFF_DatalinkShared(List<IFF_Friendly> contacts) { }

    public static record ContactEntry(
            Vec3 worldPosition, Vec3 bodyPosition,
            double fwd, double distSqr, double horizontalDistSqr,
            double azimuth, double elevation,
            boolean friendly, boolean is_maws, boolean is_datalink
    ) { }

    public static record WeaponGroup(
            String id, ArrayList<Object> ordnances
    ) {}

    public static boolean isFriendlyPos(Vec3 from) {
        if (!iff_enabled) return false;

        for (IFF_Friendly friendly : iff_friendlies) {
            if ((from.distanceToSqr(friendly.pos)) < iff_proximity*iff_proximity) {
                return true;
            }
        }

        return false;
    }

    public static boolean isInRadarCone(Vec3 pos) {
        double cone = radar_cone;

        double dx = pos.x - shipdata.position.x;
        double dz = pos.z - shipdata.position.z;
        double distance = Math.sqrt(dx*dx + dz*dz);
        if (distance < 0.1) return true;

        double world_az = Math.toDegrees(Math.atan2(dx, -dz));
        double relative = ((world_az - shipdata.orientation.y + 180) % 360) - 180;
        return Math.abs(relative) <= cone * 0.5D;
    }

    public static double stableHeading(HMDRenderContext ctx) {
        Quaternionf q = shipdata.rotation;
        if (q == null) return shipdata.orientation.y;

        double x=q.x, y=q.y, z=q.z, w=q.w;
        Vec3 forward = ctx.forwardVector();

        double tx = 2 * (y * forward.z - z * forward.y);
        double ty = 2 * (z * forward.x - x * forward.z);
        double tz = 2 * (x * forward.y - y * forward.x);

        Vec3 world_forward = new Vec3(
                forward.x + w * tx + y * tz - z * ty,
                forward.y + w * ty + z * tx - x * tz,
                forward.z + w * tz + x * ty - y * tx
        );

        return Math.toDegrees(Math.atan2(world_forward.x, -world_forward.z)) % 360;
    }

    public static double computeAoA() {
        double speed = shipdata.velocity.length();
        if (speed <= fpv_min_speed) return 0;

        Quaternionf q = shipdata.rotation;
        if (q == null) return 0;
        double x=q.x, y=q.y, z=q.z, w=q.w;

        Vec3 v = shipdata.velocity;
        double tx = 2 * (y * v.z - z * v.y);
        double ty = 2 * (z * v.x - x * v.z);
        double tz = 2 * (x * v.y - y * v.x);
        double body_vx = v.x - w * tx + (y * tz - z * ty);
        double body_vy = v.y - w * ty + (z * tx - x * tz);
        return Math.toDegrees(Math.atan2(-body_vy, body_vx));
    }

    public static void updateFightData(HMDRenderContext ctx, double dt) {
        Ship ship = ctx.ship();
        if (ship == null) return;

        Vec3 position = toVec3(ship.getWorldAABB().center(new Vector3d()));
        Vec3 velocity = toVec3(ship.getVelocity());
        Vec3 omega = toVec3(ship.getOmega());
        Quaternionf rot = ship.getShipToWorld().getNormalizedRotation(new Quaternionf());
        shipdata.rotation = rot;

        Vec3 euler = MathUtils.getEulerAngles(rot);
        double pitch = euler.x, yaw = euler.y, roll = euler.z;
        Vec3 orientation = new Vec3(Math.toDegrees(pitch), Math.toDegrees(yaw), Math.toDegrees(roll));
        if (shipdata.has_velocity && dt <= 0.25) {
            Vec3 proper_acceleration = (velocity.subtract(shipdata.velocity)).scale(1 / dt).add(omega.cross(velocity)).subtract(gravity);
            shipdata.g = proper_acceleration.length() / gravity.length();
        }
        orientation = new Vec3(orientation.x, stableHeading(ctx), orientation.z);
        shipdata.position = position;
        shipdata.velocity = velocity;
        shipdata.orientation = orientation;
        shipdata.has_velocity = true;
    }

    public static Vec3 world_to_body(Vec3 v) {
        Quaternionf q = shipdata.rotation;
        if (q == null) return v;
        double x=q.x, y=q.y, z=q.z, w=q.w;
        double tx = 2 * (y * v.z - z * v.y);
        double ty = 2 * (z * v.x - x * v.z);
        double tz = 2 * (x * v.y - y * v.x);
        return new Vec3(
                v.x - w * tx + (y * tz - z * ty),
                v.y - w * ty + (z * tx - x * tz),
                v.z - w * tz + (x * ty - y * tx)
        );
    }

    public static Vec3 local_to_world(Vec3 offset) {
        Quaternionf q = shipdata.rotation;
        if (q == null) return shipdata.position.add(offset);
        double x=q.x, y=q.y, z=q.z, w=q.w;
        double tx = 2 * (y * offset.z - z * offset.y);
        double ty = 2 * (z * offset.x - x * offset.z);
        double tz = 2 * (x * offset.y - y * offset.x);
        return new Vec3(
                offset.x + w * tx + y * tz - z * ty,
                offset.y + w * ty + z * tx - x * tz,
                offset.z + w * tz + x * ty - y * tx
        ).add(shipdata.position);
    }

    public static void drawBoresight() {
        int color = color_boresight;
        double dist = boresight_distance * 1;
        double w = boresight_radius, h = boresight_radius * 0.45, vw = boresight_radius * 0.38;
        Vec3 tl = local_to_world(new Vec3(dist,  h, -w));
        Vec3 bl = local_to_world(new Vec3(dist, -h, -vw));
        Vec3 tc = local_to_world(new Vec3(dist,  h,  0));
        Vec3 br = local_to_world(new Vec3(dist, -h,  vw));
        Vec3 tr = local_to_world(new Vec3(dist,  h,  w));
        drawLine3D("bore_s1", color, tl, bl);
        drawLine3D("bore_s2", color, bl, tc);
        drawLine3D("bore_s3", color, tc, br);
        drawLine3D("bore_s4", color, br, tr);
    }

    public static void drawFPV(double speed) {
        if (speed < fpv_min_speed) return;
        Vec3 forward = shipdata.velocity.normalize();
        Vec3 right = forward.cross(new Vec3(0,1,0));
        if (right.lengthSqr() < 0.01*0.01) right = forward.cross(new Vec3(1,0,0));
        right = right.normalize();
        Vec3 up = right.cross(forward).normalize();
        Vec3 centre = shipdata.position.add(forward.scale(fpv_distance));
        double r = fpv_radius * 0.4;
        double tick = r * 0.8;
        int color = color_fpv;
        double step = Math.toRadians(360D / 8D);

        for (int i = 0; i < 7; i++) {
            double a1 = i * step;
            double a2 = (i + 1) * step;
            DrawUtils.drawLine3D("fpv_c_" + i, color,
                    centre.add(right.scale(r).scale(Math.cos(a1)))
                            .add(up.scale(r).scale(Math.sin(a1))),
                    centre.add(right.scale(r).scale(Math.cos(a2)))
                            .add(up.scale(r).scale(Math.sin(a2)))
            );
        }

        drawLine3D("fpv_tu", color, centre.add(up.scale(r)), centre.add(up.scale(r + tick)));
        drawLine3D("fpv_td", color, centre.subtract(up.scale(r)), centre.subtract(up.scale(r + tick)));
        drawLine3D("fpv_tl", color, centre.subtract(right.scale(r)), centre.subtract(right.scale(r + tick)));
        drawLine3D("fpv_tr", color, centre.add(right.scale(r)), centre.add(right.scale (r + tick)));
    }

    public static String azimuthLabel(double degrees) {
        degrees = degrees % 360;
        if (degrees == 0) return "N";
        if (degrees == 90) return "E";
        if (degrees == 180) return "S";
        if (degrees == 270) return "W";
        return String.format("%03d", (int) degrees);
    }

    public static void drawAzimuthTape(HMDRenderContext ctx) {
        ScaledDisplay layout = DrawUtils.values(ctx);

        double heading = Math.round(shipdata.orientation.y % 360);
        double span = 42;
        double ppd = layout.az_pixels_per_deg();
        double baseline = layout.az_baseline();
        double first = Math.floor((heading - span) * 0.2D) * 5;
        double last = Math.ceil((heading + span) * 0.2D) * 5;

        drawLine("az_base", color_readout, 1,
                layout.cx() - Math.round(span * ppd), baseline,
                layout.cx() + Math.round(span * ppd), baseline);

        for (int mark = (int) first; mark < last; mark++) {
            double x = layout.cx() + Math.round((mark - heading) * ppd);
            boolean major = mark % 10 == 0, labelled =  mark % 30 == 0;
            drawLine("az_tick_" + mark, color_readout,
                    major ? 2 : 1, x, baseline, x,
                    baseline - (major ? layout.az_tick_major_h() : layout.az_tick_minor_h()));

            if (labelled) {
                drawText("az_label_" + mark, color_readout, x, baseline + layout.az_label_off_y(), azimuthLabel(mark), true);
            }
        }
        drawText("az_pointer", color_readout, layout.cx(), layout.az_pointer_y(), "v", true);
        drawText("az_heading", color_readout, layout.cx(), layout.az_heading_y(),
                "AZ " + String.format("%03d", (int) heading), true);
    }

    public static void drawGForce(HMDRenderContext ctx) {
        ScaledDisplay layout = DrawUtils.values(ctx);

        boolean is_over_g = shipdata.g > over_g_limit;
        boolean flash_on = (Math.floor(System.currentTimeMillis()) / 250) % 2 == 0;
        int g_color = is_over_g && flash_on ? color_alert : color_readout;
        drawText("g_label", g_color, layout.g_label_x(), layout.g_label_y(), "G-FORCE", true);
        drawText("g_value", g_color, layout.g_value_x(), layout.g_value_y(), formatNumber(shipdata.g, 2) + "G", true);

        if (is_over_g && flash_on) {
            drawText("over_g", color_alert, layout.over_g_x(), layout.over_g_y(), "OVER-G", true);
        }
    }

    public static void drawIFFFriendlyList(HMDRenderContext ctx) {
        if (!iff_enabled) return;
        ArrayList<Pair<String, Double>> entries = new ArrayList<>();

        int key = 0;
        for (IFF_Friendly friendly : iff_friendlies) {
            key++;
            if (key != iff_my_key) {
                Vec3 diff = friendly.pos.subtract(shipdata.position);
                double range = diff.length();
                String ident = !friendly.callsign.isEmpty() ? friendly.callsign : "PC-" + friendly.cid;
                entries.add(new Pair<>(ident, range));
            }
        }

        entries = (ArrayList<Pair<String, Double>>) entries.stream().sorted((Pair<String, Double> a, Pair<String, Double> b) -> {
            double a_range = a.component2();
            double b_range = b.component2();
            return (int)(a_range - b_range);
        }).toList();

        ScaledDisplay layout = DrawUtils.values(ctx);

        double x = layout.iff_list_x(), y = layout.iff_list_y();
        double spacing = layout.iff_list_spacing();

        for (int i = 1; i < iff_list_max; i++) {
            Pair<String, Double> entry = entries.get(i-1);

            String label = (entry != null) ? String.format("%s  %.0fm", entry.component1(), entry.component2()) : "---";
            drawText("iff_list_" + i, color_iff_list, x, y, label, false);
            y = y + spacing;
        }
    }

    public static void doMAWsScan(double dt) {
        if (!maws_ok) {
            maws_scan_timer = (int) (maws_scan_timer + dt);
            if (maws_scan_timer >= 5) {
                maws_scan_timer = 0;
                // todo: get maws
                maws_ok = true;
            }
            maws_missiles = new ArrayList<>();
        }

        maws_scan_timer = (int) (maws_scan_timer + dt);
        if (maws_scan_timer < maws_scan_interval) return;
        maws_scan_timer = 0;

        // todo: access maws
        //local ok, raw = pcall(maws.detect, maws)
        //maws_missiles = (ok and type(raw) == "table") and raw or {}
    }

    public static void drawMAWs(HMDRenderContext ctx) {
        if (!maws_ok) return;
        if (flare_own_launch > 0) return;

        ArrayList<ContactEntry> maws_entries = new ArrayList<>();
        for (ContactEntry contact : contact_cache) {
            if (contact.is_maws) {
                maws_entries.add(contact);
            }
        }

        if (maws_entries.isEmpty()) return;

        ScaledDisplay layout = DrawUtils.values(ctx);

        double alert_y = layout.maws_alert_y();

        double closest_range = -1;
        for (ContactEntry entry : maws_entries) {
            if (closest_range == -1 || entry.distSqr < closest_range*closest_range) {
                closest_range = Math.sqrt(entry.distSqr);
            }
        }

        String label = String.format("* MAWS %.0fm *", closest_range);

        drawRect("maws_bg", (int) layout.maws_alert_y(),
                layout.cx() - Math.round(layout.banner_w()/2), alert_y - layout.banner_h_top(),
                layout.cx() + Math.round(layout.banner_w()/2), alert_y + layout.banner_h_bottom());
        drawText("maws_alert", color_maws, layout.cx(), alert_y, label, true);


        double chevron_x = layout.maws_chevron_x();
        double el_scale = layout.maws_el_scale();
        double chevron_count = 0;

        for (ContactEntry entry : maws_entries) {
            if (entry.fwd > 0.1) {
                double el = entry.elevation;
                double az = entry.azimuth;

                chevron_count++;
                double cy = layout.cy() - Math.floor(el * el_scale + 0.5);

                if (cy < layout.maws_upper_limit()) {
                    cy = layout.maws_upper_limit();
                } else if (cy > layout.maws_lower_limit()) {
                    cy = layout.maws_lower_limit();
                }

                if (az < -15) {
                    drawText("maws_chv_l" + chevron_count, color_maws, chevron_x, cy, "<<<", false);
                } else {
                    double rx = layout.maws_rx1() - chevron_x - layout.maws_rx2();
                    drawText("maws_chv_r" + chevron_count, color_maws, rx, cy, ">>>", false);
                }
            }
        }
    }

    public static int flareQueue(int n) {
        if (flares <= 0) return 0;
        if (n > flares) n = flares;
        if (n <= 0) return 0;

        if (flare_pulse_count > 0) {
            int add = Math.min(n, 10 - flare_pulse_count);
            if (add <= 0) return 0;
            flare_pulse_count = flare_pulse_count + add;
            flares = flares - add;
            return add;
        }

        flare_pulse_count = n;
        flare_pulse_timer = 0;
        flare_pulse_high = false;
        flares = flares - n;
        return n;
    }

    public static void flareTick(double dt) {
        if (flare_pulse_count > 0) {
            flare_pulse_timer = flare_pulse_timer - dt;
            if (flare_pulse_timer <= 0) {
                if (flare_pulse_high) { // the redstone setOutput's interact with the flare dispenser
                    // todo: pcall(redstone.setOutput, CONFIG.flare_side, false)
                    flare_pulse_high = false;
                    flare_pulse_count = flare_pulse_count - 1;
                    flare_pulse_timer = flare_pulse_count > 0 ? flare_pulse_gap : 0;
                } else {
                    // todo: pcall(redstone.setOutput, CONFIG.flare_side, true)
                    flare_pulse_high = true;
                    flare_pulse_timer = flare_pulse_width;
                }
            }

        } else if (flare_pulse_high) {
            // todo : pcall(redstone.setOutput, CONFIG.flare_side, false)
            flare_pulse_high = false;
        }

        if (current_flare_auto_cooldown > 0) {
            current_flare_auto_cooldown = current_flare_auto_cooldown - dt;
        }
        if (flare_own_launch > 0) {
            flare_own_launch = flare_own_launch - dt;
        }

        if (KeyBinds.HMD_FLARE.consumeClick() && flare_prev == 0) {
            flareQueue(1);
        }
        flare_prev = 5;

        if (current_flare_auto_cooldown <= 0 && flare_own_launch <= 0) {
            if (maws_ok && !contact_cache.isEmpty()) {
                double closestSqr = -1;
                for (ContactEntry c : contact_cache) {
                    if (c.is_maws) {
                        if (closestSqr == -1 || c.distSqr < closestSqr) {
                            closestSqr = c.distSqr;
                        }
                    }
                }
                if (closestSqr != -1 && closestSqr <= flare_auto_range*flare_auto_range) {
                    double sent = flareQueue(flare_auto_salvo);
                    if (sent > 0) {
                        current_flare_auto_cooldown = flare_auto_cooldown;
                    }
                }
            }
        }
    }

    public static void drawFlareCount(HMDRenderContext ctx) {
        ScaledDisplay layout = DrawUtils.values(ctx);

        int color = color_flare_idle;
        if (flares <= 0) {
            color = color_flare_empty;
        } else if (flare_pulse_count > 0) {
            color = color_flare_active;
        }
        String label = String.format("FLR %d", flares);
        drawText("flr_count", color, layout.flare_x(), layout.flare_y(), label, true);
    }

    public static void radarTick() {
        if (!radar_ok || !is_linked) {
            radar_contacts = new ArrayList<>();
        } else {
            ArrayList<Vec3> new_contacts = new ArrayList<>();
            double range = radar_range;
            Vec3 from = shipdata.position;

            Ship[] detected_ships = {}; // todo: detect ships

            for (Ship ship : detected_ships) {
                Vec3 to = toVec3(ship.getWorldAABB().center(new Vector3d()));
                if (to.distanceToSqr(from) > 5*5) {
                    new_contacts.add(to);
                }
            }
            radar_contacts = new_contacts;
        }
    }

    public static void pushContactCache(ArrayList<ContactEntry> new_contact_cache, Vec3 position, boolean friendly, boolean is_maws, boolean is_datalink) {
        Vec3 diff = position.subtract(shipdata.position);
        double distSqr = diff.lengthSqr();
        double horizontalDistSqr = diff.x*diff.x + diff.z*diff.z;
        Vec3 body = world_to_body(diff);
        double fwd = Math.sqrt(body.x*body.x + body.z*body.z);

        double azimuth = 0;
        double elevation = 0;

        if (fwd > 0) {
            azimuth = Math.toDegrees(Math.atan2(body.z, body.x));
            elevation = Math.toDegrees(Math.atan2(body.y, fwd));
        }

        ContactEntry entry = new ContactEntry(
                position, body,
                fwd, distSqr, horizontalDistSqr,
                azimuth, elevation,
                friendly, is_maws, is_datalink
        );

        new_contact_cache.add(entry);
    }

    public static void buildContactCache() {
        contact_cache.clear();
        for (Vec3 radar_position : radar_contacts) {
            pushContactCache(contact_cache, radar_position, isFriendlyPos(radar_position), false, false);
        }

        if (maws_ok && flare_own_launch <= 0) {
            for (Vec3 maws_position : maws_missiles) {
                pushContactCache(contact_cache, maws_position, false, true, false);
            }
        }

        for (IFF_DatalinkShared ally : iff_shared_contacts) {
            for (IFF_Friendly contact : ally.contacts) {
                pushContactCache(contact_cache, contact.pos, isFriendlyPos(contact.pos), false, true);
            }
        }
    }

    public static void cardinal(double angle_offset, String label, int bright,
                                double cx, double cy, double half) {
        double rad = Math.toRadians(-shipdata.orientation.y + angle_offset);
        drawText("radar_" + label, bright,
                cx + Math.sin(rad) * (half - 8),
                cy - Math.cos(rad) * (half - 8), label, true);
    }

    public static void drawRadar(HMDRenderContext ctx) {
        if (!radar_ok) return;

        ScaledDisplay layout = DrawUtils.values(ctx);

        double rx = layout.radar_x();
        double ry = layout.radar_y();
        double rs = layout.radar_size();
        int bright = color_radar;
        int dim = color_radar_dim;
        double half = rs / 2;
        double cx = rx + half;
        double cy = ry +  half;
        double max_range = radar_range;
        double heading_rad = Math.toRadians(shipdata.orientation.y);
        double fdrawn = 0;

        drawRect("radar_panel", color_panel_bg, rx, ry, rx + rs, ry + rs);

        drawLine("radar_f_top", bright, 1, rx, ry, rx + rs, ry);
        drawLine("radar_f_bot", bright, 1, rx, ry + rs, rx + rs, ry + rs);
        drawLine("radar_f_lft", bright, 1, rx, ry, rx, ry + rs);
        drawLine("radar_f_rgt", bright, 1, rx + rs, ry, rx + rs, ry + rs);
        drawLine("radar_h", dim, 1, rx, cy, rx + rs, cy);
        drawLine("radar_v", dim, 1, cx, ry, cx, ry + rs);

        for (int r = 1; r < 3; r++) {
            drawCircle("radar_ring" + r, dim, 1, half * r / 3, cx, cy);
        }

        double cone = radar_cone;

        double half_cone = Math.toRadians(cone / 2);
        double s = Math.sin(half_cone), c = Math.cos(half_cone);
        drawLine("radar_cone_l", bright, 1, cx, cy, cx - half * s, cy - half * c);
        drawLine("radar_cone_r", bright, 1, cx, cy, cx + half * s, cy - half * c);

        double tip_y =  cy - 6, base_y = cy + 3;
        drawLine("radar_me_l", bright, 1, cx, tip_y, cx - 4, base_y);
        drawLine("radar_me_r", bright, 1, cx, tip_y, cx + 4, base_y);
        drawLine("radar_me_b", bright, 1, cx - 4, base_y, cx + 4, base_y);

        cardinal(0, "N", bright, cx, cy, half);
        cardinal(90, "E", bright, cx, cy, half);
        cardinal(180, "S", bright, cx, cy, half);
        cardinal(270, "W", bright, cx, cy, half);

        if (iff_enabled) {
            String status = "IFF:" + iff_key + "  FOV:" + cone + "deg";
            drawText("radar_iff_stat", bright, rx, ry - 12, status, false);
        }
        drawText("radar_range", bright, rx + 2, ry + 2, String.format("%.0fm", max_range), false);

        int i = 0;
        for (ContactEntry entry : contact_cache) {
            i++;
            if (!entry.is_maws && !entry.friendly && isInRadarCone(entry.worldPosition)) {
                if (entry.horizontalDistSqr <= max_range*max_range && entry.horizontalDistSqr > 0) {
                    double horizDist = Math.sqrt(entry.horizontalDistSqr);
                    Vec3 diff = entry.worldPosition.subtract(shipdata.position);
                    double wb = Math.atan2(diff.x, -diff.z);
                    double ra = wb - heading_rad;
                    double sc = half / max_range;
                    double tx = cx + Math.sin(ra) * horizDist * sc;
                    double bly = cy - Math.cos(ra) * horizDist * sc;
                    if (tx > rx + 1 && tx < rx + rs - 1 && bly > ry + 1 && bly < ry + rs - 1) {
                        drawRect("radar_tgt_"+i, bright, tx - 1, bly - 1, tx + 1, bly + 1);
                    }
                }
            }
        }

        for (ContactEntry entry : contact_cache) {
            if (!entry.is_maws && entry.friendly) {
                if (entry.horizontalDistSqr <= max_range*max_range && entry.horizontalDistSqr > 0) {
                    double horizDist = Math.sqrt(entry.horizontalDistSqr);
                    Vec3 diff = entry.worldPosition.subtract(shipdata.position);
                    double wb = Math.atan2(diff.x, -diff.z);
                    double ra = wb - heading_rad;
                    double sc = half / max_range;
                    double tx = cx + Math.sin(ra) * horizDist * sc;
                    double bly = cy - Math.cos(ra) * horizDist * sc;

                    if (tx > rx + 1 && tx < rx + rs - 1 && bly > ry + 1 && bly < ry + rs - 1) {
                        drawRect("radar_fr_" + fdrawn, color_radar_friendly, tx - 2, bly - 2, tx + 2, bly + 2);
                    }
                }
            }
        }

        for (IFF_Friendly friendly : iff_friendlies) {
            boolean already_seen = false;
            for (Vec3 contact : radar_contacts) {
                double dx = contact.x - friendly.pos.x;
                double dz = contact.z - friendly.pos.z;
                if (dx*dx + dz*dz < iff_proximity*iff_proximity) {
                    already_seen = true;
                    break;
                }
            }

            if (!already_seen) {
                double dx = friendly.pos.x - shipdata.position.x;
                double dz = friendly.pos.z - shipdata.position.z;
                double horizDistSqr = dx*dx + dz*dz;
                if (horizDistSqr <= max_range && horizDistSqr > 0) {
                    double horizDist = Math.sqrt(horizDistSqr);
                    double wb = Math.atan2(dx, -dz);
                    double ra = wb - heading_rad;
                    double sc = half / max_range;
                    double tx = cx + Math.sin(ra) * horizDist * sc;
                    double bly = cy - Math.cos(ra) * horizDist * sc;
                    if (tx > rx + 1 && tx < rx + rs - 1 && bly > ry + 1 && bly < ry + rs - 1){
                        fdrawn = fdrawn + 1;
                        drawLine("radar_iff_l"+fdrawn, color_radar_friendly_dim, 1, tx - 2, bly, tx, bly + 2);
                        drawLine("radar_iff_r"+fdrawn, color_radar_friendly_dim, 1, tx, bly + 2, tx + 2, bly);
                        drawLine("radar_iff_l2"+fdrawn, color_radar_friendly_dim, 1, tx - 2, bly, tx, bly - 2);
                        drawLine("radar_iff_r2"+fdrawn, color_radar_friendly_dim, 1, tx, bly - 2, tx + 2, bly);
                    }
                }
            }

            int sdrawn = 0;
            for (ContactEntry entry : contact_cache) {
                if (entry.is_datalink) {
                    if (entry.horizontalDistSqr <= max_range*max_range && entry.horizontalDistSqr > 0) {
                        double horizDist = Math.sqrt(entry.horizontalDistSqr);
                        Vec3 diff = entry.worldPosition.subtract(shipdata.position);
                        double wb = Math.atan2(diff.x, -diff.z);
                        double ra = wb - heading_rad;
                        double scf = half / max_range;
                        double tx = cx + Math.sin(ra) * horizDist * scf;
                        double bly = cy - Math.cos(ra) * horizDist * scf;

                        if (tx > rx + 2 && tx < rx + rs - 2 && bly > ry + 2 && bly < ry + rs - 2) {
                            sdrawn = sdrawn + 1;
                            int color = entry.friendly ? color_datalink_friend : color_datalink;
                            drawRect("radar_dl_" + sdrawn, color, tx - 1, bly - 1, tx + 1, bly + 1);
                        }
                    }
                }
            }
        }
    }

    public static void drawCScope(HMDRenderContext ctx) {
        if (!radar_ok) return;

        ScaledDisplay layout = DrawUtils.values(ctx);
        double cw = layout.cscope_w(),ch = layout.cscope_h();
        double bx = layout.scope_x(),by = layout.scope_y();

        double half_w = cw / 2;
        double bcx = bx + half_w;
        double az_range = cscope_azimuth_range;
        double el_up = cscope_elevation_up, el_down = cscope_elevation_down;
        double el_total = el_up + el_down;
        double horizon_y = by + ch * (el_up / el_total);

        drawRect("cscope_bg", color_scope_bg, bx - 2, by - 2, bx + cw + 2, by + ch + 2);

        drawLine("cs_top", color_bscope, 1, bx, by, bx + cw, by);
        drawLine("cs_bot", color_bscope, 1, bx, by + ch, bx + cw, by + ch);
        drawLine("cs_lft", color_bscope, 1, bx, by, bx, by + ch);
        drawLine("cs_rgt", color_bscope, 1, bx + cw, by, bx + cw, by + ch);

        double az_step = az_range / 3;
        for (int k = 1; k < 3; k++) {
            double az1 = k * az_step;
            double tx1 = bcx + (az1 / az_range) * half_w;
            drawLine("cs_gaz_" + az1, color_cscope_dim_grid, 1, tx1, by + 1, tx1, by + ch - 1);

            double az2 = -k * az_step;
            double tx2 = bcx + (az2 / az_range) * half_w;
            drawLine("cs_gaz_" + az2, color_cscope_dim_grid, 1, tx2, by + 1, tx2, by + ch - 1);
        }

        for (double elevation : new double[]{el_up, el_up / 2, -el_down / 2, -el_down}) {
            double py = by + ch * (el_up - elevation) / el_total;
            drawLine("cs_gel_" + elevation, color_cscope_dim_grid, 1, bx + 1, py, bx + cw - 1, py);
        }

        drawLine("cs_axis_h", color_cscope_axis_grid, 1, bx + 1, horizon_y, bx + cw - 1, horizon_y);
        drawLine("cs_axis_v", color_cscope_axis_grid, 1, bcx, by + 1, bcx, by + ch - 1);

        drawText("cs_mode", color_bscope, bscope_x, bscope_y - 10, "C-SCP", false);
        drawText("cs_range", color_bscope, bscope_x + layout.cscope_w() - 40, bscope_y - 10, String.format("%.0fm", (float) radar_range), false);
        drawText("cs_fov", color_bscope, bscope_x + layout.cscope_w() / 2, bscope_y - 10, String.format("%dx%d", (int) az_range*2, (int) el_total), true);
        drawText("cs_az_neg", color_bscope, bscope_x, bscope_y + layout.cscope_h() + 4, String.format("%+d", (int) -az_range), false);
        drawText("cs_az_pos", color_bscope, bscope_x + layout.cscope_w() - 25, bscope_y + layout.cscope_h() + 4, String.format("%+d", (int) az_range), false);
        drawText("cs_az_0", color_bscope, bscope_x + layout.cscope_w() / 2, bscope_y + layout.cscope_h() + 4, "0", true);
        drawText("cs_el_up", color_bscope, bscope_x - 30, bscope_y, String.format("%+d", (int) el_up), false);
        drawText("cs_el_dn", color_bscope, bscope_x - 30, bscope_y + layout.cscope_h() - 6, String.format("%+d", (int) -el_down), false);
        drawText("cs_el_0", color_bscope, bscope_x - 14, bscope_y + layout.cscope_h() * (el_up / el_total) - 4, "0", false);

        int i = 1;
        for (ContactEntry entry : contact_cache) {
            i++;
            if (!entry.is_maws && !entry.friendly && isInRadarCone(entry.worldPosition)) {
                if (entry.fwd > 0 && Math.abs(entry.azimuth) <= az_range && entry.elevation >= -el_down && entry.elevation <= el_up) {
                    double px = bcx + (entry.azimuth / az_range) * half_w;
                    double py = by + ch * (el_up - entry.elevation) / el_total;
                    if (px > bx + 2 && px < bx + cw - 2 && py > by + 2 && py < by + ch - 2) {
                        double sz = 3;
                        drawLine("cs_cv_"+i, color_bscope, 1, px, py - sz, px, py + sz);
                        drawLine("cs_ch_"+i, color_bscope, 1, px - sz, py, px + sz, py);
                    }
                }
            }
        }

        int fdrawn = 0;
        for (ContactEntry entry : contact_cache) {
            if (!entry.is_maws && entry.friendly) {
                if (entry.fwd > 0 && Math.abs(entry.azimuth) <= az_range && entry.elevation >= -el_down && entry.elevation <= el_up) {
                    double px = bcx + (entry.azimuth / az_range) * half_w;
                    double py = by + ch * (el_up - entry.elevation) / el_total;
                    if (px > bx + 2 && px < bx + cw - 2 && py > by + 2 && py < by + ch - 2) {
                        fdrawn = fdrawn + 1;
                        double sz = 4;
                        drawLine("cs_fcv_" + fdrawn, color_radar_friendly, 1, px, py - sz, px, py + sz);
                        drawLine("cs_fch_" + fdrawn, color_radar_friendly, 1, px - sz, py, px + sz, py);
                    }
                }
            }
        }

        int sdrawn = 0;
        for (ContactEntry entry : contact_cache) {
            if (entry.is_datalink) {
                if (entry.fwd > 0 && Math.abs(entry.azimuth) <= az_range && entry.elevation >= -el_down && entry.elevation <= el_up) {
                    double px = bcx + (entry.azimuth / az_range) * half_w;
                    double py = by + ch * (el_up - entry.elevation) / el_total;
                    if (px > bx + 2 && px < bx + cw - 2 && py > by + 2 && py < by + ch - 2) {
                        sdrawn = sdrawn + 1;
                        int color = entry.friendly ? color_datalink_friend : color_datalink;
                        drawLine("cs_dlcv_" + sdrawn, color, 1, px, py - 2, px, py + 2);
                        drawLine("cs_dlch_" + sdrawn, color, 1, px - 2, py, px + 2, py);
                    }
                }
            }
        }
    }


    public static void drawBScope(HMDRenderContext ctx) {
        if (!radar_ok) return;

        ScaledDisplay layout = DrawUtils.values(ctx);

        double bs = layout.bscope_size();
        double bx = layout.scope_x(), by = layout.scope_y();
        double half = bs / 2;
        double bcx = bx + half;
        double az_range = bscope_azimuth_range;
        double max_range = radar_range;

        drawRect("bscope_bg", color_scope_bg, bx - 2, by - 2, bx + bs + 2, by + bs + 2);

        drawLine("bs_top", color_bscope, 1, bx, by, bx + bs, by);
        drawLine("bs_bot", color_bscope, 1, bx, by + bs, bx + bs, by + bs);
        drawLine("bs_lft", color_bscope, 1, bx, by, bx, by + bs);
        drawLine("bs_rgt", color_bscope, 1, bx + bs, by, bx + bs, by + bs);

        double az_step = az_range / 3;

        for (int k = 0; k < 3; k++) {
            double az = k * az_step;
            double tx = bcx + (az / az_range) * half;
            drawLine("bs_gaz_" + az, color_bscope_dim_grid, 1, tx, by + 1, tx, by + bs - 1);
            if (k > 0) {
                tx = bcx - (az / az_range) * half;
                drawLine("bs_gaz_-" + az, color_bscope_dim_grid, 1, tx, by + 1, tx, by + bs - 1);
            }
        }

        for (double frac : new double[]{0.25, 0.5, 0.75}) {
            double ry = by + bs * (1 - frac);
            drawLine("bs_rng_" + frac, color_bscope_dim_grid, 1, bx + 1, ry, bx + bs - 1, ry);
            drawText("bs_rlbl_" + frac, color_bscope,
                    bscope_x - 35, bscope_y + bscope_size * (1 - frac),
                    String.format("%.0f", max_range * frac), false);
        }

        drawText("bs_mode", color_bscope, bscope_x + 2, bscope_y - 10, "B-SCP", false);
        drawText("bs_range", color_bscope, bscope_x + bscope_size - 40, bscope_y - 10, String.format("%.0fm", max_range), false);
        drawText("bs_az_neg", color_bscope, bscope_x, bscope_y + bscope_size + 4, String.format("%+d", (int) -az_range), false);
        drawText("bs_az_pos", color_bscope, bscope_x + bscope_size - 25, bscope_y + bscope_size + 4, String.format("%+d", (int) az_range), false);
        drawText("bs_az_0", color_bscope, bscope_x + (double) bscope_size / 2, bscope_y + bscope_size + 4, "0", true);

        int i = 0;
        for (ContactEntry entry : contact_cache) {
            i++;
            if (!entry.is_maws && !entry.friendly && isInRadarCone(entry.worldPosition)) {
                if (entry.fwd > 0 && Math.abs(entry.azimuth) <= az_range && entry.fwd <= max_range) {
                    double px = bcx + (entry.azimuth / az_range) * half;
                    double py = by + bs * (1 - entry.fwd / max_range);
                    if (px > bx + 2 && px < bx + bs - 2 && py > by + 2 && py < by + bs - 2) {
                        double sz = 3;
                        drawLine("bs_cv_"+i, color_bscope, 1, px, py - sz, px, py + sz);
                        drawLine("bs_ch_"+i, color_bscope, 1, px - sz, py, px + sz, py);
                    }
                }
            }
        }

        int fdrawn = 0;
        for (ContactEntry entry : contact_cache) {
            if (!entry.is_maws && entry.friendly) {
                if (entry.fwd > 0 && Math.abs(entry.azimuth) <= az_range && entry.fwd <= max_range) {
                    double px = bcx + (entry.azimuth / az_range) * half;
                    double py = by + bs * (1 - entry.fwd / max_range);
                    if (px > bx + 2 && px < bx + bs - 2 && py > by + 2 && py < by + bs - 2) {
                        fdrawn = fdrawn + 1;
                        double sz = 4;
                        drawLine("bs_fcv_" + fdrawn, color_radar_friendly, 1, px, py - sz, px, py + sz);
                        drawLine("bs_fch_" + fdrawn, color_radar_friendly, 1, px - sz, py, px + sz, py);
                    }
                }
            }
        }

        int sdrawn = 0;
        for (ContactEntry entry : contact_cache) {
            if (entry.is_datalink) {
                if (entry.fwd > 0 && Math.abs(entry.azimuth) <= az_range && entry.fwd <= max_range) {
                    double px = bcx + (entry.azimuth / az_range) * half;
                    double py = by + bs * (1 - entry.fwd / max_range);
                    if (px > bx + 2 && px < bx + bs - 2 && py > by + 2 && py < by + bs - 2) {
                        sdrawn = sdrawn + 1;
                        int color = entry.friendly ? color_datalink_friend : color_datalink;
                        drawLine("bs_dlcv_" + sdrawn, color, 1, px, py - 2, px, py + 2);
                        drawLine("bs_dlch_" + sdrawn, color, 1, px - 2, py, px + 2, py);
                    }
                }
            }
        }
    }

    public static void doWeaponScan() {
        //if not wm then weapon_cache = {}; weapon_selected = 1; return end
        //if not ok or type(raw) ~= "table" then weapon_cache = {}; weapon_selected = 1; return end

        Object[] raw = {}; // todo: get weapons
        HashMap<String, WeaponGroup> groups = new HashMap<>();

        for (Object obj : raw) {
            String id = "id".isEmpty() ? "id" :  "?";
            WeaponGroup group = groups.get(id);
            if (group == null) {
                group = new WeaponGroup(id, new ArrayList<>());
            }
            group.ordnances.add(obj);
        }

        weapon_cache = new ArrayList<>(groups.values());
        weapon_selected = Math.min(weapon_selected, Math.max(1, weapon_cache.toArray().length));
    }

    public static void drawWeapons(HMDRenderContext ctx, double dt) {
        weapon_scan_timer = weapon_scan_timer + dt;

        if (weapon_scan_timer >= 1.0) {
            weapon_scan_timer = 0;
            doWeaponScan();
        }

        ScaledDisplay layout = DrawUtils.values(ctx);
        double wx = layout.weapons_x(), wy = layout.weapons_y();
        double spacing = layout.weapons_spacing();
        double y = wy;

        if (weapon_cache.isEmpty()) {
            drawText("wpn_hdr", color_weaponsmanager, wx, y, "WPN", true);
            drawText("wpn_none", color_weaponsmanager, wx, y + spacing, "---", true);
            return;
        }

        drawText("wpn_hdr", color_weaponsmanager, wx, y, "WPN", true);
        y = y + spacing;

        int i = 0;
        for (WeaponGroup group : weapon_cache) {
            i++;
            String prefix = (i == weapon_selected) ? ">" : " ";
            int color = (i == weapon_selected) ? color_weaponsmanager_selected : color_weaponsmanager;
            drawText("wpn_" + i, color, wx, y, String.format("%s %s x%d", prefix,
                    group.id.toUpperCase(), group.ordnances.toArray().length), true);
            y = y + spacing;
        }
    }

    public static void drawAoA(HMDRenderContext ctx, double aoa) {
        ScaledDisplay layout = DrawUtils.values(ctx);
        double left = layout.aoa_left();
        double ppd = layout.aoa_px_per_deg();
        double range_lo = -10, range_hi = 10;
        double top = layout.cy() - range_hi * ppd;
        double bottom = layout.cy() + range_hi * ppd;

        drawLine("aoa_scale", color_aoa, 1, left, top, left, bottom);

        for (int deg = (int) range_lo; deg < range_hi; deg = deg + 5) {
            double y = layout.cy() - Math.round(deg * ppd);
            double major_h = deg % 10 == 0 ? layout.aoa_tick_major_h() : layout.aoa_tick_minor_h();
            drawLine("aoa_tick_" + deg, color_aoa, 1, left, y, left + major_h, y);
            if (deg % 10 == 0 && deg != 0) {
                drawText("aoa_lbl_" + deg, color_aoa, layout.aoa_lbl_x(), y, String.format("%+d", deg), false);
            }
        }

        double aoa_y = Math.max(top, Math.min(bottom, layout.cy() - aoa * ppd));
        drawRect("aoa_ptr", color_aoa, left - 3, aoa_y - 2, left + 10, aoa_y + 2);
        drawText("aoa_val", color_aoa, layout.aoa_lbl_x(), bottom + layout.aoa_val_y_offset(),
                "AOA " + formatNumber(aoa, 1) + " deg", false);
    }

    public static void  drawHUD(HMDRenderContext ctx, double dt) {
        if (KeyBinds.HMD_TOGGLE_SCOPE.consumeClick()) {
            scope_mode = (scope_mode + 1) % 3;
        }

        if (KeyBinds.HMD_CYCLE_WEAPONS.consumeClick() && !weapon_cache.isEmpty()) {
            weapon_selected = weapon_selected + 1;
            if (weapon_selected > weapon_cache.toArray().length){
                weapon_selected = 1;
            }
        }

        if (KeyBinds.HMD_FIRE_SELECTED_WEAPON.consumeClick() && !weapon_cache.isEmpty()) {
            WeaponGroup group = weapon_cache.get(weapon_selected);
            if (group != null && !group.ordnances.isEmpty()) {
                ArrayList<Object> ordnances = group.ordnances;
                Object ord = ordnances.get(0);
                // todo: fire
                ordnances.remove(0);
                weapon_selected = Math.min(weapon_selected, Math.max(1, weapon_cache.toArray().length));
            }
        }

        flareTick(dt);

        updateFightData(ctx, dt);

        doMAWsScan(dt);

        buildContactCache();

        double speed = shipdata.velocity.length();

        ScaledDisplay layout = DrawUtils.values(ctx);

        drawText("speed_label", color_readout, layout.speed_label_x(), layout.speed_label_y(), "SPEED", false);
        drawText("speed_value", color_readout, layout.speed_value_x(), layout.speed_value_y(), formatNumber(speed, 0), false);
        drawText("alt_label", color_readout, layout.alt_label_x(), layout.alt_label_y(), "ALT", false);
        drawText("alt_value", color_readout, layout.alt_value_x(), layout.alt_value_y(), formatNumber(shipdata.position.y, 0), false);

        drawRect("centre_dot", color_boresight, layout.dot_x1(), layout.dot_y1(), layout.dot_x2(), layout.dot_y2());

        double aoa = computeAoA();
        drawAzimuthTape(ctx);
        drawGForce(ctx);
        drawAoA(ctx, aoa);
        drawRadar(ctx);

        if (scope_mode == 0) {
            drawCScope(ctx);
        } else if (scope_mode == 1) {
            drawBScope(ctx);
        }

        drawBoresight();
        drawFPV(speed);
        drawWeapons(ctx, dt);
        drawMAWs(ctx);
        drawIFFFriendlyList(ctx);
        drawFlareCount(ctx);
    }

    private static HMDRenderContext cached_context;

    public static void clearContext() {
        cached_context = null;
    }

    public static HMDRenderContext getOrCreateContext() {
        if (cached_context == null) {
            cached_context = new HMDRenderContext(Minecraft.getInstance(), Minecraft.getInstance().getWindow());
        }
        return cached_context;
    }

    public static void onRender(GuiGraphics gfx) {
        HMDRenderContext context = getOrCreateContext();

        if (context.player() == null) return;
        if (context.level() == null) return;
        if (context.ship() == null) return;

        DrawUtils.setGuiGraphics(gfx);

        float partialTick = Minecraft.getInstance().getDeltaFrameTime();
        double dt = Math.min(partialTick, 0.05);

        drawHUD(context, dt);
    }
}
