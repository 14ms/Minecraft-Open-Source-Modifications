package me.earth.phobos.features.modules.misc;

import me.earth.phobos.DiscordPresence;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class RPC extends Module {
  public static RPC INSTANCE;
  
  public Setting<Boolean> catMode = register(new Setting("CatMode", Boolean.valueOf(false)));
  
  public Setting<Boolean> showIP = register(new Setting("ShowIP", Boolean.valueOf(true), "Shows the server IP in your discord presence."));
  
  public Setting<String> state = register(new Setting("State", "Catware 1.0", "Sets the state of the DiscordRPC."));
  
  public RPC() {
    super("RPC", "Discord rich presence", Module.Category.MISC, false, false, false);
    INSTANCE = this;
  }
  
  public void onEnable() {
    DiscordPresence.start();
  }
  
  public void onDisable() {
    DiscordPresence.stop();
  }
}
