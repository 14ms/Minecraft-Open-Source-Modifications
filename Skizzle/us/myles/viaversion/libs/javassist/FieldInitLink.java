/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.javassist;

import us.myles.viaversion.libs.javassist.CtField;

class FieldInitLink {
    FieldInitLink next = null;
    CtField field;
    CtField.Initializer init;

    FieldInitLink(CtField f, CtField.Initializer i) {
        this.field = f;
        this.init = i;
    }
}

