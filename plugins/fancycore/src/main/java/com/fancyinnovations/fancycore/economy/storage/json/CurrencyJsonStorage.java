package com.fancyinnovations.fancycore.economy.storage.json;

import com.fancyinnovations.fancycore.api.economy.Currency;
import com.fancyinnovations.fancycore.api.economy.CurrencyStorage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.List;

public class CurrencyJsonStorage implements CurrencyStorage {

    private static final String DATA_DIR_PATH = "mods/FancyCore/data/currencies";
    private final JDB db;

    public CurrencyJsonStorage() {
        this.db = new JDB(DATA_DIR_PATH);
    }

    @Override
    public List<Currency> getAllCurrencies() {
        try {
            return db.getAll("", Currency.class);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to load all Currencies",
                    ThrowableProperty.of(e)
            );
        }

        return List.of();
    }

    @Override
    public void setCurrency(Currency currency) {
        try {
            db.set(currency.name(), currency);
        } catch (IOException e) {
            FancyCorePlugin.get().getFancyLogger().error(
                    "Failed to store Currency",
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    public void removeCurrency(String name) {
        db.delete(name);
    }
}
