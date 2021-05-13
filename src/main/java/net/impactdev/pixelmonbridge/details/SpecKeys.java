package net.impactdev.pixelmonbridge.details;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import net.impactdev.pixelmonbridge.ImpactDevPokemon;
import net.impactdev.pixelmonbridge.details.components.Ability;
import net.impactdev.pixelmonbridge.details.components.EggInfo;
import net.impactdev.pixelmonbridge.details.components.Level;
import net.impactdev.pixelmonbridge.details.components.Marking;
import net.impactdev.pixelmonbridge.details.components.Moves;
import net.impactdev.pixelmonbridge.details.components.Nature;
import net.impactdev.pixelmonbridge.details.components.Pokerus;
import net.impactdev.pixelmonbridge.details.components.Trainer;
import net.impactdev.pixelmonbridge.details.components.generic.ItemStackWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.JSONWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.NBTWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static net.impactdev.pixelmonbridge.details.SpecKey.*;

public class SpecKeys {

    public static final SpecKey<UUID> ID = SpecKey.builder()
            .type(new TypeToken<UUID>(){})
            .name("UUID")
            .query(Query.of("id"))
            .priority(200)
            .build();

    public static final SpecKey<String> SPECIES = SpecKey.builder()
            .type(STRING_TYPE)
            .name("Species")
            .query(Query.of("species"))
            .priority(100)
            .build();

    public static final SpecKey<Boolean> SHINY = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("Shiny State")
            .query(Query.of("shiny"))
            .build();

    public static final SpecKey<Integer> FORM = SpecKey.builder()
            .type(new TypeToken<Integer>(){})
            .name("Form")
            .query(Query.of("form"))
            .priority(10)
            .build();

    public static final SpecKey<Level> LEVEL = SpecKey.builder()
            .type(new TypeToken<Level>(){})
            .name("Level")
            .query(Query.of("level"))
            .priority(5)
            .build();

    public static final SpecKey<Integer> GENDER = SpecKey.builder()
            .type(INT_TYPE)
            .name("Gender")
            .query(Query.of("gender"))
            .build();

    public static final SpecKey<Nature> NATURE = SpecKey.builder()
            .type(new TypeToken<Nature>(){})
            .name("Nature")
            .query(Query.of("nature"))
            .priority(3)
            .build();

    public static final SpecKey<Ability> ABILITY = SpecKey.builder()
            .type(new TypeToken<Ability>(){})
            .name("Ability")
            .query(Query.of("ability"))
            .build();

    public static final SpecKey<Integer> FRIENDSHIP = SpecKey.builder()
            .type(INT_TYPE)
            .name("Friendship")
            .query(Query.of("friendship"))
            .build();

    public static final SpecKey<Integer> GROWTH = SpecKey.builder()
            .type(INT_TYPE)
            .name("Growth")
            .query(Query.of("growth"))
            .build();

    public static final SpecKey<String> NICKNAME = SpecKey.builder()
            .type(STRING_TYPE)
            .name("Nickname")
            .query(Query.of("nickname"))
            .build();

    public static final SpecKey<String> TEXTURE = SpecKey.builder()
            .type(STRING_TYPE)
            .name("Custom Texture")
            .query(Query.of("texture"))
            .build();

    /** Generations specific texture key (Reforged removed this) */
    public static final SpecKey<Integer> SPECIAL_TEXTURE = SpecKey.builder()
            .type(INT_TYPE)
            .name("Special Texture")
            .query(Query.of("special-texture"))
            .build();

    public static final SpecKey<Integer> POKEBALL = SpecKey.builder()
            .type(INT_TYPE)
            .name("Pokeball")
            .query(Query.of("pokeball"))
            .build();

    public static final SpecKey<Trainer> TRAINER = SpecKey.builder()
            .type(new TypeToken<Trainer>(){})
            .name("Trainer")
            .query(Query.of("trainer"))
            .build();

    public static final SpecKey<EggInfo> EGG_INFO = SpecKey.builder()
            .type(new TypeToken<EggInfo>(){})
            .name("Egg")
            .query(Query.of("egg-info"))
            .build();

    public static final SpecKey<Pokerus> POKERUS = SpecKey.builder()
            .type(new TypeToken<Pokerus>(){})
            .name("Pokerus")
            .query(Query.of("pokerus"))
            .build();

    public static final SpecKey<Moves> MOVESET = SpecKey.builder()
            .type(new TypeToken<Moves>(){})
            .name("Moveset")
            .query(Query.of("moves", "moveset"))
            .build();

    public static final SpecKey<List<String>> SPEC_CREATION_FLAGS = SpecKey.builder()
            .type(new TypeToken<List<String>>(){})
            .name("Spec Creation Flags")
            .query(Query.of("spec-flags"))
            .build();

