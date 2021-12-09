/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import us.myles.viaversion.libs.javassist.bytecode.AttributeInfo;
import us.myles.viaversion.libs.javassist.bytecode.ByteArray;
import us.myles.viaversion.libs.javassist.bytecode.ConstPool;

public class SourceFileAttribute
extends AttributeInfo {
    public static final String tag = "SourceFile";

    SourceFileAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
        super(cp, n, in);
    }

    public SourceFileAttribute(ConstPool cp, String filename) {
        super(cp, tag);
        int index = cp.addUtf8Info(filename);
        byte[] bvalue = new byte[]{(byte)(index >>> 8), (byte)index};
        this.set(bvalue);
    }

    public String getFileName() {
        return this.getConstPool().getUtf8Info(ByteArray.readU16bit(this.get(), 0));
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        return new SourceFileAttribute(newCp, this.getFileName());
    }
}

