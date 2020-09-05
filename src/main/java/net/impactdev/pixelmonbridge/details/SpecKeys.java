package net.impactdev.pixelmonbridge.details;

import com.google.gson.reflect.TypeToken;
import net.impactdev.pixelmonbridge.details.components.Moves;

public class SpecKeys {

    private static final TypeToken<Integer> INT_TYPE = new TypeToken<Integer>(){};
    private static final TypeToken<String> STRING_TYPE = new TypeToken<String>(){};
    private static final TypeToken<Boolean> BOOLEAN_TYPE = new TypeToken<Boolean>(){};

    public static final SpecKey<String> SPECIES = SpecKey.builder()
            .type(STRING_TYPE)
            .name("Species")
            .query(Query.of("species"))
            .build();

    public static final SpecKey<Boolean> SHINY = SpecKey.builder()
            .type(BOOLEAN_TYPE)
            .name("Shiny State")
            .query(Query.of("shiny"))
            .build();

    public static final SpecKey<Integer> FORM = SpecKey.builder()
            .type(INT_TYPE)
            .name("Form")
            .query(Query.of("form"))
            .build();

    public static final SpecKey<Integer> LEVEL = SpecKey.builder()
            .type(INT_TYPE)
            .name("Level")
            .query(Query.of("level", "value"))
            .build();

    public static final SpecKey<Integer> LEVEL_EXPERIENCE = SpecKey.builder()
            .type(INT_TYPE)
            .name("Experience")
            .query(Query.of("level", "experience"))
            .build();

    public static final SpecKey<Integer> GENDER = SpecKey.builder()
            .type(INT_TYPE)
            .name("Gender")
            .query(Query.of("gender"))
            .build();

    public static final SpecKey<String> NATURE = SpecKey.builder()
            .type(STRING_TYPE)
            .name("Nature")
            .query(Query.of("nature"))
            .build();

    public static final SpecKey<String> ABILITY = SpecKey.builder()
            .type(STRING_TYPE)
            .name("Ability")
            .query(Query.of("ability.value"))
            .build();

    public static final SpecKey<Integer> ABILITY_SLOT = SpecKey.builder()
            .type(INT_TYPE)
            .name("Ability Slot")
            .query(Query.of("ability.slot"))
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

    public static final SpecKey<Moves> MOVESET = SpecKey.builder()
            .type(new TypeToken<Moves>(){})
            .name("Moveset")
            .query(Query.of("moveset"))
            .build();

    // -------------------------------------------------------------------------------------
    //
    //  Stats
    //
    // -------------------------------------------------------------------------------------

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
            .build();
    public static final SpecKey<Integer> IV_ATK = SpecKey.builder()
            .type(INT_TYPE)
            .name("Attack IV")
            .query(Query.of("stats", "ivs", "attack"))
            .build();
    public static final SpecKey<Integer> IV_DEF = SpecKey.builder()
            .type(INT_TYPE)
            .name("Defence IV")
            .query(Query.of("stats", "ivs", "defence"))
            .build();
    public static final SpecKey<Integer> IV_SPATK = SpecKey.builder()
            .type(INT_TYPE)
            .name("Special Attack IV")
            .query(Query.of("stats", "ivs", "spatk"))
            .build();
    public static final SpecKey<Integer> IV_SPDEF = SpecKey.builder()
            .type(INT_TYPE)
            .name("Special Defence IV")
            .query(Query.of("stats", "ivs", "spdef"))
            .build();
    public static final SpecKey<Integer> IV_SPEED = SpecKey.builder()
            .type(INT_TYPE)
            .name("Speed IV")
            .query(Query.of("stats", "ivs", "speed"))
            .build();



}
