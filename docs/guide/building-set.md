# Building set

LDFL comes with a small set of **futuristic decor blocks** for building arenas, bases and
training rooms, plus the **Mending Station**, a floor pad that heals. Every block is craftable and
appears in the **Frontline Heroes** creative tab.

![Every decor block in a row: light, dark and accent hull panels, lumen panel, grid floor, vent panel, objective pad, both stairs and the Mending Station](../assets/images/decor-lineup.jpg)

## The blocks

| Block | | Notes |
| --- | --- | --- |
| **Light Hull Panel** | <img class="icon" src="../../assets/images/icons/inv_hull_panel_light.png" alt=""> | The base wall block. Crafted from stone and iron, and the starting point for the rest of the set |
| **Dark Hull Panel** | <img class="icon" src="../../assets/images/icons/inv_hull_panel_dark.png" alt=""> | Darker panelling for floors, trims and contrast |
| **Accent Hull Panel** | <img class="icon" src="../../assets/images/icons/inv_hull_panel_accent.png" alt=""> | A light panel with an orange stripe, for wall bands and markings |
| **Lumen Panel** | <img class="icon" src="../../assets/images/icons/inv_lumen_panel.png" alt=""> | A glowing light panel: a full-strength light source |
| **Grid Floor** | <img class="icon" src="../../assets/images/icons/inv_grid_floor.png" alt=""> | Dark floor plating with a cyan grid |
| **Vent Panel** | <img class="icon" src="../../assets/images/icons/inv_vent_panel.png" alt=""> | A slatted vent, for walls and machinery |
| **Objective Pad** | <img class="icon" src="../../assets/images/icons/inv_objective_pad.png" alt=""> | A floor marker with cyan rings, for capture points and spawn pads |
| **Light Hull Panel Stairs** | <img class="icon" src="../../assets/images/icons/inv_hull_panel_light_stairs.png" alt=""> | Stairs in the light panel |
| **Dark Hull Panel Stairs** | <img class="icon" src="../../assets/images/icons/inv_hull_panel_dark_stairs.png" alt=""> | Stairs in the dark panel |
| **Mending Station** | <img class="icon" src="../../assets/images/icons/mending_station.png" alt=""> | A healing floor pad. See below |

All of them are mined with a pickaxe.

## Mending Station

A low pad with a cross on top. **Step on it while hurt** and it heals you **8 health** (four
hearts). Then the cross goes dark while it recharges for **10 seconds**, and lights up again with
a soft chime when it is ready.

![Two Mending Stations: the one on the right is charged (green cross), the one on the left is recharging (grey)](../assets/images/mending-station.jpg)

- It works for **any player**, hero or not.
- It only fires if you are actually hurt, so walking over it at full health does not waste it.
- You can see from across the room whether a station is ready: green means go.

## Day and night

![The arena by day](../assets/images/arena-day.jpg)

![The same arena at night, lit only by lumen panels and Mending Stations](../assets/images/arena-night.jpg)

## Recipes

<!-- recipe:hull_panel_light -->
<div class="recipe">
<div class="grid3"><span class="cell">Stone</span><span class="cell">Stone</span><span class="cell">Stone</span><span class="cell">Stone</span><span class="cell">Iron Ingot</span><span class="cell">Stone</span><span class="cell">Stone</span><span class="cell">Stone</span><span class="cell">Stone</span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="ldfl:hull_panel_light" title="ldfl:hull_panel_light"><span class="count">8</span></span>
</div>
<!-- /recipe -->
<!-- recipe:hull_panel_dark -->
<div class="recipe">
<div class="grid3"><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell">Coal</span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="ldfl:hull_panel_dark" title="ldfl:hull_panel_dark"><span class="count">8</span></span>
</div>
<!-- /recipe -->
<!-- recipe:hull_panel_accent -->
<div class="recipe">
<div class="grid3"><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell">Orange Dye</span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_hull_panel_accent.png" alt="ldfl:hull_panel_accent" title="ldfl:hull_panel_accent"><span class="count">8</span></span>
</div>
<!-- /recipe -->
<!-- recipe:lumen_panel -->
<div class="recipe">
<div class="grid3"><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell">Glowstone</span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_lumen_panel.png" alt="ldfl:lumen_panel" title="ldfl:lumen_panel"><span class="count">4</span></span>
</div>
<!-- /recipe -->
<!-- recipe:grid_floor -->
<div class="recipe">
<div class="grid3"><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span><span class="cell"></span><span class="cell"></span><span class="cell"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_grid_floor.png" alt="ldfl:grid_floor" title="ldfl:grid_floor"><span class="count">4</span></span>
</div>
<!-- /recipe -->
<!-- recipe:vent_panel -->
<div class="recipe">
<div class="grid3"><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell">Iron Bars</span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_vent_panel.png" alt="ldfl:vent_panel" title="ldfl:vent_panel"><span class="count">4</span></span>
</div>
<!-- /recipe -->
<!-- recipe:objective_pad -->
<div class="recipe">
<div class="grid3"><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_lumen_panel.png" alt="Lumen Panel" title="Lumen Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_objective_pad.png" alt="ldfl:objective_pad" title="ldfl:objective_pad"><span class="count">4</span></span>
</div>
<!-- /recipe -->
<!-- recipe:hull_panel_light_stairs -->
<div class="recipe">
<div class="grid3"><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_hull_panel_light_stairs.png" alt="ldfl:hull_panel_light_stairs" title="ldfl:hull_panel_light_stairs"><span class="count">4</span></span>
</div>
<!-- /recipe -->
<!-- recipe:hull_panel_dark_stairs -->
<div class="recipe">
<div class="grid3"><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_dark.png" alt="Dark Hull Panel" title="Dark Hull Panel"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/inv_hull_panel_dark_stairs.png" alt="ldfl:hull_panel_dark_stairs" title="ldfl:hull_panel_dark_stairs"><span class="count">4</span></span>
</div>
<!-- /recipe -->
<!-- recipe:mending_station -->
<div class="recipe">
<div class="grid3"><span class="cell">Redstone</span><span class="cell">Glistering Melon</span><span class="cell">Redstone</span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"><img src="../../assets/images/icons/inv_hull_panel_light.png" alt="Light Hull Panel" title="Light Hull Panel"></span><span class="cell"></span><span class="cell"></span><span class="cell"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/mending_station.png" alt="ldfl:mending_station" title="ldfl:mending_station"></span>
</div>
<!-- /recipe -->

Everything starts from **Light Hull Panels** (eight stone around an iron ingot). Dark panels add
coal, accent panels add orange dye, and the rest are built from those.
