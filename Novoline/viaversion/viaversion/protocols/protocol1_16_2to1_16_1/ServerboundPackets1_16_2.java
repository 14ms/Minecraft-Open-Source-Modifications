package viaversion.viaversion.protocols.protocol1_16_2to1_16_1;

import viaversion.viaversion.api.protocol.ServerboundPacketType;

public enum ServerboundPackets1_16_2 implements ServerboundPacketType {

    TELEPORT_CONFIRM, // 0x00
    QUERY_BLOCK_NBT, // 0x01
    SET_DIFFICULTY, // 0x02
    CHAT_MESSAGE, // 0x03
    CLIENT_STATUS, // 0x04
    CLIENT_SETTINGS, // 0x05
    TAB_COMPLETE, // 0x06
    WINDOW_CONFIRMATION, // 0x07
    CLICK_WINDOW_BUTTON, // 0x08
    CLICK_WINDOW, // 0x09
    CLOSE_WINDOW, // 0x0A
    PLUGIN_MESSAGE, // 0x0B
    EDIT_BOOK, // 0x0C
    ENTITY_NBT_REQUEST, // 0x0D
    INTERACT_ENTITY, // 0x0E
    GENERATE_JIGSAW, // 0x0F
    KEEP_ALIVE, // 0x10
    LOCK_DIFFICULTY, // 0x11
    PLAYER_POSITION, // 0x12
    PLAYER_POSITION_AND_ROTATION, // 0x13
    PLAYER_ROTATION, // 0x14
    PLAYER_MOVEMENT, // 0x15
    VEHICLE_MOVE, // 0x16
    STEER_BOAT, // 0x17
    PICK_ITEM, // 0x18
    CRAFT_RECIPE_REQUEST, // 0x19
    PLAYER_ABILITIES, // 0x1A
    PLAYER_DIGGING, // 0x1B
    ENTITY_ACTION, // 0x1C
    STEER_VEHICLE, // 0x1D
    RECIPE_BOOK_DATA, // 0x1E
    SEEN_RECIPE, // 0x1F
    RENAME_ITEM, // 0x20
    RESOURCE_PACK_STATUS, // 0x21
    ADVANCEMENT_TAB, // 0x22
    SELECT_TRADE, // 0x23
    SET_BEACON_EFFECT, // 0x24
    HELD_ITEM_CHANGE, // 0x25
    UPDATE_COMMAND_BLOCK, // 0x26
    UPDATE_COMMAND_BLOCK_MINECART, // 0x27
    CREATIVE_INVENTORY_ACTION, // 0x28
    UPDATE_JIGSAW_BLOCK, // 0x29
    UPDATE_STRUCTURE_BLOCK, // 0x2A
    UPDATE_SIGN, // 0x2B
    ANIMATION, // 0x2C
    SPECTATE, // 0x2D
    PLAYER_BLOCK_PLACEMENT, // 0x2E
    USE_ITEM, // 0x2F
}
