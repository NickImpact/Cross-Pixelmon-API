package net.impactdev.pixelmonbridge.generations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.pixelmongenerations.common.entity.pixelmon.EntityPixelmon;
import net.impactdev.pixelmonbridge.ImpactDevPokemon;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;

import java.util.Map;
import java.util.Optional;

public class GenerationsPokemon implements ImpactDevPokemon<EntityPixelmon> {

    private final ImmutableList<SpecKey<?>> UNSUPPORTED = ImmutableList.copyOf(Lists.newArrayList(
            SpecKeys.POKERUS
    ));

    @Override
    public EntityPixelmon getOrCreate() {
        return null;
    }

    @Override
    public <T> boolean supports(SpecKey<T> key) {
        return false;
    }

    @Override
    public <T> boolean offer(SpecKey<T> key, T data) {
        return false;
    }

    @Override
    public <T> Optional<T> get(SpecKey<T> key) {
        return Optional.empty();
    }

    @Override
    public Map<SpecKey<?>, Object> getAllDetails() {
        return null;
    }

}
