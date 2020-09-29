package net.impactdev.pixelmonbridge.data;

import com.google.gson.JsonElement;

@FunctionalInterface
public interface Reader<T> {

    T read(JsonElement object);


}
