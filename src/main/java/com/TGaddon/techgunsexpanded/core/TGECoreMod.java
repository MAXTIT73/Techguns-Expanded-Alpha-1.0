package com.TGaddon.techgunsexpanded.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * Core-mod entry point. Registers our bytecode transformer so it runs while
 * classes load, before Techguns' GenericGun is first used. This is required
 * because the shield-block spread penalty is a hard-coded constant inside
 * GenericGun.shootGunPrimary that Techguns exposes no API/config for.
 *
 * The jar is BOTH a core-mod and a normal @Mod; the manifest attributes
 * FMLCorePlugin + FMLCorePluginContainsFMLMod (see build.gradle) tell Forge that.
 */
@IFMLLoadingPlugin.Name("TechgunsExpandedCore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001) // after deobf/other core transformers
public class TGECoreMod implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.TGaddon.techgunsexpanded.core.TGEClassTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
