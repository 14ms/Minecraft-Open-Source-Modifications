/*
 * Decompiled with CFR 0.150.
 */
package us.myles.viaversion.libs.opennbt.conversion;

public class ConversionException
extends RuntimeException {
    private static final long serialVersionUID = -2022049594558041160L;

    public ConversionException() {
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}

