package com.fancyinnovations.fancycore.commands.economy.currencytemplate;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import org.jetbrains.annotations.NotNull;

public class CurrencyTemplateCMD extends AbstractCommandCollection {

    public CurrencyTemplateCMD(@NotNull Currency currency) {
        super(currency.name().toLowerCase(), "Manage the " + currency.name().toLowerCase() + " currency");
        requirePermission("fancycore.commands." + currency.name().toLowerCase());

        addSubCommand(new CurrencyTemplateBalanceCMD(currency));
        addSubCommand(new CurrencyTemplatePayCMD(currency));
        addSubCommand(new CurrencyTemplateAddCMD(currency));
        addSubCommand(new CurrencyTemplateRemoveCMD(currency));
        addSubCommand(new CurrencyTemplateSetCMD(currency));
    }

}
