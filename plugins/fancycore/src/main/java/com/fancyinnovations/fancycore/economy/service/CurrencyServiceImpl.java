package com.fancyinnovations.fancycore.economy.service;

import com.fancyinnovations.fancycore.api.FancyCoreConfig;
import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyService;
import com.fancyinnovations.fancycore.api.economy.CurrencyStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyServiceImpl implements CurrencyService {

    private static final CurrencyStorage STORAGE = FancyCorePlugin.get().getCurrencyStorage();
    private static final FancyCoreConfig CONFIG = FancyCorePlugin.get().getConfig();

    private final Map<String, Currency> currencies;
    private Currency primaryCurrency;

    public CurrencyServiceImpl() {
        this.currencies = new ConcurrentHashMap<>();
        load();
    }

    private void load() {
        for (Currency currency : STORAGE.getAllCurrencies()) {
            this.currencies.put(currency.name(), currency);
        }
    }

    @Override
    public Currency getCurrency(String name) {
        return this.currencies.get(name);
    }

    @Override
    public void registerCurrency(Currency currency) {
        this.currencies.put(currency.name(), currency);
        STORAGE.setCurrency(currency);
    }

    @Override
    public void unregisterCurrency(String name) {
        this.currencies.remove(name);
        STORAGE.removeCurrency(name);
    }

    @Override
    public Currency getPrimaryCurrency() {
        return getCurrency(CONFIG.primaryCurrencyName());
    }
}
