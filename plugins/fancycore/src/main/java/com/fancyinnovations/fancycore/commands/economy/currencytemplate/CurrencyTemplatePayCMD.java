package com.fancyinnovations.fancycore.commands.economy.currencytemplate;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.fancycore.utils.NumberUtils;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.jetbrains.annotations.NotNull;

public class CurrencyTemplatePayCMD extends CommandBase {

    protected final RequiredArg<FancyPlayer> targetArg = this.withRequiredArg("target", "Username or UUID", FancyCoreArgs.PLAYER);
    protected final RequiredArg<Double> amountArg = this.withRequiredArg("target", "amount you want to pay", ArgTypes.DOUBLE);

    protected final Currency currency;

    public CurrencyTemplatePayCMD(Currency currency) {
        super("pay", "Pay a specific amount of " + currency.name() + " to another player");
        this.currency = currency;
        requirePermission("fancycore.commands." + currency.name().toLowerCase() + ".pay");
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

        FancyPlayer target = targetArg.get(ctx);
        double amount = amountArg.get(ctx);
        if (amount <= 0) {
            fp.sendMessage("You must pay a positive amount.");
            return;
        }

        fp.getData().removeBalance(currency, amount);
        target.getData().addBalance(currency, amount);

        String formattedAmount = NumberUtils.formatNumber(amount);

        fp.sendMessage("You have paid " + currency.symbol() + formattedAmount + " to " + target.getData().getUsername() + ".");
        target.sendMessage("You have received " + currency.symbol() + formattedAmount + " from " + fp.getData().getUsername() + ".");
    }
}
