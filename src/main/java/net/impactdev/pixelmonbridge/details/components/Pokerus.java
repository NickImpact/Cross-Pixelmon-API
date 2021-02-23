package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.PixelmonSource;

public class Pokerus implements Writable<JObject> {

    /**
     * The value regarding the state/type of the Pokerus infection
     * on a pokemon. Due to differences between the implementations
     * of the Reforged and Generations code, we will let Reforged
     * control values 0, 10, 11, 12, 13. This is to match up with the
     * names of their enum values, A, B, C, and D. For Generations,
     * we will simply use 0, 1, 2.
     * <p/>
     * For generations the types go as follows: <br>
     * - 0 = not infected <br>
     * - 1 = infected <br>
     * - 2 = cured (can't be infected again) <br>
     */
    private int type;
    private int secondsSinceInfection;
    private boolean announced;

    /** The source mod that created this Pokerus data */
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
