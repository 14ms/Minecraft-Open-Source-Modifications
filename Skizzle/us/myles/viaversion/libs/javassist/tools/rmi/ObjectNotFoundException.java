/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.javassist.tools.rmi;

public class ObjectNotFoundException
extends Exception {
    private static final long serialVersionUID = 1L;

    public ObjectNotFoundException(String name) {
        super(name + " is not exported");
    }

    public ObjectNotFoundException(String name, Exception e) {
        super(name + " because of " + e.toString());
    }
}

