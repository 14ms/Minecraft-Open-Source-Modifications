/*
 * Decompiled with CFR 0.150.
 */
package us.myles.ViaVersion.api.rewriters;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonPrimitive;
import us.myles.viaversion.libs.gson.JsonSyntaxException;

public class ComponentRewriter {
    protected final Protocol protocol;

    public ComponentRewriter(Protocol protocol) {
        this.protocol = protocol;
    }

    public ComponentRewriter() {
        this.protocol = null;
    }

    public void registerChatMessage(ClientboundPacketType packetType) {
        this.protocol.registerOutgoing(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> ComponentRewriter.this.processText(wrapper.passthrough(Type.COMPONENT)));
            }
        });
    }

    public void registerBossBar(ClientboundPacketType packetType) {
        this.protocol.registerOutgoing(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UUID);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int action = wrapper.get(Type.VAR_INT, 0);
                    if (action == 0 || action == 3) {
                        ComponentRewriter.this.processText(wrapper.passthrough(Type.COMPONENT));
                    }
                });
            }
        });
    }

    public void registerCombatEvent(ClientboundPacketType packetType) {
        this.protocol.registerOutgoing(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    if (wrapper.passthrough(Type.VAR_INT) == 2) {
                        wrapper.passthrough(Type.VAR_INT);
                        wrapper.passthrough(Type.INT);
                        ComponentRewriter.this.processText(wrapper.passthrough(Type.COMPONENT));
                    }
                });
            }
        });
    }

    public void registerTitle(ClientboundPacketType packetType) {
        this.protocol.registerOutgoing(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int action = wrapper.passthrough(Type.VAR_INT);
                    if (action >= 0 && action <= 2) {
                        ComponentRewriter.this.processText(wrapper.passthrough(Type.COMPONENT));
                    }
                });
            }
        });
    }

    public JsonElement processText(String value) {
        try {
            JsonElement root = GsonUtil.getJsonParser().parse(value);
            this.processText(root);
            return root;
        }
        catch (JsonSyntaxException e) {
            if (Via.getManager().isDebug()) {
                Via.getPlatform().getLogger().severe("Error when trying to parse json: " + value);
                throw e;
            }
            return new JsonPrimitive(value);
        }
    }

    public void processText(JsonElement element) {
        JsonObject hoverEvent;
        JsonElement extra;
        JsonElement translate;
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (element.isJsonArray()) {
            this.processAsArray(element);
            return;
        }
        if (element.isJsonPrimitive()) {
            this.handleText(element.getAsJsonPrimitive());
            return;
        }
        JsonObject object = element.getAsJsonObject();
        JsonPrimitive text = object.getAsJsonPrimitive("text");
        if (text != null) {
            this.handleText(text);
        }
        if ((translate = object.get("translate")) != null) {
            this.handleTranslate(object, translate.getAsString());
            JsonElement with = object.get("with");
            if (with != null) {
                this.processAsArray(with);
            }
        }
        if ((extra = object.get("extra")) != null) {
            this.processAsArray(extra);
        }
        if ((hoverEvent = object.getAsJsonObject("hoverEvent")) != null) {
            this.handleHoverEvent(hoverEvent);
        }
    }

    protected void handleText(JsonPrimitive text) {
    }

    protected void handleTranslate(JsonObject object, String translate) {
    }

    protected void handleHoverEvent(JsonObject hoverEvent) {
        JsonObject contents;
        String action = hoverEvent.getAsJsonPrimitive("action").getAsString();
        if (action.equals("show_text")) {
            JsonElement value = hoverEvent.get("value");
            this.processText(value != null ? value : hoverEvent.get("contents"));
        } else if (action.equals("show_entity") && (contents = hoverEvent.getAsJsonObject("contents")) != null) {
            this.processText(contents.get("name"));
        }
    }

    private void processAsArray(JsonElement element) {
        for (JsonElement jsonElement : element.getAsJsonArray()) {
            this.processText(jsonElement);
        }
    }

    public <T extends Protocol> T getProtocol() {
        return (T)this.protocol;
    }
}

