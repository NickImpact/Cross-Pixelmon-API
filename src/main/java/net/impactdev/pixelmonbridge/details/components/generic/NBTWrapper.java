package net.impactdev.pixelmonbridge.details.components.generic;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.minecraft.nbt.CompoundNBT;

public class NBTWrapper implements Writable<JObject> {

    private CompoundNBT nbt;

    public NBTWrapper() {
        this.nbt = new CompoundNBT();
    }

    public NBTWrapper(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public CompoundNBT getNBT() {
        return nbt;
    }

    @Override
    public JObject serialize() {
        return new JObject().add("data", this.nbt.toString());
    }

}
