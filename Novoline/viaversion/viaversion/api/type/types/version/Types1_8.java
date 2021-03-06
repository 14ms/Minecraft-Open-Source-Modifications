package viaversion.viaversion.api.type.types.version;

import viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import viaversion.viaversion.api.minecraft.metadata.Metadata;
import viaversion.viaversion.api.type.Type;

import java.util.List;

public class Types1_8 {
    /**
     * Metadata list type for 1.8
     */
    public static final Type<List<Metadata>> METADATA_LIST = new MetadataList1_8Type();

    /**
     * Metadata type for 1.8
     */
    public static final Type<Metadata> METADATA = new Metadata1_8Type();

    public static final Type<ChunkSection> CHUNK_SECTION = new ChunkSectionType1_8();
}
