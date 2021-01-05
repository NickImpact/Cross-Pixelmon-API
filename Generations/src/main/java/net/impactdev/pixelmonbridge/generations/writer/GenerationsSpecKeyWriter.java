package net.impactdev.pixelmonbridge.generations.writer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.pixelmongenerations.common.battle.attacks.Attack;
import com.pixelmongenerations.common.entity.pixelmon.EntityPixelmon;
import com.pixelmongenerations.common.entity.pixelmon.stats.Gender;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.LakeTrioStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.LightTrioStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.MeloettaStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.MewStats;
import com.pixelmongenerations.core.enums.EnumGrowth;
import com.pixelmongenerations.core.enums.EnumNature;
import com.pixelmongenerations.core.enums.EnumSpecies;
import com.pixelmongenerations.core.enums.items.EnumPokeball;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;
import net.impactdev.pixelmonbridge.details.components.Ability;
import net.impactdev.pixelmonbridge.details.components.EggInfo;
import net.impactdev.pixelmonbridge.details.components.Level;
import net.impactdev.pixelmonbridge.details.components.Moves;
import net.impactdev.pixelmonbridge.details.components.Nature;
import net.impactdev.pixelmonbridge.details.components.Trainer;
import net.impactdev.pixelmonbridge.details.components.generic.ItemStackWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.NBTWrapper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.function.BiConsumer;

public class GenerationsSpecKeyWriter {

    private static Map<SpecKey<?>, BiConsumer<EntityPixelmon, Object>> writers = Maps.newHashMap();
    static {
        writers.put(SpecKeys.SPECIES, (p, v) -> {
            Preconditions.checkArgument(EnumSpecies.hasPokemon((String) v), "Invalid species");
            p.setName((String) v);
        });
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
            if(nature.getMint() != null) {
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
            p.eggCycles = info.getCycles();

            // Differ to NBT because Generations needs to catch up with their API
            NBTTagCompound nbt = new NBTTagCompound();
            nbt = p.writeToNBT(nbt);
            nbt.setInteger("steps", info.getSteps());
            p.readFromNBT(nbt);
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
        writers.put(SpecKeys.REFORGED_DATA, (p, v) -> {
            // If this key is present, it indicates that we managed to read in Reforged Data
            // that we cannot accept. As such, we should write this data out to the NBT
            // of the recipient of the data

            NBTWrapper wrapper = (NBTWrapper) v;
            NBTTagCompound parent = new NBTTagCompound();
            p.writeToNBT(parent);

            parent.setTag("reforged", wrapper.getNBT());
            p.readFromNBT(parent);
        });
        writers.put(SpecKeys.GENERATIONS_DATA, (p, v) -> {
            NBTWrapper wrapper = (NBTWrapper) v;

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
        });
    }


    public static void write(SpecKey<?> key, EntityPixelmon target, Object value) {
        if(writers.containsKey(key)) {
            writers.get(key).accept(target, value);
        }
    }
}
