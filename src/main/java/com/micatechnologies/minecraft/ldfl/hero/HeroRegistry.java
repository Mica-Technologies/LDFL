package com.micatechnologies.minecraft.ldfl.hero;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Every hero LDFL knows, by id. Filled once during preInit by {@link Heroes#init()}; read-only
 * after that, so it is safe to read from both the server and client threads.
 */
public final class HeroRegistry {

    private static final Map<String, HeroDefinition> HEROES = new LinkedHashMap<>();

    private HeroRegistry() {
        throw new AssertionError("No instances.");
    }

    static HeroDefinition register(HeroDefinition def) {
        String id = def.spec().id();
        if (HEROES.putIfAbsent(id, def) != null) {
            throw new IllegalStateException("Duplicate hero id: " + id);
        }
        return def;
    }

    /** The hero with this id, or {@code null} for an unknown or null id. */
    public static HeroDefinition get(String id) {
        return id == null ? null : HEROES.get(id);
    }

    public static Collection<HeroDefinition> all() {
        return Collections.unmodifiableCollection(HEROES.values());
    }
}
