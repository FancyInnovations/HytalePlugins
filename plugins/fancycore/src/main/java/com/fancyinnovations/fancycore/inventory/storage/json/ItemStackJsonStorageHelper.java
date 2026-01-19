package com.fancyinnovations.fancycore.inventory.storage.json;

import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonWriterSettings;

import java.util.ArrayList;
import java.util.List;

public class ItemStackJsonStorageHelper {

    public static String toJson(List<ItemStack> items) {
        BsonArray array = new BsonArray();
        for (ItemStack item : items) {
            if (item == null) {
                // Store null as an empty document or null value
                array.add(new BsonDocument());
            } else {
                BsonDocument doc = ItemStack.CODEC.encode(item, EmptyExtraInfo.EMPTY);
                array.add(doc);
            }
        }

        return new BsonDocument("items", array).toJson(
                JsonWriterSettings.builder()
                        .indent(true)
                        .build()
        );
    }

    public static List<ItemStack> fromJson(String json) {
        List<ItemStack> items = new ArrayList<>();

        BsonDocument document = BsonDocument.parse(json);
        BsonArray array = document.getArray("items");

        for (BsonValue value : array.getValues()) {
            BsonDocument doc = value.asDocument();
            // Check if document is empty (null item) or has content
            if (doc.isEmpty()) {
                items.add(null);
            } else {
                ItemStack item = ItemStack.CODEC.decode(doc, EmptyExtraInfo.EMPTY);
                items.add(item);
            }
        }

        return items;
    }

}