    public static final SpecKey<List<Integer>> RELEARNABLE_MOVES = SpecKey.builder()
            .type(new TypeToken<List<Integer>>(){})
            .name("Relearnable Moves")
            .query(Query.of("moves", "relearnable"))
            .build();

    public static final SpecKey<NBTWrapper> EXTRA_DATA = SpecKey.builder()
            .type(new TypeToken<NBTWrapper>(){})
            .name("Extra Data")
            .query(Query.of("extra-data"))
            .build();

    public static final SpecKey<ItemStackWrapper> HELD_ITEM = SpecKey.builder()
            .type(new TypeToken<ItemStackWrapper>(){})
            .name("Held Item")
            .query(Query.of("held-item"))
            .build();

    public static final SpecKey<Integer> STATUS = SpecKey.builder()
            .type(INT_TYPE)
            .name("Persistant Status")
            .query(Query.of("status"))
            .build();

    public static final SpecKey<Integer> DYNAMAX_LEVEL = SpecKey.builder()
            .type(INT_TYPE)
            .name("Dynamax Level")
            .query(Query.of("dynamax-level"))
            .build();

    public static final SpecKey<List<ImpactDevPokemon<?>>> EMBEDDED_POKEMON = SpecKey.builder()
            .type(new TypeToken<List<ImpactDevPokemon<?>>>(){})
            .name("Embedded Pokemon")
            .priority(-1)
            .query(Query.of("embedded-pokemon"))
            .build();

    public static final SpecKey<List<Marking>> MARKS = SpecKey.builder()
            .type(new TypeToken<List<Marking>>(){})
            .name("Marks")
            .query(Query.of("marks"))
            .build();

    public static final SpecKey<List<Integer>> RIBBONS = SpecKey.builder()
            .type(new TypeToken<List<Integer>>(){})
            .name("Ribbons")
            .query(Query.of("ribbons"))
            .build();

    // -------------------------------------------------------------------------------------
    //
    //  Extra Stats
    //
    // -------------------------------------------------------------------------------------

    public static final SpecKey<Integer> MEW_CLONES = SpecKey.builder()
            .type(INT_TYPE)
            .name("Mew - Number of Clones")
            .query(Query.of("extra", "mew", "clones"))
            .build();

    public static final SpecKey<Integer> LAKE_TRIO_ENCHANTS = SpecKey.builder()
            .type(INT_TYPE)
            .name("Lake Trio - Number of Enchants")
            .query(Query.of("extra", "lake-trio", "enchants"))
            .build();

    public static final SpecKey<Integer> MELTAN_ORES_SMELTED = SpecKey.builder()
            .type(INT_TYPE)
            .name("Meltan - Ores Smelted")
            .query(Query.of("extra", "meltan", "ores-smelted"))
            .build();

    public static final SpecKey<Byte> MAREEP_WOOL_GROWTH = SpecKey.builder()
            .type(new TypeToken<Byte>(){})
            .name("Mareep - Wool Growth Stage")
            .query(Query.of("extra", "mareep", "wool-growth-stage"))
            .build();

    public static final SpecKey<Byte> MINIOR_COLOR = SpecKey.builder()
            .type(new TypeToken<Byte>(){})
            .name("Minior - Color")
            .query(Query.of("extra", "minior", "color"))
            .build();

    public static final SpecKey<Integer> LIGHT_TRIO_WORMHOLES = SpecKey.builder()
            .type(INT_TYPE)
            .name("Light Trio - Number of Wormholes")
            .query(Query.of("extra", "light-trio", "wormholes"))
            .build();

    public static final SpecKey<Integer> MELOETTA_ACTIVATIONS = SpecKey.builder()
            .type(INT_TYPE)
            .name("Meloetta - Abundant Activations")
            .query(Query.of("extra", "meloetta", "activations"))
            .build();

    public static final SpecKey<Boolean> CAN_GMAX = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("GMax - Allowed State")
            .query(Query.of("can-gmax"))
            .build();

    // -------------------------------------------------------------------------------------
    //
    //  Stats
    //
    // -------------------------------------------------------------------------------------

    /** The current amount of HP this pokemon has */
    public static final SpecKey<Integer> HP = SpecKey.builder()
            .type(INT_TYPE)
            .name("Health")
            .query(Query.of("hp"))
            .build();

    public static final SpecKey<Integer> EV_HP = SpecKey.builder()
            .type(INT_TYPE)
            .name("HP EV")
            .query(Query.of("stats", "evs", "hp"))
            .build();
    public static final SpecKey<Integer> EV_ATK = SpecKey.builder()
            .type(INT_TYPE)
            .name("Attack EV")
            .query(Query.of("stats", "evs", "attack"))
            .build();
    public static final SpecKey<Integer> EV_DEF = SpecKey.builder()
            .type(INT_TYPE)
            .name("Defence EV")
            .query(Query.of("stats", "evs", "defence"))
            .build();
    public static final SpecKey<Integer> EV_SPATK = SpecKey.builder()
            .type(INT_TYPE)
            .name("Special Attack EV")
            .query(Query.of("stats", "evs", "spatk"))
            .build();
    public static final SpecKey<Integer> EV_SPDEF = SpecKey.builder()
            .type(INT_TYPE)
            .name("Special Defence EV")
            .query(Query.of("stats", "evs", "spdef"))
            .build();
    public static final SpecKey<Integer> EV_SPEED = SpecKey.builder()
            .type(INT_TYPE)
            .name("Speed EV")
            .query(Query.of("stats", "evs", "speed"))
            .build();

