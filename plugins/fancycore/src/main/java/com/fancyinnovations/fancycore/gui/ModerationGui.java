package com.fancyinnovations.fancycore.gui;

import com.fancyinnovations.fancycore.api.moderation.PunishmentService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.uihelper.BaseGui;
import com.fancyinnovations.uihelper.UIAction;
import com.fancyinnovations.uihelper.UIBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

/**
 * Moderation GUI implementation modeled after the official Custom UI docs
 * ({@code Common/UI/Custom/FancyCore/ModerationPage.ui}) and HyUI's PageBuilder usage.
 *
 * The layout is defined in the .ui file; this class only wires events and actions.
 */
public class ModerationGui extends BaseGui<ModerationGuiData> {

    private static final String DEFAULT_REASON = "No reason specified";

    // Cache the last value typed into the player name field.
    // BaseGui sends input and button events separately, so we can't rely on
    // data.playerName being populated on button clicks.
    private String cachedPlayerName;

    public ModerationGui(PlayerRef playerRef) {
        super(playerRef, ModerationGuiData.CODEC);
    }

    @Override
    protected void buildUI(@NotNull UIBuilder ui, @NotNull GuiContext ctx) {
        // Path is relative to Common/UI/Custom/, matching the pattern from the docs:
        // https://hytale-docs.com/docs/api/server-internals/custom-ui
        ui.page("FancyCore/ModerationPage.ui")
                .onInput("#PlayerNameInput", "@PlayerName")
                .onClick("#KickBtn", "Kick")
                .onClick("#MuteBtn", "Mute")
                .onClick("#BanBtn", "Ban")
                .onClick("#WarnBtn", "Warn")
                .onClick("#UnmuteBtn", "Unmute")
                .onClick("#UnbanBtn", "Unban");
    }

    @Override
    protected void onInput(@NotNull String value, @NotNull ModerationGuiData data, @NotNull GuiContext ctx) {
        // Prefer the codec-populated value if present, otherwise fall back to raw input.
        String name = data.playerName != null ? data.playerName : value;
        if (name != null) {
            name = name.trim();
        }
        if (name != null && !name.isEmpty()) {
            cachedPlayerName = name;
        }
    }

    @Override
    protected void onAction(@NotNull UIAction action, @NotNull ModerationGuiData data, @NotNull GuiContext ctx) {
        String playerName = data.playerName;
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = cachedPlayerName;
        }

        FancyPlayer staff = FancyPlayerService.get().getByUUID(ctx.playerRef().getUuid());
        if (staff == null) {
            // Fallback to raw message if FancyPlayer lookup fails
            ctx.message("&cError: Could not find your player data.");
            return;
        }

        if (playerName == null || playerName.trim().isEmpty()) {
            staff.sendMessage("&cPlease enter a player name first.");
            return;
        }

        FancyPlayer target = FancyPlayerService.get().getByUsername(playerName.trim());
        if (target == null) {
            staff.sendMessage("&cPlayer '" + playerName + "' not found.");
            return;
        }

        PunishmentService punishmentService = PunishmentService.get();

        if (action.is("Kick")) {
            if (!target.isOnline()) {
                staff.sendMessage("&cPlayer '" + playerName + "' is not online.");
                return;
            }
            punishmentService.kickPlayer(target, staff, DEFAULT_REASON);
            staff.sendMessage("&aSuccessfully kicked " + target.getData().getUsername());

        } else if (action.is("Mute")) {
            punishmentService.mutePlayer(target, staff, DEFAULT_REASON);
            staff.sendMessage("&aSuccessfully muted " + target.getData().getUsername());

        } else if (action.is("Ban")) {
            punishmentService.banPlayer(target, staff, DEFAULT_REASON);
            staff.sendMessage("&aSuccessfully banned " + target.getData().getUsername());

        } else if (action.is("Warn")) {
            punishmentService.warnPlayer(target, staff, DEFAULT_REASON);
            staff.sendMessage("&aSuccessfully warned " + target.getData().getUsername());

        } else if (action.is("Unmute")) {
            boolean success = punishmentService.unmute(target);
            if (success) {
                staff.sendMessage("&aSuccessfully unmuted " + target.getData().getUsername());
            } else {
                staff.sendMessage("&cPlayer '" + playerName + "' is not muted.");
            }

        } else if (action.is("Unban")) {
            boolean success = punishmentService.unban(target);
            if (success) {
                staff.sendMessage("&aSuccessfully unbanned " + target.getData().getUsername());
            } else {
                staff.sendMessage("&cPlayer '" + playerName + "' is not banned.");
            }
        }
    }
}

