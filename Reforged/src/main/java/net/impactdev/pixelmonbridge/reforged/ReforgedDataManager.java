package net.impactdev.pixelmonbridge.reforged;

import com.google.gson.JsonObject;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.impactdev.pixelmonbridge.data.DataManager;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.Query;
import net.impactdev.pixelmonbridge.details.SpecKey;

import java.util.Map;

public class ReforgedDataManager implements DataManager<ReforgedPokemon> {

    @Override
    public JObject serialize(ReforgedPokemon pokemon) {
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
    public ReforgedPokemon deserialize(JsonObject json) {
        if(json.has("species")) {
            ReforgedPokemon output = new ReforgedPokemon(EnumSpecies.getFromName(json.get("species").getAsString()).orElseThrow(() -> new RuntimeException("Invalid pokemon species provided")));

        }


        return null;
    }

}
