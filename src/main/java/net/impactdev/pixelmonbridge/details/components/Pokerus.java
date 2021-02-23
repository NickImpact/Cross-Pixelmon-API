package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;

public class Pokerus implements Writable<JObject> {

    /** The ordinal value (Reforged) of the type of pokerus */
    private int type;
    private int secondsSinceInfection;
    private boolean announced;

    public Pokerus(int type, int secondsSinceInfection, boolean announced) {
        this.type = type;
        this.secondsSinceInfection = secondsSinceInfection;
        this.announced = announced;
    }

    public int getType(boolean reforged) {
        if(reforged && this.type != 0) {
            return this.type - 0xA;
        }
        return type;
    }

    public int getSecondsSinceInfection() {
        return secondsSinceInfection;
    }

    public boolean isAnnounced() {
        return announced;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("type", this.type)
                .add("secondsSinceInfection", this.secondsSinceInfection)
                .add("announced", this.announced);
    }
}
