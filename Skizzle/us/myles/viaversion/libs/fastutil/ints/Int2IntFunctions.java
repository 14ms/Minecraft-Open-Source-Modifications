/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  us.myles.viaversion.libs.fastutil.ints.Int2IntFunctions$SynchronizedFunction
 *  us.myles.viaversion.libs.fastutil.ints.Int2IntFunctions$UnmodifiableFunction
 */
package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntUnaryOperator;
import us.myles.viaversion.libs.fastutil.Function;
import us.myles.viaversion.libs.fastutil.ints.AbstractInt2IntFunction;
import us.myles.viaversion.libs.fastutil.ints.Int2IntFunction;
import us.myles.viaversion.libs.fastutil.ints.Int2IntFunctions;

public final class Int2IntFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Int2IntFunctions() {
    }

    public static Int2IntFunction singleton(int key, int value) {
        return new Singleton(key, value);
    }

    public static Int2IntFunction singleton(Integer key, Integer value) {
        return new Singleton(key, value);
    }

    public static Int2IntFunction synchronize(Int2IntFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Int2IntFunction synchronize(Int2IntFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Int2IntFunction unmodifiable(Int2IntFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Int2IntFunction primitive(java.util.function.Function<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        if (f instanceof Int2IntFunction) {
            return (Int2IntFunction)f;
        }
        if (f instanceof IntUnaryOperator) {
            return ((IntUnaryOperator)((Object)f))::applyAsInt;
        }
        return new PrimitiveFunction(f);
    }

    public static class Singleton
    extends AbstractInt2IntFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final int key;
        protected final int value;

        protected Singleton(int key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(int k) {
            return this.key == k;
        }

        @Override
        public int get(int k) {
            return this.key == k ? this.value : this.defRetValue;
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class PrimitiveFunction
    implements Int2IntFunction {
        protected final java.util.function.Function<? super Integer, ? extends Integer> function;

        protected PrimitiveFunction(java.util.function.Function<? super Integer, ? extends Integer> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(int key) {
            return this.function.apply((Integer)key) != null;
        }

        @Override
        @Deprecated
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Integer)key) != null;
        }

        @Override
        public int get(int key) {
            Integer v = this.function.apply((Integer)key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Override
        @Deprecated
        public Integer get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Integer)key);
        }

        @Override
        @Deprecated
        public Integer put(Integer key, Integer value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyFunction
    extends AbstractInt2IntFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public int get(int k) {
            return 0;
        }

        @Override
        public boolean containsKey(int k) {
            return false;
        }

        @Override
        public int defaultReturnValue() {
            return 0;
        }

        @Override
        public void defaultReturnValue(int defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        public Object clone() {
            return EMPTY_FUNCTION;
        }

        public int hashCode() {
            return 0;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Function)) {
                return false;
            }
            return ((Function)o).size() == 0;
        }

        public String toString() {
            return "{}";
        }

        private Object readResolve() {
            return EMPTY_FUNCTION;
        }
    }
}

