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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.StringJoiner;

/**
 * Represents a query that will ultimately create the path to json represented data.
 *
 * This is based on the Sponge DataQuery object.
 */
public final class Query implements Iterable<Query> {

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

    public String getHead() {
        return this.parts.get(0);
    }

    public String getTail() {
        return this.parts.get(this.parts.size() - 1);
    }

    public List<String> getParts() {
        return this.parts;
    }

    public int getSize() {
        return this.parts.size();
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

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(".");
        for(String s : this.parts) {
            joiner.add(s);
        }
        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return Objects.equals(parts, query.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parts);
    }

    @Override
    public Iterator<Query> iterator() {
        return new QueryIterator(this);
    }

    public static class QueryIterator implements Iterator<Query> {

        public Query query;

        public QueryIterator(Query query) {
            this.query = query;
        }

        @Override
        public boolean hasNext() {
            return this.query.getSize() > 0;
        }

        @Override
        public Query next() {
            Query result = this.query;
            this.query = this.query.pop();
            return result;
        }

    }
}
