package net.impactdev.pixelmonbridge.generations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixelmongenerations.common.battle.attacks.Attack;
import com.pixelmongenerations.common.entity.pixelmon.EntityPixelmon;
import com.pixelmongenerations.common.entity.pixelmon.stats.ExtraStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.Gender;
import com.pixelmongenerations.common.entity.pixelmon.stats.Stats;
import com.pixelmongenerations.common.entity.pixelmon.stats.StatsType;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.LakeTrioStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.LightTrioStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.MeloettaStats;
import com.pixelmongenerations.common.entity.pixelmon.stats.extraStats.MewStats;
import com.pixelmongenerations.core.config.PixelmonEntityList;
import com.pixelmongenerations.core.enums.EnumSpecies;
import net.impactdev.pixelmonbridge.ImpactDevPokemon;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.PixelmonSource;
import net.impactdev.pixelmonbridge.details.Query;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;
import net.impactdev.pixelmonbridge.details.components.*;
import net.impactdev.pixelmonbridge.details.components.generic.ItemStackWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.JSONWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.NBTWrapper;
import net.impactdev.pixelmonbridge.generations.writer.GenerationsSpecKeyWriter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GenerationsPokemon implements ImpactDevPokemon<EntityPixelmon> {

    private final ImmutableList<SpecKey<?>> UNSUPPORTED = ImmutableList.copyOf(Lists.newArrayList(
            SpecKeys.MELTAN_ORES_SMELTED,
            SpecKeys.MAREEP_WOOL_GROWTH,
            SpecKeys.MINIOR_COLOR,
            SpecKeys.RIBBONS
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

    private transient EntityPixelmon pokemon;

    @Override
    public EntityPixelmon getOrCreate() {
        return this.pokemon == null ? (this.pokemon = this.initialize()) : this.pokemon;
    }

    private EntityPixelmon initialize() {
        EnumSpecies species = this.get(SpecKeys.SPECIES).flatMap(EnumSpecies::getFromName).orElseThrow(() -> new RuntimeException("Species is not yet populated..."));
        EntityPixelmon result = new EntityPixelmon(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());

        this.get(SpecKeys.FORM).ifPresent((form) -> result.setForm(form, false));
        this.get(SpecKeys.GENDER).ifPresent(gender -> result.setGender(Gender.getGender(gender.shortValue())));
        result.init(species.name);

        return this.writeAll(result);
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
            if(!EnumSpecies.getFromName((String) data).isPresent()) {
                return false;
            }
        }

        if(this.supports(key)) {
            this.data.put(key, data);
        } else {
            this.data.put(SpecKeys.REFORGED_DATA, this.get(SpecKeys.REFORGED_DATA).orElseGet(JSONWrapper::new).append(key, data));
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

    public static GenerationsPokemon from(EntityPixelmon pokemon) {
        GenerationsPokemon result = new GenerationsPokemon();
        result.offer(SpecKeys.SPECIES, pokemon.getSpecies().name);
        result.offer(SpecKeys.FORM, pokemon.getForm());
        result.offer(SpecKeys.SHINY, pokemon.isShiny());
        result.offer(SpecKeys.LEVEL, new Level(pokemon.level.getLevel(), pokemon.level.getExp(), pokemon.doesLevel));

        result.offer(SpecKeys.GENDER, pokemon.getGender().ordinal());
        result.offer(SpecKeys.NATURE, new Nature(pokemon.getNature().name(), Optional.ofNullable(pokemon.getPseudoNature()).map(Enum::name).orElse(null)));
        result.offer(SpecKeys.ABILITY, new Ability(pokemon.getAbility().getName(), pokemon.getAbilitySlot()));
        result.offer(SpecKeys.FRIENDSHIP, pokemon.friendship.getFriendship());
        result.offer(SpecKeys.GROWTH, pokemon.getGrowth().index);
        result.offer(SpecKeys.NICKNAME, pokemon.getNickname());
        result.offer(SpecKeys.TEXTURE, pokemon.getCustomTexture());
        result.offer(SpecKeys.SPECIAL_TEXTURE, pokemon.getSpecialTextureIndex());

        if(!pokemon.originalTrainerUUID.equals("")) {
            result.offer(SpecKeys.TRAINER, new Trainer(UUID.fromString(pokemon.originalTrainerUUID), pokemon.originalTrainer));
        }

        if(pokemon.caughtBall != null) {
            result.offer(SpecKeys.POKEBALL, pokemon.caughtBall.ordinal());
        }

        if(pokemon.isEgg) {
            result.offer(SpecKeys.EGG_INFO, new EggInfo(
                    pokemon.eggCycles,
                    pokemon.writeToNBT(new NBTTagCompound()).getInteger("steps")
            ));
        }

        if(pokemon.getPokeRus() > 0) {
            result.offer(SpecKeys.POKERUS, new Pokerus(
                    PixelmonSource.Generations,
                    pokemon.getPokeRus(),
                    pokemon.getPokeRusTimer() / 20, //conversion from ticks to seconds
                    true
            ));
        }

        result.offer(SpecKeys.HELD_ITEM, new ItemStackWrapper(pokemon.getHeldItem(EnumHand.MAIN_HAND)));
        if(pokemon.status != null) {
            result.offer(SpecKeys.STATUS, pokemon.status.type.ordinal());
        }

        if(pokemon.getSpecies() == EnumSpecies.Mew) {
            result.offer(SpecKeys.MEW_CLONES, convert(MewStats.class, pokemon.extraStats).numCloned);
        } else if(pokemon.getSpecies() == EnumSpecies.Azelf || pokemon.getSpecies() == EnumSpecies.Mesprit || pokemon.getSpecies() == EnumSpecies.Uxie) {
            result.offer(SpecKeys.LAKE_TRIO_ENCHANTS, convert(LakeTrioStats.class, pokemon.extraStats).numEnchanted);
        } else if(pokemon.getSpecies() == EnumSpecies.Meloetta) {
            result.offer(SpecKeys.MELOETTA_ACTIVATIONS, convert(MeloettaStats.class, pokemon.extraStats).abundantActivations);
        } else if(pokemon.getSpecies() == EnumSpecies.Lunala || pokemon.getSpecies() == EnumSpecies.Solgaleo || pokemon.getSpecies() == EnumSpecies.Necrozma) {
            result.offer(SpecKeys.LIGHT_TRIO_WORMHOLES, convert(LightTrioStats.class, pokemon.extraStats).numWormholes);
        }

        result.offer(SpecKeys.HP, pokemon.stats.HP);
        result.offer(SpecKeys.EV_HP, pokemon.stats.EVs.HP);
        result.offer(SpecKeys.EV_ATK, pokemon.stats.EVs.Attack);
        result.offer(SpecKeys.EV_DEF, pokemon.stats.EVs.Defence);
        result.offer(SpecKeys.EV_SPATK, pokemon.stats.EVs.SpecialAttack);
        result.offer(SpecKeys.EV_SPDEF, pokemon.stats.EVs.SpecialDefence);
        result.offer(SpecKeys.EV_SPEED, pokemon.stats.EVs.Speed);
        result.offer(SpecKeys.IV_HP, pokemon.stats.IVs.HP);
        result.offer(SpecKeys.IV_ATK, pokemon.stats.IVs.Attack);
        result.offer(SpecKeys.IV_DEF, pokemon.stats.IVs.Defence);
        result.offer(SpecKeys.IV_SPATK, pokemon.stats.IVs.SpAtt);
        result.offer(SpecKeys.IV_SPDEF, pokemon.stats.IVs.SpDef);
        result.offer(SpecKeys.IV_SPEED, pokemon.stats.IVs.Speed);
        result.offer(SpecKeys.HYPER_HP, pokemon.stats.isBottleCapIV(StatsType.HP));
        result.offer(SpecKeys.HYPER_ATTACK, pokemon.stats.isBottleCapIV(StatsType.Attack));
        result.offer(SpecKeys.HYPER_DEFENCE, pokemon.stats.isBottleCapIV(StatsType.Defence));
        result.offer(SpecKeys.HYPER_SPECIAL_ATTACK, pokemon.stats.isBottleCapIV(StatsType.SpecialAttack));
        result.offer(SpecKeys.HYPER_SPECIAL_DEFENCE, pokemon.stats.isBottleCapIV(StatsType.SpecialDefence));
        result.offer(SpecKeys.HYPER_SPEED, pokemon.stats.isBottleCapIV(StatsType.Speed));
        result.offer(SpecKeys.DYNAMAX_LEVEL, pokemon.getDataManager().get(EntityPixelmon.dwDynamaxLevel));
        result.offer(SpecKeys.CAN_GMAX, pokemon.hasGmaxFactor());

        List<Marking> marks = Lists.newArrayList();
        Marking.createFor(PixelmonSource.Generations, pokemon.getMark().ordinal())
                .ifPresent(marks::add);
        result.offer(SpecKeys.MARKS, marks);

        result.calculateExtraNBT(pokemon).ifPresent(nbt -> {
            result.offer(SpecKeys.EXTRA_DATA, new NBTWrapper(nbt));
        });

        if(!pokemon.embeddedPokemon.isEmpty()) {
            List<ImpactDevPokemon<?>> embeds = pokemon.embeddedPokemon.stream()
                    .map(nbt -> GenerationsPokemon.from((EntityPixelmon) PixelmonEntityList.createEntityFromNBT(nbt, pokemon.world)))
                    .collect(Collectors.toList());
            result.offer(SpecKeys.EMBEDDED_POKEMON, embeds);
        }

        Moves moves = new Moves();
        for(Attack attack : pokemon.getMoveset().attacks) {
            if(attack == null) {
                continue;
            }

            moves.append(new Moves.Move(
                    attack.getAttackBase().getUnlocalizedName(),
                    attack.pp,
                    (attack.pp / attack.ppBase - 1) / 2 * 10
            ));
        }
        result.offer(SpecKeys.MOVESET, moves);

        NBTTagCompound nbt = new NBTTagCompound();
        pokemon.writeToNBT(nbt);
        nbt = nbt.getCompoundTag("ForgeData");
        if(nbt.hasKey("bridge-api")) {
            NBTTagCompound data = nbt.getCompoundTag("bridge-api");
            if(data.hasKey("reforged")) {
                JSONWrapper wrapper = new JSONWrapper();
                String stored = data.getString("reforged");
                JsonObject json = new GsonBuilder().create().fromJson(stored, JsonObject.class);
                wrapper = wrapper.deserialize(json);
                result.offer(SpecKeys.REFORGED_DATA, wrapper);
            }
        }

        result.pokemon = pokemon;
        return result;
    }

    private EntityPixelmon writeAll(EntityPixelmon pokemon) {
        for(Map.Entry<SpecKey<?>, Object> entry : this.data.entrySet()) {
            SpecKey<?> key = entry.getKey();
            Object value = entry.getValue();

            GenerationsSpecKeyWriter.write(key, pokemon, value);
        }

        pokemon.updateStats();
        GenerationsSpecKeyWriter.write(SpecKeys.HP, pokemon, this.data.get(SpecKeys.HP));
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
            this.data.put(SpecKeys.REFORGED_DATA, this.get(SpecKeys.REFORGED_DATA).orElseGet(JSONWrapper::new).offerUnsafe(key, value));
        }
    }

    private Optional<NBTTagCompound> calculateExtraNBT(EntityPixelmon pokemon) {
        if(!populated) {
            pokemon.getNBTTags(tags);
            populated = true;
        }

        List<String> extra = Lists.newArrayList();
        NBTTagCompound nbt = pokemon.writeToNBT(new NBTTagCompound());
        for(String key : nbt.getKeySet()) {
            if(!tags.containsKey(key)) {
                extra.add(key);
            }
        }

        if(extra.isEmpty()) {
            return Optional.empty();
        }

        NBTTagCompound result = new NBTTagCompound();
        for(String key : extra) {
            result.setTag(key, nbt.getTag(key));
        }

        return Optional.of(result);
    }

    @Override
    public JObject serialize() {
        GenerationsDataManager manager = new GenerationsDataManager();
        return manager.serialize(this);
    }

    private static boolean populated;
    @SuppressWarnings("rawtypes")
    private static HashMap<String, Class> tags = new HashMap<>();
    private static List<String> ignoreNBTTagList = Lists.newArrayList(
            "embeddedPokemon",
            "HurtByTimestamp",
            "ForgeData",
            "Sitting",
            "Attributes",
            "Invulnerable",
            "FallFlying",
            "ForcedAge",
            "AbsorptionAmount",
            "PortalCooldown",
            "IsInBall",
            "FallDistance",
            "transform",
            "InLove",
            "DeathTime",
            "HandDropChances",
            "PersistenceRequired",
            "EggMoves",
            "Age",
            "Motion",
            "Leashed",
            "Aggression",
            "UUIDLeast",
            "UUIDMost",
            "LeftHanded",
            "Air",
            "OnGround",
            "Dimension",
            "gmaxfactor",
            "Rotation",
            "UpdateBlocked",
            "HandItems",
            "ArmorDropChances",
            "OwnerUUID",
            "Pos",
            "Fire",
            "ArmorItems",
            "CanPickupLoot",
            "HurtTime",
            "pixelmonOwnerUUIDLeast",
            "pixelmonOwnerUUIDMost",
            "CanPickupLoot"
    );

    static {
        for(String key : ignoreNBTTagList) {
            tags.put(key, Void.class);
        }
    }
}
