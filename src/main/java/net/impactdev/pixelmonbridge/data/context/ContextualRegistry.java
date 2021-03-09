package net.impactdev.pixelmonbridge.data.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ContextualRegistry {

    private static Map<UUID, Context> registry = new HashMap<>();

    public static void register(UUID uuid) {
        registry.put(uuid, new Context());
    }

    public static Context get(UUID uuid) {
        return Optional.ofNullable(registry.get(uuid)).orElseThrow(() -> new IllegalArgumentException("No context available"));
    }

    public static void complete(UUID uuid) {
        registry.remove(uuid);
    }

}
