/*
 * Decompiled with CFR 0.150.
 */
package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections;

import java.util.ArrayList;
import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.AbstractFenceConnectionHandler;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;

public class GlassConnectionHandler
extends AbstractFenceConnectionHandler {
    static List<ConnectionData.ConnectorInitAction> init() {
        ArrayList<ConnectionData.ConnectorInitAction> actions = new ArrayList<ConnectionData.ConnectorInitAction>(18);
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:white_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:orange_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:magenta_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:light_blue_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:yellow_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:lime_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:pink_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:gray_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:light_gray_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:cyan_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:purple_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:blue_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:brown_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:green_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:red_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:black_stained_glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:glass_pane"));
        actions.add(new GlassConnectionHandler("paneConnections").getInitAction("minecraft:iron_bars"));
        return actions;
    }

    public GlassConnectionHandler(String blockConnections) {
        super(blockConnections);
    }

    @Override
    protected byte getStates(UserConnection user, Position position, int blockState) {
        int states = super.getStates(user, position, blockState);
        if (states != 0) {
            return (byte)states;
        }
        ProtocolInfo protocolInfo = user.getProtocolInfo();
        return (byte)(protocolInfo.getServerProtocolVersion() <= 47 && protocolInfo.getServerProtocolVersion() != -1 ? 15 : states);
    }
}

