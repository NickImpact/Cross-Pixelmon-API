/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.impactdev.pixelmonbridge.details;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Represents a query that will ultimately create the path to json represented data.
 *
 * This is based on the Sponge DataQuery object.
 */
public final class Query {

    private static final Query EMPTY = new Query();

    private final ImmutableList<String> parts;

    public Query(String... parts) {
        this.parts = ImmutableList.copyOf(parts);
    }

    public Query(List<String> parts) {
        this.parts = ImmutableList.copyOf(parts);
    }

    public static Query of(String... parts) {
        return new Query(parts);
    }

    public List<String> getParts() {
        return this.parts;
    }

    public Query pop() {
        if(this.parts.size() <= 1) {
            return EMPTY;
        }

        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for(int i = 1; i < this.parts.size(); i++) {
            builder.add(this.parts.get(i));
        }

        return new Query(builder.build());
    }

}
