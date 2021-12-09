/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToMessageEncoder
 */
package us.myles.ViaVersion.bungee.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.bungee.util.BungeePipelineUtil;
import us.myles.ViaVersion.exception.CancelCodecException;
import us.myles.ViaVersion.exception.CancelEncoderException;

@ChannelHandler.Sharable
public class BungeeEncodeHandler
extends MessageToMessageEncoder<ByteBuf> {
    private final UserConnection info;
    private boolean handledCompression;

    public BungeeEncodeHandler(UserConnection info) {
        this.info = info;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void encode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        if (!this.info.checkOutgoingPacket()) {
            throw CancelEncoderException.generate(null);
        }
        if (!this.info.shouldTransformPacket()) {
            out.add((Object)bytebuf.retain());
            return;
        }
        ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);
        try {
            boolean needsCompress = this.handleCompressionOrder(ctx, transformedBuf);
            this.info.transformOutgoing(transformedBuf, CancelEncoderException::generate);
            if (needsCompress) {
                this.recompress(ctx, transformedBuf);
            }
            out.add((Object)transformedBuf.retain());
        }
        finally {
            transformedBuf.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean handleCompressionOrder(ChannelHandlerContext ctx, ByteBuf buf) {
        boolean needsCompress = false;
        if (!this.handledCompression && ctx.pipeline().names().indexOf("compress") > ctx.pipeline().names().indexOf("via-encoder")) {
            ByteBuf decompressed = BungeePipelineUtil.decompress(ctx, buf);
            try {
                buf.clear().writeBytes(decompressed);
            }
            finally {
                decompressed.release();
            }
            ChannelHandler dec = ctx.pipeline().get("via-decoder");
            ChannelHandler enc = ctx.pipeline().get("via-encoder");
            ctx.pipeline().remove(dec);
            ctx.pipeline().remove(enc);
            ctx.pipeline().addAfter("decompress", "via-decoder", dec);
            ctx.pipeline().addAfter("compress", "via-encoder", enc);
            needsCompress = true;
            this.handledCompression = true;
        }
        return needsCompress;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void recompress(ChannelHandlerContext ctx, ByteBuf buf) {
        ByteBuf compressed = BungeePipelineUtil.compress(ctx, buf);
        try {
            buf.clear().writeBytes(compressed);
        }
        finally {
            compressed.release();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof CancelCodecException) {
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}

