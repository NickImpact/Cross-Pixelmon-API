package net.impactdev.pixelmonbridge.reforged;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.impactdev.pixelmonbridge.data.common.BaseDataManager;
import net.impactdev.pixelmonbridge.data.context.ContextualRegistry;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.Query;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;
import net.impactdev.pixelmonbridge.details.components.generic.JSONWrapper;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class ReforgedDataManager extends BaseDataManager<ReforgedPokemon> {

    public ReforgedDataManager() {
        super();
        this.customReaders.put(SpecKeys.EMBEDDED_POKEMON, data -> {
            List<ReforgedPokemon> results = Lists.newArrayList();
            JsonArray array = data.getAsJsonArray();
            for(JsonElement element : array) {
                JsonObject json = element.getAsJsonObject();
                results.add(this.deserialize(json));
            }

            return results;
        });
        this.customReaders.put(SpecKeys.REFORGED_DATA, data -> new JSONWrapper().deserialize((JsonObject) data));
    }

    @Override
    public JObject serialize(ReforgedPokemon pokemon) {
        JObject out = new JObject();

        UUID key = UUID.randomUUID();
        ContextualRegistry.register(key);

        for(Map.Entry<SpecKey<?>, Object> data : pokemon.getAllDetails().entrySet()) {
            Query query = data.getKey().getQuery();
            Object value = data.getValue();

            this.writeToQuery(key, out, query, value);
        }

        ContextualRegistry.complete(key);

        return out;
    }

    @Override
    public ReforgedPokemon deserialize(JsonObject json) {
        ReforgedPokemon result = new ReforgedPokemon();
        if(!json.has("species")) {
            throw new IllegalStateException("JSON data is lacking pokemon species");
        }

        List<String> queries = this.track(json);

        for(SpecKey<?> key : SpecKeys.getKeys()) {
            result.offerUnsafe(key, this.translate(json, key));
            queries.removeIf(s -> s.startsWith(key.getQuery().toString()));
        }

        result.get(SpecKeys.REFORGED_DATA).ifPresent(wrapper -> {
            List<String> convert = this.track(wrapper.serialize().toJson());
            for(SpecKey<?> key : SpecKeys.getKeys()) {
                result.offerUnsafe(key, this.translate(wrapper.serialize().toJson(), key));
                convert.removeIf(s -> s.startsWith(key.getQuery().toString()));
            }
        });

        if(!queries.isEmpty()) {
            JObject incompatible = new JObject();
            for(String query : queries) {
                incompatible.add(query, this.getLeaf(json, query));
            }

            JSONWrapper wrapper = new JSONWrapper().deserialize(incompatible.toJson());
            result.offer(SpecKeys.GENERATIONS_DATA, wrapper);
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

    private JsonElement getLeaf(JsonObject json, String query) {
        String[] components = query.split("[.]");
        JsonElement element = json.get(components[0]);
        for(int i = 1; i < components.length; i++) {
            element = element.getAsJsonObject().get(components[i]);
        }

        return element;
    }

}
