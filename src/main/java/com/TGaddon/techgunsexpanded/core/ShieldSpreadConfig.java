package com.TGaddon.techgunsexpanded.core;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Player-editable values for the shield-block bullet-spread penalty.
 *
 * These getters are called directly from Techguns' GenericGun.shootGunPrimary
 * after our core-mod transformer redirects the hard-coded 4.0 / 8.0 constants
 * here (see TGEClassTransformer). Defaults are 0.0 = no penalty, matching the
 * requested "remove the shield spread penalty for one-handed weapons", while
 * still letting players restore/tune it via the JSON file.
 *
 * Vanilla Techguns values were: one-handed = 4.0, 1.5-/two-handed = 8.0.
 */
public final class ShieldSpreadConfig {

    // Read from Techguns bytecode at runtime; volatile so the worldgen/render
    // threads see the value written on the main thread during preInit.
    public static volatile float ONE_HANDED = 0.0f;
    public static volatile float OTHER = 0.0f;

    private static final String FILE_NAME = "techguns_expanded_shield_spread.json";
    private static final String KEY_ONE   = "oneHandedShieldSpread";
    private static final String KEY_OTHER = "oneAndAHalfOrTwoHandedShieldSpread";

    private ShieldSpreadConfig() {}

    /** Called from patched Techguns code — must stay cheap. */
    public static float oneHanded() { return ONE_HANDED; }
    public static float other()     { return OTHER; }

    /** Load (or create with defaults) the JSON config. Call once in preInit. */
    public static void load(File configDir) {
        try {
            if (configDir != null && !configDir.exists()) {
                configDir.mkdirs();
            }
            File file = new File(configDir, FILE_NAME);
            if (file.exists()) {
                try (Reader r = new FileReader(file)) {
                    JsonObject o = new JsonParser().parse(r).getAsJsonObject();
                    if (o.has(KEY_ONE))   ONE_HANDED = o.get(KEY_ONE).getAsFloat();
                    if (o.has(KEY_OTHER)) OTHER      = o.get(KEY_OTHER).getAsFloat();
                }
            } else {
                writeDefaults(file);
            }
        } catch (Exception e) {
            System.out.println("[TechgunsExpanded] Could not read " + FILE_NAME
                    + ", using defaults (0.0). " + e);
        }
    }

    private static void writeDefaults(File file) throws IOException {
        JsonObject o = new JsonObject();
        o.addProperty("_comment", "Extra bullet spread applied to a Techguns gun while you block "
                + "with a shield in the off-hand. 0 = no penalty. Vanilla Techguns: "
                + "oneHanded=4.0, oneAndAHalfOrTwoHanded=8.0. Nether Blaster and other "
                + "1.5-handed guns use the second value.");
        o.addProperty(KEY_ONE, ONE_HANDED);
        o.addProperty(KEY_OTHER, OTHER);
        try (Writer w = new FileWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(o, w);
        }
    }
}
