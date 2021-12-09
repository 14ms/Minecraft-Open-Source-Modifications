/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ListChunkFactory
implements IRenderChunkFactory {
    @Override
    public RenderChunk func_178602_a(World worldIn, RenderGlobal p_178602_2_, BlockPos p_178602_3_, int p_178602_4_) {
        return new ListedRenderChunk(worldIn, p_178602_2_, p_178602_3_, p_178602_4_);
    }
}

