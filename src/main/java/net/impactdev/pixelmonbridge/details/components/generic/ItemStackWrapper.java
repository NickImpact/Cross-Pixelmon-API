package net.impactdev.pixelmonbridge.details.components.generic;

import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.minecraft.item.ItemStack;

public class ItemStackWrapper implements Writable<JObject> {

    private ItemStack item;

    public ItemStackWrapper(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("data", this.item.serializeNBT().toString());
    }
}
