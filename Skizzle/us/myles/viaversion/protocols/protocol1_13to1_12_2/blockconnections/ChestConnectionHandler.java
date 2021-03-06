/*
 * Decompiled with CFR 0.150.
 */
package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.BlockFace;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.WrappedBlockData;

class ChestConnectionHandler
extends ConnectionHandler {
    private static final Map<Integer, BlockFace> chestFacings = new HashMap<Integer, BlockFace>();
    private static final Map<Byte, Integer> connectedStates = new HashMap<Byte, Integer>();
    private static final Set<Integer> trappedChests = new HashSet<Integer>();

    ChestConnectionHandler() {
    }

    static ConnectionData.ConnectorInitAction init() {
        ChestConnectionHandler connectionHandler = new ChestConnectionHandler();
        return blockData -> {
            if (!blockData.getMinecraftKey().equals("minecraft:chest") && !blockData.getMinecraftKey().equals("minecraft:trapped_chest")) {
                return;
            }
            if (blockData.getValue("waterlogged").equals("true")) {
                return;
            }
            chestFacings.put(blockData.getSavedBlockStateId(), BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)));
            if (blockData.getMinecraftKey().equalsIgnoreCase("minecraft:trapped_chest")) {
                trappedChests.add(blockData.getSavedBlockStateId());
            }
            connectedStates.put(ChestConnectionHandler.getStates(blockData), blockData.getSavedBlockStateId());
            ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), (ConnectionHandler)connectionHandler);
        };
    }

    private static Byte getStates(WrappedBlockData blockData) {
        byte states = 0;
        String type = blockData.getValue("type");
        if (type.equals("left")) {
            states = (byte)(states | true ? 1 : 0);
        }
        if (type.equals("right")) {
            states = (byte)(states | 2);
        }
        states = (byte)(states | BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)).ordinal() << 2);
        if (blockData.getMinecraftKey().equals("minecraft:trapped_chest")) {
            states = (byte)(states | 0x10);
        }
        return states;
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        int relative;
        BlockFace facing = chestFacings.get(blockState);
        byte states = 0;
        states = (byte)(states | facing.ordinal() << 2);
        boolean trapped = trappedChests.contains(blockState);
        if (trapped) {
            states = (byte)(states | 0x10);
        }
        if (chestFacings.containsKey(relative = this.getBlockData(user, position.getRelative(BlockFace.NORTH))) && trapped == trappedChests.contains(relative)) {
            states = (byte)(states | (facing == BlockFace.WEST ? 1 : 2));
        } else {
            relative = this.getBlockData(user, position.getRelative(BlockFace.SOUTH));
            if (chestFacings.containsKey(relative) && trapped == trappedChests.contains(relative)) {
                states = (byte)(states | (facing == BlockFace.EAST ? 1 : 2));
            } else {
                relative = this.getBlockData(user, position.getRelative(BlockFace.WEST));
                if (chestFacings.containsKey(relative) && trapped == trappedChests.contains(relative)) {
                    states = (byte)(states | (facing == BlockFace.NORTH ? 2 : 1));
                } else {
                    relative = this.getBlockData(user, position.getRelative(BlockFace.EAST));
                    if (chestFacings.containsKey(relative) && trapped == trappedChests.contains(relative)) {
                        states = (byte)(states | (facing == BlockFace.SOUTH ? 2 : 1));
                    }
                }
            }
        }
        Integer newBlockState = connectedStates.get(states);
        return newBlockState == null ? blockState : newBlockState;
    }
}