    public static final SpecKey<Integer> IV_HP = SpecKey.builder()
            .type(INT_TYPE)
            .name("HP IV")
            .query(Query.of("stats", "ivs", "hp"))
            .priority(5)
            .build();
    public static final SpecKey<Integer> IV_ATK = SpecKey.builder()
            .type(INT_TYPE)
            .name("Attack IV")
            .query(Query.of("stats", "ivs", "attack"))
            .priority(5)
            .build();
    public static final SpecKey<Integer> IV_DEF = SpecKey.builder()
            .type(INT_TYPE)
            .name("Defence IV")
            .query(Query.of("stats", "ivs", "defence"))
            .priority(5)
            .build();
    public static final SpecKey<Integer> IV_SPATK = SpecKey.builder()
            .type(INT_TYPE)
            .name("Special Attack IV")
            .query(Query.of("stats", "ivs", "spatk"))
            .priority(5)
            .build();
    public static final SpecKey<Integer> IV_SPDEF = SpecKey.builder()
            .type(INT_TYPE)
            .name("Special Defence IV")
            .query(Query.of("stats", "ivs", "spdef"))
            .priority(5)
            .build();
    public static final SpecKey<Integer> IV_SPEED = SpecKey.builder()
            .type(INT_TYPE)
            .name("Speed IV")
            .query(Query.of("stats", "ivs", "speed"))
            .priority(5)
            .build();

    public static final SpecKey<Boolean> HYPER_HP = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("Hyper HP IV")
            .query(Query.of("stats", "ivs", "hyper", "hp"))
            .build();
    public static final SpecKey<Boolean> HYPER_ATTACK = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("Hyper Attack IV")
            .query(Query.of("stats", "ivs", "hyper", "attack"))
            .build();
    public static final SpecKey<Boolean> HYPER_DEFENCE = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("Hyper Defence IV")
            .query(Query.of("stats", "ivs", "hyper", "defence"))
            .build();
    public static final SpecKey<Boolean> HYPER_SPECIAL_ATTACK = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("Hyper Special Attack IV")
            .query(Query.of("stats", "ivs", "hyper", "special-attack"))
            .build();
    public static final SpecKey<Boolean> HYPER_SPECIAL_DEFENCE = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("Hyper Special Defence IV")
            .query(Query.of("stats", "ivs", "hyper", "special-defence"))
            .build();
    public static final SpecKey<Boolean> HYPER_SPEED = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("Hyper Speed IV")
            .query(Query.of("stats", "ivs", "hyper", "speed"))
            .build();

    // -------------------------------------------------------------------------------------
    //
    //  Invalid Data Keys
    //
    //  AKA for data that is missing content
    //
    // -------------------------------------------------------------------------------------

    /** This key is for a generations pokemon that cannot use reforged data */
    public static final SpecKey<JSONWrapper> REFORGED_DATA = SpecKey.builder()
            .type(new TypeToken<JSONWrapper>(){})
            .name("Reforged Data")
            .query(Query.of("incompatible", "reforged"))
            .build();

    /** This key is for a generations pokemon that cannot use reforged data */
    public static final SpecKey<JSONWrapper> GENERATIONS_DATA = SpecKey.builder()
            .type(new TypeToken<JSONWrapper>(){})
            .name("Generations Data")
            .query(Query.of("incompatible", "generations"))
            .build();

    private static final List<SpecKey<?>> KEYS;
    private static final int SIZE;

    static {
        List<SpecKey<?>> keys = new LinkedList<>();
        Field[] values = SpecKeys.class.getFields();
        int i = 0;

        for (Field f : values) {
            // ignore non-static fields
            if (!Modifier.isStatic(f.getModifiers())) {
                continue;
            }

            // ignore fields that aren't spec keys
            if (!SpecKey.class.equals(f.getType())) {
                continue;
            }

            try {
                // get the key instance
                SpecKey<?> key = (SpecKey<?>) f.get(null);

                // add the key to the return map
                keys.add(key);
            } catch (Exception e) {
                throw new RuntimeException("Exception processing field: " + f, e);
            }
        }

        KEYS = ImmutableList.copyOf(keys);
        SIZE = i;
    }

    public static List<SpecKey<?>> getKeys() {
        return KEYS;
    }

    public static int getSize() {
        return SIZE;
    }
}
