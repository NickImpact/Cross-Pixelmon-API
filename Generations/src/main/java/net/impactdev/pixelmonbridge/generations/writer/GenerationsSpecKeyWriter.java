package net.impactdev.pixelmonbridge.generations.writer;

import com.google.common.collect.Maps;
import com.pixelmongenerations.common.battle.attacks.Attack;
import com.pixelmongenerations.common.entity.pixelmon.EntityPixelmon;
import com.pixelmongenerations.common.entity.pixelmon.stats.Gender;
import com.pixelmongenerations.common.entity.pixelmon.stats.StatsType;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.LakeTrioStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.LightTrioStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.MeloettaStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.MewStats;
import com.pixelmongenerations.core.enums.EnumGrowth;
import com.pixelmongenerations.core.enums.EnumMark;
import com.pixelmongenerations.core.enums.EnumNature;
import com.pixelmongenerations.core.enums.EnumSpecies;
import com.pixelmongenerations.core.enums.items.EnumPokeball;
import net.impactdev.pixelmonbridge.details.PixelmonSource;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;
import net.impactdev.pixelmonbridge.details.components.*;
import net.impactdev.pixelmonbridge.details.components.generic.ItemStackWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.JSONWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.NBTWrapper;
import net.impactdev.pixelmonbridge.generations.GenerationsPokemon;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class GenerationsSpecKeyWriter {

    private static Map<SpecKey<?>, BiConsumer<EntityPixelmon, Object>> writers = Maps.newHashMap();
    static {
        writers.put(SpecKeys.SPECIES, (p, v) -> p.setName((String) v));
        writers.put(SpecKeys.SHINY, (p, v) -> p.setShiny((boolean) v));
        writers.put(SpecKeys.FORM, (p, v) -> p.setForm((int) v));
        writers.put(SpecKeys.LEVEL, (p, v) -> {
            Level level = (Level) v;
            p.level.setLevel(level.getLevel());
            p.level.setExp(level.getExperience());
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
            if(nature.getMint() != null && !nature.getMint().equals(nature.getActual())) {
                EnumNature mint = EnumNature.natureFromString(nature.getMint());
                if(mint == null) {
                    throw new IllegalArgumentException("Invalid mint nature");
                }

                p.setPseudoNature(mint);
            }
        });
        writers.put(SpecKeys.ABILITY, (p, v) -> {
            Ability ability = (Ability) v;
            p.setAbility(ability.getAbility());
            p.setAbilitySlot(ability.getSlot());
        });
        writers.put(SpecKeys.FRIENDSHIP, (p, v) -> {
            p.friendship.setFriendship((int) v);
        });
        writers.put(SpecKeys.GROWTH, (p, v) -> {
            int ordinal = (int) v;
            if(EnumGrowth.values().length <= ordinal || ordinal < 0) {
                throw new IllegalArgumentException("Invalid growth index");
            }

            p.setGrowth(EnumGrowth.getGrowthFromIndex(ordinal));
        });
        writers.put(SpecKeys.NICKNAME, (p, v) -> p.setNickname((String) v));
        writers.put(SpecKeys.TEXTURE, (p, v) -> p.setCustomSpecialTexture((String) v));
        writers.put(SpecKeys.SPECIAL_TEXTURE, (p, v) -> p.setSpecialTexture((int) v));
        writers.put(SpecKeys.POKEBALL, (p, v) -> {
            int ordinal = (int) v;
            if(EnumPokeball.values().length <= ordinal || ordinal < 0) {
                throw new IllegalArgumentException("Invalid pokeball index");
            }

            p.caughtBall = EnumPokeball.getFromIndex(ordinal);
        });
        writers.put(SpecKeys.TRAINER, (p, v) -> {
            Trainer trainer = (Trainer) v;
            p.originalTrainerUUID = trainer.getUUID().toString();
            p.originalTrainer = trainer.getLastKnownName();
        });
        writers.put(SpecKeys.EGG_INFO, (p, v) -> {
            EggInfo info = (EggInfo) v;
            p.isEgg = true;
            p.eggCycles = info.getCycles();

            // Differ to NBT because Generations needs to catch up with their API
            NBTTagCompound nbt = new NBTTagCompound();
            nbt = p.writeToNBT(nbt);
            nbt.setInteger("steps", info.getSteps());
            p.readFromNBT(nbt);
        });
        writers.put(SpecKeys.POKERUS, (p, v) -> {
            Pokerus pokerus = (Pokerus) v;

            int type = pokerus.getSource() == PixelmonSource.Generations ? pokerus.getType() : 2;

            p.setPokeRus(type);
            p.setPokeRusTimer(pokerus.getSecondsSinceInfection() * 20); //pokerus  timer in gens is in ticks
            // we also don't use the isAnnounced flag here
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
                p.getMoveset().add(attack);
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

            ((MewStats) p.extraStats).numCloned = (int) v;
        });
        writers.put(SpecKeys.LAKE_TRIO_ENCHANTS, (p, v) -> {
            if(p.getSpecies() != EnumSpecies.Azelf && p.getSpecies() != EnumSpecies.Uxie && p.getSpecies() != EnumSpecies.Mesprit) {
                throw new IllegalStateException("Pokemon must be a lake trio member for this data");
            }

            ((LakeTrioStats) p.extraStats).numEnchanted = (int) v;
        });
        writers.put(SpecKeys.MELOETTA_ACTIVATIONS, (p, v) -> {
            if(p.getSpecies() != EnumSpecies.Meloetta) {
                throw new IllegalStateException("Pokemon must be a Mew for this data");
            }

            ((MeloettaStats) p.extraStats).abundantActivations = (int) v;
        });
        writers.put(SpecKeys.EXTRA_DATA, (p, v) -> {
            NBTTagCompound nbt = p.writeToNBT(new NBTTagCompound());
            NBTWrapper wrapper = (NBTWrapper) v;

            for(String key : wrapper.getNBT().getKeySet()) {
                nbt.setTag(key, wrapper.getNBT().getTag(key));
            }
        });
        writers.put(SpecKeys.EMBEDDED_POKEMON, (p, v) -> {
            List<GenerationsPokemon> embeds = ((List<GenerationsPokemon>) v);
            embeds.stream()
                    .map(gp -> gp.getOrCreate().writeToNBT(new NBTTagCompound()))
                    .forEach(nbt -> p.embeddedPokemon.add(nbt));
        });
        writers.put(SpecKeys.HP, (p, v) -> p.setHealth((int) v));
        writers.put(SpecKeys.EV_HP, (p, v) -> p.stats.EVs.HP = (int) v);
        writers.put(SpecKeys.EV_ATK, (p, v) -> p.stats.EVs.Attack = (int) v);
        writers.put(SpecKeys.EV_DEF, (p, v) -> p.stats.EVs.Defence = (int) v);
        writers.put(SpecKeys.EV_SPATK, (p, v) -> p.stats.EVs.SpecialAttack = (int) v);
        writers.put(SpecKeys.EV_SPDEF, (p, v) -> p.stats.EVs.SpecialDefence = (int) v);
        writers.put(SpecKeys.EV_SPEED, (p, v) -> p.stats.EVs.Speed = (int) v);
        writers.put(SpecKeys.IV_HP, (p, v) -> p.stats.IVs.HP = (int) v);
        writers.put(SpecKeys.IV_ATK, (p, v) -> p.stats.IVs.Attack = (int) v);
        writers.put(SpecKeys.IV_DEF, (p, v) -> p.stats.IVs.Defence = (int) v);
        writers.put(SpecKeys.IV_SPATK, (p, v) -> p.stats.IVs.SpAtt = (int) v);
        writers.put(SpecKeys.IV_SPDEF, (p, v) -> p.stats.IVs.SpDef = (int) v);
        writers.put(SpecKeys.IV_SPEED, (p, v) -> p.stats.IVs.Speed = (int) v);

        writers.put(SpecKeys.HYPER_HP, (p, v) -> actor((boolean) v, () -> p.stats.addBottleCapIV(StatsType.HP)));
        writers.put(SpecKeys.HYPER_ATTACK, (p, v) -> actor((boolean) v, () -> p.stats.addBottleCapIV(StatsType.Attack)));
        writers.put(SpecKeys.HYPER_DEFENCE, (p, v) -> actor((boolean) v, () -> p.stats.addBottleCapIV(StatsType.Defence)));
        writers.put(SpecKeys.HYPER_SPECIAL_ATTACK, (p, v) -> actor((boolean) v, () -> p.stats.addBottleCapIV(StatsType.SpecialAttack)));
        writers.put(SpecKeys.HYPER_SPECIAL_DEFENCE, (p, v) -> actor((boolean) v, () -> p.stats.addBottleCapIV(StatsType.SpecialDefence)));
        writers.put(SpecKeys.HYPER_SPEED, (p, v) -> actor((boolean) v, () -> p.stats.addBottleCapIV(StatsType.Speed)));
        writers.put(SpecKeys.CAN_GMAX, (p, v) -> p.setGmaxFactor((boolean) v));
        writers.put(SpecKeys.MARKS, (p, v) -> {
            List<Marking> markings = (List<Marking>) v;
            if(!markings.isEmpty()) {
                p.setMark(EnumMark.values()[markings.get(0).getMark().getIndexes().get(PixelmonSource.Generations)]);
            }
        });

        writers.put(SpecKeys.REFORGED_DATA, (p, v) -> {
            // If this key is present, it indicates that we managed to read in Reforged Data
            // that we cannot accept. As such, we should write this data out to the NBT
            // of the recipient of the data
            JSONWrapper wrapper = (JSONWrapper) v;

            NBTTagCompound root = new NBTTagCompound();
            NBTTagCompound parent = new NBTTagCompound();
            p.writeToNBT(root);

            NBTTagCompound forge = root.getCompoundTag("ForgeData");
            forge.setTag("bridge-api", parent);
            parent.setString("reforged", wrapper.serialize().toJson().toString());

            p.readFromNBT(root);
        });
        writers.put(SpecKeys.GENERATIONS_DATA, (p, v) -> {
            JSONWrapper wrapper = (JSONWrapper) v;

            wrapper.get(SpecKeys.MELOETTA_ACTIVATIONS).ifPresent(data -> {
                MeloettaStats stats = new MeloettaStats();
                stats.abundantActivations = data;
                p.extraStats = stats;
            });
            wrapper.get(SpecKeys.LIGHT_TRIO_WORMHOLES).ifPresent(data -> {
                LightTrioStats stats = new LightTrioStats();
                stats.numWormholes = data;
                p.extraStats = stats;
            });
            wrapper.get(SpecKeys.CAN_GMAX).ifPresent(p::setGmaxFactor);
        });
    }

    public static void write(SpecKey<?> key, EntityPixelmon target, Object value) {
        if(writers.containsKey(key)) {
            writers.get(key).accept(target, value);
        }
    }

    public static void actor(boolean value, Runnable consumer) {
        if(value) {
            consumer.run();
        }
    }

    public static <T> void test(SpecKey<T> key, BiConsumer<EntityPixelmon, T> consumer) {
        writers.put(key, (BiConsumer<EntityPixelmon, Object>) consumer);
    }
}
