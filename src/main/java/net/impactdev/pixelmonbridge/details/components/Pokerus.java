package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.PixelmonSource;

public class Pokerus implements Writable<JObject> {

    /** The value regarding the state/type of the Pokerus infection on a pokemon */
    private int type;
    private int secondsSinceInfection;
    private boolean announced;

    /**
     * The source mod that created this Pokerus data
     *
     * <p>Due to the vast differences in Pokerus implementations, this source will control
     * whether or not the pokemon will retain the data, or be forced into a cured state. A
     * pokemon will be forced into a cured state only when it is transferred across pixelmon
     * versions, due to the lack of variables missing </p>
     */
    private PixelmonSource source;

    public Pokerus(PixelmonSource source, int type, int secondsSinceInfection, boolean announced) {
        this.source = source;
        this.type = type;
        this.secondsSinceInfection = secondsSinceInfection;
        this.announced = announced;
    }

    public PixelmonSource getSource() {
        return this.source;
    }

    public int getType() {
        if(this.source == PixelmonSource.Reforged && this.type != 0) {
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
                .add("source", this.source.name())
                .add("type", this.type)
                .add("secondsSinceInfection", this.secondsSinceInfection)
                .add("announced", this.announced);
    }
}
