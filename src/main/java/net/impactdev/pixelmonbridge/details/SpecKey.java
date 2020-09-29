package net.impactdev.pixelmonbridge.details;

import com.google.common.base.Preconditions;
import com.google.gson.reflect.TypeToken;

import java.util.Objects;

public final class SpecKey<T> {

    public static final TypeToken<Integer> INT_TYPE = new TypeToken<Integer>(){};
    public static final TypeToken<String> STRING_TYPE = new TypeToken<String>(){};
    public static final TypeToken<Boolean> BOOLEAN_TYPE = new TypeToken<Boolean>(){};

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecKey<?> specKey = (SpecKey<?>) o;
        return Objects.equals(name, specKey.name) &&
                Objects.equals(query, specKey.query) &&
                Objects.equals(type, specKey.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, query, type);
    }
}
