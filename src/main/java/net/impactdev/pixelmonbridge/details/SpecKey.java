package net.impactdev.pixelmonbridge.details;

import com.google.common.base.Preconditions;
import com.google.gson.reflect.TypeToken;

public final class SpecKey<T> {

    private final String name;
    private final Query query;
    private final TypeToken<T> type;

    public SpecKey(Builder<T> builder) {
        this.name = builder.name;
        this.query = builder.query;
        this.type = new TypeToken<T>(){};
    }

    public String getName() {
        return this.name;
    }

    public TypeToken<T> getType() {
        return this.type;
    }

    public Query getQuery() {
        return this.query;
    }

    static <V> Builder<V> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private String name;
        private Query query;

        public <B> Builder<B> type(TypeToken<B> type) {
            return new Builder<>();
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> query(Query query) {
            this.query = query;
            return this;
        }

        public SpecKey<T> build() {
            Preconditions.checkNotNull(this.name);
            Preconditions.checkNotNull(this.query);

            return new SpecKey<>(this);
        }

    }

}
