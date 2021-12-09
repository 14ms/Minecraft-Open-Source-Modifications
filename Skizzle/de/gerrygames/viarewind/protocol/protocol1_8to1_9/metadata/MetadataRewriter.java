/*
 * Decompiled with CFR 0.150.
 */
package de.gerrygames.viarewind.protocol.protocol1_8to1_9.metadata;

import de.gerrygames.viarewind.ViaRewind;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.items.ItemRewriter;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.metadata.MetaIndex1_8to1_9;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import us.myles.ViaVersion.api.entities.Entity1_10Types;
import us.myles.ViaVersion.api.minecraft.EulerAngle;
import us.myles.ViaVersion.api.minecraft.Vector;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_8;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.metadata.MetaIndex;

public class MetadataRewriter {
    public static void transform(Entity1_10Types.EntityType type, List<Metadata> list) {
        for (Metadata entry : new ArrayList<Metadata>(list)) {
            MetaIndex metaIndex = MetaIndex1_8to1_9.searchIndex(type, entry.getId());
            try {
                if (metaIndex != null) {
                    if (metaIndex.getOldType() == MetaType1_8.NonExistent || metaIndex.getNewType() == MetaType1_9.Discontinued) {
                        list.remove(entry);
                        continue;
                    }
                    Object value = entry.getValue();
                    entry.setMetaType(metaIndex.getOldType());
                    entry.setId(metaIndex.getIndex());
                    switch (metaIndex.getNewType()) {
                        case Byte: {
                            if (metaIndex.getOldType() == MetaType1_8.Byte) {
                                entry.setValue(value);
                            }
                            if (metaIndex.getOldType() != MetaType1_8.Int) break;
                            entry.setValue(((Byte)value).intValue());
                            break;
                        }
                        case OptUUID: {
                            if (metaIndex.getOldType() != MetaType1_8.String) {
                                list.remove(entry);
                                break;
                            }
                            UUID owner = (UUID)value;
                            if (owner == null) {
                                entry.setValue("");
                                break;
                            }
                            entry.setValue(owner.toString());
                            break;
                        }
                        case BlockID: {
                            list.remove(entry);
                            list.add(new Metadata(metaIndex.getIndex(), MetaType1_8.Short, ((Integer)value).shortValue()));
                            break;
                        }
                        case VarInt: {
                            if (metaIndex.getOldType() == MetaType1_8.Byte) {
                                entry.setValue(((Integer)value).byteValue());
                            }
                            if (metaIndex.getOldType() == MetaType1_8.Short) {
                                entry.setValue(((Integer)value).shortValue());
                            }
                            if (metaIndex.getOldType() != MetaType1_8.Int) break;
                            entry.setValue(value);
                            break;
                        }
                        case Float: {
                            entry.setValue(value);
                            break;
                        }
                        case String: {
                            entry.setValue(value);
                            break;
                        }
                        case Boolean: {
                            if (metaIndex == MetaIndex.AGEABLE_AGE) {
                                entry.setValue((byte)((Boolean)value != false ? -1 : 0));
                                break;
                            }
                            entry.setValue((byte)((Boolean)value != false ? 1 : 0));
                            break;
                        }
                        case Slot: {
                            entry.setValue(ItemRewriter.toClient((Item)value));
                            break;
                        }
                        case Position: {
                            Vector vector = (Vector)value;
                            entry.setValue(vector);
                            break;
                        }
                        case Vector3F: {
                            EulerAngle angle = (EulerAngle)value;
                            entry.setValue(angle);
                            break;
                        }
                        case Chat: {
                            entry.setValue(value);
                            break;
                        }
                        default: {
                            ViaRewind.getPlatform().getLogger().warning("[Out] Unhandled MetaDataType: " + metaIndex.getNewType());
                            list.remove(entry);
                        }
                    }
                    if (metaIndex.getOldType().getType().getOutputClass().isAssignableFrom(entry.getValue().getClass())) continue;
                    list.remove(entry);
                    continue;
                }
                throw new Exception("Could not find valid metadata");
            }
            catch (Exception e) {
                list.remove(entry);
            }
        }
    }
}

