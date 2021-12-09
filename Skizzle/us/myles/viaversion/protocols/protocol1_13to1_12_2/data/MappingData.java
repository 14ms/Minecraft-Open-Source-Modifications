/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.io.CharStreams
 *  org.jetbrains.annotations.Nullable
 */
package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.MappingDataLoader;
import us.myles.ViaVersion.api.data.Mappings;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.reflect.TypeToken;

public class MappingData
extends us.myles.ViaVersion.api.data.MappingData {
    private final Map<String, Integer[]> blockTags = new HashMap<String, Integer[]>();
    private final Map<String, Integer[]> itemTags = new HashMap<String, Integer[]>();
    private final Map<String, Integer[]> fluidTags = new HashMap<String, Integer[]>();
    private final BiMap<Short, String> oldEnchantmentsIds = HashBiMap.create();
    private final Map<String, String> translateMapping = new HashMap<String, String>();
    private final Map<String, String> mojangTranslation = new HashMap<String, String>();
    private final BiMap<String, String> channelMappings = HashBiMap.create();
    private Mappings enchantmentMappings;

    public MappingData() {
        super("1.12", "1.13");
    }

    @Override
    public void loadExtras(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings) {
        JsonObject object;
        this.loadTags(this.blockTags, newMappings.getAsJsonObject("block_tags"));
        this.loadTags(this.itemTags, newMappings.getAsJsonObject("item_tags"));
        this.loadTags(this.fluidTags, newMappings.getAsJsonObject("fluid_tags"));
        this.loadEnchantments((Map<Short, String>)this.oldEnchantmentsIds, oldMappings.getAsJsonObject("enchantments"));
        this.enchantmentMappings = new Mappings(72, oldMappings.getAsJsonObject("enchantments"), newMappings.getAsJsonObject("enchantments"));
        if (Via.getConfig().isSnowCollisionFix()) {
            this.blockMappings.getOldToNew()[1248] = 3416;
        }
        if (Via.getConfig().isInfestedBlocksFix()) {
            short[] oldToNew = this.blockMappings.getOldToNew();
            oldToNew[1552] = 1;
            oldToNew[1553] = 14;
            oldToNew[1554] = 3983;
            oldToNew[1555] = 3984;
            oldToNew[1556] = 3985;
            oldToNew[1557] = 3986;
        }
        if ((object = MappingDataLoader.loadFromDataDir("channelmappings-1.13.json")) != null) {
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                String oldChannel = entry.getKey();
                String newChannel = entry.getValue().getAsString();
                if (!MappingData.isValid1_13Channel(newChannel)) {
                    Via.getPlatform().getLogger().warning("Channel '" + newChannel + "' is not a valid 1.13 plugin channel, please check your configuration!");
                    continue;
                }
                this.channelMappings.put((Object)oldChannel, (Object)newChannel);
            }
        }
        Map translateData = (Map)GsonUtil.getGson().fromJson((Reader)new InputStreamReader(MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/mapping-lang-1.12-1.13.json")), new TypeToken<Map<String, String>>(){}.getType());
        try {
            String[] lines;
            try (InputStreamReader reader = new InputStreamReader(MappingData.class.getClassLoader().getResourceAsStream("mojang-translations/en_US.properties"), StandardCharsets.UTF_8);){
                lines = CharStreams.toString((Readable)reader).split("\n");
            }
            for (String line : lines) {
                String[] keyAndTranslation;
                if (line.isEmpty() || (keyAndTranslation = line.split("=", 2)).length != 2) continue;
                String key = keyAndTranslation[0];
                if (!translateData.containsKey(key)) {
                    String translation = keyAndTranslation[1].replaceAll("%(\\d\\$)?d", "%$1s");
                    this.mojangTranslation.put(key, translation);
                    continue;
                }
                String dataValue = (String)translateData.get(key);
                if (dataValue == null) continue;
                this.translateMapping.put(key, dataValue);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Mappings loadFromObject(JsonObject oldMappings, JsonObject newMappings, @Nullable JsonObject diffMappings, String key) {
        if (key.equals("blocks")) {
            return new Mappings(4084, oldMappings.getAsJsonObject("blocks"), newMappings.getAsJsonObject("blockstates"));
        }
        return super.loadFromObject(oldMappings, newMappings, diffMappings, key);
    }

    public static String validateNewChannel(String newId) {
        if (!MappingData.isValid1_13Channel(newId)) {
            return null;
        }
        int separatorIndex = newId.indexOf(58);
        if ((separatorIndex == -1 || separatorIndex == 0) && newId.length() <= 10) {
            newId = "minecraft:" + newId;
        }
        return newId;
    }

    public static boolean isValid1_13Channel(String channelId) {
        return channelId.matches("([0-9a-z_.-]+):([0-9a-z_/.-]+)");
    }

    private void loadTags(Map<String, Integer[]> output, JsonObject newTags) {
        for (Map.Entry<String, JsonElement> entry : newTags.entrySet()) {
            JsonArray ids = entry.getValue().getAsJsonArray();
            Integer[] idsArray = new Integer[ids.size()];
            for (int i = 0; i < ids.size(); ++i) {
                idsArray[i] = ids.get(i).getAsInt();
            }
            output.put(entry.getKey(), idsArray);
        }
    }

    private void loadEnchantments(Map<Short, String> output, JsonObject enchantments) {
        for (Map.Entry<String, JsonElement> enchantment : enchantments.entrySet()) {
            output.put(Short.parseShort(enchantment.getKey()), enchantment.getValue().getAsString());
        }
    }

    public Map<String, Integer[]> getBlockTags() {
        return this.blockTags;
    }

    public Map<String, Integer[]> getItemTags() {
        return this.itemTags;
    }

    public Map<String, Integer[]> getFluidTags() {
        return this.fluidTags;
    }

    public BiMap<Short, String> getOldEnchantmentsIds() {
        return this.oldEnchantmentsIds;
    }

    public Map<String, String> getTranslateMapping() {
        return this.translateMapping;
    }

    public Map<String, String> getMojangTranslation() {
        return this.mojangTranslation;
    }

    public BiMap<String, String> getChannelMappings() {
        return this.channelMappings;
    }

    public Mappings getEnchantmentMappings() {
        return this.enchantmentMappings;
    }
}

