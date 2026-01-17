package com.fancyinnovations.fancycore.commands.moderation.punishments;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class PunishmentsCMD extends AbstractCommandCollection {

    public PunishmentsCMD() {
        super("punishments", "Manage player punishments");
        requirePermission("fancycore.commands.punishments");

        addSubCommand(new PunishmentsListCMD());
        addSubCommand(new PunishmentsListActiveCMD());
        addSubCommand(new PunishmentsInfoCMD());
    }
}
