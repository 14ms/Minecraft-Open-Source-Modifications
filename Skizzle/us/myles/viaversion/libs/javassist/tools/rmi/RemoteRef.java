/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.javassist.tools.rmi;

import java.io.Serializable;

public class RemoteRef
implements Serializable {
    private static final long serialVersionUID = 1L;
    public int oid;
    public String classname;

    public RemoteRef(int i) {
        this.oid = i;
        this.classname = null;
    }

    public RemoteRef(int i, String name) {
        this.oid = i;
        this.classname = name;
    }
}

