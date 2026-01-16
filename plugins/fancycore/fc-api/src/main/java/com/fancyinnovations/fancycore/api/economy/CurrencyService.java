package com.fancyinnovations.fancycore.api.economy;

import com.fancyinnovations.fancycore.api.FancyCore;

import java.util.List;

public interface CurrencyService {

    static CurrencyService get() {
        return FancyCore.get().getCurrencyService();
    }

    Currency getCurrency(String name);

    List<Currency> getAllCurrencies();

    void registerCurrency(Currency currency);

    void unregisterCurrency(String name);

    Currency getPrimaryCurrency();

}
