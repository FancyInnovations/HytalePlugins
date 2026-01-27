package com.fancyinnovations.fancycore.scoreboard;

import com.fancyinnovations.fancycore.api.placeholders.PlaceholderService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardLine;
import com.fancyinnovations.fancycore.api.scoreboard.ScoreboardPage;
import com.fancyinnovations.fancycore.main.FancyCorePlugin;
import com.fancyinnovations.fancycore.utils.ColorUtils;
import com.hypixel.hytale.protocol.ColorAlpha;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class ScoreboardUI extends CustomUIHud {

    private final FancyPlayer fancyPlayer;
    private ScoreboardPage currentPage;
    private Method setBsonValueMethod = null;

    public ScoreboardUI(FancyPlayer fancyPlayer, ScoreboardPage scoreboardPage) {
        super(fancyPlayer.getPlayer());
        this.fancyPlayer = fancyPlayer;
        this.currentPage = scoreboardPage;

        try {
            this.setBsonValueMethod = UICommandBuilder.class.getDeclaredMethod("setBsonValue", String.class, BsonValue.class);
            this.setBsonValueMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            FancyCorePlugin.get().getFancyLogger().info(
                    "Failed to access setBsonValue method via reflection",
                    ThrowableProperty.of(e)
            );
        }
    }

    @Override
    protected void build(@NotNull UICommandBuilder builder) {
        refreshUI(builder);
    }

    public void refreshUI() {
        refreshUI(null);
    }

    private void refreshUI(UICommandBuilder builder) {
        if (builder == null) {
            builder = new UICommandBuilder();
        }

        if (currentPage == null) {
            FancyCorePlugin.get().getFancyLogger().warn("Attempting to refresh the UI without the current page for " + fancyPlayer.getPlayer().getUsername());
            builder.append("Hud/ScoreboardBase.ui");
            update(true, builder);
            return;
        }

        builder.append("Hud/ScoreboardBase.ui");

        setAnchorForPanel(builder, "#ScoreboardPanel.Anchor", currentPage.getOffset(), currentPage.getWidth(), currentPage.getHeight());
        setLayoutMode(builder, currentPage.getAlignment());

        ColorAlpha bgColor = new ColorAlpha(
                currentPage.getBackgroundColor().alpha(),
                currentPage.getBackgroundColor().red(),
                currentPage.getBackgroundColor().green(),
                currentPage.getBackgroundColor().blue()
        );
        setBackgroundColor(builder, "#ScoreboardPanel.Background", ColorParseUtil.colorToHexAlphaString(bgColor));

        int i = 0;
        for (ScoreboardLine line : currentPage.getLines()) {
            String processedText = line.getContent();
            try {
                processedText = PlaceholderService.get().parse(fancyPlayer, line.getContent());
            } catch (Exception e) {
                FancyCorePlugin.get().getFancyLogger().warn(
                        "Failed to parse placeholders for scoreboard line: " + line.getContent(),
                        ThrowableProperty.of(e)
                );
            }
            Message colouredText = ColorUtils.colour(processedText);

            builder.appendInline("#ScoreboardBody", "Group { LayoutMode: Top; Anchor: (Bottom: 0); }");
            builder.append("#ScoreboardBody[" + i + "]", "Hud/ScoreboardLine.ui");
            builder.set("#ScoreboardBody[" + i + "] #ScoreboardLine.TextSpans", colouredText);

            int fontSize = line.getFontSize() != null ? line.getFontSize() : 20;
            String alignment = line.getAlignment() != null ? line.getAlignment() : "Left";
            setStyle(builder, "#ScoreboardBody[" + i + "] #ScoreboardLine.Style", fontSize, alignment);

            int paddingTop = line.getPaddingTop() != null ? line.getPaddingTop() : 0;
            int paddingBottom = line.getPaddingBottom() != null ? line.getPaddingBottom() : 0;
            int paddingLeft = line.getPaddingLeft() != null ? line.getPaddingLeft() : 0;
            int paddingRight = line.getPaddingRight() != null ? line.getPaddingRight() : 0;
            setPadding(builder, "#ScoreboardBody[" + i + "] #ScoreboardLine.Padding", paddingTop, paddingBottom, paddingLeft, paddingRight);

            i++;
        }

        update(true, builder);
    }


    private void setStyle(UICommandBuilder builder, String selector, int fontSize, String alignment) {
        BsonDocument style = new BsonDocument();

        style.put("FontSize", new BsonInt32(fontSize));

        if (!alignment.equals("Left")) {
            style.put("Alignment", new BsonString(alignment));
        }

        try {
            if (this.setBsonValueMethod != null) {
                this.setBsonValueMethod.invoke(builder, selector, style);
            }
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().info(
                    "Failed to set font size via reflection",
                    ThrowableProperty.of(e)
            );
        }
    }

    private void setPadding(UICommandBuilder builder, String selector, int top, int bottom, int left, int right) {
        boolean changed = false;

        BsonDocument anchor = new BsonDocument();
        if (top != 0) {
            anchor.put("Top", new BsonInt32(top));
            changed = true;
        }

        if (bottom != 0) {
            anchor.put("Bottom", new BsonInt32(bottom));
            changed = true;
        }

        if (left != 0) {
            anchor.put("Left", new BsonInt32(left));
            changed = true;
        }

        if (right != 0) {
            anchor.put("Right", new BsonInt32(right));
            changed = true;
        }

        if (!changed) {
            return;
        }

        try {
            if (this.setBsonValueMethod != null) {
                this.setBsonValueMethod.invoke(builder, selector, anchor);
            }
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().info(
                    "Failed to set font size via reflection",
                    ThrowableProperty.of(e)
            );
        }
    }

    private void setAnchorForPanel(UICommandBuilder builder, String selector, int offset, int width, int height) {
        BsonDocument anchor = new BsonDocument();
        anchor.put("Top", new BsonInt32(offset));
        anchor.put("Width", new BsonInt32(width));
        anchor.put("Height", new BsonInt32(height));

        try {
            if (this.setBsonValueMethod != null) {
                this.setBsonValueMethod.invoke(builder, selector, anchor);
            }
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().info(
                    "Failed to set font size via reflection",
                    ThrowableProperty.of(e)
            );
        }
    }

    private void setLayoutMode(UICommandBuilder builder, String layoutMode) {
        BsonString layoutModeValue = new BsonString(layoutMode);

        try {
            if (this.setBsonValueMethod != null) {
                this.setBsonValueMethod.invoke(builder, "#ScoreboardWrapper.LayoutMode", layoutModeValue);
            }
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().info(
                    "Failed to set layout mode via reflection",
                    ThrowableProperty.of(e)
            );
        }
    }

    private void setBackgroundColor(UICommandBuilder builder, String selector, String color) {
        BsonString colorValue = new BsonString(color);

        try {
            if (this.setBsonValueMethod != null) {
                this.setBsonValueMethod.invoke(builder, selector, colorValue);
            }
        } catch (Exception e) {
            FancyCorePlugin.get().getFancyLogger().info(
                    "Failed to set background color via reflection",
                    ThrowableProperty.of(e)
            );
        }
    }

    public FancyPlayer getFancyPlayer() {
        return fancyPlayer;
    }

    public ScoreboardPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(ScoreboardPage currentPage) {
        this.currentPage = currentPage;
    }
}
