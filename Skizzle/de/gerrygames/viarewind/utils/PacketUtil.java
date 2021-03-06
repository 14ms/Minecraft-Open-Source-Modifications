/*
 * Decompiled with CFR 0.150.
 */
package de.gerrygames.viarewind.utils;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.exception.CancelException;

public class PacketUtil {
    public static void sendToServer(PacketWrapper packet, Class<? extends Protocol> packetProtocol) {
        PacketUtil.sendToServer(packet, packetProtocol, true);
    }

    public static void sendToServer(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline) {
        PacketUtil.sendToServer(packet, packetProtocol, skipCurrentPipeline, false);
    }

    public static void sendToServer(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, boolean currentThread) {
        try {
            packet.sendToServer(packetProtocol, skipCurrentPipeline, currentThread);
        }
        catch (CancelException cancelException) {
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean sendPacket(PacketWrapper packet, Class<? extends Protocol> packetProtocol) {
        return PacketUtil.sendPacket(packet, packetProtocol, true);
    }

    public static boolean sendPacket(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline) {
        return PacketUtil.sendPacket(packet, packetProtocol, true, false);
    }

    public static boolean sendPacket(PacketWrapper packet, Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, boolean currentThread) {
        try {
            packet.send(packetProtocol, skipCurrentPipeline, currentThread);
            return true;
        }
        catch (CancelException cancelException) {
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

