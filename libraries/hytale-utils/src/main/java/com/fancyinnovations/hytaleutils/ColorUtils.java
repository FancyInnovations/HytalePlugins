package com.fancyinnovations.hytaleutils;


import com.hypixel.hytale.server.core.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColorUtils {

        // Minecraft -> hex colour mappings
        private static final Map<String, String> MINECRAFT_COLOUR_MAP = Map.ofEntries(
                Map.entry("&0", "#000000"),
                Map.entry("&1", "#0000AA"),
                Map.entry("&2", "#00AA00"),
                Map.entry("&3", "#00AAAA"),
                Map.entry("&4", "#AA0000"),
                Map.entry("&5", "#AA00AA"),
                Map.entry("&6", "#FFAA00"),
                Map.entry("&7", "#AAAAAA"),
                Map.entry("&8", "#555555"),
                Map.entry("&9", "#5555FF"),
                Map.entry("&a", "#55FF55"),
                Map.entry("&b", "#55FFFF"),
                Map.entry("&c", "#FF5555"),
                Map.entry("&d", "#FF55FF"),
                Map.entry("&e", "#FFFF55"),
                Map.entry("&f", "#FFFFFF"),
                Map.entry("&r", "#FFFFFF")
        );

    public static String formatColorInHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

        public static Message colour(String input) {
            return colour(input, true, true, true);
        }

        public static Message colour(String input, boolean translateColourCodes, boolean translateFormatCodes, boolean translateHex) {
            List<Message> parts = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();

            String currentColor = "#FFFFFF";
            boolean bold = false;
            boolean italic = false;

            for (int i=0; i<input.length(); i++) {
                char c = input.charAt(i);

                // Translating hex colour codes (&#FFFFFF format)
                if (translateHex && c == '&' && i+7 < input.length() && input.charAt(i+1) == '#') {
                    String hex = input.substring(i + 2, i + 8);
                    flush(buffer, parts, currentColor, bold, italic);

                    currentColor = "#" + hex;

                    // your rule: colour change clears formatting
                    bold = false;
                    italic = false;

                    i += 7; // skip over '&#' + 6 hex chars
                    continue;
                }

                // Translating minecraft colour codes
                if (c == '&' && i+1 < input.length()) {
                    char code = Character.toLowerCase(input.charAt(i + 1));

                    // --- Colour codes ---
                    String colourKey = "&" + code;
                    String codeHex = MINECRAFT_COLOUR_MAP.get(colourKey);

                    if (translateColourCodes && codeHex != null) {
                        flush(buffer, parts, currentColor, bold, italic);
                        currentColor = codeHex;

                        bold = false;
                        italic = false;

                        i++;
                        continue;
                    }

                    // --- Bold / Italics ---
                    if (translateFormatCodes && code == 'l' || code == 'o') {
                        flush(buffer, parts, currentColor, bold, italic);

                        if (code == 'l') bold = true;
                        if (code == 'o') italic = true;

                        i++;
                        continue;
                    }
                }

                buffer.append(c);
            }

            // Flush remaining text
            if (!buffer.isEmpty()) {
                flush(buffer, parts, currentColor, bold, italic);
            }

            return Message.join(parts.toArray(new Message[0]));
        }

        private static void flush(StringBuilder buffer, List<Message> parts, String color, boolean bold, boolean italic) {
            if (buffer.isEmpty()) return;
            Message msg = Message.raw(buffer.toString()).color(color);

            if (bold) {
                msg = msg.bold(true);
            }
            if (italic) {
                msg = msg.italic(true);
            }

            parts.add(msg);
            buffer.setLength(0);
        }

}
