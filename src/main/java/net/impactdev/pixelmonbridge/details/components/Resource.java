package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;

import javax.annotation.Nonnull;

public class Resource implements Writable<JObject> {

    private final String namespace;
    private final String value;

    public Resource(String namespace, String value) {
        this.namespace = namespace;
        this.value = value;
    }

    public Resource(final @Nonnull String input) {
        String[] result = input.split(":");
        this.namespace = result[0];
        this.value = result[1];
    }

    public String namespace() {
        return this.namespace;
    }

    public String value() {
        return this.value;
    }

    public String formatted() {
        return this.namespace + ":" + this.value;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("namespace", this.namespace)
                .add("value", this.value);
    }

}
