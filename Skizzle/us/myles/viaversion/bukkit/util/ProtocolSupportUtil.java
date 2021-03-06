/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package us.myles.ViaVersion.bukkit.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

public class ProtocolSupportUtil {
    private static Method protocolVersionMethod = null;
    private static Method getIdMethod = null;

    public static int getProtocolVersion(Player player) {
        if (protocolVersionMethod == null) {
            return -1;
        }
        try {
            Object version = protocolVersionMethod.invoke(null, new Object[]{player});
            return (Integer)getIdMethod.invoke(version, new Object[0]);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    static {
        try {
            protocolVersionMethod = Class.forName("protocolsupport.api.ProtocolSupportAPI").getMethod("getProtocolVersion", Player.class);
            getIdMethod = Class.forName("protocolsupport.api.ProtocolVersion").getMethod("getId", new Class[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

