package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;

public class Ability implements Writable<JObject> {

    private String ability;
    private int slot;

    public Ability(String ability, int slot) {
        this.ability = ability;
        this.slot = slot;
    }

    public String getAbility() {
        return ability;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("ability", this.ability)
                .add("slot", this.slot);
    }

}
