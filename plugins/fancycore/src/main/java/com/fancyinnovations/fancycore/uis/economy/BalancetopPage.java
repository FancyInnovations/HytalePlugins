package com.fancyinnovations.fancycore.uis.economy;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.economy.LeaderboardEntry;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.uis.EmptyUIData;
import com.fancyinnovations.hytaleutils.NumberUtils;
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

public class BalancetopPage extends InteractiveCustomUIPage<EmptyUIData> {

    private final Currency currency;

    public BalancetopPage(@NotNull PlayerRef playerRef, Currency currency) {
        super(playerRef, CustomPageLifetime.CanDismiss, EmptyUIData.CODEC);
        this.currency = currency;
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

        command.append("Pages/BalancetopPage.ui");

        command.clear("#BalancetopCards");
        command.appendInline("#Main #BalancetopList", "Group #BalancetopCards { LayoutMode: Left; }");

        int i = 0;
        for (LeaderboardEntry entry : CurrencyService.get().getTopBalances(currency, 10)) {
            command.append("#BalancetopCards", "Pages/BalancetopEntry.ui");

            command.set("#BalancetopCards[" + i + "] #Username.Text", entry.username() + " (#" + entry.rank() + ")");
            command.set("#BalancetopCards[" + i + "] #Balance.Text", currency.symbol() + NumberUtils.formatNumber(entry.balance()));

            i++;
        }
    }
}
