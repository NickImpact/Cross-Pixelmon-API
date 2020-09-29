package net.impactdev.pixelmonbridge.details.components;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;

import java.util.UUID;

public class Trainer implements Writable<JObject> {

    private UUID uuid;
    private String lastKnownName;

    public Trainer(UUID uuid, String lastKnownName) {
        this.uuid = uuid;
        this.lastKnownName = lastKnownName;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("uuid", this.uuid.toString())
                .add("lastKnownName", this.lastKnownName);
    }
}
