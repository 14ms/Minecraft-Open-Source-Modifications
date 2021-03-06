/*
 * Decompiled with CFR 0.150.
 */
package nl.matsv.viabackwards.protocol.protocol1_11_1to1_12.packets;

import nl.matsv.viabackwards.api.rewriters.Rewriter;
import nl.matsv.viabackwards.protocol.protocol1_11_1to1_12.Protocol1_11_1To1_12;
import nl.matsv.viabackwards.protocol.protocol1_11_1to1_12.data.AdvancementTranslations;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.ComponentRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.ClientboundPackets1_12;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

public class ChatPackets1_12
extends Rewriter<Protocol1_11_1To1_12> {
    private final ComponentRewriter componentRewriter = new ComponentRewriter(){

        @Override
        protected void handleTranslate(JsonObject object, String translate) {
            String text = AdvancementTranslations.get(translate);
            if (text != null) {
                object.addProperty("translate", text);
            }
        }
    };

    public ChatPackets1_12(Protocol1_11_1To1_12 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        ((Protocol1_11_1To1_12)this.protocol).registerOutgoing(ClientboundPackets1_12.CHAT_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    JsonElement element = wrapper.passthrough(Type.COMPONENT);
                    ChatPackets1_12.this.componentRewriter.processText(element);
                });
            }
        });
    }
}

