package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;

public class Nature implements Writable<JObject> {

    private String actual;
    private String mint;

    public Nature(String actual, String mint) {
        this.actual = actual;
        this.mint = mint;
    }

    public String getActual() {
        return actual;
    }

    public String getMint() {
        return mint;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("actual", this.actual)
                .add("mint", this.mint);
    }
}
