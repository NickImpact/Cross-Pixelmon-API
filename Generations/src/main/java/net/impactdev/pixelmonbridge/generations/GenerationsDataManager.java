package net.impactdev.pixelmonbridge.generations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.impactdev.pixelmonbridge.data.DataManager;
import net.impactdev.pixelmonbridge.data.Reader;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.Query;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GenerationsDataManager implements DataManager<GenerationsPokemon> {

    private static Map<SpecKey<?>, Reader<?>> customReaders = Maps.newHashMap();
    static {
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
        customReaders.put(SpecKeys.POKEBALL, JsonElement::getAsInt);
        customReaders.put(SpecKeys.TRAINER, data -> new Trainer(
                read(SpecKeys.TRAINER, () -> data.getAsJsonObject().get("uuid"), x -> UUID.fromString(x.getAsString())),
                read(SpecKeys.TRAINER, () -> data.getAsJsonObject().get("lastKnownName"), JsonElement::getAsString)
        ));
        customReaders.put(SpecKeys.EGG_INFO, data -> new EggInfo(
                read(SpecKeys.EGG_INFO, () -> data.getAsJsonObject().get("cycles"), JsonElement::getAsInt),
                read(SpecKeys.EGG_INFO, () -> data.getAsJsonObject().get("steps"), JsonElement::getAsInt)
        ));
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
        customReaders.put(SpecKeys.HELD_ITEM, data -> {
            try {
                ItemStack item = ItemStack.EMPTY;
                item.deserializeNBT(JsonToNBT.getTagFromJson(read(SpecKeys.HELD_ITEM, () -> data.getAsJsonObject().get("data"), JsonElement::getAsString)));
                return new ItemStackWrapper(item);
            } catch (NBTException e) {
                throw new RuntimeException(e);
            }
        });
        customReaders.put(SpecKeys.STATUS, JsonElement::getAsInt);
        customReaders.put(SpecKeys.MEW_CLONES, JsonElement::getAsInt);
        customReaders.put(SpecKeys.LAKE_TRIO_ENCHANTS, JsonElement::getAsInt);
        customReaders.put(SpecKeys.MELTAN_ORES_SMELTED, JsonElement::getAsInt);
        customReaders.put(SpecKeys.MAREEP_WOOL_GROWTH, JsonElement::getAsByte);
        customReaders.put(SpecKeys.MINIOR_COLOR, JsonElement::getAsByte);
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
        customReaders.put(SpecKeys.GENERATIONS_DATA, json -> {
            try {
                return new NBTWrapper(JsonToNBT.getTagFromJson(read(SpecKeys.GENERATIONS_DATA, () -> json.getAsJsonObject().get("data"), JsonElement::getAsString)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public JObject serialize(GenerationsPokemon pokemon) {
        JObject out = new JObject();

        for(Map.Entry<SpecKey<?>, Object> data : pokemon.getAllDetails().entrySet()) {
            Query query = data.getKey().getQuery();
            Object value = data.getValue();

            this.writeToQuery(out, query, value);
        }

        PARENTS.clear();

        return out;
    }

    @Override
    public GenerationsPokemon deserialize(JsonObject json) {
        GenerationsPokemon result = new GenerationsPokemon();
        if(!json.has("species")) {
            throw new IllegalStateException("JSON data is lacking pokemon species");
        }

        List<String> queries = this.track(json);

        for(SpecKey<?> key : SpecKeys.getKeys()) {
            result.offerUnsafe(key, this.translate(json, key));

            queries.removeIf(s -> s.startsWith(key.getQuery().toString()));
        }

        if(!queries.isEmpty()) {
            JObject incompatible = new JObject();
            for(String query : queries) {
                incompatible.add(query, this.getLeaf(json, query));
            }

            NBTWrapper wrapper = NBTWrapper.create(incompatible.toJson());
            result.offer(SpecKeys.REFORGED_DATA, wrapper);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T translate(JsonObject data, SpecKey<T> key) {
        Query query = key.getQuery();
        return (T) this.translate$1(data, query, key);
    }

    private Object translate$1(JsonObject data, Query query, SpecKey<?> key) {
        String index = query.getHead();
        if(!data.has(index)) {
            return null;
        }

        if(query.getParts().size() > 1) {
            return this.translate$1(data.get(index).getAsJsonObject(), query.pop(), key);
        } else {
            if(customReaders.containsKey(key)) {
                return customReaders.get(key).read(data.get(index));
            } else {
                throw new IllegalStateException("Unsure how to deserialize key: " + key.getName());
            }
        }
    }

    private List<String> track(JsonObject json) {
        List<String> track = Lists.newArrayList();
        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if(entry.getValue().isJsonObject()) {
                track.addAll(this.track$1(entry.getValue().getAsJsonObject(), entry.getKey()));
            } else {
                track.add(entry.getKey());
            }
        }

        return track;
    }

    private List<String> track$1(JsonObject json, String current) {
        List<String> results = Lists.newArrayList();

        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if(entry.getValue().isJsonObject()) {
                List<String> next = this.track$1(entry.getValue().getAsJsonObject(), entry.getKey());
                next.forEach(s -> {
                    StringJoiner x = new StringJoiner(".");
                    x.add(current);
                    x.add(s);
                    results.add(x.toString());
                });
            } else {
                StringJoiner x = new StringJoiner(".");
                x.add(current);
                x.add(entry.getKey());
                results.add(x.toString());
            }
        }

        return results;
    }

    private static <T> T read(SpecKey<?> key, Supplier<JsonElement> supplier, Function<JsonElement, T> mapper) {
        return Optional.ofNullable(supplier.get()).map(mapper).orElseThrow(() -> new IllegalStateException("Failed to translate data for key: " + key.getName()));
    }

    private static <T> T readAndAllowNull(Supplier<JsonElement> supplier, Function<JsonElement, T> mapper) {
        return Optional.ofNullable(supplier.get()).map(mapper).orElse(null);
    }

    private JsonElement getLeaf(JsonObject json, String query) {
        String[] components = query.split("[.]");
        JsonElement element = json.get(components[0]);
        for(int i = 1; i < components.length; i++) {
            element = element.getAsJsonObject().get(components[i]);
        }

        return element;
    }
}
