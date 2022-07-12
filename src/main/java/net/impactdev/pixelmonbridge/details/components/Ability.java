package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;

public class Ability implements Writable<JObject> {

    private String ability;

    public Ability(String ability) {
        this.ability = ability;
    }

    public String getAbility() {
        return ability;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("ability", this.ability);
    }

}
