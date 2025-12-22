package com.fancyinnovations.fancycore.api.economy;

import com.fancyinnovations.fancycore.api.FancyCore;

public interface CurrencyService {

    static CurrencyService get() {
        return FancyCore.get().getCurrencyService();
    }

    Currency getCurrency(String name);

    void registerCurrency(Currency currency);

    void unregisterCurrency(String name);

    Currency getPrimaryCurrency();

}
