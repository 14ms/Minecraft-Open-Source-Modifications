/*
 * Decompiled with CFR 0.150.
 */
package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.BlockFace;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;

public class SnowyGrassConnectionHandler
extends ConnectionHandler {
    private static final Map<Pair<Integer, Boolean>, Integer> grassBlocks = new HashMap<Pair<Integer, Boolean>, Integer>();
    private static final Set<Integer> snows = new HashSet<Integer>();

    static ConnectionData.ConnectorInitAction init() {
        HashSet<String> snowyGrassBlocks = new HashSet<String>();
        snowyGrassBlocks.add("minecraft:grass_block");
        snowyGrassBlocks.add("minecraft:podzol");
        snowyGrassBlocks.add("minecraft:mycelium");
        SnowyGrassConnectionHandler handler = new SnowyGrassConnectionHandler();
        return blockData -> {
            if (snowyGrassBlocks.contains(blockData.getMinecraftKey())) {
                ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), (ConnectionHandler)handler);
                blockData.set("snowy", "true");
                grassBlocks.put(new Pair<Integer, Boolean>(blockData.getSavedBlockStateId(), true), blockData.getBlockStateId());
                blockData.set("snowy", "false");
                grassBlocks.put(new Pair<Integer, Boolean>(blockData.getSavedBlockStateId(), false), blockData.getBlockStateId());
            }
            if (blockData.getMinecraftKey().equals("minecraft:snow") || blockData.getMinecraftKey().equals("minecraft:snow_block")) {
                ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), (ConnectionHandler)handler);
                snows.add(blockData.getSavedBlockStateId());
            }
        };
    }

    @Override
    public int connect(UserConnection user, Position position, int blockState) {
        int blockUpId = this.getBlockData(user, position.getRelative(BlockFace.TOP));
        Integer newId = grassBlocks.get(new Pair<Integer, Boolean>(blockState, snows.contains(blockUpId)));
        if (newId != null) {
            return newId;
        }
        return blockState;
    }
}

