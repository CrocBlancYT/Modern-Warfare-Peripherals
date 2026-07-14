package net.croc.mw_peripherals.integration.tallyho.guid;

import com.sun.jna.platform.win32.Guid;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import kotlin.Pair;
import net.croc.mw_peripherals.integration.tallyho.GuidanceDecisions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultiModeSeeker extends GuidanceComponent {

    public static final int REFRESH_DELAY = 5;

    public final SeekerProperties properties;
    private final ArrayList<GuidanceComponent> guidances;

    private GuidanceComponent selectedGuidance;

    public GuidanceComponent[] getGuidances() {
        return this.guidances.toArray(new GuidanceComponent[0]);
    }

    public void activate(GuidanceComponent selectedGuidance) {
        this.selectedGuidance = selectedGuidance;
    }

    private MultiModeSeeker(SeekerProperties properties, ArrayList<IGuidanceFactory<?>> guidance_factories) {
        this.properties = properties;
        this.guidances = new ArrayList<>();

        for (IGuidanceFactory<?> guid : guidance_factories) {
            this.guidances.add(guid.create());
        }

        this.selectedGuidance = this.guidances.get(0);
    }

    public void trySwitchGuidance(MountedMissileEntity missile) {
        IOG iog = null;
        GuidanceComponent activeGuid = null;

        for (GuidanceComponent guidance : guidances) {
            if (iog == null && guidance instanceof IOG foundIog) {
                iog = foundIog;
            } else if (activeGuid == null && GuidanceDecisions.isActive(missile, this, guidance)) {
                activeGuid = guidance;
            }
        }

        if (iog != null) {
            iog.saveTargetInertia(missile, this);
        }

        if (activeGuid == null && !guidances.isEmpty()) {
            activeGuid = guidances.get(guidances.size()-1);
        }

        if (activeGuid != null) {
            this.activate(activeGuid);
        }
    }

    @Override
    public void tick(MountedMissileEntity missile) {
        if (missile.getTicksSinceLaunch() % REFRESH_DELAY == 0) {
            trySwitchGuidance(missile);
        }

        selectedGuidance.tick(missile);
    }

    @Override
    public boolean launch(MountedMissileEntity missile) {
        for (GuidanceComponent guidance : this.guidances) {
            if (!guidance.launch(missile)) {
                return false;
            }
        }
        activateSeeker();
        return true;
    }

    public static final class SeekerProperties {
        public SeekerProperties() { }
        public void appendHoverText(List<Component> components) { }
    }

    public static class Factory implements IGuidanceFactory<MultiModeSeeker> {
        private final MultiModeSeeker.SeekerProperties props;

        private final ArrayList<IGuidanceFactory<?>> guidance_factories;

        public Factory() {
            this.guidance_factories = new ArrayList<>();
            this.props = new MultiModeSeeker.SeekerProperties();
        }

        public MultiModeSeeker create() {
            return new MultiModeSeeker(this.props, this.guidance_factories);
        }

        public Factory addGuidance(IGuidanceFactory<?> guid) {
            this.guidance_factories.add(guid);
            return this;
        }

        private static final String[] Names = {"Solo", "Dual", "Tri", "Quad"};

        private String getName(int number) {
            if (number <= 4) return Names[number - 1];
            return ""+number;
        }

        public void appendHoverText(List<Component> all_components) {
            this.props.appendHoverText(all_components);


            all_components.add(Component.literal("Guidance: "+getName(this.guidance_factories.size())+"-Mode").setStyle(Style.EMPTY
                    .withColor(ChatFormatting.AQUA)));

            int index = 0;
            for (IGuidanceFactory<?> guid : this.guidance_factories) {
                List<Component> components = new ArrayList<>();
                guid.appendHoverText(components);

                Component comp1 = components.get(0);
                if (comp1 != null) {
                    comp1 = Component.literal((index+1) + ". "+comp1.getString().substring(10))
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
                    components.set(0, comp1);
                }

                all_components.addAll(components);
                index++;
            }
        }
    }
}