package net.impactdev.pixelmonbridge.reforged.writer;

import com.google.common.collect.Maps;
import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokerusStrain;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbilityRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.pokemon.species.palette.PaletteProperties;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.ExtraStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MeltanStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MiniorStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.ShearableStats;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumRibbonType;
import net.impactdev.pixelmonbridge.details.PixelmonSource;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;
import net.impactdev.pixelmonbridge.details.components.Ability;
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
import net.impactdev.pixelmonbridge.reforged.ReforgedPokemon;
import net.impactdev.reforged.mixins.api.registry.Registries;
import net.impactdev.reforged.mixins.api.translations.forms.Destination;
import net.impactdev.reforged.mixins.api.translations.forms.LegacyFormTranslation;
import net.impactdev.reforged.mixins.api.translations.forms.LegacyFormTranslator;
import net.impactdev.reforged.mixins.api.translations.forms.LegacyKey;
import net.impactdev.reforged.mixins.api.translations.forms.types.PaletteTranslation;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies.*;

@SuppressWarnings("unchecked")
public class ReforgedSpecKeyWriter {

    private static Map<SpecKey<?>, BiConsumer<Pokemon, Object>> writers = Maps.newHashMap();

    static {
        writers.put(SpecKeys.ID, (p, v) -> p.setUUID((UUID) v));
        writers.put(SpecKeys.SPECIES, (p, v) -> p.setSpecies(PixelmonSpecies.get((String) v).orElseThrow(IllegalArgumentException::new), false));
        writers.put(SpecKeys.SHINY, (p, v) -> p.setShiny((boolean) v));
        writers.put(SpecKeys.FORM, (p, v) -> p.setForm((String) v));
        writers.put(SpecKeys.LEVEL, (p, v) -> {
            Level level = (Level) v;
            p.setLevel(level.getLevel());
            p.setExperience(level.getExperience());
        });
        writers.put(SpecKeys.GENDER, (p, v) -> {
            short ordinal = ((Integer) v).shortValue();
            if(Gender.values().length <= ordinal || ordinal < 0) {
                throw new IllegalArgumentException("Invalid gender index");
            }
            Gender gender = Gender.getGender(ordinal);
            p.setGender(gender);
        });
        writers.put(SpecKeys.NATURE, (p, v) -> {
            Nature nature = (Nature) v;
            com.pixelmonmod.pixelmon.api.pokemon.Nature actual = com.pixelmonmod.pixelmon.api.pokemon.Nature.natureFromString(nature.getActual());
            if(actual == null) {
                throw new IllegalArgumentException("Invalid nature");
            }

            p.setNature(actual);
            if(nature.getMint() != null) {
                com.pixelmonmod.pixelmon.api.pokemon.Nature mint = com.pixelmonmod.pixelmon.api.pokemon.Nature.natureFromString(nature.getMint());
                if(mint == null) {
                    throw new IllegalArgumentException("Invalid mint nature");
                }

                p.setMintNature(mint);
            }
        });
        writers.put(SpecKeys.ABILITY, (p, v) -> {
            p.setAbility(AbilityRegistry.getAbility((String) v));
        });
        writers.put(SpecKeys.FRIENDSHIP, (p, v) -> {
            p.setFriendship((int) v);
        });
        writers.put(SpecKeys.GROWTH, (p, v) -> {
            int ordinal = (int) v;
            if(EnumGrowth.values().length <= ordinal || ordinal < 0) {
                throw new IllegalArgumentException("Invalid growth index");
            }

            p.setGrowth(EnumGrowth.getGrowthFromIndex(ordinal));
        });
        writers.put(SpecKeys.NICKNAME, (p, v) -> p.setNickname((String) v));
        writers.put(SpecKeys.TEXTURE, (p, v) -> p.getPalette().setTexture(new ResourceLocation(((Resource) v).formatted())));
        writers.put(SpecKeys.POKEBALL, (p, v) -> {
            Optional<PokeBall> ball = PokeBallRegistry.getPokeBall((String) v);
            if(!ball.isPresent()) {
                throw new IllegalArgumentException("Invalid pokeball index");
            }

            p.setBall(ball.get());
        });
        writers.put(SpecKeys.TRAINER, (p, v) -> {
            Trainer trainer = (Trainer) v;
            p.setOriginalTrainer(trainer.getUUID(), trainer.getLastKnownName());
        });
        writers.put(SpecKeys.EGG_INFO, (p, v) -> {
            EggInfo info = (EggInfo) v;
            p.setEggCycles(info.getCycles());
            p.setEggSteps(info.getSteps());
        });
        writers.put(SpecKeys.POKERUS, (p, v) -> {
            Pokerus pokerus = (Pokerus) v;
            int type = pokerus.getSource() == PixelmonSource.Reforged ? pokerus.getType() : 0;

            com.pixelmonmod.pixelmon.api.pokemon.stats.Pokerus actual = new com.pixelmonmod.pixelmon.api.pokemon.stats.Pokerus(PokerusStrain.values()[type]);
            actual.secondsSinceInfection = pokerus.getSecondsSinceInfection();
            actual.announced = pokerus.isAnnounced();
            p.setPokerus(actual);
        });
        writers.put(SpecKeys.MOVESET, (p, v) -> {
            Moves moves = (Moves) v;
            p.getMoveset().clear();
            for(Moves.Move move : moves.getMoves()) {
                if (move == null) {
                    continue;
                }

                Attack attack = new Attack(move.getID());
                attack.pp = move.getPP();
                attack.ppLevel = move.getPPLevel();
                p.getMoveset().add(attack);
            }
        });
        writers.put(SpecKeys.SPEC_CREATION_FLAGS, (p, v) -> {
            List<String> flags = (List<String>) v;
            for(String f : flags) {
                p.addFlag(f);
            }
        });
        writers.put(SpecKeys.RELEARNABLE_MOVES, (p, v) -> {
            List<String> ids = (List<String>) v;
            for(String id : ids) {
                AttackRegistry.getAttackBase(id).ifPresent(a -> p.getRelearnableMoves().add(a));
            }
        });
        writers.put(SpecKeys.EXTRA_DATA, (p, v) -> {
            try {
                Field field = Pokemon.class.getDeclaredField("persistentData");
                field.setAccessible(true);
                field.set(p, ((NBTWrapper) v).getNBT());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        writers.put(SpecKeys.EMBEDDED_POKEMON, (p, v) -> {
            p.getPersistentData().put("FusedPokemon", ((List<ReforgedPokemon>) v).get(0).getOrCreate().writeToNBT(new CompoundNBT()));
        });

        writers.put(SpecKeys.HELD_ITEM, (p, v) -> {
            ItemStackWrapper wrapper = (ItemStackWrapper) v;
            p.setHeldItem(wrapper.getItem());
        });
        writers.put(SpecKeys.MEW_CLONES, (p, v) -> {
            if(!p.getSpecies().is(MEW)) {
                throw new IllegalStateException("Pokemon must be a Mew for this data");
            }

            ((MewStats) p.getExtraStats()).numCloned = (int) v;
        });
        writers.put(SpecKeys.LAKE_TRIO_ENCHANTS, (p, v) -> {
            if(!p.getSpecies().is(AZELF, MESPRIT, UXIE)) {
                throw new IllegalStateException("Pokemon must be a lake trio member for this data");
            }

            ((LakeTrioStats) p.getExtraStats()).numEnchanted = (int) v;
        });
        writers.put(SpecKeys.MELTAN_ORES_SMELTED, (p, v) -> {
            if(!p.getSpecies().is(MELTAN)) {
                throw new IllegalStateException("Pokemon must be a Meltan for this data");
            }

            ((MeltanStats) p.getExtraStats()).oresSmelted = (int) v;
        });
        writers.put(SpecKeys.MAREEP_WOOL_GROWTH, (p, v) -> {
            if(!p.getSpecies().is(MAREEP)) {
                throw new IllegalStateException("Pokemon must be a Mareep for this data");
            }

            ((ShearableStats) p.getExtraStats()).growthStage = (byte) v;
        });
        writers.put(SpecKeys.MINIOR_COLOR, (p, v) -> {
            if(!p.getSpecies().is(MINIOR)) {
                throw new IllegalStateException("Pokemon must be a Minior for this data");
            }

            ((MiniorStats) p.getExtraStats()).color = (byte) v;
        });
        writers.put(SpecKeys.HP, (p, v) -> p.setHealth((int) v));
        writers.put(SpecKeys.EV_HP, (p, v) -> p.getStats().getEVs().setStat(BattleStatsType.HP, (int) v));
        writers.put(SpecKeys.EV_ATK, (p, v) -> p.getStats().getEVs().setStat(BattleStatsType.ATTACK, (int) v));
        writers.put(SpecKeys.EV_DEF, (p, v) -> p.getStats().getEVs().setStat(BattleStatsType.DEFENSE, (int) v));
        writers.put(SpecKeys.EV_SPATK, (p, v) -> p.getStats().getEVs().setStat(BattleStatsType.SPECIAL_ATTACK, (int) v));
        writers.put(SpecKeys.EV_SPDEF, (p, v) -> p.getStats().getEVs().setStat(BattleStatsType.SPECIAL_DEFENSE, (int) v));
        writers.put(SpecKeys.EV_SPEED, (p, v) -> p.getStats().getEVs().setStat(BattleStatsType.SPEED, (int) v));
        writers.put(SpecKeys.IV_HP, (p, v) -> p.getStats().getIVs().setStat(BattleStatsType.HP, (int) v));
        writers.put(SpecKeys.IV_ATK, (p, v) -> p.getStats().getIVs().setStat(BattleStatsType.ATTACK, (int) v));
        writers.put(SpecKeys.IV_DEF, (p, v) -> p.getStats().getIVs().setStat(BattleStatsType.DEFENSE, (int) v));
        writers.put(SpecKeys.IV_SPATK, (p, v) -> p.getStats().getIVs().setStat(BattleStatsType.SPECIAL_ATTACK, (int) v));
        writers.put(SpecKeys.IV_SPDEF, (p, v) -> p.getStats().getIVs().setStat(BattleStatsType.SPECIAL_DEFENSE, (int) v));
        writers.put(SpecKeys.IV_SPEED, (p, v) -> p.getStats().getIVs().setStat(BattleStatsType.SPEED, (int) v));
        writers.put(SpecKeys.HYPER_HP, (p, v) -> p.getStats().getIVs().setHyperTrained(BattleStatsType.HP, (boolean) v));
        writers.put(SpecKeys.HYPER_ATTACK, (p, v) -> p.getStats().getIVs().setHyperTrained(BattleStatsType.ATTACK, (boolean) v));
        writers.put(SpecKeys.HYPER_DEFENCE, (p, v) -> p.getStats().getIVs().setHyperTrained(BattleStatsType.DEFENSE, (boolean) v));
        writers.put(SpecKeys.HYPER_SPECIAL_ATTACK, (p, v) -> p.getStats().getIVs().setHyperTrained(BattleStatsType.SPECIAL_ATTACK, (boolean) v));
        writers.put(SpecKeys.HYPER_SPECIAL_DEFENCE, (p, v) -> p.getStats().getIVs().setHyperTrained(BattleStatsType.SPECIAL_DEFENSE, (boolean) v));
        writers.put(SpecKeys.HYPER_SPEED, (p, v) -> p.getStats().getIVs().setHyperTrained(BattleStatsType.SPEED, (boolean) v));
        writers.put(SpecKeys.MARKS, (p, v) -> {
            List<Marking> markings = (List<Marking>) v;
            for(Marking marking : markings) {
                p.addRibbon(EnumRibbonType.values()[marking.getMark().getIndexes().get(PixelmonSource.Reforged)]);
            }
        });
        writers.put(SpecKeys.RIBBONS, (p, v) -> {
            List<Integer> ribbons = (List<Integer>) v;
            for(int ribbon : ribbons) {
                p.addRibbon(EnumRibbonType.values()[ribbon]);
            }
        });
        writers.put(SpecKeys.CAN_GMAX, (p, v) -> p.setGigantamaxFactor((boolean) v));
        writers.put(SpecKeys.DYNAMAX_LEVEL, (p, v) -> p.setDynamaxLevel((int) v));

        writers.put(SpecKeys.GENERATIONS_DATA, (p, v) -> {
            // If this key is present, it indicates that we managed to read in Generations Data
            // that we cannot accept. As such, we should write this data out to the NBT
            // of the recipient of the data

            JSONWrapper wrapper = (JSONWrapper) v;

            CompoundNBT root = new CompoundNBT();
            CompoundNBT parent = new CompoundNBT();
            p.writeToNBT(root);

            CompoundNBT forge = root.getCompound("ForgeData");
            forge.put("bridge-api", parent);
            parent.putString("generations", wrapper.serialize().toJson().toString());

            p.readFromNBT(root);
        });
        writers.put(SpecKeys.REFORGED_DATA, (p, v) -> {
            JSONWrapper wrapper = (JSONWrapper) v;

            wrapper.get(SpecKeys.POKERUS).ifPresent(data -> {
                p.setPokerus(new com.pixelmonmod.pixelmon.api.pokemon.stats.Pokerus(PokerusStrain.values()[data.getType()]));
                p.getPokerus().announced = data.isAnnounced();
                p.getPokerus().secondsSinceInfection = data.getSecondsSinceInfection();
            });
            wrapper.get(SpecKeys.MINIOR_COLOR).ifPresent(data -> {
                MiniorStats stats = new MiniorStats();
                stats.color = data;
                apply(p, stats);
            });
            wrapper.get(SpecKeys.MAREEP_WOOL_GROWTH).ifPresent(data -> {
                ShearableStats stats = new ShearableStats();
                stats.growthStage = data;
                apply(p, stats);
            });
            wrapper.get(SpecKeys.MELTAN_ORES_SMELTED).ifPresent(data -> {
                MeltanStats stats = new MeltanStats();
                stats.oresSmelted = data;
                apply(p, stats);
            });
        });

        writers.put(SpecKeys.FORM_LEGACY, (p, v) -> {
            LegacyFormTranslator translator = Registries.LEGACY_FORMS.get();
            RegistryValue<Species> species = p.getSpecies().getRegistryValue();
            int form = Math.max(0, (int) v);

            LegacyKey a = LegacyKey.of(form, species);
            if(translator.translations().containsKey(a)) {
                LegacyFormTranslation translation = translator.translations().get(a);
                if(translation.destination() == Destination.FORM) {
                    p.setForm(translation.name());
                } else {
                    PaletteTranslation x = (PaletteTranslation) translation;
                    p.setForm(x.name());
                    p.setPalette(translation.name());
                }
            } else {
                LegacyKey b = LegacyKey.of(form);
                if(translator.translations().containsKey(b)) {
                    LegacyFormTranslation translation = translator.translations().get(b);
                    if(translation.destination() == Destination.FORM) {
                        p.setForm(translation.name());
                    } else {
                        PaletteTranslation x = (PaletteTranslation) translation;
                        p.setForm(x.name());
                        p.setPalette(translation.name());
                    }
                } else {
                    p.setForm(species.getValueUnsafe().getDefaultForm());
                }

            }
        });
        writers.put(SpecKeys.ABILITY_LEGACY, (p, v) -> p.setAbility(AbilityRegistry.getAbility(((Ability) v).getAbility())));
        writers.put(SpecKeys.POKEBALL_LEGACY, (p, v) -> p.setBall(PokeBallRegistry.getAll().get((int) v)));
        writers.put(SpecKeys.TEXTURE_LEGACY, (p, v) -> p.setPalette((String) v));
        writers.put(SpecKeys.RELEARNABLE_MOVES_LEGACY, (p, v) -> {
            List<ImmutableAttack> ids = ((List<Integer>) v).stream()
                    .map(id -> AttackRegistry.getAllAttacks().get(id))
                    .collect(Collectors.toList());
            p.getRelearnableMoves().addAll(ids);

        });
    }

    public static void write(SpecKey<?> key, Pokemon target, Object value) {
        if(writers.containsKey(key)) {
            writers.get(key).accept(target, value);
        }
    }

    private static void apply(Pokemon pokemon, ExtraStats stats) {
        try {
            Field field = Pokemon.class.getDeclaredField("extraStats");
            field.setAccessible(true);
            field.set(pokemon, stats);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
