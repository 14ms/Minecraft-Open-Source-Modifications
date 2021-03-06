package vestige.module.impl.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventMotionUpdate;
import vestige.event.impl.EventPostMotionUpdate;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventSendPacket;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;
import vestige.setting.impl.BooleanSetting;
import vestige.setting.impl.ModeSetting;
import vestige.setting.impl.NumberSetting;
import vestige.util.network.PacketUtils;
import vestige.util.world.BlockUtil;

public class Scaffold extends Module {
	
	private BlockPos currentPos, lastPos;
	private EnumFacing currentFacing, lastFacing;
	
	private boolean diagonal = true;
	
	private float yaw, pitch;
	private float finalYaw, finalPitch;
	private int currentDelay;
	
	public int oldItem, itemSpoofed;
	private int towerTicks;
	
	private double oldY;
	private int ticks, sneakTicks;
	private long placedBlocks;
	
	private ArrayList<Packet> savedPackets = new ArrayList<>();
	
	public List<Block> blockBlacklist = Arrays.asList(Blocks.air, Blocks.water, Blocks.tnt, Blocks.chest,
			Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet,
			Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice,
			Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch,
			Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore,
			Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore, Blocks.quartz_ore,
			Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
			Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
			Blocks.wooden_button, Blocks.lever, Blocks.enchanting_table, Blocks.red_flower, Blocks.double_plant,
			Blocks.yellow_flower, Blocks.web);
	
	public ModeSetting mode = new ModeSetting("Mode", "NCP", "NCP", "UpdatedNCP", "Hypixel", "AAC4", "Redesky", "Redesky2", "Verus", "Matrix");
	public ModeSetting tower = new ModeSetting("Tower", "NCP", "Vanilla", "NCP", "Quick Jump", "None");
	public ModeSetting autoblock = new ModeSetting("Autoblock", "Switch", "None", "Switch", "Spoof");
	public NumberSetting timer = new NumberSetting("Timer", 1, 0.5, 4, 0.1, this);
	
	private BooleanSetting jump = new BooleanSetting("Jump", false, this);
	
	private NumberSetting vanillaTowerSpeed = new NumberSetting("Vanilla Tower Speed", 0.42, 0.1, 1, 0.02, this);
	
	private BooleanSetting redeskyTimer = new BooleanSetting("Redesky timer", true, this);
	
	public Scaffold() {
		super("Scaffold", Category.WORLD, ListeningType.ALWAYS, Keyboard.KEY_H);
		this.addSettings(mode, tower, vanillaTowerSpeed, autoblock, timer, redeskyTimer, jump);
	}
	
