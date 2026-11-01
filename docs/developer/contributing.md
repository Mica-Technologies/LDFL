# Contributing

Bug reports, ideas, fixes and wiki corrections are all welcome.

## Reporting bugs and requesting features

Use [GitHub Issues](https://github.com/Mica-Technologies/LDFL/issues). For a bug, include the LDFL
and Forge versions, the steps to reproduce it, and `logs/latest.log` (plus the crash report, if
there was one). See [Troubleshooting](../help/troubleshooting.md#reporting-a-bug).

## Changing the code

1. Fork the repository and create a branch.
2. Follow the rules in [Architecture](architecture.md#rules-the-code-depends-on). The two that
   catch people out: no Minecraft types in `hero.core`, and nothing client-only in common code.
3. Never hard-code the mod id, name or version: use `LdflConstants`.
4. Registry names use the `ldfl:` prefix; lang keys follow `ldfl.hero.<id>`, `ldfl.role.<id>`
   and `ldfl.ability.<hero>.<ability>`.
5. `./gradlew build` must pass, and check the game actually starts (`runClient`, and `runServer`
   for anything touching common code).
6. Open a pull request. The PR workflow builds, tests and boots a dedicated server.

## Original content only

LDFL is a public, MIT-licensed, **original** work, inspired by the hero-shooter genre in general.
Contributions must keep it that way:

- No names of heroes, abilities, weapons, maps, items or factions from any existing game, in code,
  assets, lang files, docs or commit messages.
- No art, textures, models, sounds or UI layouts copied, traced or derived from any game.
- When unsure whether a name is taken, pick another one.

## Editing this wiki

Every page is a Markdown file under
[`docs/`](https://github.com/Mica-Technologies/LDFL/tree/main/docs). The :material-pencil: icon at
the top of each page opens it in GitHub's editor. Recipe grids are generated from the recipe JSON:
write `<!-- recipe:item_id -->` in a page and run `py tools/docs/fill_recipes.py` to fill it in.
See [Building from source](building-from-source.md#previewing-this-wiki) to preview locally.

## License

By contributing you agree that your contribution is licensed under the
[MIT License](https://github.com/Mica-Technologies/LDFL/blob/main/LICENSE).
