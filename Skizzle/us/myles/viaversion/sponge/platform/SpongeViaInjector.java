/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  org.spongepowered.api.MinecraftVersion
 *  org.spongepowered.api.Sponge
 */
package us.myles.ViaVersion.sponge.platform;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.platform.ViaInjector;
import us.myles.ViaVersion.sponge.handlers.SpongeChannelInitializer;
import us.myles.ViaVersion.util.ListWrapper;
import us.myles.ViaVersion.util.ReflectionUtil;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonObject;

public class SpongeViaInjector
implements ViaInjector {
    private List<ChannelFuture> injectedFutures = new ArrayList<ChannelFuture>();
    private List<Pair<Field, Object>> injectedLists = new ArrayList<Pair<Field, Object>>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void inject() throws Exception {
        try {
            Object connection = SpongeViaInjector.getServerConnection();
            if (connection == null) {
                throw new Exception("We failed to find the core component 'ServerConnection', please file an issue on our GitHub.");
            }
            for (Field field : connection.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(connection);
                if (!(value instanceof List)) continue;
                ListWrapper wrapper = new ListWrapper((List)value){

                    @Override
                    public void handleAdd(Object o) {
                        if (o instanceof ChannelFuture) {
                            try {
                                SpongeViaInjector.this.injectChannelFuture((ChannelFuture)o);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                this.injectedLists.add(new Pair<Field, Object>(field, connection));
                field.set(connection, wrapper);
                ListWrapper listWrapper = wrapper;
                synchronized (listWrapper) {
                    for (Object o : (List)value) {
                        if (!(o instanceof ChannelFuture)) break;
                        this.injectChannelFuture((ChannelFuture)o);
                    }
                }
            }
        }
        catch (Exception e) {
            Via.getPlatform().getLogger().severe("Unable to inject ViaVersion, please post these details on our GitHub and ensure you're using a compatible server version.");
            throw e;
        }
    }

    private void injectChannelFuture(ChannelFuture future) throws Exception {
        try {
            List names = future.channel().pipeline().names();
            ChannelHandler bootstrapAcceptor = null;
            for (String name : names) {
                ChannelHandler handler = future.channel().pipeline().get(name);
                try {
                    ReflectionUtil.get((Object)handler, "childHandler", ChannelInitializer.class);
                    bootstrapAcceptor = handler;
                }
                catch (Exception exception) {}
            }
            if (bootstrapAcceptor == null) {
                bootstrapAcceptor = future.channel().pipeline().first();
            }
            try {
                ChannelInitializer oldInit = ReflectionUtil.get((Object)bootstrapAcceptor, "childHandler", ChannelInitializer.class);
                SpongeChannelInitializer newInit = new SpongeChannelInitializer((ChannelInitializer<Channel>)oldInit);
                ReflectionUtil.set((Object)bootstrapAcceptor, "childHandler", (Object)newInit);
                this.injectedFutures.add(future);
            }
            catch (NoSuchFieldException e) {
                throw new Exception("Unable to find core component 'childHandler', please check your plugins. issue: " + bootstrapAcceptor.getClass().getName());
            }
        }
        catch (Exception e) {
            Via.getPlatform().getLogger().severe("We failed to inject ViaVersion, have you got late-bind enabled with something else?");
            throw e;
        }
    }

    @Override
    public void uninject() {
        for (ChannelFuture channelFuture : this.injectedFutures) {
            List names = channelFuture.channel().pipeline().names();
            ChannelHandler bootstrapAcceptor = null;
            for (String name : names) {
                ChannelHandler handler = channelFuture.channel().pipeline().get(name);
                try {
                    ChannelInitializer oldInit = ReflectionUtil.get((Object)handler, "childHandler", ChannelInitializer.class);
                    if (!(oldInit instanceof SpongeChannelInitializer)) continue;
                    bootstrapAcceptor = handler;
                }
                catch (Exception exception) {}
            }
            if (bootstrapAcceptor == null) {
                bootstrapAcceptor = channelFuture.channel().pipeline().first();
            }
            try {
                ChannelInitializer oldInit = ReflectionUtil.get((Object)bootstrapAcceptor, "childHandler", ChannelInitializer.class);
                if (!(oldInit instanceof SpongeChannelInitializer)) continue;
                ReflectionUtil.set((Object)bootstrapAcceptor, "childHandler", ((SpongeChannelInitializer)oldInit).getOriginal());
            }
            catch (Exception e) {
                Via.getPlatform().getLogger().severe("Failed to remove injection handler, reload won't work with connections, please reboot!");
            }
        }
        this.injectedFutures.clear();
        for (Pair pair : this.injectedLists) {
            try {
                Object o = ((Field)pair.getKey()).get(pair.getValue());
                if (!(o instanceof ListWrapper)) continue;
                ((Field)pair.getKey()).set(pair.getValue(), ((ListWrapper)o).getOriginalList());
            }
            catch (IllegalAccessException e) {
                Via.getPlatform().getLogger().severe("Failed to remove injection, reload won't work with connections, please reboot!");
            }
        }
        this.injectedLists.clear();
    }

    public static Object getServer() throws Exception {
        return Sponge.getServer();
    }

    @Override
    public int getServerProtocolVersion() throws Exception {
        MinecraftVersion mcv = Sponge.getPlatform().getMinecraftVersion();
        try {
            return (Integer)mcv.getClass().getDeclaredMethod("getProtocol", new Class[0]).invoke((Object)mcv, new Object[0]);
        }
        catch (Exception e) {
            throw new Exception("Failed to get server protocol", e);
        }
    }

    @Override
    public String getEncoderName() {
        return "encoder";
    }

    @Override
    public String getDecoderName() {
        return "decoder";
    }

    public static Object getServerConnection() throws Exception {
        Class<?> serverClazz = Class.forName("net.minecraft.server.MinecraftServer");
        Object server = SpongeViaInjector.getServer();
        Object connection = null;
        for (Method m : serverClazz.getDeclaredMethods()) {
            if (m.getReturnType() == null || !m.getReturnType().getSimpleName().equals("NetworkSystem") || m.getParameterTypes().length != 0) continue;
            connection = m.invoke(server, new Object[0]);
        }
        return connection;
    }

    @Override
    public JsonObject getDump() {
        JsonObject data = new JsonObject();
        JsonArray injectedChannelInitializers = new JsonArray();
        for (ChannelFuture cf : this.injectedFutures) {
            JsonObject info = new JsonObject();
            info.addProperty("futureClass", cf.getClass().getName());
            info.addProperty("channelClass", cf.channel().getClass().getName());
            JsonArray pipeline = new JsonArray();
            for (String pipeName : cf.channel().pipeline().names()) {
                JsonObject pipe = new JsonObject();
                pipe.addProperty("name", pipeName);
                if (cf.channel().pipeline().get(pipeName) != null) {
                    pipe.addProperty("class", cf.channel().pipeline().get(pipeName).getClass().getName());
                    try {
                        ChannelInitializer child = ReflectionUtil.get((Object)cf.channel().pipeline().get(pipeName), "childHandler", ChannelInitializer.class);
                        pipe.addProperty("childClass", child.getClass().getName());
                        if (child instanceof SpongeChannelInitializer) {
                            pipe.addProperty("oldInit", ((SpongeChannelInitializer)child).getOriginal().getClass().getName());
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                pipeline.add(pipe);
            }
            info.add("pipeline", pipeline);
            injectedChannelInitializers.add(info);
        }
        data.add("injectedChannelInitializers", injectedChannelInitializers);
        JsonObject wrappedLists = new JsonObject();
        JsonObject currentLists = new JsonObject();
        try {
            for (Pair<Field, Object> pair : this.injectedLists) {
                Object list = pair.getKey().get(pair.getValue());
                currentLists.addProperty(pair.getKey().getName(), list.getClass().getName());
                if (!(list instanceof ListWrapper)) continue;
                wrappedLists.addProperty(pair.getKey().getName(), ((ListWrapper)list).getOriginalList().getClass().getName());
            }
            data.add("wrappedLists", wrappedLists);
            data.add("currentLists", currentLists);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return data;
    }
}

