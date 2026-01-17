![](https://raw.githubusercontent.com/FancyInnovations/FancyDocs/refs/heads/main/public/logos-and-banners/fancycore-banner.png)

> The all-in-one core plugin for Hytale servers. From powerful permission management and moderation tools to a flexible economy with multiple currencies and much more.

## Why FancyCore?

- All-in-one core plugin – fewer dependencies, fewer conflicts
- Designed for both small servers and large networks
- Highly configurable without sacrificing performance
- Actively maintained with a clear development roadmap
- Built with developers in mind (clean API & extensions)

## Features

With **FancyCore**, you get a wide variety of features that are essential for running a modern Hytale server.
It includes **100+ commands** covering countless use cases for both small community servers and large server networks.

FancyCore is designed with ease of use, high performance, and extensibility in mind.

**Feature categories:**
- Permissions System (groups, user permissions, etc.)
- Economy System (multiple currencies, player balances, transactions)
- Punishment System (kicks, bans, mutes, warnings, reports)
- Chat System (chatrooms, private messaging, ignore system)
- Inventory System (kits, backpacks, remote inventory access)
- Teleportation System (warps, homes, spawn, teleport requests)
- Player Utilities (player list with extra info, etc.)
- Plugin Management (check for updates, view version info)


### Permissions System

Commands:
- `/permissions set <target> <permission> [--enabled=?]`
- `/permissions remove <target> <permission>`
- `/permissions list <target>`
- `/permissions check <permission> [--target=?]`
- `/groups info <group>`
- `/groups list`
- `/groups info <group>`
- `/groups create <group name>`
- `/groups delete <group>`
- `/groups setprefix <group> <prefix>`
- `/groups setsuffix <group> <suffix>`
- `/groups setweight <group> <weight>`
- `/groups permissions list <group>`
- `/groups permissions set <group> <permission> [--enabled=?]`
- `/groups permissions remove <group> <permission>`
- `/groups permissions clear <group>`
- `/groups parents list <group>`
- `/groups parents add <group> <parent>`
- `/groups parents remove <group> <parent>`
- `/groups parents clear <group>`
- `/groups members list <group>`
- `/groups members add <group> <target>`
- `/groups members remove <group> <target>`
- `/groups members clear <group>`

### Economy System

Commands:
- `/currency list`
- `/currency info <currency>`
- `/currency create <currency> [--symbol=?] [--serverbound=?]`
- `/currency remove <currency>`
- `/<currency> balance [--target=?]` (each currency gets its own command)
- `/<currency> pay <target> <amount>`
- `/<currency> add <target> <amount>`
- `/<currency> remove <target> <amount>`
- `/<currency> set <target> <amount>`
- `/balance [--target=?]` (the primary currency will be used in the following commands)
- `/pay <target> <amount>`
- `/addmoney <target> <amount>`
- `/removemoney <target> <amount>`
- `/setmoney <target> <amount>`

### Punishment System

Commands:
- `/kick <target> <reason>`
- `/warn <target> <reason>`,
- `/mute <target> <reason>`
- `/tempmute <target> <duration> <reason>>`
- `/unmute <target>`
- `/ban <target> <reason>`
- `/tempban <target> <duration> <reason>`
- `/unban <target>`
- `/punishments list <target>`
- `/punishments listactive <target>`
- `/punishments info <punishment id>`
- `/report <target> <reason>`
- `/reports list`
- `/reports info <report id>`
- `/reports resolve <report id>`

### Chat System

Commands:
- `/chatroom info [--chatroom=?]`
- `/chatroom list`
- `/chatroom create <chatroom name>`
- `/chatroom delete [--chatroom=?]` (defaults to the chatroom the player is currently in)
- `/chatroom clearchat [--chatroom=?]`
- `/chatroom mute [--chatroom=?]`
- `/chatroom unmute [--chatroom=?]`
- `/chatroom cooldown <cooldown> [--chatroom=?]`
- `/chatroom watch <cdhatroom>`
- `/chatroom watching`
- `/chatroom switch <chatroom>`
- `/message <target> <message>`
- `/reply <message>`
- `/ignore <target>`
- `/unignore <target>`
- `/togglemessages`
- `/chatcolor set <color code>`

### Inventory System

Commands:
- `/kit <kit>`
- `/createkit <kit name>`
- `/deletekit <kit>`
- `/listkits`
- `/backpack <backpack> [--player=?]`
- `/createbackpack <backpack name> <size>`
- `/deletebackpack <backpack>`
- `/listbackpacks`
- `/openinventory <target>`
- `/clearinventory [--target=?]`

### Teleportation System

Commands:
- `/setspawn`
- `/spawn`
- `/setwarp <warp name>`
- `/warp <warp name>`
- `/deletewarp <warp name>`
- `/listwarps`
- `/sethome <home name>`
- `/home <home name>`
- `/deletehome <home name>`
- `/listhomes`
- `/teleport <target>`
- `/teleporthere <target>`
- `/teleporall`
- `/teleportpos <x> <y> <z> [--world=?]`
- `/teleportrequest <target>`
- `/teleportaccept [--target=?]`
- `/teleportdeny [--target=?]`
- `/teleportback`
- `/teleportdeathback [--target=?]`

### Player Utilities

Commands:
- `/playerlist`

### Plugin commands

Commands:
- `/fancycore version`
- `/fancycore update`