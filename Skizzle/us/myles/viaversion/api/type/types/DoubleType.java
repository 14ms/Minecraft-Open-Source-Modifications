/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.TypeConverter;

public class DoubleType
extends Type<Double>
implements TypeConverter<Double> {
    public DoubleType() {
        super(Double.class);
    }

    @Override
    public Double read(ByteBuf buffer) {
        return buffer.readDouble();
    }

    @Override
    public void write(ByteBuf buffer, Double object) {
        buffer.writeDouble(object.doubleValue());
    }

    @Override
    public Double from(Object o) {
        if (o instanceof Number) {
            return ((Number)o).doubleValue();
        }
        if (o instanceof Boolean) {
            return (Boolean)o != false ? 1.0 : 0.0;
        }
        return (Double)o;
    }
}

