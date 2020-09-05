package net.impactdev.pixelmonbridge.data;

import net.impactdev.pixelmonbridge.data.factory.JElement;

public interface Writable<T extends JElement> {

    T serialize();

}
