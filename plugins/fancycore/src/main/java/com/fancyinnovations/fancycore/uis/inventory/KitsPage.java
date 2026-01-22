package com.fancyinnovations.fancycore.uis.inventory;

import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.utils.TimeUtils;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class KitsPage extends InteractiveCustomUIPage<KitUIData> {

    public KitsPage(@NotNull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, KitUIData.CODEC);
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

        command.append("Pages/KitsPage.ui");

        command.clear("#KitCards");
        command.appendInline("#Main #KitsList", "Group #KitCards { LayoutMode: Left; }");

        int i = 0;
        for (Kit kit : KitsService.get().getAllKits()) {
            if (!PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.kits." + kit.name())) {
                continue;
            }

            String cooldown = "";
            if (kit.cooldown() <= 0) {
                cooldown = "No cooldown";
            } else if (PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.kits.bypasscooldown")){
                cooldown = "Ready to use (bypass cooldown)";
            } else {
                long lastTimeUsedKit = fp.getData().getLastTimeUsedKit(kit.name());
                long timeSinceLastUse = System.currentTimeMillis() - lastTimeUsedKit;
                if (timeSinceLastUse < kit.cooldown()) {
                    long timeLeft = kit.cooldown() - timeSinceLastUse;
                    String remainingTime = TimeUtils.formatTime(timeLeft);
                    cooldown = "Cooldown:" + remainingTime;
                } else {
                    cooldown = "Ready to use";
                }
            }

            command.append("#KitCards", "Pages/KitEntry.ui");

            command.set("#KitCards[" + i + "] #KitName.Text", kit.displayName());
            command.set("#KitCards[" + i + "] #Cooldown.Text", cooldown);

            if (cooldown.startsWith("Cooldown:")) {
                command.remove("#KitCards[" + i + "] #GetButton");
            } else {
                event.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        "#KitCards[" + i + "] #GetButton",
                        EventData.of("KitName", kit.name()),
                        false
                );
            }


            i++;
        }
    }

    @Override
    public void handleDataEvent(@NotNull Ref<EntityStore> ref, @NotNull Store<EntityStore> store, @NotNull KitUIData data) {
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

        Kit kit = KitsService.get().getKit(data.getKitName());
        if (!PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.kits." + kit.name())) {
            fp.sendMessage("You do not have permission to use this kit.");
            return;
        }

        if (kit.cooldown() > 0 && !PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.kits.bypasscooldown")) {
            long lastTimeUsedKit = fp.getData().getLastTimeUsedKit(kit.name());
            long timeSinceLastUse = System.currentTimeMillis() - lastTimeUsedKit;
            if (timeSinceLastUse < kit.cooldown()) {
                long timeLeft = kit.cooldown() - timeSinceLastUse;
                fp.sendMessage("You must wait " + TimeUtils.formatTime(timeLeft) + " before using this kit again.");
                return;
            }

            fp.getData().setLastTimeUsedKit(kit.name(), System.currentTimeMillis());
        }

        KitsService.get().giveKitToPlayer(kit, fp);

        player.getPageManager().setPage(ref, store, Page.None);
        fp.sendMessage("You have received the kit: " + kit.name());
    }
}
