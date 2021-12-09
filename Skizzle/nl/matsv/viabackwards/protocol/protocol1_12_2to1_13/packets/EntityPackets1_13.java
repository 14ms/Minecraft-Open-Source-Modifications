/*
 * Decompiled with CFR 0.150.
 */
package nl.matsv.viabackwards.protocol.protocol1_12_2to1_13.packets;

import java.util.Optional;
import nl.matsv.viabackwards.ViaBackwards;
import nl.matsv.viabackwards.api.entities.storage.EntityPositionHandler;
import nl.matsv.viabackwards.api.exceptions.RemovedValueException;
import nl.matsv.viabackwards.api.rewriters.LegacyEntityRewriter;
import nl.matsv.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import nl.matsv.viabackwards.protocol.protocol1_12_2to1_13.data.EntityTypeMapping;
import nl.matsv.viabackwards.protocol.protocol1_12_2to1_13.data.PaintingMapping;
import nl.matsv.viabackwards.protocol.protocol1_12_2to1_13.data.ParticleMapping;
import nl.matsv.viabackwards.protocol.protocol1_12_2to1_13.storage.BackwardsBlockStorage;
import nl.matsv.viabackwards.protocol.protocol1_12_2to1_13.storage.PlayerPositionStorage1_13;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.entities.Entity1_13Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_12;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.Particle;
import us.myles.ViaVersion.api.type.types.version.Types1_12;
import us.myles.ViaVersion.api.type.types.version.Types1_13;
import us.myles.ViaVersion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ChatRewriter;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;

