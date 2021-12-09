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

public class VarLongType
extends Type<Long>
implements TypeConverter<Long> {
    public VarLongType() {
        super("VarLong", Long.class);
    }

    public long readPrimitive(ByteBuf buffer) {
        byte in;
        long out = 0L;
        int bytes = 0;
        do {
            in = buffer.readByte();
            out |= (long)(in & 0x7F) << bytes++ * 7;
            if (bytes <= 10) continue;
            throw new RuntimeException("VarLong too big");
        } while ((in & 0x80) == 128);
        return out;
    }

    public void writePrimitive(ByteBuf buffer, long object) {
        do {
            int part = (int)(object & 0x7FL);
            if ((object >>>= 7) != 0L) {
                part |= 0x80;
            }
            buffer.writeByte(part);
        } while (object != 0L);
    }

    @Override
    @Deprecated
    public Long read(ByteBuf buffer) {
        return this.readPrimitive(buffer);
    }

    @Override
    @Deprecated
    public void write(ByteBuf buffer, Long object) {
        this.writePrimitive(buffer, object);
    }

    @Override
    public Long from(Object o) {
        if (o instanceof Number) {
            return ((Number)o).longValue();
        }
        if (o instanceof Boolean) {
            return (Boolean)o != false ? 1L : 0L;
        }
        return (Long)o;
    }
}

