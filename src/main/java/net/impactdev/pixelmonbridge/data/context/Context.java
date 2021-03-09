package net.impactdev.pixelmonbridge.data.context;

import net.impactdev.pixelmonbridge.data.factory.JObject;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private Map<String, JObject> context = new HashMap<>();

    public Map<String, JObject> get() {
        return this.context;
    }

}
