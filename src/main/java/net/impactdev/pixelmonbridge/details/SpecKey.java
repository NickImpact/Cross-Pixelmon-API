package net.impactdev.pixelmonbridge.details;

import com.google.common.base.Preconditions;
import com.google.gson.reflect.TypeToken;

import java.util.Objects;
import java.util.UUID;

public final class SpecKey<T> {

    public static final TypeToken<Integer> INT_TYPE = new TypeToken<Integer>(){};
    public static final TypeToken<String> STRING_TYPE = new TypeToken<String>(){};
    public static final TypeToken<Boolean> BOOLEAN_TYPE = new TypeToken<Boolean>(){};

    private final UUID uuid = UUID.randomUUID();

    private final String name;
    private final Query query;
    private final TypeToken<T> type;

    private final int priority;
    private final int version;

    public SpecKey(Builder<T> builder) {
        this.name = builder.name;
        this.query = builder.query;
        this.type = new TypeToken<T>(){};
        this.priority = builder.priority;
        this.version = builder.version;
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

    public int getPriority() {
        return this.priority;
    }

    public int getVersion() {
        return this.version;
    }

    static <V> Builder<V> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private String name;
        private Query query;
        private int priority;
        private int version;

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

        public Builder<T> priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder<T> version(int version) {
            this.version = version;
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
        return priority == specKey.priority && version == specKey.version && uuid.equals(specKey.uuid) && name.equals(specKey.name) && query.equals(specKey.query) && type.equals(specKey.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, query, type, priority, version);
    }
}