	public void onEnable() {
		sneakTicks = 0;
		placedBlocks = 0;
		ticks = 0;
		for(int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                continue;
            if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !blockBlacklist.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
            	if(autoblock.is("Switch")) {
            		oldItem = mc.thePlayer.inventory.currentItem;
            		mc.thePlayer.inventory.currentItem = i;
            		itemSpoofed = i;
					break;
            	}
            	if(autoblock.is("Spoof")) {
            		oldItem = mc.thePlayer.inventory.currentItem;
            		itemSpoofed = i;
					break;
            	}
            }
        }
		if(autoblock.is("Spoof")) {
			PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(itemSpoofed));
		}
		
		oldY = mc.thePlayer.posY;
		if(mode.is("Hypixel")) {
			finalYaw = mc.thePlayer.rotationYaw - 180;
			finalPitch = (float) (80 + Math.random());
			//PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.START_SNEAKING));
		}
	}
	
	public void onDisable() {
		placedBlocks = 0;
		if(sneakTicks > 0 || mode.is("Redesky")) {
			mc.gameSettings.keyBindSneak.pressed = false;
		}
		
		mc.timer.timerSpeed = 1F;
		if(autoblock.is("Switch")) {
			mc.thePlayer.inventory.currentItem = oldItem;
		}
		if(autoblock.is("Spoof")) {
			PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		}
		
		switch(mode.getMode()) {
		case "Redesky":
		case "Redesky2":
			//sendPackets();
			break;
		case "Hypixel":
			//PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SNEAKING));
			move.setSpeed(0);
			break;
		}
		
		
		
		currentDelay = 0;
	}
	
	public void onEvent(Event e) {
		if(mc.thePlayer.ticksExisted < 2) {
			this.setEnabled(false);
			return;
		}
		
		if(e instanceof EventRender) {
			timer.setShowed(!mode.is("Redesky") && !mode.is("Redesky2"));
			redeskyTimer.setShowed(mode.is("Redesky") || mode.is("Redesky2"));
			
			if(tower.is("Vanilla")) {
				vanillaTowerSpeed.setShowed(true);
			} else {
				vanillaTowerSpeed.setShowed(false);
			}
			if(mode.is("Hypixel")) {
				jump.setShowed(true);
			} else {
				jump.setShowed(false);
			}
		}
		
		if(!this.isEnabled()) {
			return;
		}
		
		switch(mode.getMode()) {
		case "NCP":
			NCP(e);
			break;
		case "UpdatedNCP":
			UpdatedNCP(e);
			break;
		case "Hypixel":
			Hypixel(e);
			break;
		case "AAC4":
			AAC4(e);
			break;
		case "Redesky":
			Redesky(e);
			break;
		case "Redesky2":
			Redesky2(e);
			break;
		case "Verus":
			Verus(e);
			break;
		case "Matrix":
			Matrix(e);
			break;
		}
		
		tower(e);
		
		this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
		
		if(e instanceof EventUpdate) {
			if(timer.getValue() != 1) {
				mc.timer.timerSpeed = (float) timer.getValue();
			}
			
			manageBlocks();
		}
		if(e instanceof EventSendPacket) {
			EventSendPacket event = (EventSendPacket) e;
			if(autoblock.is("Spoof")) {
				if(event.getPacket() instanceof C09PacketHeldItemChange) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	private void manageBlocks() {
		if(autoblock.is("Spoof") && mc.thePlayer.inventory.getStackInSlot(itemSpoofed) == null) {
			for(int i = 0; i < 9; i++) {
				if (mc.thePlayer.inventory.getStackInSlot(i) == null)
					continue;
				List<Block> blockBlacklist = Arrays.asList(Blocks.air, Blocks.water, Blocks.tnt, Blocks.chest,
						Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet,
						Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice,
						Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch,
						Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore,
						Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore, Blocks.quartz_ore,
						Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
						Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
						Blocks.wooden_button, Blocks.lever, Blocks.enchanting_table, Blocks.red_flower, Blocks.double_plant,
						Blocks.yellow_flower);
				if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !blockBlacklist.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
					oldItem = mc.thePlayer.inventory.currentItem;
					itemSpoofed = i;
					break;
				}
			}
			PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(itemSpoofed));
		}
		
		if(autoblock.is("Switch")) {
			for(int i = 0; i < 9; i++) {
				if (mc.thePlayer.inventory.getStackInSlot(i) == null)
					continue;
				if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !blockBlacklist.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
					mc.thePlayer.inventory.currentItem = i;
					itemSpoofed = i;
				}
			}
		}
	}
	
	private void tower(Event event) {
		if(event instanceof EventUpdate) {
			switch(tower.getMode()) {
			case "Vanilla":
				if(mc.gameSettings.keyBindJump.isKeyDown() && !move.isWalking()) {
					mc.thePlayer.motionY = vanillaTowerSpeed.getValue();
				}
				break;
			case "NCP":
				if(mc.thePlayer.onGround) {
					towerTicks = 0;
				}
				if(mc.gameSettings.keyBindJump.isKeyDown() && !move.isWalking() && !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
					int IntPosY = (int) mc.thePlayer.posY;
					if(mc.thePlayer.posY - IntPosY < 0.05) {
						mc.thePlayer.setPosition(mc.thePlayer.posX, IntPosY, mc.thePlayer.posZ);
						mc.thePlayer.motionY = 0.42;
						towerTicks = 1;
					} else if(towerTicks == 1) {
						mc.thePlayer.motionY = 0.34;
						towerTicks++;
					} else if(towerTicks == 2) {
						mc.thePlayer.motionY = 0.25;
						towerTicks++;
					}
					/*
					if(mc.thePlayer.posY - IntPosY >= 0.75) {
						mc.thePlayer.motionY = 0.24;
					}
					*/
				}
			case "Quick Jump":
				if(mc.gameSettings.keyBindJump.isKeyDown() && !move.isWalking()) {
					if(mc.thePlayer.motionY < 0.3) {
						mc.thePlayer.motionY -= 0.008;
					}
				}
				break;
			}
		}
	}
	
	private void NCP(Event event) {
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			if(mc.thePlayer.onGround) {
				oldY = mc.thePlayer.posY;
			}
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if(Vestige.getModuleManager().getModuleByName("Speed").isEnabled()) {
				pos = new BlockPos(mc.thePlayer.posX, oldY - 1, mc.thePlayer.posZ);
			}
			
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
				setBlockFacing(pos);

				if(currentFacing != null) {
					lastPos = currentPos;
					lastFacing = currentFacing;
				}
			}
			
			if(lastPos != null && lastFacing != null) {
				float facing[] = BlockUtil.getDirectionToBlock(lastPos.getX(), lastPos.getY(), lastPos.getZ(), lastFacing);

				float yaw = facing[0];
				float pitch = facing[1] + 9;
				if(pitch >= 90) {
					pitch = 90;
				}
				finalYaw = yaw;
				finalPitch = pitch;
				
				clientRotations(finalYaw, finalPitch);
				e.setYaw(finalYaw);
				e.setPitch(finalPitch);
			}
		} else if(event instanceof EventPostMotionUpdate) {
			if(currentPos != null && currentFacing != null) {
				placeBlock(0.02);
			}
		}
	}
	
	private void UpdatedNCP(Event event) {
		mc.thePlayer.setSprinting(false);
		
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ);
			}
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
				setBlockFacing(pos);

				if(currentFacing != null) {
					lastPos = currentPos;
					lastFacing = currentFacing;
				}
			}
			
			if(lastPos != null && lastFacing != null) {
				float facing[] = BlockUtil.getDirectionToBlock(lastPos.getX(), lastPos.getY(), lastPos.getZ(), lastFacing);

				float yaw = facing[0];
				float pitch = facing[1] + 9;
				if(pitch >= 90) {
					pitch = 90;
				}
				finalYaw = yaw;
				finalPitch = pitch;
				
				clientRotations(finalYaw, finalPitch);
				e.setYaw(finalYaw);
				e.setPitch(finalPitch);
			}
		} else if(event instanceof EventPostMotionUpdate) {
			if(currentPos != null && currentFacing != null) {
				if(raytraceBlock(finalYaw, finalPitch)) {
					placeBlock(0.12);
				}
			}
		}
	}
	
	private void Hypixel(Event event) {	
		if(Vestige.getModuleManager().getModuleByName("Fly").isEnabled()) {
			Vestige.getModuleManager().getModuleByName("Fly").setEnabled(false);
		}
		
		if(placedBlocks < 2) {
			//mc.thePlayer.setSprinting(false);
		}
		
		if(event instanceof EventUpdate) {
			if(jump.isEnabled()) {
				if(!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
				} else {
					double mult = 1 - ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.008);
					mc.thePlayer.motionX *= mult;
					mc.thePlayer.motionZ *= mult;
				}
				
				if(!mc.gameSettings.keyBindJump.isKeyDown()) {
					if(mc.thePlayer.onGround) {
						mc.thePlayer.jump();
						motionMult(1.4);
						if(Math.abs(mc.thePlayer.motionX) < 0.27 && Math.abs(mc.thePlayer.motionZ) < 0.27) {
							motionMult(1.35);
						} else {
							motionMult(1.08);
						}
					} else {
						//mc.thePlayer.motionX *= 1.005;
						//mc.thePlayer.motionZ *= 1.005;
					}
				}
			} else {
				if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
					double mult = 1 - ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.167);
					mc.thePlayer.motionX *= mult;
					mc.thePlayer.motionZ *= mult;
				}
			}
			
			if(negativeExpand(-0.1)) {
				if(mc.thePlayer.onGround) {
					for(int i = 0; i < 2; i++) {
						PacketUtils.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX + Math.random() * 0.001, mc.thePlayer.posY, mc.thePlayer.posZ + Math.random() * 0.001, true));
					}
				}
				//mc.timer.timerSpeed = 1;
				mc.timer.timerSpeed = (float) timer.getValue();
			} else {
				mc.timer.timerSpeed = (float) timer.getValue();
			}
			
			if(negativeExpand(0.08)) {
				motionMult(0.7);
			}
			
		}
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			if(mc.thePlayer.onGround) {
				oldY = mc.thePlayer.posY;
			}
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.2, mc.thePlayer.posZ);
			} else if(jump.isEnabled()) {
				pos = new BlockPos(mc.thePlayer.posX, oldY - 1, mc.thePlayer.posZ);
			}
			
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
				mc.timer.timerSpeed = 1F;
				setBlockFacing(pos);

				if(currentPos != null && currentFacing != null) {
					lastPos = currentPos;
					lastFacing = currentFacing;
					
					finalYaw = BlockUtil.getDirectionToBlock(currentPos.getX(), currentPos.getY(), currentPos.getZ(), currentFacing)[0];
					finalPitch = BlockUtil.getDirectionToBlock(currentPos.getX(), currentPos.getY(), currentPos.getZ(), currentFacing)[1];
				}
				
				if(move.isGoingDiagonally()) {
					finalYaw = getAAC4ScaffoldYaw();
					finalPitch = getAAC4ScaffoldPitch();
				}
				
				if(currentPos != null && currentFacing != null) {
					if(!jump.isEnabled() && placedBlocks < 2) {
						//motionMult(0.4);
					}
				}
			}
			
			clientRotations(finalYaw, finalPitch);
			e.setYaw(finalYaw);
			e.setPitch(finalPitch);
		} else if(event instanceof EventPostMotionUpdate) {
			if(currentPos != null && currentFacing != null) {
				if(jump.isEnabled()) {
					placeBlock(0.06);
				} else {
					if(negativeExpand(0.16)) {
						if(mc.thePlayer.onGround) {
							for(int i = 0; i < 4; i++) {
								PacketUtils.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX + Math.random() * 0.001, mc.thePlayer.posY, mc.thePlayer.posZ + Math.random() * 0.001, true));
							}
						}
						if(placeBlock(0.16)) {
							ticks = 0;
						}
					}
					
				}
			}
		}
	}
	
	private void AAC4(Event event) {
		mc.thePlayer.setSprinting(false);
		
		if(event instanceof EventUpdate) {
			finalYaw = getAAC4ScaffoldYaw();
			finalPitch = getAAC4ScaffoldPitch();
			
			if(negativeExpand(0.08)) {
				//motionMult(0);
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ);
			}
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
				setBlockFacing(pos);

				if(currentFacing != null) {
					lastPos = currentPos;
					lastFacing = currentFacing;
				}
			}
			
			e.setYaw(finalYaw);
			e.setPitch(finalPitch);
			
			clientRotations(finalYaw, finalPitch);
			
			if(currentPos != null && currentFacing != null) {
				if(raytraceBlock(finalYaw, finalPitch)) {
					placeBlock(0.14);
				}
			}
		} else if(event instanceof EventPostMotionUpdate) {
			/*
			if(currentPos != null && currentFacing != null) {
				if(raytraceBlock(finalYaw, finalPitch)) {
					placeBlock(0.08);
				}
			}
			*/
		}
	}
	
	private void Redesky(Event event) {
		float negativeExpand = 0.12F;
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				mc.gameSettings.keyBindSneak.pressed = true;
			}
		}
		if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			
			/*
			if(mc.thePlayer.ticksExisted % 3 != 0 && placedBlocks > 1 && redeskyTimer.isEnabled()) {
				e.setCancelled(true);
				savedPackets.add(e.getPacket());
			} else {
				sendPackets();
			}
			*/
		} else if(event instanceof EventUpdate) {
			//mc.thePlayer.setSprinting(false);
			
			finalYaw = getAAC4ScaffoldYaw();
			finalPitch = getAAC4ScaffoldPitch();
			
			if(!mc.thePlayer.onGround) {
				motionMult(0.4);
			}
		} else if(event instanceof EventMotionUpdate) {
			if(redeskyTimer.isEnabled()) {
				mc.timer.timerSpeed = mc.thePlayer.ticksExisted % 2 == 0 ? 0.95F : 1.45F;
			} else {
				mc.timer.timerSpeed = 1F;
				if(--ticks > 0) {
					mc.timer.timerSpeed = 1.3F;
				}
			}
			//mc.timer.timerSpeed = mc.thePlayer.ticksExisted % 2 == 0 ? 1.18F : 1.6F;
			
			/*
			if(ticks > 0 && placedBlocks > 1) {
				mc.timer.timerSpeed = 1.1F;
				ticks--;
			}
			*/
			
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
				setBlockFacing(pos);
				
				if(currentFacing != null) {
					lastPos = currentPos;
					lastFacing = currentFacing;
				}
			}
			
			if(currentPos != null && currentFacing != null) {
				mc.gameSettings.keyBindSneak.pressed = false;
				if(negativeExpand(negativeExpand)) {
					if(!goingDiagonally() && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
						motionMult(0.6);
					}
					if(!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
						if(placedBlocks < 2) {
							motionMult(0.4);
						} else if(placedBlocks % 3 == 0) {
							motionMult(0.9);
						}
					} else {
						if(placedBlocks == 0) {
							motionMult(0.5);
						} else {
							motionMult(0.75);
						}
					}
					clientRotations(finalYaw, finalPitch);
					e.setYaw(finalYaw);
					e.setPitch(finalPitch);
					if(goingDiagonally()) {
						mc.gameSettings.keyBindSneak.pressed = true;
					}
				}
			}
		} else if(event instanceof EventPostMotionUpdate) {
			if(currentPos != null && currentFacing != null) {
				if(placeBlock(negativeExpand)) {
					ticks = 2;
				}
			}
		}
	}
	
	private void Redesky2(Event event) {
		mc.thePlayer.setSprinting(false);
		
		if(event instanceof EventUpdate) {
			if(redeskyTimer.isEnabled()) {
				mc.timer.timerSpeed = placedBlocks < 2 || placedBlocks % 9 == 0 ? 0.9F : mc.thePlayer.ticksExisted % 2 == 0 ? 0.95F : 1.45F;
			}
			
			finalYaw = getAAC4ScaffoldYaw();
			finalPitch = getAAC4ScaffoldPitch();
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ);
			}
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
				setBlockFacing(pos);

				if(currentFacing != null) {
					lastPos = currentPos;
					lastFacing = currentFacing;
				}
			}
			
			e.setYaw(finalYaw);
			e.setPitch(finalPitch);
			
			if(placedBlocks > 2 && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
				motionMult(1.04);
			}
			
			clientRotations(finalYaw, finalPitch);
		} else if(event instanceof EventPostMotionUpdate) {
			if(currentPos != null && currentFacing != null) {
				double negExpand = goingDiagonally() ? 0.08 : 0.14;
				placeBlock(negExpand);
			}
		}
	}
	
	private void Verus(Event event) {
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			
			if(!mc.thePlayer.onGround) {
				pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ);
			}
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
				setBlockFacing(pos);

				if(currentFacing != null) {
					lastPos = currentPos;
					lastFacing = currentFacing;
				}
			}
			
			finalYaw = e.getYaw();
			finalPitch = e.getPitch();
			
			if(currentPos != null && currentFacing != null) {
				placeBlock(0);
			}
		}
	}
	
	private void Matrix(Event event) {
		mc.thePlayer.setSprinting(false);
		
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1, mc.thePlayer.posZ);
			}
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
				setBlockFacing(pos);

				if(currentFacing != null) {
					lastPos = currentPos;
					lastFacing = currentFacing;
					
					float facing[] = BlockUtil.getDirectionToBlock(currentPos.getX(), currentPos.getY(), currentPos.getZ(), currentFacing);

					float yaw = facing[0] + 35;
					float pitch = facing[1] + 9;
					if(pitch >= 90) {
						pitch = 90;
					}
					finalYaw = yaw;
					finalPitch = pitch;
				}
			}
			
			if(negativeExpand(0)) {
				sneakTicks = 4;
			}
			
			double m = 0.005 * 50;
            double f = m * 0.6 + 0.2;
            double gcd = m * m * m * 1.2;
            finalYaw -= finalYaw % gcd;
            finalPitch -= finalPitch % gcd;
			
            if(finalPitch > 90) {
            	finalPitch = 90;
            } else if(finalPitch < -90) {
            	finalPitch = -90;
            }
            
			e.setYaw(finalYaw);
			e.setPitch(finalPitch);
			
			clientRotations(finalYaw, finalPitch);
			
			if(sneakTicks > 0) {
				mc.gameSettings.keyBindSneak.pressed = true;
				move.setSpeed(0);
				sneakTicks--;
			} else {
				mc.gameSettings.keyBindSneak.pressed = false;
			}
			
			PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));

			if(currentPos != null && currentFacing != null) {
				//if(raytraceBlock(finalYaw, finalPitch)) {
					placeBlock(0.26);
				//}
			}
			
		} else if(event instanceof EventPostMotionUpdate) {
			
		}
	}
	
	private void motionMult(double mult) {
		mc.thePlayer.motionX *= mult;
		mc.thePlayer.motionZ *= mult;
	}
	
	private void clientRotations(float yaw, float pitch) {
		mc.thePlayer.rotationYawHead = yaw;
		mc.thePlayer.renderYawOffset = yaw;
		mc.thePlayer.rotationPitchHead = pitch;
	}
	
	private boolean placeBlock(double negativeExpand) {
		if(autoblock.is("Spoof") ? mc.thePlayer.inventory.getStackInSlot(itemSpoofed) != null && mc.thePlayer.inventory.getStackInSlot(itemSpoofed).getItem() instanceof ItemBlock && negativeExpand(negativeExpand) : mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock && negativeExpand(negativeExpand)) {
			if(mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, autoblock.is("None") ? mc.thePlayer.getCurrentEquippedItem() : mc.thePlayer.inventory.getStackInSlot(itemSpoofed), currentPos, currentFacing, getVec3(currentPos, currentFacing) /*new Vec3(currentPos).addVector(0.5, 0.5, 0.5).add(new Vec3(currentFacing.getDirectionVec()).scale(0.5))*/)) {
				PacketUtils.sendPacket(new C0APacketAnimation());
				placedBlocks++;
				return true;
			}
		}
		return false;
	}
	
	private boolean negativeExpand(double negativeExpandValue) {
		if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + negativeExpandValue, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - negativeExpandValue, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - negativeExpandValue, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + negativeExpandValue, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir) {
			return true;
		} else {
			return false;
		}
	}
	
	private float aac4Yaw = 0;
	
	private float getAAC4ScaffoldYaw() {
		if(mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindLeft.isKeyDown()) {
			if(mc.thePlayer.rotationYaw <= 135) {
				aac4Yaw = mc.thePlayer.rotationYaw + 45;
			} else {
				aac4Yaw = mc.thePlayer.rotationYaw - 315;
			}
		} else if (mc.gameSettings.keyBindBack.isKeyDown() && mc.gameSettings.keyBindRight.isKeyDown()) {
			if(mc.thePlayer.rotationYaw >= -135) {
				aac4Yaw = mc.thePlayer.rotationYaw - 45;
			} else {
				aac4Yaw = mc.thePlayer.rotationYaw + 315;
			}
		} else if (mc.gameSettings.keyBindLeft.isKeyDown() && mc.gameSettings.keyBindForward.isKeyDown()) {
			if(mc.thePlayer.rotationYaw <= 45) {
				aac4Yaw = mc.thePlayer.rotationYaw + 135;
			} else {
				aac4Yaw = mc.thePlayer.rotationYaw - 225;
			}
		} else if (mc.gameSettings.keyBindRight.isKeyDown() && mc.gameSettings.keyBindForward.isKeyDown()) {
			if(mc.thePlayer.rotationYaw >= -45) {
				aac4Yaw = mc.thePlayer.rotationYaw - 135;
			} else {
				aac4Yaw = mc.thePlayer.rotationYaw + 225;
			}
		} else if (mc.gameSettings.keyBindLeft.isKeyDown()) {
			if(mc.thePlayer.rotationYaw <= 90) {
				aac4Yaw = mc.thePlayer.rotationYaw + 90;
			} else {
				aac4Yaw = mc.thePlayer.rotationYaw - 270;
			}
		} else if (mc.gameSettings.keyBindRight.isKeyDown()) {
			if(mc.thePlayer.rotationYaw >= -90) {
				aac4Yaw = mc.thePlayer.rotationYaw - 90;
			} else {
				aac4Yaw = mc.thePlayer.rotationYaw + 270;
			}
		} else if (mc.gameSettings.keyBindBack.isKeyDown()) {
			aac4Yaw = mc.thePlayer.rotationYaw;
		} else if (mc.gameSettings.keyBindForward.isKeyDown()) {
			//Random random = new Random();
			//int randomRotation = random.nextInt(8) - 4;
			if(mc.thePlayer.rotationYaw >= 0) {
				aac4Yaw = mc.thePlayer.rotationYaw - 180F;
			} else {
				aac4Yaw = mc.thePlayer.rotationYaw + 180F;
			}
		}
		return aac4Yaw;
	}
	
	private float getAAC4ScaffoldPitch() {
		float random = (float) Math.random();
		float pitch = 81 + random;
		
		if(mc.gameSettings.keyBindJump.isKeyDown()) {
			if(move.getCurrentMotion() > 0.1) {
				return 82 + random;
			}
			return 90;
		}
		
		return pitch;
	}
	
	public static Vec3 getVec3(BlockPos pos, EnumFacing facing) {
		double x = (double) pos.getX();
		double y = (double) pos.getY();
		double z = (double) pos.getZ();
		double random1 = ThreadLocalRandom.current().nextDouble(0.6D, 1.0D);
		double random2 = ThreadLocalRandom.current().nextDouble(0.9D, 1.0D);
		if(facing.equals(EnumFacing.UP)) {
			x += random1;
			z += random1;
			++y;
		} else if(facing.equals(EnumFacing.DOWN)) {
			x += random1;
			z += random1;
        } else if(facing.equals(EnumFacing.WEST)) {
        	y += random2;
        	z += random1;
        } else if(facing.equals(EnumFacing.EAST)) {
        	y += random2;
        	z += random1;
        	++x;
        } else if(facing.equals(EnumFacing.SOUTH)) {
        	y += random2;
        	x += random1;
        	++z;
        } else if(facing.equals(EnumFacing.NORTH)) {
        	y += random2;
        	x += random1;
        }

        return new Vec3(x, y, z);
	}
	
	private boolean raytraceBlock(float yaw, float pitch) {
		Vec3 src = mc.thePlayer.getPositionEyes(1.0F);
		Vec3 rotationVec = Entity.getVectorForRotation(pitch, yaw);
		Vec3 dest = src.addVector(rotationVec.xCoord * 3.5F, rotationVec.yCoord * 3F, rotationVec.zCoord * 3.5F);
		if(mc.theWorld.getBlockState(currentPos) == null) return false;
		IBlockState blockState = mc.theWorld.getBlockState(currentPos);
		if(blockState.getBlock().getCollisionBoundingBox(mc.theWorld,currentPos, blockState) == null) return false;
		AxisAlignedBB bb = blockState.getBlock().getCollisionBoundingBox(mc.theWorld,currentPos, blockState);
		return bb.calculateIntercept(src, dest) != null;
	}
	
	public void setBlockFacing(BlockPos pos) {
		if(diagonal) {
			if (mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(0, -1, 0);
				currentFacing = EnumFacing.UP;
				if(yaw != 0) {
					currentDelay = 0;
				}
				yaw = 0;
			} else if (mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(-1, 0, 0);
				currentFacing = EnumFacing.EAST;
				if(yaw != 90) {
					currentDelay = 0;
				}
				yaw = 90;
			} else if (mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(1, 0, 0);
				currentFacing = EnumFacing.WEST;
				if(yaw != -90) {
					currentDelay = 0;
				}
				yaw = -90;
			} else if (mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(0, 0, -1);
				currentFacing = EnumFacing.SOUTH;
				if(yaw != 180) {
					currentDelay = 0;
				}
				yaw = 180;
			} else if (mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(0, 0, 1);
				currentFacing = EnumFacing.NORTH;
				if(yaw != 0) {
					currentDelay = 0;
				}
				yaw = 0;
			}
			else if (mc.theWorld.getBlockState(pos.add(-1, 0, -1)).getBlock() != Blocks.air) {
				currentFacing = EnumFacing.EAST;
				this.currentPos = pos.add(-1, 0, -1);
				if(yaw != 135) {
					currentDelay = 0;
				}
				yaw = 135;
			} else if (mc.theWorld.getBlockState(pos.add(1, 0, 1)).getBlock() != Blocks.air) {
				currentFacing = EnumFacing.WEST;
				this.currentPos = pos.add(1, 0, 1);
				if(yaw != -45) {
					currentDelay = 0;
				}
				yaw = -45;
			} else if (mc.theWorld.getBlockState(pos.add(1, 0, -1)).getBlock() != Blocks.air) {
				currentFacing = EnumFacing.SOUTH;
				this.currentPos = pos.add(1, 0, -1);
				if(yaw != 135) {
					currentDelay = 0;
				}
				yaw = -135;
			} else if (mc.theWorld.getBlockState(pos.add(-1, 0, 1)).getBlock() != Blocks.air) {
				currentFacing = EnumFacing.NORTH;
				this.currentPos = pos.add(-1, 0, 1);
				if(yaw != 45) {
					currentDelay = 0;
				}
				yaw = 45;
			}
			else if (mc.theWorld.getBlockState(pos.add(0, -1, 1)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(0, -1, 1);
				currentFacing = EnumFacing.UP;
				yaw = mc.thePlayer.rotationYaw;
			}
			else if (mc.theWorld.getBlockState(pos.add(0, -1, -1)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(0, -1, -1);
				currentFacing = EnumFacing.UP;
				yaw = mc.thePlayer.rotationYaw;
			}
			else if (mc.theWorld.getBlockState(pos.add(1, -1, 0)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(1, -1, 0);
				currentFacing = EnumFacing.UP;
				yaw = mc.thePlayer.rotationYaw;
			}
			else if (mc.theWorld.getBlockState(pos.add(-1, -1, 0)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(-1, -1, 0);
				currentFacing = EnumFacing.UP;
				yaw = mc.thePlayer.rotationYaw;
			}
			else {
				currentPos = null;
				currentFacing = null;
			}
		} else {
			if (mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(0, -1, 0);
				currentFacing = EnumFacing.UP;
			} else if (mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(-1, 0, 0);
				currentFacing = EnumFacing.EAST;
			} else if (mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(1, 0, 0);
				currentFacing = EnumFacing.WEST;
			} else if (mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(0, 0, -1);
				currentFacing = EnumFacing.SOUTH;
			} else if (mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
				this.currentPos = pos.add(0, 0, 1);
				currentFacing = EnumFacing.NORTH;
			} else {
				currentPos = null;
				currentFacing = null;
			}
		}
	}
	
	private boolean goingDiagonally() {
		return Math.abs(mc.thePlayer.motionX) > 0.07 && Math.abs(mc.thePlayer.motionZ) > 0.07;
	}
	
}
