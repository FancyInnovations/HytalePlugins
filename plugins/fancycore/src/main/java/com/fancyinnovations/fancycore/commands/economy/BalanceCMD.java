package com.fancyinnovations.fancycore.commands.economy;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.hytaleutils.NumberUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class BalanceCMD extends CommandBase {

    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("target", "Username or UUID", FancyCoreArgs.PLAYER);

    public BalanceCMD() {
        super("balance", "Check your or someone else's balance.");
        addAliases("bal");
        requirePermission("fancycore.commands.balance");
    }

    @Override
    protected void executeSync(@NotNull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        FancyPlayer target = targetArg.provided(ctx) ? targetArg.get(ctx) : fp;

        Currency currency = CurrencyService.get().getPrimaryCurrency();
        if (currency == null) {
            fp.sendMessage("No primary currency is set.");
            return;
        }

        double balance = target.getData().getBalance(currency);

        fp.sendMessage(target.getData().getUsername() + " currently has " +  currency.symbol() + NumberUtils.formatNumber(balance) + " " + currency.name());
    }
}
