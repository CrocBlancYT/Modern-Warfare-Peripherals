package net.croc.mw_peripherals.integration.tallyho;

import java.util.List;
import net.minecraft.network.chat.Component;

public interface IPodFactory<T extends PodComponent> {
    T create();

    void appendHoverText(List<Component> paramList);
}
