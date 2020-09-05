package net.impactdev.pixelmonbridge;

import net.impactdev.pixelmonbridge.details.SpecKey;

import java.util.Map;
import java.util.Optional;

public interface ImpactDevPokemon<P> {

    P getOrCreate();

    <T> boolean supports(SpecKey<T> key);

    <T> boolean offer(SpecKey<T> key, T data);

    <T> Optional<T> get(SpecKey<T> key);

    Map<SpecKey<?>, Object> getAllDetails();

    void from(P pokemon);

}
