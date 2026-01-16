package com.fancyinnovations.fancycore.economy.service;

import com.fancyinnovations.fancycore.api.FancyCoreConfig;
import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.economy.CurrencyStorage;
import com.fancyinnovations.fancycore.commands.economy.currencytemplate.CurrencyTemplateCMD;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.hypixel.hytale.server.core.command.system.CommandManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyServiceImpl implements CurrencyService {

    private static final CurrencyStorage STORAGE = FancyCorePlugin.get().getCurrencyStorage();
    private static final FancyCoreConfig CONFIG = FancyCorePlugin.get().getConfig();

    private final Map<String, Currency> currencies;

    public CurrencyServiceImpl() {
        this.currencies = new ConcurrentHashMap<>();
        load();
    }

    private void load() {
        String serverName = FancyCorePlugin.get().getConfig().getServerName();

        for (Currency currency : STORAGE.getAllCurrencies()) {
            // Only load currencies for this server
            if (!currency.server().equalsIgnoreCase("global") && !currency.server().equalsIgnoreCase(serverName)) {
                continue;
            }

            this.currencies.put(currency.name(), currency);
            CommandManager.get().register(new CurrencyTemplateCMD(currency));
        }
    }

    @Override
    public Currency getCurrency(String name) {
        return this.currencies.get(name);
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return List.copyOf(this.currencies.values());
    }

    @Override
    public void registerCurrency(Currency currency) {
        this.currencies.put(currency.name(), currency);
        STORAGE.setCurrency(currency);

        CommandManager.get().register(new CurrencyTemplateCMD(currency));
    }

    @Override
    public void unregisterCurrency(String name) {
        this.currencies.remove(name);
        STORAGE.removeCurrency(name);

        CommandManager.get().getCommandRegistration().remove(name.toLowerCase());
    }

    @Override
    public Currency getPrimaryCurrency() {
        return getCurrency(CONFIG.primaryCurrencyName());
    }
}
