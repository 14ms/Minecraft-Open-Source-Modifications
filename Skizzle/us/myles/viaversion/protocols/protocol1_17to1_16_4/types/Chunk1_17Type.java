/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package us.myles.ViaVersion.protocols.protocol1_17to1_16_4.types;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.chunks.BaseChunk;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.minecraft.BaseChunkType;
import us.myles.ViaVersion.api.type.types.version.Types1_16;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public class Chunk1_17Type
extends Type<Chunk> {
    private static final CompoundTag[] EMPTY_COMPOUNDS = new CompoundTag[0];

    public Chunk1_17Type() {
        super(Chunk.class);
    }

    @Override
    public Chunk read(ByteBuf input) throws Exception {
        int chunkX = input.readInt();
        int chunkZ = input.readInt();
        int primaryBitmask = Type.VAR_INT.readPrimitive(input);
        CompoundTag heightMap = (CompoundTag)Type.NBT.read(input);
        int[] biomeData = (int[])Type.VAR_INT_ARRAY_PRIMITIVE.read(input);
        Type.VAR_INT.readPrimitive(input);
        ChunkSection[] sections = new ChunkSection[16];
        for (int i = 0; i < 16; ++i) {
            if ((primaryBitmask & 1 << i) == 0) continue;
            short nonAirBlocksCount = input.readShort();
            ChunkSection section = (ChunkSection)Types1_16.CHUNK_SECTION.read(input);
            section.setNonAirBlocksCount(nonAirBlocksCount);
            sections[i] = section;
        }
        ArrayList<Object> nbtData = new ArrayList<Object>(Arrays.asList((Object[])Type.NBT_ARRAY.read(input)));
        if (input.readableBytes() > 0) {
            byte[] array = (byte[])Type.REMAINING_BYTES.read(input);
            if (Via.getManager().isDebug()) {
                Via.getPlatform().getLogger().warning("Found " + array.length + " more bytes than expected while reading the chunk: " + chunkX + "/" + chunkZ);
            }
        }
        return new BaseChunk(chunkX, chunkZ, true, false, primaryBitmask, sections, biomeData, heightMap, nbtData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(ByteBuf output, Chunk chunk) throws Exception {
        output.writeInt(chunk.getX());
        output.writeInt(chunk.getZ());
        Type.VAR_INT.writePrimitive(output, chunk.getBitmask());
        Type.NBT.write(output, chunk.getHeightMap());
        Type.VAR_INT_ARRAY_PRIMITIVE.write(output, chunk.getBiomeData());
        ByteBuf buf = output.alloc().buffer();
        try {
            for (int i = 0; i < 16; ++i) {
                ChunkSection section = chunk.getSections()[i];
                if (section == null) continue;
                buf.writeShort(section.getNonAirBlocksCount());
                Types1_16.CHUNK_SECTION.write(buf, section);
            }
            buf.readerIndex(0);
            Type.VAR_INT.writePrimitive(output, buf.readableBytes());
            output.writeBytes(buf);
        }
        finally {
            buf.release();
        }
        Type.NBT_ARRAY.write(output, chunk.getBlockEntities().toArray(EMPTY_COMPOUNDS));
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return BaseChunkType.class;
    }
}

