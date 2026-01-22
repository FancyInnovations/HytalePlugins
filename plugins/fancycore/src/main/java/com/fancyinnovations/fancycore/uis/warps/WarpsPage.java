package com.fancyinnovations.fancycore.uis.warps;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.teleport.Warp;
import com.fancyinnovations.fancycore.api.teleport.WarpService;
import com.fancyinnovations.fancycore.uis.common.LocationUIData;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class WarpsPage extends InteractiveCustomUIPage<LocationUIData> {

    public WarpsPage(@NotNull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, LocationUIData.CODEC);
    }

    @Override
    public void build(@NotNull Ref<EntityStore> ref, @NotNull UICommandBuilder command, @NotNull UIEventBuilder event, @NotNull Store<EntityStore> store) {
        command.append("Pages/WarpsPage.ui");

        command.clear("#LocationCards");
        command.appendInline("#Main #LocationList", "Group #LocationCards { LayoutMode: Left; }");

        int i = 0;
        for (Warp warp : WarpService.get().getAllWarps()) {
            command.append("#LocationCards", "Pages/LocationEntry.ui");

            command.set("#LocationCards[" + i + "] #LocationName.Text", warp.name());
            command.set("#LocationCards[" + i + "] #LocationWorld.Text", warp.location().worldName());
            command.set("#LocationCards[" + i + "] #LocationX.Text", (int) warp.location().x() +"");
            command.set("#LocationCards[" + i + "] #LocationY.Text", (int) warp.location().y() +"");
            command.set("#LocationCards[" + i + "] #LocationZ.Text", (int) warp.location().z() +"");

            event.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#LocationCards[" + i + "] #GoButton",
                    EventData.of("LocationName", warp.name()),
                    false
            );

            i++;
        }
    }

    @Override
    public void handleDataEvent(@NotNull Ref<EntityStore> ref, @NotNull Store<EntityStore> store, @NotNull LocationUIData data) {
        super.handleDataEvent(ref, store, data);

        Warp warp = WarpService.get().getWarp(data.getLocationName());
        if (warp == null) {
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(uuidComponent.getUuid());
        if (fp == null) {
            return;
        }

        World targetWorld = Universe.get().getWorld(warp.location().worldName());
        if (targetWorld == null) {
            return;
        }

        Teleport teleport = new Teleport(targetWorld, warp.location().positionVec(), warp.location().rotationVec());
        store.addComponent(ref, Teleport.getComponentType(), teleport);


        player.getPageManager().setPage(ref, store, Page.None);

        fp.sendMessage("Teleported to warp: " + warp.name());
    }
}
