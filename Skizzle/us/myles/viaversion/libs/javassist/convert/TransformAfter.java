/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.javassist.convert;

import us.myles.viaversion.libs.javassist.CtMethod;
import us.myles.viaversion.libs.javassist.NotFoundException;
import us.myles.viaversion.libs.javassist.bytecode.BadBytecode;
import us.myles.viaversion.libs.javassist.bytecode.CodeIterator;
import us.myles.viaversion.libs.javassist.convert.TransformBefore;
import us.myles.viaversion.libs.javassist.convert.Transformer;

public class TransformAfter
extends TransformBefore {
    public TransformAfter(Transformer next, CtMethod origMethod, CtMethod afterMethod) throws NotFoundException {
        super(next, origMethod, afterMethod);
    }

    @Override
    protected int match2(int pos, CodeIterator iterator) throws BadBytecode {
        iterator.move(pos);
        iterator.insert(this.saveCode);
        iterator.insert(this.loadCode);
        int p = iterator.insertGap(3);
        iterator.setMark(p);
        iterator.insert(this.loadCode);
        pos = iterator.next();
        p = iterator.getMark();
        iterator.writeByte(iterator.byteAt(pos), p);
        iterator.write16bit(iterator.u16bitAt(pos + 1), p + 1);
        iterator.writeByte(184, pos);
        iterator.write16bit(this.newIndex, pos + 1);
        iterator.move(p);
        return iterator.next();
    }
}

