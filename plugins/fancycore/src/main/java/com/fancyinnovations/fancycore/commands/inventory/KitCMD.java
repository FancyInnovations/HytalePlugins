package com.fancyinnovations.fancycore.commands.inventory;

import com.fancyinnovations.fancycore.api.inventory.Kit;
import com.fancyinnovations.fancycore.api.inventory.KitsService;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.fancyinnovations.fancycore.api.player.FancyPlayerService;
import com.fancyinnovations.fancycore.commands.arguments.FancyCoreArgs;
import com.fancyinnovations.hytaleutils.TimeUtils;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

public class KitCMD extends AbstractPlayerCommand {

    protected final RequiredArg<Kit> kitArg = this.withRequiredArg("kit", "the name of the kit", FancyCoreArgs.KIT);
    protected final OptionalArg<FancyPlayer> targetArg = this.withOptionalArg("target", "target player", FancyCoreArgs.PLAYER);

    public KitCMD() {
        super("kit", "Gives the specified kit to the targeted player");
        requirePermission("fancycore.commands.kit");
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be executed by a player."));
            return;
        }

        FancyPlayer fp = FancyPlayerService.get().getByUUID(ctx.sender().getUuid());
        if (fp == null) {
            ctx.sendMessage(Message.raw("FancyPlayer not found."));
            return;
        }

        Kit kit = kitArg.get(ctx);

        if (!PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.kits." + kit.name())) {
            fp.sendMessage("You do not have permission to use this kit.");
            return;
        }

        if (kit.cooldown() > 0 && !PermissionsModule.get().hasPermission(fp.getData().getUUID(), "fancycore.kits.bypasscooldown")) {
            long lastTimeUsedKit = fp.getData().getLastTimeUsedKit(kit.name());
            long timeSinceLastUse = System.currentTimeMillis() - lastTimeUsedKit;
            if (timeSinceLastUse < kit.cooldown()) {
                long timeLeft = kit.cooldown() - timeSinceLastUse;
                fp.sendMessage("You must wait " + TimeUtils.formatTime(timeLeft) + " before using this kit again.");
                return;
            }

            fp.getData().setLastTimeUsedKit(kit.name(), System.currentTimeMillis());
        }

        KitsService.get().giveKitToPlayer(kit, fp);

        fp.sendMessage("You have received the kit: " + kit.name());
    }
}
