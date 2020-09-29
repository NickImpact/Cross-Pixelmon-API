package net.impactdev.pixelmonbridge.reforged.writer;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.MeltanStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.MewStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.MiniorStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.ShearableStats;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokerusType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;
import net.impactdev.pixelmonbridge.details.components.Ability;
import net.impactdev.pixelmonbridge.details.components.EggInfo;
import net.impactdev.pixelmonbridge.details.components.Level;
import net.impactdev.pixelmonbridge.details.components.Moves;
import net.impactdev.pixelmonbridge.details.components.Nature;
import net.impactdev.pixelmonbridge.details.components.Pokerus;
import net.impactdev.pixelmonbridge.details.components.Trainer;
import net.impactdev.pixelmonbridge.details.components.generic.ItemStackWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.NBTWrapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class SpecKeyWriter {

    private static Map<SpecKey<?>, BiConsumer<Pokemon, Object>> writers = Maps.newHashMap();

    static {
        writers.put(SpecKeys.SPECIES, (p, v) -> p.setSpecies(EnumSpecies.getFromNameAnyCase((String) v)));
        writers.put(SpecKeys.SHINY, (p, v) -> p.setShiny((boolean) v));
        writers.put(SpecKeys.FORM, (p, v) -> p.setForm((int) v));
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
            EnumNature actual = EnumNature.natureFromString(nature.getActual());
            if(actual == null) {
                throw new IllegalArgumentException("Invalid nature");
            }

            p.setNature(actual);
            if(nature.getMint() != null) {
                EnumNature mint = EnumNature.natureFromString(nature.getMint());
                if(mint == null) {
                    throw new IllegalArgumentException("Invalid mint nature");
                }

                p.setMintNature(mint);
            }
        });
        writers.put(SpecKeys.ABILITY, (p, v) -> {
            Ability ability = (Ability) v;
            p.setAbility(ability.getAbility());
            p.setAbilitySlot(ability.getSlot());
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
        writers.put(SpecKeys.TEXTURE, (p, v) -> p.setCustomTexture((String) v));
        writers.put(SpecKeys.POKEBALL, (p, v) -> {
            int ordinal = (int) v;
            if(EnumPokeballs.values().length <= ordinal || ordinal < 0) {
                throw new IllegalArgumentException("Invalid pokeball index");
            }

            p.setCaughtBall(EnumPokeballs.getFromIndex(ordinal));
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
            com.pixelmonmod.pixelmon.entities.pixelmon.stats.Pokerus actual = new com.pixelmonmod.pixelmon.entities.pixelmon.stats.Pokerus(EnumPokerusType.values()[pokerus.getType()]);
            actual.secondsSinceInfection = pokerus.getSecondsSinceInfection();
            actual.announced = pokerus.isAnnounced();
            p.setPokerus(actual);
        });
        writers.put(SpecKeys.MOVESET, (p, v) -> {
            Moves moves = (Moves) v;
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
                p.addSpecFlag(f);
            }
        });
        writers.put(SpecKeys.RELEARNABLE_MOVES, (p, v) -> {
            List<Integer> ids = (List<Integer>) v;
            for(int id : ids) {
                p.getRelearnableMoves().add(id);
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
        writers.put(SpecKeys.HELD_ITEM, (p, v) -> {
            ItemStackWrapper wrapper = (ItemStackWrapper) v;
            p.setHeldItem(wrapper.getItem());
        });
        writers.put(SpecKeys.MEW_CLONES, (p, v) -> {
            if(p.getSpecies() != EnumSpecies.Mew) {
                throw new IllegalStateException("Pokemon must be a Mew for this data");
            }

            ((MewStats) p.getExtraStats()).numCloned = (int) v;
        });
        writers.put(SpecKeys.LAKE_TRIO_ENCHANTS, (p, v) -> {
            if(p.getSpecies() != EnumSpecies.Azelf && p.getSpecies() != EnumSpecies.Uxie && p.getSpecies() != EnumSpecies.Mesprit) {
                throw new IllegalStateException("Pokemon must be a lake trio member for this data");
            }

            ((LakeTrioStats) p.getExtraStats()).numEnchanted = (int) v;
        });
        writers.put(SpecKeys.MELTAN_ORES_SMELTED, (p, v) -> {
            if(p.getSpecies() != EnumSpecies.Meltan) {
                throw new IllegalStateException("Pokemon must be a Meltan for this data");
            }

            ((MeltanStats) p.getExtraStats()).oresSmelted = (int) v;
        });
        writers.put(SpecKeys.MAREEP_WOOL_GROWTH, (p, v) -> {
            if(p.getSpecies() != EnumSpecies.Mareep) {
                throw new IllegalStateException("Pokemon must be a Mareep for this data");
            }

            ((ShearableStats) p.getExtraStats()).growthStage = (byte) v;
        });
        writers.put(SpecKeys.MINIOR_COLOR, (p, v) -> {
            if(p.getSpecies() != EnumSpecies.Minior) {
                throw new IllegalStateException("Pokemon must be a Minior for this data");
            }

            ((MiniorStats) p.getExtraStats()).color = (byte) v;
        });
        writers.put(SpecKeys.HP, (p, v) -> p.setHealth((int) v));
        writers.put(SpecKeys.EV_HP, (p, v) -> p.getStats().evs.hp = (int) v);
        writers.put(SpecKeys.EV_ATK, (p, v) -> p.getStats().evs.attack = (int) v);
        writers.put(SpecKeys.EV_DEF, (p, v) -> p.getStats().evs.defence = (int) v);
        writers.put(SpecKeys.EV_SPATK, (p, v) -> p.getStats().evs.specialAttack = (int) v);
        writers.put(SpecKeys.EV_SPDEF, (p, v) -> p.getStats().evs.specialDefence = (int) v);
        writers.put(SpecKeys.EV_SPEED, (p, v) -> p.getStats().evs.speed = (int) v);
        writers.put(SpecKeys.IV_HP, (p, v) -> p.getStats().ivs.hp = (int) v);
        writers.put(SpecKeys.IV_ATK, (p, v) -> p.getStats().ivs.attack = (int) v);
        writers.put(SpecKeys.IV_DEF, (p, v) -> p.getStats().ivs.defence = (int) v);
        writers.put(SpecKeys.IV_SPATK, (p, v) -> p.getStats().ivs.specialAttack = (int) v);
        writers.put(SpecKeys.IV_SPDEF, (p, v) -> p.getStats().ivs.specialDefence = (int) v);
        writers.put(SpecKeys.IV_SPEED, (p, v) -> p.getStats().ivs.speed = (int) v);
    }

    public static boolean write(SpecKey<?> key, Pokemon target, Object value) {
        if(writers.containsKey(key)) {
            writers.get(key).accept(target, value);
            return true;
        }

        return false;
    }

}
