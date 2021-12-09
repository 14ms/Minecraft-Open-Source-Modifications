/*
 * Decompiled with CFR 0.150.
 */
package nl.matsv.viabackwards.protocol.protocol1_13_1to1_13_2;

import nl.matsv.viabackwards.api.BackwardsProtocol;
import nl.matsv.viabackwards.protocol.protocol1_13_1to1_13_2.packets.EntityPackets1_13_2;
import nl.matsv.viabackwards.protocol.protocol1_13_1to1_13_2.packets.InventoryPackets1_13_2;
import nl.matsv.viabackwards.protocol.protocol1_13_1to1_13_2.packets.WorldPackets1_13_2;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;

public class Protocol1_13_1To1_13_2
extends BackwardsProtocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13> {
    public Protocol1_13_1To1_13_2() {
        super(ClientboundPackets1_13.class, ClientboundPackets1_13.class, ServerboundPackets1_13.class, ServerboundPackets1_13.class);
    }

    @Override
    protected void registerPackets() {
        InventoryPackets1_13_2.register(this);
        WorldPackets1_13_2.register(this);
        EntityPackets1_13_2.register(this);
        this.registerIncoming(ServerboundPackets1_13.EDIT_BOOK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.FLAT_ITEM, Type.FLAT_VAR_INT_ITEM);
            }
        });
        this.registerOutgoing(ClientboundPackets1_13.ADVANCEMENTS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.passthrough(Type.BOOLEAN);
                        int size = wrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < size; ++i) {
                            wrapper.passthrough(Type.STRING);
                            if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                                wrapper.passthrough(Type.STRING);
                            }
                            if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                                wrapper.passthrough(Type.COMPONENT);
                                wrapper.passthrough(Type.COMPONENT);
                                Item icon = wrapper.read(Type.FLAT_VAR_INT_ITEM);
                                wrapper.write(Type.FLAT_ITEM, icon);
                                wrapper.passthrough(Type.VAR_INT);
                                int flags = wrapper.passthrough(Type.INT);
                                if ((flags & 1) != 0) {
                                    wrapper.passthrough(Type.STRING);
                                }
                                wrapper.passthrough(Type.FLOAT);
                                wrapper.passthrough(Type.FLOAT);
                            }
                            wrapper.passthrough(Type.STRING_ARRAY);
                            int arrayLength = wrapper.passthrough(Type.VAR_INT);
                            for (int array = 0; array < arrayLength; ++array) {
                                wrapper.passthrough(Type.STRING_ARRAY);
                            }
                        }
                    }
                });
            }
        });
    }
}

