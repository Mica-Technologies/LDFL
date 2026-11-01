"""Fill the crafting-grid blocks in the wiki from the mod's own recipe JSON.

    py tools/docs/fill_recipes.py          (Windows)
    python3 tools/docs/fill_recipes.py     (elsewhere)

A page asks for a recipe with a marker line:

    <!-- recipe:bulwark_shield -->

The script puts the grid after it, between the marker and a closing <!-- /recipe -->, and
replaces whatever was there on later runs, so it is safe to run any number of times. The
marker's indentation is kept, so markers inside content tabs work too.

Ingredient names come from NAMES below; LDFL ingredients and outputs are drawn with the icons
in docs/assets/images/icons/. Re-run after changing a recipe.
"""

import json
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
RECIPES = os.path.join(ROOT, "src", "main", "resources", "assets", "ldfl", "recipes")
DOCS = os.path.join(ROOT, "docs")
ICONS = os.path.join(DOCS, "assets", "images", "icons")

NAMES = {
    "minecraft:iron_ingot": "Iron Ingot",
    "minecraft:redstone": "Redstone",
    "minecraft:bow": "Bow",
    "minecraft:gold_ingot": "Gold Ingot",
    "minecraft:iron_block": "Iron Block",
    "minecraft:stick": "Stick",
    "minecraft:glass_pane": "Glass Pane",
    "minecraft:shield": "Shield",
    "minecraft:diamond": "Diamond",
    "minecraft:stone": "Stone",
    "minecraft:dye:14": "Orange Dye",
    "minecraft:coal": "Coal",
    "minecraft:glowstone": "Glowstone",
    "minecraft:speckled_melon": "Glistering Melon",
    "minecraft:emerald": "Emerald",
    "minecraft:noteblock": "Note Block",
    "minecraft:iron_bars": "Iron Bars",
    "ldfl:hull_panel_light": "Light Hull Panel",
    "ldfl:hull_panel_dark": "Dark Hull Panel",
    "ldfl:lumen_panel": "Lumen Panel",
}

MARKER = re.compile(r"^(?P<indent>[ \t]*)<!-- recipe:(?P<id>[a-z0-9_]+) -->[ \t]*$")
END = "<!-- /recipe -->"


def icon_for(item_id):
    """Icon file name for an LDFL item or block: the 3D inventory render if we have one."""
    name = item_id.split(":", 1)[1]
    for candidate in ("inv_" + name, name):
        if os.path.exists(os.path.join(ICONS, candidate + ".png")):
            return candidate
    return None


def grid_html(recipe_id, prefix):
    with open(os.path.join(RECIPES, recipe_id + ".json"), encoding="utf-8") as f:
        recipe = json.load(f)
    rows = [row.ljust(3) for row in recipe["pattern"]]
    rows += ["   "] * (3 - len(rows))
    cells = []
    for row in rows:
        for ch in row:
            if ch == " ":
                cells.append('<span class="cell"></span>')
                continue
            key = recipe["key"][ch]
            item = key["item"]
            full = item + (":%d" % key["data"] if key.get("data") else "")
            label = NAMES[full]
            icon = icon_for(item) if item.startswith("ldfl:") else None
            if icon:
                cells.append('<span class="cell"><img src="%sassets/images/icons/%s.png" alt="%s" '
                             'title="%s"></span>' % (prefix, icon, label, label))
            else:
                cells.append('<span class="cell">%s</span>' % label)
    result = recipe["result"]
    count = result.get("count", 1)
    out_icon = icon_for(result["item"])
    count_html = '<span class="count">%d</span>' % count if count > 1 else ""
    return [
        '<div class="recipe">',
        '<div class="grid3">%s</div>' % "".join(cells),
        '<span class="arrow">→</span>',
        '<span class="out"><img src="%sassets/images/icons/%s.png" alt="%s" title="%s">%s</span>'
        % (prefix, out_icon, result["item"], result["item"], count_html),
        "</div>",
    ]


def page_prefix(path):
    """Relative path from a built page back to the site root (pages build to <name>/index.html)."""
    rel = os.path.relpath(path, DOCS).replace(os.sep, "/")
    depth = rel.count("/") + (0 if rel.endswith("index.md") else 1)
    return "../" * depth


def fill(path):
    with open(path, encoding="utf-8") as f:
        lines = f.read().split("\n")
    out, i, changed = [], 0, 0
    while i < len(lines):
        m = MARKER.match(lines[i])
        if not m:
            out.append(lines[i])
            i += 1
            continue
        indent = m.group("indent")
        out.append(lines[i])
        # Skip a previous fill, if any.
        j = i + 1
        while j < len(lines) and lines[j].strip() != END and not MARKER.match(lines[j]) \
                and lines[j].strip().startswith("<"):
            j += 1
        if j < len(lines) and lines[j].strip() == END:
            j += 1
        else:
            j = i + 1
        out.extend(indent + l for l in grid_html(m.group("id"), page_prefix(path)))
        out.append(indent + END)
        changed += 1
        i = j
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(out))
    return changed


def main():
    total = 0
    for dirpath, _, files in os.walk(DOCS):
        if "AGENT-PLANS" in dirpath:
            continue
        for name in files:
            if name.endswith(".md"):
                n = fill(os.path.join(dirpath, name))
                if n:
                    print("%-45s %d recipe(s)" % (os.path.relpath(os.path.join(dirpath, name), ROOT), n))
                    total += n
    print("filled %d recipe grid(s)" % total)
    return 0


if __name__ == "__main__":
    sys.exit(main())
