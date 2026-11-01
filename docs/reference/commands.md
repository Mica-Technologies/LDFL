# Commands

LDFL has one command, `/ldfl`, for testing heroes and for server admins. It needs **operator
permission level 2** (cheats on, in singleplayer).

```
/ldfl <ult|reset> [player]
```

| Subcommand | What it does |
| --- | --- |
| `/ldfl ult [player]` | Fills the **ultimate meter** of the player's current hero, so the ultimate is ready at once |
| `/ldfl reset [player]` | Clears the **cooldowns** of the player's current hero's two abilities |

Without a player name, the command applies to whoever runs it. Player names tab-complete.

The player must currently **be a hero** (holding a signature item). If not, the command fails with
"*name* is not a hero (hold a signature item)."

## Useful vanilla commands

LDFL works with ordinary Minecraft commands for setting up tests and games.

```
# Every signature item
/give @p ldfl:bulwark_shield
/give @p ldfl:bulwark_maul
/give @p ldfl:cragfist
/give @p ldfl:bolt_driver
/give @p ldfl:tinkers_wrench
/give @p ldfl:longshot_rifle
/give @p ldfl:solace_staff
/give @p ldfl:tuning_rod

# A practice target that stands still and takes a beating
/summon zombie ~ ~ ~6 {NoAI:1b,PersistenceRequired:1b,ArmorItems:[{},{},{},{id:"minecraft:iron_helmet",Count:1b}]}

# Clear LDFL's own effects
/effect @p clear
```

For teams and PvP, see [Allies, enemies and PvP](../guide/allies-and-enemies.md#setting-up-teams).
