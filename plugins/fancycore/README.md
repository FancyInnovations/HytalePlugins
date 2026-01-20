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
- `/group metadata list <group>`
- `/group metadata set <group> <key> <value>`
- `/group metadata remove <group> <key>`
- `/group metadata clear <group>`
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
- `/<currency> top`
- `/balance [--target=?]` (the primary currency will be used in the following commands)
- `/pay <target> <amount>`
- `/addmoney <target> <amount>`
- `/removemoney <target> <amount>`
- `/setmoney <target> <amount>`
- `/balancetop`

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
- `/broadcast <message>`

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
- `/gamemode <gamemode> [--target=?]`
- `/gmc [--target=?]`
- `/gma [--target=?]`
- `/fly [--target=?]`
- `/feed [--target=?]`
- `/heal [--target=?]`
- `/god [--target=?]`
- `/vanish [--target=?]`
- `/speed [--spped=?]`
- `/near [--radius=?]`
- `/seen <target>`
- `/playtime [--target=?]`
- `/ping [--target=?]`
- `/playerlist`

### Scoreboard System

- `/scoreboard listpages`
- `/scoreboard switchpage <page> [--target=?]`
- `/scoreboard page info <page>`
- `/scoreboard page create <name>`
- `/scoreboard page delete <page>`
- `/scoreboard page setalignment <page> <alignment>`
- `/scoreboard page setofffset <page> <offset>`
- `/scoreboard page setwidth <page> <width>`
- `/scoreboard page setheight <page> <height>`
- `/scoreboard page setbackground <page> <color>`
- `/scoreboard page setopacity <page> <opacity>`
- `/scoreboard page lines info <page> <line>`
- `/scoreboard page lines add <page> <text>`
- `/scoreboard page lines remove <page> <line>`
- `/scoreboard page lines settext <page> <line> <text>`
- `/scoreboard page lines setalignment <page> <line> <alignment>`
- `/scoreboard page lines setfontsize <page> <line> <fontsize>`
- `/scoreboard page lines setpaddingtop <page> <line> <padding>`
- `/scoreboard page lines setpaddingbottom <page> <line> <padding>`
- `/scoreboard page lines setpaddingleft <page> <line> <padding>`
- `/scoreboard page lines setpaddingright <page> <line> <padding>`

### Plugin commands

Commands:
- `/fancycore version`
- `/fancycore update`
- `/fancycore reloadconfig`

### Placeholders

Player placeholders:
- `%player_name%` - Player username
- `%player_nickname%` - Player nickname
- `%player_playtime%` - Player play time (formatted)
- `%player_playtime_ms%` - Player play time (milliseconds)
- `%player_uuid%` - Player UUID
- `%player_balance%` - Player balance (formatted)
- `%player_balance_raw%` - Player balance (raw)
- `%player_chat_color%` - Player chat color
- `%player_first_time_joined%` - Player first time joined (formatted)
- `%player_first_time_joined_raw%` - Player first time joined (raw timestamp)
- `%player_group%` - Player group name
- `%player_group_prefix%` - Player group prefix
- `%player_group_suffix%` - Player group suffix
- `%player_world%` - Player current world
- `%player_is_op%` - Player is OP
- `%player_location_x%` - Player X coordinate
- `%player_location_y%` - Player Y coordinate
- `%player_location_z%` - Player Z coordinate
- `%player_location_yaw%` - Player yaw
- `%player_location_pitch%` - Player pitch
- `%player_location_roll%` - Player roll

Server placeholders:
- `%online_players%` - Number of online players
- `%max_players%` - Maximum number of players allowed

LuckPerms placeholders:
- `%luckperms_prefix%` - LuckPerms player prefix
- `%luckperms_suffix%` - LuckPerms player suffix
- `%luckperms_primary_group_name%` - LuckPerms primary group name