public class EntityPackets1_13
extends LegacyEntityRewriter<Protocol1_12_2To1_13> {
    public EntityPackets1_13(Protocol1_12_2To1_13 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        ((Protocol1_12_2To1_13)this.protocol).registerOutgoing(ClientboundPackets1_13.PLAYER_POSITION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.BYTE);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (!ViaBackwards.getConfig().isFix1_13FacePlayer()) {
                            return;
                        }
                        PlayerPositionStorage1_13 playerStorage = wrapper.user().get(PlayerPositionStorage1_13.class);
                        byte bitField = wrapper.get(Type.BYTE, 0);
                        playerStorage.setX(this.toSet(bitField, 0, playerStorage.getX(), wrapper.get(Type.DOUBLE, 0)));
                        playerStorage.setY(this.toSet(bitField, 1, playerStorage.getY(), wrapper.get(Type.DOUBLE, 1)));
                        playerStorage.setZ(this.toSet(bitField, 2, playerStorage.getZ(), wrapper.get(Type.DOUBLE, 2)));
                    }

                    private double toSet(int field, int bitIndex, double origin, double packetValue) {
                        return (field & 1 << bitIndex) != 0 ? origin + packetValue : packetValue;
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerOutgoing(ClientboundPackets1_13.SPAWN_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.BYTE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.INT);
                this.handler(EntityPackets1_13.this.getObjectTrackerHandler());
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Optional<Entity1_13Types.ObjectType> optionalType = Entity1_13Types.ObjectType.findById(wrapper.get(Type.BYTE, 0).byteValue());
                        if (!optionalType.isPresent()) {
                            return;
                        }
                        Entity1_13Types.ObjectType type = optionalType.get();
                        if (type == Entity1_13Types.ObjectType.FALLING_BLOCK) {
                            int blockState = wrapper.get(Type.INT, 0);
                            int combined = Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(blockState);
                            combined = combined >> 4 & 0xFFF | (combined & 0xF) << 12;
                            wrapper.set(Type.INT, 0, combined);
                        } else if (type == Entity1_13Types.ObjectType.ITEM_FRAME) {
                            int data = wrapper.get(Type.INT, 0);
                            switch (data) {
                                case 3: {
                                    data = 0;
                                    break;
                                }
                                case 4: {
                                    data = 1;
                                    break;
                                }
                                case 5: {
                                    data = 3;
                                }
                            }
                            wrapper.set(Type.INT, 0, data);
                        } else if (type == Entity1_13Types.ObjectType.TRIDENT) {
                            wrapper.set(Type.BYTE, 0, (byte)Entity1_13Types.ObjectType.TIPPED_ARROW.getId());
                        }
                    }
                });
            }
        });
        this.registerExtraTracker(ClientboundPackets1_13.SPAWN_EXPERIENCE_ORB, Entity1_13Types.EntityType.EXPERIENCE_ORB);
        this.registerExtraTracker(ClientboundPackets1_13.SPAWN_GLOBAL_ENTITY, Entity1_13Types.EntityType.LIGHTNING_BOLT);
        ((Protocol1_12_2To1_13)this.protocol).registerOutgoing(ClientboundPackets1_13.SPAWN_MOB, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.VAR_INT);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int type = wrapper.get(Type.VAR_INT, 1);
                        Entity1_13Types.EntityType entityType = Entity1_13Types.getTypeFromId(type, false);
                        EntityPackets1_13.this.addTrackedEntity(wrapper, wrapper.get(Type.VAR_INT, 0), entityType);
                        int oldId = EntityTypeMapping.getOldId(type);
                        if (oldId == -1) {
                            if (!EntityPackets1_13.this.hasData(entityType)) {
                                ViaBackwards.getPlatform().getLogger().warning("Could not find 1.12 entity type for 1.13 entity type " + type + "/" + entityType);
                            }
                        } else {
                            wrapper.set(Type.VAR_INT, 1, oldId);
                        }
                    }
                });
                this.handler(EntityPackets1_13.this.getMobSpawnRewriter(Types1_12.METADATA_LIST));
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerOutgoing(ClientboundPackets1_13.SPAWN_PLAYER, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
                this.handler(EntityPackets1_13.this.getTrackerAndMetaHandler(Types1_12.METADATA_LIST, Entity1_13Types.EntityType.PLAYER));
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerOutgoing(ClientboundPackets1_13.SPAWN_PAINTING, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.handler(EntityPackets1_13.this.getTrackerHandler(Entity1_13Types.EntityType.PAINTING, Type.VAR_INT));
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int motive = wrapper.read(Type.VAR_INT);
                        String title = PaintingMapping.getStringId(motive);
                        wrapper.write(Type.STRING, title);
                    }
                });
            }
        });
        this.registerJoinGame(ClientboundPackets1_13.JOIN_GAME, Entity1_13Types.EntityType.PLAYER);
        ((Protocol1_12_2To1_13)this.protocol).registerOutgoing(ClientboundPackets1_13.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.handler(EntityPackets1_13.this.getDimensionHandler(0));
                this.handler(wrapper -> wrapper.user().get(BackwardsBlockStorage.class).clear());
            }
        });
        this.registerEntityDestroy(ClientboundPackets1_13.DESTROY_ENTITIES);
        this.registerMetadataRewriter(ClientboundPackets1_13.ENTITY_METADATA, Types1_13.METADATA_LIST, Types1_12.METADATA_LIST);
        ((Protocol1_12_2To1_13)this.protocol).registerOutgoing(ClientboundPackets1_13.FACE_PLAYER, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.cancel();
                        if (!ViaBackwards.getConfig().isFix1_13FacePlayer()) {
                            return;
                        }
                        int anchor = wrapper.read(Type.VAR_INT);
                        double x = wrapper.read(Type.DOUBLE);
                        double y = wrapper.read(Type.DOUBLE);
                        double z = wrapper.read(Type.DOUBLE);
                        PlayerPositionStorage1_13 positionStorage = wrapper.user().get(PlayerPositionStorage1_13.class);
                        PacketWrapper positionAndLook = wrapper.create(47);
                        positionAndLook.write(Type.DOUBLE, 0.0);
                        positionAndLook.write(Type.DOUBLE, 0.0);
                        positionAndLook.write(Type.DOUBLE, 0.0);
                        EntityPositionHandler.writeFacingDegrees(positionAndLook, positionStorage.getX(), anchor == 1 ? positionStorage.getY() + 1.62 : positionStorage.getY(), positionStorage.getZ(), x, y, z);
                        positionAndLook.write(Type.BYTE, (byte)7);
                        positionAndLook.write(Type.VAR_INT, -1);
                        positionAndLook.send(Protocol1_12_2To1_13.class, true, true);
                    }
                });
            }
        });
        if (ViaBackwards.getConfig().isFix1_13FacePlayer()) {
            PacketRemapper movementRemapper = new PacketRemapper(){

                @Override
                public void registerMap() {
                    this.map(Type.DOUBLE);
                    this.map(Type.DOUBLE);
                    this.map(Type.DOUBLE);
                    this.handler(wrapper -> wrapper.user().get(PlayerPositionStorage1_13.class).setCoordinates(wrapper, false));
                }
            };
            ((Protocol1_12_2To1_13)this.protocol).registerIncoming(ServerboundPackets1_12_1.PLAYER_POSITION, movementRemapper);
            ((Protocol1_12_2To1_13)this.protocol).registerIncoming(ServerboundPackets1_12_1.PLAYER_POSITION_AND_ROTATION, movementRemapper);
            ((Protocol1_12_2To1_13)this.protocol).registerIncoming(ServerboundPackets1_12_1.VEHICLE_MOVE, movementRemapper);
        }
    }

    @Override
    protected void registerRewrites() {
        this.mapEntity(Entity1_13Types.EntityType.DROWNED, Entity1_13Types.EntityType.ZOMBIE_VILLAGER).mobName("Drowned");
        this.mapEntity(Entity1_13Types.EntityType.COD, Entity1_13Types.EntityType.SQUID).mobName("Cod");
        this.mapEntity(Entity1_13Types.EntityType.SALMON, Entity1_13Types.EntityType.SQUID).mobName("Salmon");
        this.mapEntity(Entity1_13Types.EntityType.PUFFERFISH, Entity1_13Types.EntityType.SQUID).mobName("Puffer Fish");
        this.mapEntity(Entity1_13Types.EntityType.TROPICAL_FISH, Entity1_13Types.EntityType.SQUID).mobName("Tropical Fish");
        this.mapEntity(Entity1_13Types.EntityType.PHANTOM, Entity1_13Types.EntityType.PARROT).mobName("Phantom").spawnMetadata(storage -> storage.add(new Metadata(15, MetaType1_12.VarInt, 3)));
        this.mapEntity(Entity1_13Types.EntityType.DOLPHIN, Entity1_13Types.EntityType.SQUID).mobName("Dolphin");
        this.mapEntity(Entity1_13Types.EntityType.TURTLE, Entity1_13Types.EntityType.OCELOT).mobName("Turtle");
        this.registerMetaHandler().handle(e -> {
            Metadata meta = e.getData();
            int typeId = meta.getMetaType().getTypeID();
            if (typeId == 5) {
                meta.setMetaType(MetaType1_12.String);
                if (meta.getValue() == null) {
                    meta.setValue("");
                }
            } else if (typeId == 6) {
                meta.setMetaType(MetaType1_12.Slot);
                Item item = (Item)meta.getValue();
                meta.setValue(((Protocol1_12_2To1_13)this.protocol).getBlockItemPackets().handleItemToClient(item));
            } else if (typeId == 15) {
                meta.setMetaType(MetaType1_12.Discontinued);
            } else if (typeId > 5) {
                meta.setMetaType(MetaType1_12.byId(typeId - 1));
            }
            return meta;
        });
        this.registerMetaHandler().filter(Entity1_13Types.EntityType.ENTITY, true, 2).handle(e -> {
            Metadata meta = e.getData();
            String value = meta.getValue().toString();
            if (value.isEmpty()) {
                return meta;
            }
            meta.setValue(ChatRewriter.jsonTextToLegacy(value));
            return meta;
        });
        this.registerMetaHandler().filter(Entity1_13Types.EntityType.ZOMBIE, true, 15).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.ZOMBIE, true).handle(e -> {
            Metadata meta = e.getData();
            if (meta.getId() > 15) {
                meta.setId(meta.getId() - 1);
            }
            return meta;
        });
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.TURTLE, 13).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.TURTLE, 14).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.TURTLE, 15).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.TURTLE, 16).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.TURTLE, 17).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.TURTLE, 18).removed();
        this.registerMetaHandler().filter(Entity1_13Types.EntityType.ABSTRACT_FISHES, true, 12).removed();
        this.registerMetaHandler().filter(Entity1_13Types.EntityType.ABSTRACT_FISHES, true, 13).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.PHANTOM, 12).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.BOAT, 12).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.TRIDENT, 7).removed();
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.WOLF, 17).handle(e -> {
            Metadata meta = e.getData();
            meta.setValue(15 - (Integer)meta.getValue());
            return meta;
        });
        this.registerMetaHandler().filter((EntityType)Entity1_13Types.EntityType.AREA_EFFECT_CLOUD, 9).handle(e -> {
            Metadata meta = e.getData();
            Particle particle = (Particle)meta.getValue();
            ParticleMapping.ParticleData data = ParticleMapping.getMapping(particle.getId());
            int firstArg = 0;
            int secondArg = 0;
            int[] particleArgs = data.rewriteMeta((Protocol1_12_2To1_13)this.protocol, particle.getArguments());
            if (particleArgs != null && particleArgs.length != 0) {
                if (data.getHandler().isBlockHandler() && particleArgs[0] == 0) {
                    particleArgs[0] = 102;
                }
                firstArg = particleArgs[0];
                secondArg = particleArgs.length == 2 ? particleArgs[1] : 0;
            }
            e.createMeta(new Metadata(9, MetaType1_12.VarInt, data.getHistoryId()));
            e.createMeta(new Metadata(10, MetaType1_12.VarInt, firstArg));
            e.createMeta(new Metadata(11, MetaType1_12.VarInt, secondArg));
            throw RemovedValueException.EX;
        });
    }

    @Override
    protected EntityType getTypeFromId(int typeId) {
        return Entity1_13Types.getTypeFromId(typeId, false);
    }

    @Override
    protected EntityType getObjectTypeFromId(int typeId) {
        return Entity1_13Types.getTypeFromId(typeId, true);
    }

    @Override
    public int getOldEntityId(int newId) {
        return EntityTypeMapping.getOldId(newId);
    }
}

