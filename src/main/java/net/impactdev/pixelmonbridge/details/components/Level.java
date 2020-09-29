package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;

public class Level implements Writable<JObject> {

    private int level;
    private int experience;
    private boolean doesLevel;

    public Level(int level, int experience, boolean doesLevel) {
        this.level = level;
        this.experience = experience;
        this.doesLevel = doesLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public boolean doesLevel() {
        return this.doesLevel;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("level", this.level)
                .add("experience", this.experience)
                .add("does-level", this.doesLevel);
    }
}
