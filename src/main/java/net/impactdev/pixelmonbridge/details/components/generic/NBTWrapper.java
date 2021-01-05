package net.impactdev.pixelmonbridge.details.components.generic;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.Query;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class NBTWrapper implements Writable<JObject> {

    private NBTTagCompound nbt;

    public NBTWrapper(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    public NBTTagCompound getNBT() {
        return nbt;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("data", this.nbt.toString());
    }

    public <T> Optional<T> get(SpecKey<T> key) {
        return Optional.empty();
    }

    public NBTWrapper append(Query query, Object value) {
        Consumer<NBTTagCompound> consumer = n -> {};
        if (Number.class.isAssignableFrom(value.getClass())) {
            consumer = n -> n.setInteger(query.getTail(), ((Number) value).intValue());
        } else if(String.class.isAssignableFrom(value.getClass())) {
            consumer = n -> n.setString(query.getTail(), (String) value);
        } else if(Boolean.class.isAssignableFrom(value.getClass())) {
            consumer = n -> n.setBoolean(query.getTail(), (boolean) value);
        } else if(int[].class.isAssignableFrom(value.getClass())) {
            consumer = n -> n.setIntArray(query.getTail(), (int[]) value);
        }

        NBTTagCompound nbt = this.append$recurse(query, this.nbt, consumer);
        this.nbt.setTag(query.getHead(), nbt);
        return this;
    }

    public NBTTagCompound append$recurse(Query query, NBTTagCompound parent, Consumer<NBTTagCompound> consumer) {
        if(query.getSize() <= 1) {
            return new NBTTagCompound();
        }

        NBTTagCompound result = parent.getCompoundTag(query.getHead());
        result.setTag(query.getHead(), this.append$recurse(query.pop(), result, consumer));

        return result;
    }

    public static NBTWrapper create(JsonObject json) {
        return new NBTWrapper(compose(json));
    }

    private static NBTTagCompound compose(JsonObject parent) {
        NBTTagCompound child = new NBTTagCompound();
        for(Map.Entry<String, JsonElement> entry : parent.entrySet()) {
            JsonElement element = entry.getValue();
            if(element.isJsonObject()) {
                child.setTag(entry.getKey(), compose(element.getAsJsonObject()));
            } else if(element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if(primitive.isBoolean()) {
                    child.setBoolean(entry.getKey(), primitive.getAsBoolean());
                } else if(primitive.isNumber()) {
                    Number number = primitive.getAsNumber();
                    if(number.doubleValue() - number.intValue() > 0) {
                        child.setDouble(entry.getKey(), number.doubleValue());
                    } else {
                        child.setLong(entry.getKey(), number.longValue());
                    }
                } else if(primitive.isString()) {
                    child.setString(entry.getKey(), primitive.getAsString());
                }
            } else if(element.isJsonArray()) {
                List<Integer> values = Lists.newArrayList();
                for(JsonElement content : element.getAsJsonArray()) {
                    if(content.isJsonPrimitive()) {
                        JsonPrimitive primitive = content.getAsJsonPrimitive();
                        if(primitive.isNumber()) {
                            values.add(primitive.getAsInt());
                        }
                    }
                }

                child.setIntArray(entry.getKey(), toIntArray(values));
            }
        }

        return child;
    }

    private static int[] toIntArray(List<Integer> source) {
        int[] ret = new int[source.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = source.get(i);
        return ret;
    }
}
