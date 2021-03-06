/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.CtField;
import us.myles.viaversion.libs.javassist.compiler.CompileError;
import us.myles.viaversion.libs.javassist.compiler.ast.Symbol;
import us.myles.viaversion.libs.javassist.compiler.ast.Visitor;

public class Member
extends Symbol {
    private static final long serialVersionUID = 1L;
    private CtField field = null;

    public Member(String name) {
        super(name);
    }

    public void setField(CtField f) {
        this.field = f;
    }

    public CtField getField() {
        return this.field;
    }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atMember(this);
    }
}

