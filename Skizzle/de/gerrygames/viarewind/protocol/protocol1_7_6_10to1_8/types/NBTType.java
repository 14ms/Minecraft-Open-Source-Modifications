/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import us.myles.ViaVersion.api.type.Type;
import us.myles.viaversion.libs.opennbt.NBTIO;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class NBTType
extends Type<CompoundTag> {
    public NBTType() {
        super(CompoundTag.class);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompoundTag read(ByteBuf buffer) {
        short length = buffer.readShort();
        if (length < 0) {
            return null;
        }
        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(buffer);
        DataInputStream dataInputStream = new DataInputStream((InputStream)byteBufInputStream);
        try {
            CompoundTag compoundTag = (CompoundTag)NBTIO.readTag(dataInputStream);
            return compoundTag;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        finally {
            try {
                dataInputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void write(ByteBuf buffer, CompoundTag nbt) throws Exception {
        if (nbt == null) {
            buffer.writeShort(-1);
        } else {
            ByteBuf buf = buffer.alloc().buffer();
            ByteBufOutputStream bytebufStream = new ByteBufOutputStream(buf);
            DataOutputStream dataOutputStream = new DataOutputStream((OutputStream)bytebufStream);
            NBTIO.writeTag(dataOutputStream, (Tag)nbt);
            dataOutputStream.close();
            buffer.writeShort(buf.readableBytes());
            buffer.writeBytes(buf);
            buf.release();
        }
    }
}

