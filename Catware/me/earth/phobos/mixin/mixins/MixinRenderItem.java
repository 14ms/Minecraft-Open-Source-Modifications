package me.earth.phobos.mixin.mixins;

import net.minecraft.client.renderer.RenderItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({RenderItem.class})
public abstract class MixinRenderItem {}
