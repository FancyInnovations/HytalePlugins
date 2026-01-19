package com.fancyinnovations.fancycore.commands.moderation;

import com.fancyinnovations.fancycore.gui.ModerationGui;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

/**
 * /modgui command – opens the moderation GUI for the executing player.
 */

public class ModerationGuiCMD extends AbstractPlayerCommand {

    public ModerationGuiCMD() {
        super("modgui", "Opens the moderation GUI");
        requirePermission("fancycore.commands.modgui");
    }

    @Override
    protected void execute(@NotNull CommandContext ctx,
                           @NotNull Store<EntityStore> store,
                           @NotNull Ref<EntityStore> ref,
                           @NotNull PlayerRef playerRef,
                           @NotNull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        ModerationGui gui = new ModerationGui(playerRef);
        player.getPageManager().openCustomPage(ref, store, gui);
    }
}

