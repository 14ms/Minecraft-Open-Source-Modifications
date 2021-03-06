package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Crasher extends Module {
  private final Setting<Boolean> oneDot15 = register(new Setting("1.15", Boolean.valueOf(false)));
  
  private final Setting<Float> placeRange = register(new Setting("PlaceRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F)));
  
  private final Setting<Integer> crystals = register(new Setting("Packets", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(100)));
  
  private final Setting<Integer> coolDown = register(new Setting("CoolDown", Integer.valueOf(400), Integer.valueOf(0), Integer.valueOf(1000)));
  
  private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
  
  private final Timer timer = new Timer();
  
  private final List<Integer> entityIDs = new ArrayList<>();
  
  public Setting<Integer> sort = register(new Setting("Sort", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2)));
  
  private boolean offhand = false;
  
  private boolean mainhand = false;
  
  private int lastHotbarSlot = -1;
  
  private boolean switchedItem = false;
  
  private boolean chinese = false;
  
  private int currentID = -1000;
  
  public Crasher() {
    super("CrystalCrash", "Attempts to crash chinese AutoCrystals", Module.Category.COMBAT, false, false, true);
  }
  
  public void onEnable() {
    this.chinese = false;
    if (fullNullCheck() || !this.timer.passedMs(((Integer)this.coolDown.getValue()).intValue())) {
      disable();
      return;
    } 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    placeCrystals();
    disable();
  }
  
  public void onDisable() {
    if (!fullNullCheck())
      for (Iterator<Integer> iterator = this.entityIDs.iterator(); iterator.hasNext(); ) {
        int i = ((Integer)iterator.next()).intValue();
        mc.world.removeEntityFromWorld(i);
      }  
    this.entityIDs.clear();
    this.currentID = -1000;
    this.timer.reset();
  }
  
  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (fullNullCheck() || event.phase == TickEvent.Phase.START || (isOff() && this.timer.passedMs(10L)))
      return; 
    switchItem(true);
  }
  
  private void placeCrystals() {
    this.offhand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
    this.mainhand = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
    int crystalcount = 0;
    List<BlockPos> blocks = BlockUtil.possiblePlacePositions(((Float)this.placeRange.getValue()).floatValue(), false, ((Boolean)this.oneDot15.getValue()).booleanValue());
    if (((Integer)this.sort.getValue()).intValue() == 1) {
      blocks.sort(Comparator.comparingDouble(hole -> mc.player.getDistanceSq(hole)));
    } else if (((Integer)this.sort.getValue()).intValue() == 2) {
      blocks.sort(Comparator.comparingDouble(hole -> -mc.player.getDistanceSq(hole)));
    } 
    for (BlockPos pos : blocks) {
      if (isOff() || crystalcount >= ((Integer)this.crystals.getValue()).intValue())
        break; 
      if (!BlockUtil.canPlaceCrystal(pos, false, ((Boolean)this.oneDot15.getValue()).booleanValue()))
        continue; 
      placeCrystal(pos);
      crystalcount++;
    } 
  }
  
  private void placeCrystal(BlockPos pos) {
    if (!this.chinese && !this.mainhand && !this.offhand && !switchItem(false)) {
      disable();
      return;
    } 
    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D));
    EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
    mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
    mc.player.swingArm(EnumHand.MAIN_HAND);
    EntityEnderCrystal fakeCrystal = new EntityEnderCrystal((World)mc.world, (pos.getX() + 0.5F), (pos.getY() + 1), (pos.getZ() + 0.5F));
    int newID = this.currentID--;
    this.entityIDs.add(Integer.valueOf(newID));
    mc.world.addEntityToWorld(newID, (Entity)fakeCrystal);
  }
  
  private boolean switchItem(boolean back) {
    this.chinese = true;
    if (this.offhand)
      return true; 
    boolean[] value = InventoryUtil.switchItemToItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), Items.END_CRYSTAL);
    this.switchedItem = value[0];
    return value[1];
  }
}
