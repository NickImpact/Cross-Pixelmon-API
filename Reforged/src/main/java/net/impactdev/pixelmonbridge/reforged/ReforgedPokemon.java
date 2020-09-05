package net.impactdev.pixelmonbridge.reforged;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.impactdev.pixelmonbridge.ImpactDevPokemon;
import net.impactdev.pixelmonbridge.details.SpecKey;
import net.impactdev.pixelmonbridge.details.SpecKeys;
import net.impactdev.pixelmonbridge.details.components.Moves;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class ReforgedPokemon implements ImpactDevPokemon<Pokemon> {

    private final ImmutableList<SpecKey<?>> UNSUPPORTED = ImmutableList.copyOf(Lists.newArrayList(

    ));

    private final Map<SpecKey<?>, Object> data = Maps.newTreeMap(Comparator.comparing(k -> k.getQuery().getParts().get(0)));
    private transient Pokemon pokemon;

    public ReforgedPokemon() {}

    public ReforgedPokemon(EnumSpecies species) {
        this.offer(SpecKeys.SPECIES, species.name);
    }

    @Override
    public Pokemon getOrCreate() {
        EnumSpecies species = this.get(SpecKeys.SPECIES).flatMap(EnumSpecies::getFromName).orElseThrow(() -> new RuntimeException("Species is not yet populated..."));
        Pokemon pokemon = this.pokemon == null ? this.pokemon = Pixelmon.pokemonFactory.create(species) : this.pokemon;
        return writeAll(pokemon);
    }

    @Override
    public <T> boolean supports(SpecKey<T> key) {
        return !this.UNSUPPORTED.contains(key);
    }

    @Override
    public <T> boolean offer(SpecKey<T> key, T data) {
        if(this.supports(key)) {
            if(SpecKeys.SPECIES.equals(key)) {
                if(!EnumSpecies.getFromName((String) data).isPresent()) {
                    return false;
                }
            }

            this.data.put(key, data);
            return true;
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(SpecKey<T> key) {
        return Optional.ofNullable((T) this.data.get(key));
    }

    @Override
    public Map<SpecKey<?>, Object> getAllDetails() {
        return this.data;
    }

    @Override
    public void from(Pokemon pokemon) {

    }

    private Pokemon writeAll(Pokemon pokemon) {
        for(Map.Entry<SpecKey<?>, Object> entry : this.data.entrySet()) {
            SpecKey<?> key = entry.getKey();
            Object value = entry.getValue();

            if(SpecKeys.MOVESET.equals(key)) {
                Moves moves = (Moves) value;
                for(Moves.Move move : moves.getMoves()) {
                    if (move == null) {
                        continue;
                    }

                    Attack attack = new Attack(move.getID());
                    attack.pp = move.getPP();
                    attack.ppLevel = move.getPPLevel();
                    pokemon.getMoveset().add(attack);
                }
            } else if(SpecKeys.IV_HP.equals(key)) {
                pokemon.getStats().ivs.hp = (int) value;
            } else if(SpecKeys.IV_ATK.equals(key)) {
                pokemon.getStats().ivs.attack = (int) value;
            } else if(SpecKeys.IV_DEF.equals(key)) {
                pokemon.getStats().ivs.defence = (int) value;
            }
        }

        return pokemon;
    }

}
