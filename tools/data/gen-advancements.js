// Generate LDFL's advancements: recipe-book unlocks for every recipe, and the small hero tree.
//   node tools/data/gen-advancements.js
// Recipe unlocks follow vanilla's pattern: a recipe appears in the recipe book once the player
// holds its gating ingredient (or already knows the recipe).
const fs = require('fs');
const path = require('path');

const root = path.resolve(__dirname, '..', '..');
const adv = path.join(root, 'src/main/resources/assets/ldfl/advancements');
const write = (rel, obj) => {
  const file = path.join(adv, rel);
  fs.mkdirSync(path.dirname(file), { recursive: true });
  fs.writeFileSync(file, JSON.stringify(obj, null, 2) + '\n');
};

// recipe -> the ingredient that unlocks it in the recipe book
const gates = {
  bulwark_shield: 'minecraft:shield',
  bulwark_maul: 'minecraft:iron_block',
  cragfist: 'minecraft:iron_block',
  bolt_driver: 'minecraft:bow',
  tinkers_wrench: 'minecraft:iron_ingot',
  longshot_rifle: 'minecraft:bow',
  solace_staff: 'minecraft:emerald',
  tuning_rod: 'minecraft:noteblock',
  hull_panel_light: 'minecraft:iron_ingot',
  hull_panel_dark: 'ldfl:hull_panel_light',
  hull_panel_accent: 'ldfl:hull_panel_light',
  hull_panel_light_stairs: 'ldfl:hull_panel_light',
  hull_panel_dark_stairs: 'ldfl:hull_panel_dark',
  lumen_panel: 'minecraft:glowstone',
  grid_floor: 'ldfl:hull_panel_dark',
  vent_panel: 'ldfl:hull_panel_dark',
  objective_pad: 'ldfl:lumen_panel',
  mending_station: 'minecraft:speckled_melon',
};

const recipesDir = path.join(root, 'src/main/resources/assets/ldfl/recipes');
const recipes = fs.readdirSync(recipesDir).map((f) => f.replace(/\.json$/, '')).sort();
const missing = recipes.filter((r) => !gates[r]);
if (missing.length) {
  throw new Error('No unlock ingredient for recipe(s): ' + missing.join(', '));
}

for (const r of recipes) {
  write(`recipes/${r}.json`, {
    parent: 'minecraft:recipes/root',
    rewards: { recipes: [`ldfl:${r}`] },
    criteria: {
      has_ingredient: { trigger: 'minecraft:inventory_changed', conditions: { items: [{ item: gates[r] }] } },
      has_the_recipe: { trigger: 'minecraft:recipe_unlocked', conditions: { recipe: `ldfl:${r}` } },
    },
    requirements: [['has_ingredient', 'has_the_recipe']],
  });
}

// --- Hero tree -----------------------------------------------------------------------------
const has = (item) => ({ trigger: 'minecraft:inventory_changed', conditions: { items: [{ item }] } });
const display = (key, icon, frame = 'task', extra = {}) => ({
  icon: { item: icon },
  title: { translate: `advancements.ldfl.${key}.title` },
  description: { translate: `advancements.ldfl.${key}.description` },
  frame,
  ...extra,
});
const anyOf = (items) => {
  const criteria = {};
  items.forEach((i) => { criteria[i.split(':')[1]] = has(i); });
  return { criteria, requirements: [Object.keys(criteria)] };
};

const signatures = ['ldfl:bulwark_shield', 'ldfl:bulwark_maul', 'ldfl:cragfist', 'ldfl:bolt_driver',
  'ldfl:tinkers_wrench', 'ldfl:longshot_rifle', 'ldfl:solace_staff', 'ldfl:tuning_rod'];

write('heroes/root.json', {
  display: display('root', 'ldfl:bulwark_shield', 'task', {
    background: 'ldfl:textures/blocks/grid_floor.png', show_toast: false, announce_to_chat: false,
  }),
  ...anyOf(signatures),
});
write('heroes/tank.json', { parent: 'ldfl:heroes/root', display: display('tank', 'ldfl:bulwark_shield'),
  ...anyOf(['ldfl:bulwark_shield', 'ldfl:bulwark_maul', 'ldfl:cragfist']) });
write('heroes/damage.json', { parent: 'ldfl:heroes/root', display: display('damage', 'ldfl:longshot_rifle'),
  ...anyOf(['ldfl:bolt_driver', 'ldfl:tinkers_wrench', 'ldfl:longshot_rifle']) });
write('heroes/support.json', { parent: 'ldfl:heroes/root', display: display('support', 'ldfl:solace_staff'),
  ...anyOf(['ldfl:solace_staff', 'ldfl:tuning_rod']) });
write('heroes/mending_station.json', { parent: 'ldfl:heroes/support',
  display: display('mending_station', 'ldfl:mending_station'), ...anyOf(['ldfl:mending_station']) });

// Full roster: one signature item from each of the six heroes.
const heroes = {
  bulwark: ['ldfl:bulwark_shield', 'ldfl:bulwark_maul'], monolith: ['ldfl:cragfist'],
  forgewright: ['ldfl:bolt_driver', 'ldfl:tinkers_wrench'], kestrel: ['ldfl:longshot_rifle'],
  solace: ['ldfl:solace_staff'], chime: ['ldfl:tuning_rod'],
};
const rosterCriteria = {};
const rosterReq = [];
for (const [hero, items] of Object.entries(heroes)) {
  const group = [];
  items.forEach((i) => { const k = `${hero}_${i.split(':')[1]}`; rosterCriteria[k] = has(i); group.push(k); });
  rosterReq.push(group);
}
write('heroes/full_roster.json', { parent: 'ldfl:heroes/root',
  display: display('full_roster', 'ldfl:tuning_rod', 'challenge'),
  criteria: rosterCriteria, requirements: rosterReq });

console.log(`wrote ${recipes.length} recipe unlocks and 6 hero advancements`);
