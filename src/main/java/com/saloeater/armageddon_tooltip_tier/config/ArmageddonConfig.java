package com.saloeater.armageddon_tooltip_tier.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmageddonConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<ClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
    }

    public static class ClientConfig {
        public final ForgeConfigSpec.ConfigValue<List<? extends List<String>>> tags;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Armageddon Tooltip Tier Configuration")
                    .push("general");

            // Load defaults from JSON
            List<List<String>> defaultTags = new ArrayList<>();

            try {
                InputStream stream = ArmageddonConfig.class.getClassLoader().getResourceAsStream("armageddontags.json");
                if (stream != null) {
                    JsonObject root = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                    if (root.has("tags")) {
                        root.getAsJsonArray("tags").forEach(element -> {
                            JsonObject tagObj = element.getAsJsonObject();
                            String tag = tagObj.get("tags").getAsString();
                            String label = tagObj.get("label").getAsString();
                            String color = tagObj.get("color").getAsString();
                            String advancement = tagObj.get("advancement").getAsString();

                            defaultTags.add(Arrays.asList(tag, label, color, advancement));
                        });
                    }
                    LOGGER.info("Loaded {} default tag entries from armageddontags.json", defaultTags.size());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load defaults from armageddontags.json", e);
            }

            tags = builder
                    .comment("List of tag entries. Format: [tags, label, color, advancement]",
                            "Tags can be comma-separated for multiple tags",
                            "Example: [\"forge:diamond_tools,minecraft:swords\", \"Eldorath the Ancient Builder!\", \"c\", \"armageddon_mod:the_diamond_keeper\"]")
                    .defineList("tags", defaultTags, obj -> {
                        if (!(obj instanceof List)) return false;
                        List<?> list = (List<?>) obj;
                        return list.size() == 4 && list.stream().allMatch(item -> item instanceof String);
                    });

            builder.pop();
        }

        public List<TagEntry> getTagEntries() {
            List<TagEntry> entries = new ArrayList<>();

            for (List<String> entry : tags.get()) {
                if (entry.size() >= 4) {
                    entries.add(new TagEntry(entry.get(0), entry.get(1), entry.get(2), entry.get(3)));
                }
            }

            return entries;
        }
    }

    public static class TagEntry {
        private final String tag;
        private final List<String> tags;
        private final String label;
        private final String color;
        private final String advancement;

        public TagEntry(String tag, String label, String color, String advancement) {
            this.tag = tag;
            this.label = label;
            this.color = color;
            this.advancement = advancement;

            // Parse comma-separated tags
            this.tags = new ArrayList<>();
            for (String t : tag.split(",")) {
                String trimmed = t.trim();
                if (!trimmed.isEmpty()) {
                    this.tags.add(trimmed);
                }
            }
        }

        public String getTag() {
            return tag;
        }

        public List<String> getTags() {
            return tags;
        }

        public List<ResourceLocation> getTagLocations() {
            List<ResourceLocation> locations = new ArrayList<>();
            for (String t : tags) {
                locations.add(new ResourceLocation(t));
            }
            return locations;
        }

        public String getLabel() {
            return label;
        }

        public String getColor() {
            return color;
        }

        public ResourceLocation getAdvancement() {
            return new ResourceLocation(advancement);
        }

        public List<TagKey<Item>> getTagKeys() {
            List<TagKey<Item>> tagKeys = new ArrayList<>();
            for (String t : tags) {
                tagKeys.add(TagKey.create(net.minecraft.core.registries.Registries.ITEM, new ResourceLocation(t)));
            }
            return tagKeys;
        }
    }
}
