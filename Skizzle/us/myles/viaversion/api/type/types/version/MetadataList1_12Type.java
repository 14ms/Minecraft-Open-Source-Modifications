/*
 * Decompiled with CFR 0.150.
 */
package us.myles.ViaVersion.api.type.types.version;

import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.minecraft.ModernMetaListType;
import us.myles.ViaVersion.api.type.types.version.Types1_12;

public class MetadataList1_12Type
extends ModernMetaListType {
    @Override
    protected Type<Metadata> getType() {
        return Types1_12.METADATA;
    }
}

