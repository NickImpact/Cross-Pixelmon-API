package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;

public class EggInfo implements Writable<JObject> {

    private int cycles;
    private int steps;

    public EggInfo(int cycles, int steps) {
        this.cycles = cycles;
        this.steps = steps;
    }

    public int getCycles() {
        return cycles;
    }

    public int getSteps() {
        return steps;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("cycles", this.cycles)
                .add("steps", this.steps);
    }
}
