package com.fancyinnovations.fancycore.uis.player;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.permissions.Group;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.uis.EmptyUIData;
import com.fancyinnovations.hytaleutils.NumberUtils;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerListPage extends InteractiveCustomUIPage<EmptyUIData> {

    public PlayerListPage(@NotNull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, EmptyUIData.CODEC);
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

        command.append("Pages/PlayerListPage.ui");

        command.clear("#PlayerListCards");
        command.appendInline("#Main #PlayerListList", "Group #PlayerListCards { LayoutMode: Left; }");

        int i = 0;
        for (FancyPlayer onlinePlayer : FancyPlayerService.get().getOnlinePlayers()) {
            command.append("#PlayerListCards", "Pages/PlayerListEntry.ui");

            String primaryGroupName = "None";
            List<Group> groupSortedByWeight = onlinePlayer.getData().getGroupSortedByWeight();
            if (!groupSortedByWeight.isEmpty()) {
                primaryGroupName = groupSortedByWeight.getFirst().getName();
            }

            Currency primaryCurrency = CurrencyService.get().getPrimaryCurrency();
            double balance = onlinePlayer.getData().getBalance(primaryCurrency);

            command.set("#PlayerListCards[" + i + "] #Username.Text", onlinePlayer.getData().getUsername());
            command.set("#PlayerListCards[" + i + "] #Group.Text", primaryGroupName);
            command.set("#PlayerListCards[" + i + "] #Balance.Text", primaryCurrency.symbol() + NumberUtils.formatNumber(balance));
            command.set("#PlayerListCards[" + i + "] #Playtime.Text", TimeUtils.formatTime(onlinePlayer.getData().getPlayTime()));

            i++;
        }
    }
}
