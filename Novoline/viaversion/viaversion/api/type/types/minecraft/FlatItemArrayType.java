package viaversion.viaversion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import viaversion.viaversion.api.minecraft.item.Item;
import viaversion.viaversion.api.type.Type;

public class FlatItemArrayType extends BaseItemArrayType {

    public FlatItemArrayType() {
        super("Flat Item Array");
    }

    @Override
    public Item[] read(ByteBuf buffer) throws Exception {
        int amount = Type.SHORT.readPrimitive(buffer);
        Item[] array = new Item[amount];
        for (int i = 0; i < amount; i++) {
            array[i] = Type.FLAT_ITEM.read(buffer);
        }
        return array;
    }

    @Override
    public void write(ByteBuf buffer, Item[] object) throws Exception {
        Type.SHORT.writePrimitive(buffer, (short) object.length);
        for (Item o : object) {
            Type.FLAT_ITEM.write(buffer, o);
        }
    }
}