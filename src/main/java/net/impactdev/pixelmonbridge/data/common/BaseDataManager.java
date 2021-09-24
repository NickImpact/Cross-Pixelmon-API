package net.impactdev.pixelmonbridge.data.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.impactdev.pixelmonbridge.data.DataManager;
import net.impactdev.pixelmonbridge.data.Reader;
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
import net.impactdev.pixelmonbridge.details.components.Trainer;
import net.impactdev.pixelmonbridge.details.components.generic.ItemStackWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.JSONWrapper;
import net.impactdev.pixelmonbridge.details.components.generic.NBTWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BaseDataManager<P> implements DataManager<P> {

    protected Map<SpecKey<?>, Reader<?>> customReaders = Maps.newHashMap();

    public BaseDataManager() {
        this.init();
    }

    public void init() {
        customReaders.put(SpecKeys.ID, element -> UUID.fromString(element.getAsString()));
        customReaders.put(SpecKeys.SPECIES, JsonElement::getAsString);
        customReaders.put(SpecKeys.SHINY, JsonElement::getAsBoolean);
        customReaders.put(SpecKeys.FORM, JsonElement::getAsInt);
        customReaders.put(SpecKeys.LEVEL, data -> new Level(
                read(SpecKeys.LEVEL, () -> data.getAsJsonObject().get("level"), JsonElement::getAsInt),
                read(SpecKeys.LEVEL, () -> data.getAsJsonObject().get("experience"), JsonElement::getAsInt),
                read(SpecKeys.LEVEL, () -> data.getAsJsonObject().get("does-level"), JsonElement::getAsBoolean)
        ));
        customReaders.put(SpecKeys.GENDER, JsonElement::getAsInt);
        customReaders.put(SpecKeys.NATURE, data -> new Nature(
                read(SpecKeys.NATURE, () -> data.getAsJsonObject().get("actual"), JsonElement::getAsString),
                readAndAllowNull(() -> data.getAsJsonObject().get("mint"), JsonElement::getAsString)
        ));
        customReaders.put(SpecKeys.ABILITY, data -> new Ability(
                read(SpecKeys.ABILITY, () -> data.getAsJsonObject().get("ability"), JsonElement::getAsString),
                read(SpecKeys.ABILITY, () -> data.getAsJsonObject().get("slot"), JsonElement::getAsInt)
        ));
        customReaders.put(SpecKeys.FRIENDSHIP, JsonElement::getAsInt);
        customReaders.put(SpecKeys.GROWTH, JsonElement::getAsInt);
        customReaders.put(SpecKeys.NICKNAME, JsonElement::getAsString);
        customReaders.put(SpecKeys.TEXTURE, JsonElement::getAsString);
        customReaders.put(SpecKeys.SPECIAL_TEXTURE, JsonElement::getAsInt);
        customReaders.put(SpecKeys.POKEBALL, JsonElement::getAsInt);
        customReaders.put(SpecKeys.TRAINER, data -> new Trainer(
                read(SpecKeys.TRAINER, () -> data.getAsJsonObject().get("uuid"), x -> UUID.fromString(x.getAsString())),
                read(SpecKeys.TRAINER, () -> data.getAsJsonObject().get("lastKnownName"), JsonElement::getAsString)
        ));
        customReaders.put(SpecKeys.EGG_INFO, data -> new EggInfo(
                read(SpecKeys.EGG_INFO, () -> data.getAsJsonObject().get("cycles"), JsonElement::getAsInt),
                read(SpecKeys.EGG_INFO, () -> data.getAsJsonObject().get("steps"), JsonElement::getAsInt)
        ));
        customReaders.put(SpecKeys.POKERUS, data -> {
            JsonObject json = data.getAsJsonObject();

            boolean legacy = json.get("source") == null;
            return new Pokerus(
                    readOrSupply(() -> json.get("source"), value -> PixelmonSource.valueOf(value.getAsString()), PixelmonSource.Reforged),
                    read(SpecKeys.POKERUS, () -> json.get("type"), j -> {
                        if(legacy) {
                            return j.getAsInt() + 0xA;
                        } else {
                            return j.getAsInt();
                        }
                    }),
                    read(SpecKeys.POKERUS, () -> json.get("secondsSinceInfection"), JsonElement::getAsInt),
                    read(SpecKeys.POKERUS, () -> json.get("announced"), JsonElement::getAsBoolean)
            );
        });
        customReaders.put(SpecKeys.MOVESET, data -> {
            Moves moves = new Moves();
            JsonArray array = data.getAsJsonArray();
            for(JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                Moves.Move move = new Moves.Move(
                        object.get("id").getAsString(),
                        object.get("pp").getAsInt(),
                        object.get("ppLevel").getAsInt()
                );
                moves.append(move);
            }
            return moves;
        });
        customReaders.put(SpecKeys.SPEC_CREATION_FLAGS, data -> Lists.newArrayList(data.getAsJsonArray())
                .stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.toList())
        );
        customReaders.put(SpecKeys.RELEARNABLE_MOVES, data -> Lists.newArrayList(data.getAsJsonArray())
                .stream()
                .map(JsonElement::getAsInt)
                .collect(Collectors.toList())
        );
        customReaders.put(SpecKeys.EXTRA_DATA, data -> {
            try {
                return new NBTWrapper(
                        JsonToNBT.getTagFromJson(read(SpecKeys.EXTRA_DATA, () -> data.getAsJsonObject().get("data"), JsonElement::getAsString))
                );
            } catch (NBTException e) {
                throw new RuntimeException(e);
            }
        });
        customReaders.put(SpecKeys.EMBEDDED_POKEMON, data -> Lists.newArrayList(data.getAsJsonArray())
                .stream()
                .map(JsonElement::getAsJsonObject)
                .map(obj -> {
                    try {
                        return new NBTWrapper(
                                JsonToNBT.getTagFromJson(obj.getAsString())
                        );
                    } catch (NBTException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList())
        );
        customReaders.put(SpecKeys.HELD_ITEM, data -> {
            try {
                ItemStack item = new ItemStack(JsonToNBT.getTagFromJson(read(SpecKeys.HELD_ITEM, () -> data.getAsJsonObject().get("data"), JsonElement::getAsString)));
                return new ItemStackWrapper(item);
            } catch (NBTException e) {
                throw new RuntimeException(e);
            }
        });
        customReaders.put(SpecKeys.STATUS, JsonElement::getAsInt);
        customReaders.put(SpecKeys.MEW_CLONES, JsonElement::getAsInt);
        customReaders.put(SpecKeys.LAKE_TRIO_ENCHANTS, JsonElement::getAsInt);
        customReaders.put(SpecKeys.MELTAN_ORES_SMELTED, JsonElement::getAsInt);
        customReaders.put(SpecKeys.MELOETTA_ACTIVATIONS, JsonElement::getAsInt);
        customReaders.put(SpecKeys.MAREEP_WOOL_GROWTH, JsonElement::getAsByte);
        customReaders.put(SpecKeys.MINIOR_COLOR, JsonElement::getAsByte);
        customReaders.put(SpecKeys.LIGHT_TRIO_WORMHOLES, JsonElement::getAsInt);
        customReaders.put(SpecKeys.HP, JsonElement::getAsInt);
        customReaders.put(SpecKeys.EV_HP, JsonElement::getAsInt);
        customReaders.put(SpecKeys.EV_ATK, JsonElement::getAsInt);
        customReaders.put(SpecKeys.EV_DEF, JsonElement::getAsInt);
        customReaders.put(SpecKeys.EV_SPATK, JsonElement::getAsInt);
        customReaders.put(SpecKeys.EV_SPDEF, JsonElement::getAsInt);
        customReaders.put(SpecKeys.EV_SPEED, JsonElement::getAsInt);
        customReaders.put(SpecKeys.IV_HP, JsonElement::getAsInt);
        customReaders.put(SpecKeys.IV_ATK, JsonElement::getAsInt);
        customReaders.put(SpecKeys.IV_DEF, JsonElement::getAsInt);
        customReaders.put(SpecKeys.IV_SPATK, JsonElement::getAsInt);
        customReaders.put(SpecKeys.IV_SPDEF, JsonElement::getAsInt);
        customReaders.put(SpecKeys.IV_SPEED, JsonElement::getAsInt);
        customReaders.put(SpecKeys.HYPER_HP, JsonElement::getAsBoolean);
        customReaders.put(SpecKeys.HYPER_ATTACK, JsonElement::getAsBoolean);
        customReaders.put(SpecKeys.HYPER_DEFENCE, JsonElement::getAsBoolean);
        customReaders.put(SpecKeys.HYPER_SPECIAL_ATTACK, JsonElement::getAsBoolean);
        customReaders.put(SpecKeys.HYPER_SPECIAL_DEFENCE, JsonElement::getAsBoolean);
        customReaders.put(SpecKeys.HYPER_SPEED, JsonElement::getAsBoolean);
        customReaders.put(SpecKeys.DYNAMAX_LEVEL, JsonElement::getAsInt);
        customReaders.put(SpecKeys.CAN_GMAX, JsonElement::getAsBoolean);
        this.register(SpecKeys.MARKS, json -> {
            JsonArray array = json.getAsJsonArray();
            List<Marking> markings = Lists.newArrayList();
            for(JsonElement element : array) {
                JsonObject data = element.getAsJsonObject();
                PixelmonSource source = read(SpecKeys.MARKS, () -> data.get("source"), s -> PixelmonSource.valueOf(s.getAsString()));
                int ordinal = read(SpecKeys.MARKS, () -> data.get("ordinal"), JsonElement::getAsInt);

                Marking.createFor(source, ordinal).ifPresent(markings::add);
            }

            return markings;
        });
        this.register(SpecKeys.RIBBONS, json -> {
            List<Integer> results = Lists.newArrayList();
            JsonArray array = json.getAsJsonArray();
            for(JsonElement element : array) {
                results.add(read(SpecKeys.RIBBONS, () -> element, JsonElement::getAsInt));
            }

            return results;
        });
        this.register(SpecKeys.GENERATIONS_DATA, data -> new JSONWrapper().deserialize((JsonObject) data));
        this.register(SpecKeys.REFORGED_DATA, data -> new JSONWrapper().deserialize((JsonObject) data));
    }

    <T> void register(SpecKey<T> key, Reader<T> reader) {
        customReaders.put(key, reader);
    }

    <T> T read(SpecKey<?> key, Supplier<JsonElement> supplier, Function<JsonElement, T> mapper) {
        return Optional.ofNullable(supplier.get()).map(mapper).orElseThrow(() -> new IllegalStateException("Failed to translate data for key: " + key.getName()));
    }

    <T> T readAndAllowNull(Supplier<JsonElement> supplier, Function<JsonElement, T> mapper) {
        return Optional.ofNullable(supplier.get()).map(mapper).orElse(null);
    }

    <T> T readOrSupply(Supplier<JsonElement> supplier, Function<JsonElement, T> mapper, T def) {
        return Optional.ofNullable(supplier.get()).map(mapper).orElse(def);
    }


}
