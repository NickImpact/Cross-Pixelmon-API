package net.impactdev.pixelmonbridge.details.components.generic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.common.BaseDataManager;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.Query;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class JSONWrapper extends BaseDataManager<JSONWrapper> implements Writable<JObject> {

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

    @Override
    public JObject serialize() {
        return this.serialize(this);
    }

    public <T> JSONWrapper append(SpecKey<T> key, T value)  {
        this.data.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(SpecKey<T> key) {
        return Optional.ofNullable((T) this.data.get(key));
    }

    @Override
    public JObject serialize(JSONWrapper wrapper) {
        JObject out = new JObject();

        for(Map.Entry<SpecKey<?>, Object> data : wrapper.data.entrySet()) {
            Query query = data.getKey().getQuery();
            Object value = data.getValue();

            this.writeToQuery(out, query, value);
        }

        PARENTS.clear();

        return out;
    }

    @Override
    public JSONWrapper deserialize(JsonObject json) {
        JSONWrapper result = new JSONWrapper();
        List<String> queries = this.track(json);

        for(SpecKey<?> key : SpecKeys.getKeys()) {
            result.offerUnsafe(key, this.translate(json, key));
            queries.removeIf(s -> s.startsWith(key.getQuery().toString()));
        }

        return result;
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

    public JSONWrapper offerUnsafe(SpecKey<?> key, Object value) {
        if(value == null) {
            return this;
        }

        this.data.put(key, value);
        return this;
    }
}
