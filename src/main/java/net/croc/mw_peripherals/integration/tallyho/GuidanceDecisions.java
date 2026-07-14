package net.croc.mw_peripherals.integration.tallyho;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.guid.SACLOS;
import net.croc.mw_peripherals.integration.tallyho.guid.*;
import net.croc.mw_peripherals.mixin.tallyho.SACLOSAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static edn.stratodonut.tallyho.missile.GuidanceComponent.tryLock;

public class GuidanceDecisions {
    public record Context<T>(MountedMissileEntity missile, MultiModeSeeker multiMode, T guid) {}

    public static class Handler<T> {
        private static final Map<Class<?>, Handler<?>> handlers = new HashMap<>();

        private Predicate<Context<T>> predicate;

        public Handler(Class<T> clazz, Predicate<Context<T>> predicate) {
            this.predicate = predicate;
            handlers.put(clazz, this);
        }

        public boolean isActive(MountedMissileEntity missile, MultiModeSeeker multiMode, T guid) {
            if (this.predicate == null) return false;
            return this.predicate.test(new Context<>(missile, multiMode, guid));
        }

        private Handler(T guid) {
            if (guid != null) {
                Handler<T> handler = (Handler<T>) handlers.get(guid.getClass());

                if (handler != null) {
                    this.predicate = handler.predicate;
                }
            }
        }

        public static Handler<GuidanceComponent> getForGuidance(GuidanceComponent guid) {
            return new Handler<>(guid);
        }
    }

    public static boolean isSeekerActive(MountedMissileEntity e, int fov, int range) {
        return tryLock(e.level(), e.getLookAngle(), e.position(), null, fov, range).isPresent();
    }

    public static boolean isActiveSACLOS(Context<SACLOS> ctx) {
        SACLOSAccessor saclos = (SACLOSAccessor) ctx.guid;
        return saclos.steerOrigin() != null && saclos.steerDir() != null;
    }

    static {
        new Handler<>(ARHSeeker.class, (ctx)
                -> isSeekerActive(ctx.missile, ctx.guid.properties.seeker_fov(), ctx.guid.properties.seeker_range()));

        new Handler<>(SARHSeeker.class, (ctx)
                -> isSeekerActive(ctx.missile, ctx.guid.properties.seeker_fov(), ctx.guid.properties.seeker_range()));

        new Handler<>(ARMGuidance.class, (ctx)
                -> isSeekerActive(ctx.missile, ctx.guid.properties.seeker_fov(), ctx.guid.properties.seeker_range()));

        new Handler<>(IIRSeeker.class, (ctx)
                -> isSeekerActive(ctx.missile, ctx.guid.properties.seeker_fov(), ctx.guid.properties.seeker_range()));

        new Handler<>(IRSeeker.class, (ctx)
                -> isSeekerActive(ctx.missile, ctx.guid.properties.gimbal_fov(), ctx.guid.properties.seeker_range()));

        new Handler<>(IRSeekerRear.class, (ctx)
                -> isSeekerActive(ctx.missile, ctx.guid.properties.gimbal_fov(), ctx.guid.properties.seeker_range()));

        new Handler<>(IRSeekerGround.class, (ctx)
                -> isSeekerActive(ctx.missile, ctx.guid.properties.gimbal_fov(), ctx.guid.properties.seeker_range()));

        new Handler<>(IRSeekerIRCCM.class, (ctx)
                -> isSeekerActive(ctx.missile, ctx.guid.properties.gimbal_fov(), ctx.guid.properties.seeker_range()));

        new Handler<>(TVGuidance.class, (ctx)
                -> ctx.guid.getSteerPlayer() != null);

        new Handler<>(DataLink.class, (ctx)
                -> ctx.guid.isActive(ctx.missile.level().getGameTime()));

        new Handler<>(IOG.class, (ctx) -> true);

        new Handler<>(SACLOS.class, GuidanceDecisions::isActiveSACLOS);
    }

    public static boolean isActive(MountedMissileEntity missile, MultiModeSeeker multiMode, GuidanceComponent guid) {
        return Handler.getForGuidance(guid).isActive(missile, multiMode, guid);
    }
}