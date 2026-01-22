package com.fancyinnovations.fancycore.uis.teleportation;

import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.api.player.Home;
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

public class HomesPage extends InteractiveCustomUIPage<LocationUIData> {

    public HomesPage(@NotNull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, LocationUIData.CODEC);
    }

    @Override
    public void build(@NotNull Ref<EntityStore> ref, @NotNull UICommandBuilder command, @NotNull UIEventBuilder event, @NotNull Store<EntityStore> store) {
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(uuidComponent.getUuid());
        if (fp == null) {
            return;
        }

        command.append("Pages/HomesPage.ui");

        command.clear("#LocationCards");
        command.appendInline("#Main #LocationList", "Group #LocationCards { LayoutMode: Left; }");

        int i = 0;
        for (Home home : fp.getData().getHomes()) {
            command.append("#LocationCards", "Pages/LocationEntry.ui");

            command.set("#LocationCards[" + i + "] #LocationName.Text", home.name());
            command.set("#LocationCards[" + i + "] #LocationWorld.Text", home.location().worldName());
            command.set("#LocationCards[" + i + "] #LocationX.Text", (int) home.location().x() +"");
            command.set("#LocationCards[" + i + "] #LocationY.Text", (int) home.location().y() +"");
            command.set("#LocationCards[" + i + "] #LocationZ.Text", (int) home.location().z() +"");

            event.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#LocationCards[" + i + "] #GoButton",
                    EventData.of("LocationName", home.name()),
                    false
            );

            i++;
        }
    }

    @Override
    public void handleDataEvent(@NotNull Ref<EntityStore> ref, @NotNull Store<EntityStore> store, @NotNull LocationUIData data) {
        super.handleDataEvent(ref, store, data);

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

        Home home = fp.getData().getHome(data.getLocationName());
        if (home == null) {
            return;
        }

        World targetWorld = Universe.get().getWorld(home.location().worldName());
        if (targetWorld == null) {
            return;
        }

        Teleport teleport = new Teleport(targetWorld, home.location().positionVec(), home.location().rotationVec());
        store.addComponent(ref, Teleport.getComponentType(), teleport);


        player.getPageManager().setPage(ref, store, Page.None);

        fp.sendMessage("Teleported to home: " + home.name());
    }
}
