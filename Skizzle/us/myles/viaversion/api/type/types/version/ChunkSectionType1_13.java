/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package us.myles.ViaVersion.api.type.types.version;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.util.CompactArrayUtil;

public class ChunkSectionType1_13
extends Type<ChunkSection> {
    private static final int GLOBAL_PALETTE = 14;

    public ChunkSectionType1_13() {
        super("Chunk Section Type", ChunkSection.class);
    }

    @Override
    public ChunkSection read(ByteBuf buffer) throws Exception {
        long[] blockData;
        ChunkSection chunkSection;
        int bitsPerBlock;
        int originalBitsPerBlock = bitsPerBlock = buffer.readUnsignedByte();
        if (bitsPerBlock == 0 || bitsPerBlock > 8) {
            bitsPerBlock = 14;
        }
        if (bitsPerBlock != 14) {
            int paletteLength = Type.VAR_INT.readPrimitive(buffer);
            chunkSection = new ChunkSection(paletteLength);
            for (int i = 0; i < paletteLength; ++i) {
                chunkSection.addPaletteEntry(Type.VAR_INT.readPrimitive(buffer));
            }
        } else {
            chunkSection = new ChunkSection();
        }
        if ((blockData = new long[Type.VAR_INT.readPrimitive(buffer)]).length > 0) {
            int expectedLength = (int)Math.ceil((double)(4096 * bitsPerBlock) / 64.0);
            if (blockData.length != expectedLength) {
                throw new IllegalStateException("Block data length (" + blockData.length + ") does not match expected length (" + expectedLength + ")! bitsPerBlock=" + bitsPerBlock + ", originalBitsPerBlock=" + originalBitsPerBlock);
            }
            for (int i = 0; i < blockData.length; ++i) {
                blockData[i] = buffer.readLong();
            }
            CompactArrayUtil.iterateCompactArray(bitsPerBlock, 4096, blockData, bitsPerBlock == 14 ? (arg_0, arg_1) -> chunkSection.setFlatBlock(arg_0, arg_1) : (arg_0, arg_1) -> chunkSection.setPaletteIndex(arg_0, arg_1));
        }
        return chunkSection;
    }

    @Override
    public void write(ByteBuf buffer, ChunkSection chunkSection) throws Exception {
        int bitsPerBlock = 4;
        while (chunkSection.getPaletteSize() > 1 << bitsPerBlock) {
            ++bitsPerBlock;
        }
        if (bitsPerBlock > 8) {
            bitsPerBlock = 14;
        }
        buffer.writeByte(bitsPerBlock);
        if (bitsPerBlock != 14) {
            Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteSize());
            for (int i = 0; i < chunkSection.getPaletteSize(); ++i) {
                Type.VAR_INT.writePrimitive(buffer, chunkSection.getPaletteEntry(i));
            }
        }
        long[] data = CompactArrayUtil.createCompactArray(bitsPerBlock, 4096, bitsPerBlock == 14 ? chunkSection::getFlatBlock : chunkSection::getPaletteIndex);
        Type.VAR_INT.writePrimitive(buffer, data.length);
        for (long l : data) {
            buffer.writeLong(l);
        }
    }
}

