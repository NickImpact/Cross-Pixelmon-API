package net.impactdev.pixelmonbridge.reforged;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.InitializeCategory;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.ExtraStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MeltanStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MiniorStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.ShearableStats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonForms;
import com.pixelmonmod.pixelmon.api.registries.PixelmonRegistries;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.util.helpers.SpeciesHelper;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import com.pixelmonmod.pixelmon.battles.status.NoStatus;
import com.pixelmonmod.pixelmon.enums.EnumRibbonType;
import net.impactdev.pixelmonbridge.ImpactDevPokemon;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.PixelmonSource;
import net.impactdev.pixelmonbridge.details.Query;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;
import net.impactdev.pixelmonbridge.details.components.EggInfo;
import net.impactdev.pixelmonbridge.details.components.Level;
import net.impactdev.pixelmonbridge.details.components.Marking;
import net.impactdev.pixelmonbridge.details.components.Moves;
import net.impactdev.pixelmonbridge.details.components.Nature;
import net.impactdev.pixelmonbridge.details.components.Pokerus;
import net.impactdev.pixelmonbridge.details.components.Resource;
import net.impactdev.pixelmonbridge.details.components.Trainer;
import net.impactdev.pixelmonbridge.details.components.generic.ItemStackWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.JSONWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.NBTWrapper;
import net.impactdev.pixelmonbridge.reforged.writer.ReforgedSpecKeyWriter;
import net.minecraft.nbt.CompoundNBT;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReforgedPokemon implements ImpactDevPokemon<Pokemon> {

    private final ImmutableList<SpecKey<?>> UNSUPPORTED = ImmutableList.copyOf(Lists.newArrayList(
            SpecKeys.LIGHT_TRIO_WORMHOLES,
            SpecKeys.MELOETTA_ACTIVATIONS,
            SpecKeys.SPECIAL_TEXTURE
    ));

    private final Map<SpecKey<?>, Object> data = Maps.newTreeMap((k1, k2) -> {
        Query q1 = k1.getQuery();
        Query q2 = k2.getQuery();

        while(q1.getParts().get(0).compareTo(q2.getParts().get(0)) == 0) {
            q1 = q1.pop();
            q2 = q2.pop();

            if(q1.getParts().size() == 0 || q2.getParts().size() == 0) {
                return Integer.compare(q1.getParts().size(), q2.getParts().size());
            }
        }

        return q1.getParts().get(0).compareTo(q2.getParts().get(0));
    });

    private transient Pokemon pokemon;

    @Override
    public Pokemon getOrCreate() {
        Species species = this.get(SpecKeys.SPECIES)
                .map(PixelmonSpecies::fromName)
                .flatMap(RegistryValue::getValue)
                .orElseThrow(() -> new RuntimeException("Unknown species..."));
        return this.pokemon == null ? this.pokemon = writeAll(PokemonFactory.create(species)) : this.pokemon;
    }

    @Override
    public int version() {
        return 2;
    }

    @Override
    public <T> boolean supports(SpecKey<T> key) {
        return !this.UNSUPPORTED.contains(key);
    }

    @Override
    public <T> boolean offer(SpecKey<T> key, T data) {
        if(data == null) {
            return false;
        }

        if(SpecKeys.SPECIES.equals(key)) {
            if(!PixelmonSpecies.has((String) data)) {
                return false;
            }
        }

        if(this.supports(key)) {
            this.data.put(key, data);
        } else {
            this.data.put(SpecKeys.GENERATIONS_DATA, this.get(SpecKeys.GENERATIONS_DATA).orElseGet(JSONWrapper::new).append(key, data));
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(SpecKey<T> key) {
        return Optional.ofNullable((T) this.data.get(key));
    }

    @Override
    public Map<SpecKey<?>, Object> getAllDetails() {
        return this.data;
    }

    public static ReforgedPokemon from(Pokemon pokemon) {
        ReforgedPokemon result = new ReforgedPokemon();
        result.offer(SpecKeys.ID, pokemon.getUUID());
        result.offer(SpecKeys.SPECIES, pokemon.getSpecies().getName());
        result.offer(SpecKeys.FORM, pokemon.getForm().getName());
        result.offer(SpecKeys.SHINY, pokemon.isShiny());
        result.offer(SpecKeys.LEVEL, new Level(pokemon.getPokemonLevel(), pokemon.getExperience(), pokemon.doesLevel()));
        result.offer(SpecKeys.GENDER, pokemon.getGender().ordinal());
        result.offer(SpecKeys.NATURE, new Nature(pokemon.getBaseNature().name(), Optional.ofNullable(pokemon.getMintNature()).map(Enum::name).orElse(null)));
        result.offer(SpecKeys.ABILITY, pokemon.getAbilityName());
        result.offer(SpecKeys.FRIENDSHIP, pokemon.getFriendship());
        result.offer(SpecKeys.GROWTH, pokemon.getGrowth().index);
        result.offer(SpecKeys.NICKNAME, pokemon.getNickname());
        result.offer(SpecKeys.TEXTURE, new Resource(pokemon.getPalette().getTexture().toString()));

        Optional.ofNullable(pokemon.getBall())
                .ifPresent(ball -> {
                    result.offer(SpecKeys.POKEBALL, ball.getName());
                });

        // If a pokemon doesn't have an original trainer UUID, they shouldn't have an original trainer last known
        // name. The only reason the case of no UUID, last known name present is due to a Reforged bug. To avoid
        // user querying on this, with the potential to fail to even find a match, this will only add original
        // trainer information IF AND ONLY IF the UUID is present.
        Optional.ofNullable(pokemon.getOriginalTrainerUUID())
                .ifPresent(id -> {
                    result.offer(SpecKeys.TRAINER, new Trainer(id, pokemon.getOriginalTrainer()));
                });

        if(pokemon.isEgg()) {
            result.offer(SpecKeys.EGG_INFO, new EggInfo(pokemon.getEggCycles(), pokemon.getEggSteps()));
        }

        if(pokemon.getPokerus() != null) {
            result.offer(SpecKeys.POKERUS, new Pokerus(
                    PixelmonSource.Reforged,
                    pokemon.getPokerus().type.ordinal() == 0 ? 0 : pokemon.getPokerus().type.ordinal() + 0xA,
                    pokemon.getPokerus().secondsSinceInfection,
                    pokemon.getPokerus().announced)
            );
        }

        Moves moves = new Moves();
        for(Attack attack : pokemon.getMoveset().attacks) {
            if(attack == null) {
                continue;
            }

            moves.append(new Moves.Move(attack.getActualMove().getAttackName(), attack.pp, attack.ppLevel));
        }
        result.offer(SpecKeys.MOVESET, moves);

        try {
            Field flags = Pokemon.class.getDeclaredField("flags");
            flags.setAccessible(true);
            @SuppressWarnings("unchecked") List<String> f = ((ArrayList<String>)flags.get(pokemon));
            result.offer(SpecKeys.SPEC_CREATION_FLAGS, f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        result.offer(SpecKeys.RELEARNABLE_MOVES, pokemon.getRelearnableMoves().stream().map(ImmutableAttack::getAttackName).collect(Collectors.toList()));
        result.offer(SpecKeys.EXTRA_DATA, result.extraData(pokemon));
        result.offer(SpecKeys.HELD_ITEM, new ItemStackWrapper(pokemon.getHeldItem()));
        if(!pokemon.getStatus().equals(NoStatus.noStatus)) {
            result.offer(SpecKeys.STATUS, pokemon.getStatus().type.ordinal());
        }

        if(pokemon.getSpecies().is(PixelmonSpecies.MEW)) {
            result.offer(SpecKeys.MEW_CLONES, convert(MewStats.class, pokemon.getExtraStats()).numCloned);
        } else if(pokemon.getSpecies().is(PixelmonSpecies.AZELF, PixelmonSpecies.MESPRIT, PixelmonSpecies.UXIE)) {
            result.offer(SpecKeys.LAKE_TRIO_ENCHANTS, convert(LakeTrioStats.class, pokemon.getExtraStats()).numEnchanted);
        } else if(pokemon.getSpecies().is(PixelmonSpecies.MELTAN)) {
            result.offer(SpecKeys.MELTAN_ORES_SMELTED, convert(MeltanStats.class, pokemon.getExtraStats()).oresSmelted);
        } else if(pokemon.getSpecies().is(PixelmonSpecies.MAREEP)) {
            result.offer(SpecKeys.MAREEP_WOOL_GROWTH, convert(ShearableStats.class, pokemon.getExtraStats()).growthStage);
        } else if(pokemon.getSpecies().is(PixelmonSpecies.MINIOR)) {
            result.offer(SpecKeys.MINIOR_COLOR, convert(MiniorStats.class, pokemon.getExtraStats()).color);
        }

        result.offer(SpecKeys.HP, pokemon.getHealth());
        result.offer(SpecKeys.EV_HP, pokemon.getStats().getEVs().getStat(BattleStatsType.HP));
        result.offer(SpecKeys.EV_ATK, pokemon.getStats().getEVs().getStat(BattleStatsType.ATTACK));
        result.offer(SpecKeys.EV_DEF, pokemon.getStats().getEVs().getStat(BattleStatsType.DEFENSE));
        result.offer(SpecKeys.EV_SPATK, pokemon.getStats().getEVs().getStat(BattleStatsType.SPECIAL_ATTACK));
        result.offer(SpecKeys.EV_SPDEF, pokemon.getStats().getEVs().getStat(BattleStatsType.SPECIAL_DEFENSE));
        result.offer(SpecKeys.EV_SPEED, pokemon.getStats().getEVs().getStat(BattleStatsType.SPEED));
        result.offer(SpecKeys.IV_HP, pokemon.getStats().getIVs().getStat(BattleStatsType.HP));
        result.offer(SpecKeys.IV_ATK, pokemon.getStats().getIVs().getStat(BattleStatsType.ATTACK));
        result.offer(SpecKeys.IV_DEF, pokemon.getStats().getIVs().getStat(BattleStatsType.DEFENSE));
        result.offer(SpecKeys.IV_SPATK, pokemon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_ATTACK));
        result.offer(SpecKeys.IV_SPDEF, pokemon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_DEFENSE));
        result.offer(SpecKeys.IV_SPEED, pokemon.getStats().getIVs().getStat(BattleStatsType.SPEED));
        result.offer(SpecKeys.HYPER_HP, pokemon.getStats().getIVs().isHyperTrained(BattleStatsType.HP));
        result.offer(SpecKeys.HYPER_ATTACK, pokemon.getStats().getIVs().isHyperTrained(BattleStatsType.ATTACK));
        result.offer(SpecKeys.HYPER_DEFENCE, pokemon.getStats().getIVs().isHyperTrained(BattleStatsType.DEFENSE));
        result.offer(SpecKeys.HYPER_SPECIAL_ATTACK, pokemon.getStats().getIVs().isHyperTrained(BattleStatsType.SPECIAL_ATTACK));
        result.offer(SpecKeys.HYPER_SPECIAL_DEFENCE, pokemon.getStats().getIVs().isHyperTrained(BattleStatsType.SPECIAL_DEFENSE));
        result.offer(SpecKeys.HYPER_SPEED, pokemon.getStats().getIVs().isHyperTrained(BattleStatsType.SPEED));
        result.offer(SpecKeys.DYNAMAX_LEVEL, pokemon.getDynamaxLevel());
        result.offer(SpecKeys.CAN_GMAX, pokemon.canGigantamax());
        if(pokemon.getPersistentData().contains("FusedPokemon")) {
            result.offer(SpecKeys.EMBEDDED_POKEMON, Lists.newArrayList(result.getEmbeddedPokemon(pokemon)));
        }

        List<Marking> marks = Lists.newArrayList();
        List<Integer> ribbons = Lists.newArrayList();
        for(EnumRibbonType entry : pokemon.getRibbons()) {
            Marking.createFor(PixelmonSource.Reforged, entry.ordinal())
                    .map(marks::add)
                    .orElseGet(() -> {
                        ribbons.add(entry.ordinal());
                        return true;
                    });
        }
        result.offer(SpecKeys.MARKS, marks);
        result.offer(SpecKeys.RIBBONS, ribbons);

        CompoundNBT nbt = new CompoundNBT();
        pokemon.writeToNBT(nbt);
        nbt = nbt.getCompound("ForgeData");

        if(nbt.contains("bridge-api")) {
            CompoundNBT data = nbt.getCompound("bridge-api");
            if(data.contains("generations")) {
                JSONWrapper wrapper = new JSONWrapper();
                String stored = data.getString("generations");
                JsonObject json = new GsonBuilder().create().fromJson(stored, JsonObject.class);
                wrapper = wrapper.deserialize(json);
                result.offer(SpecKeys.GENERATIONS_DATA, wrapper);
            }
        }

        result.pokemon = pokemon;
        return result;
    }

    private Pokemon writeAll(Pokemon pokemon) {
        List<Map.Entry<SpecKey<?>, Object>> prioritized = this.data.entrySet().stream()
                .sorted(Comparator.<Map.Entry<SpecKey<?>, Object>, Integer>comparing(x -> x.getKey().getPriority()).reversed())
                .collect(Collectors.toList());

        boolean initialized = false;
        for(Map.Entry<SpecKey<?>, Object> entry : prioritized) {
            SpecKey<?> key = entry.getKey();
            Object value = entry.getValue();

            if(key.getPriority() <= 0 && !initialized) {
                pokemon.initialize(InitializeCategory.SPECIES);
                initialized = true;
            }

            ReforgedSpecKeyWriter.write(key, pokemon, value);
        }

        return pokemon;
    }

    private static <T extends ExtraStats> T convert(Class<T> type, ExtraStats stats) {
        if(type.isAssignableFrom(stats.getClass())) {
            return type.cast(stats);
        }

        throw new IllegalArgumentException("Invalid stats type");
    }

    public void offerUnsafe(SpecKey<?> key, Object value) {
        if(value == null) {
            return;
        }

        if(this.supports(key)) {
            this.data.put(key, value);
        } else {
            this.data.put(SpecKeys.GENERATIONS_DATA, this.get(SpecKeys.GENERATIONS_DATA).orElseGet(JSONWrapper::new).offerUnsafe(key, value));
        }
    }

    private NBTWrapper extraData(Pokemon pokemon) {
        CompoundNBT nbt = pokemon.getPersistentData().copy();
        nbt.remove("FusedPokemon");
        return new NBTWrapper(nbt);
    }

    private ReforgedPokemon getEmbeddedPokemon(Pokemon pokemon) {
        CompoundNBT nbt = pokemon.getPersistentData().getCompound("FusedPokemon");
        return ReforgedPokemon.from(PokemonFactory.create(nbt));
    }

    @Override
    public JObject serialize() {
        ReforgedDataManager manager = new ReforgedDataManager();
        return manager.serialize(this);
    }
}
