package net.impactdev.pixelmonbridge.details.components.generic;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.minecraft.nbt.NBTTagCompound;

public class NBTWrapper implements Writable<JObject> {

    private NBTTagCompound nbt;

    public NBTWrapper(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    public NBTTagCompound getNBT() {
        return nbt;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("data", this.nbt.toString());
    }
}
