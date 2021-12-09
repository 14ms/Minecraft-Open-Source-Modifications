/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.gson;

import us.myles.viaversion.libs.gson.JsonElement;

public final class JsonNull
extends JsonElement {
    public static final JsonNull INSTANCE = new JsonNull();

    @Deprecated
    public JsonNull() {
    }

    @Override
    public JsonNull deepCopy() {
        return INSTANCE;
    }

    public int hashCode() {
        return JsonNull.class.hashCode();
    }

    public boolean equals(Object other) {
        return this == other || other instanceof JsonNull;
    }
}

