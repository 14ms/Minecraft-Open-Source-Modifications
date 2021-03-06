package me.earth.phobos.mixin.mixins;

import java.util.UUID;
import javax.annotation.Nullable;
import me.earth.phobos.features.modules.client.Capes;
import me.earth.phobos.features.modules.render.Chams;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {
  @Shadow
  @Nullable
  protected abstract NetworkPlayerInfo getPlayerInfo();
  
  @Inject(method = {"getLocationSkin()Lnet/minecraft/util/ResourceLocation;"}, at = {@At("HEAD")}, cancellable = true)
  public void getLocationSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
    if (((Boolean)(Chams.getInstance()).textured.getValue()).booleanValue() && Chams.getInstance().isEnabled())
      callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/shinechams3.png")); 
  }
  
  @Inject(method = {"getLocationCape"}, at = {@At("HEAD")}, cancellable = true)
  public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
    if (Capes.getInstance().isEnabled()) {
      NetworkPlayerInfo info = getPlayerInfo();
      UUID uuid = null;
      if (info != null)
        uuid = getPlayerInfo().getGameProfile().getId(); 
      ResourceLocation cape = Capes.getCapeResource((AbstractClientPlayer)this);
      if (uuid != null && Capes.hasCape(uuid))
        callbackInfoReturnable.setReturnValue(cape); 
    } 
  }
}
