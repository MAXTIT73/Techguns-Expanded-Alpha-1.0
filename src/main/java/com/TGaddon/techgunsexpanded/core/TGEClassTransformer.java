package com.TGaddon.techgunsexpanded.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Rebalances the one-handed-weapon spread penalty applied while blocking with a
 * shield in the off-hand.
 *
 * In Techguns, GenericGun.shootGunPrimary picks a spread factor of 4.0 normally
 * and 8.0 while the player is actively blocking (isActiveItemStackBlocking) with
 * a ONE_HANDED gun — i.e. blocking doubles (x2) the spread. That 8.0 is a
 * hard-coded constant with no API/config, so we rewrite it to 6.0 (x1.5).
 *
 * Techguns mod classes are NOT obfuscated at runtime, so we can match the class
 * and method by their plain names. The 8.0f constant is unique inside
 * shootGunPrimary, so the replacement is safe and targeted.
 */
public class TGEClassTransformer implements IClassTransformer {

    private static final String TARGET_CLASS  = "techguns.items.guns.GenericGun";
    private static final String TARGET_METHOD = "shootGunPrimary";

    private static final float FROM = 8.0f; // original blocking spread factor (x2)
    private static final float TO   = 6.0f; // new blocking spread factor (x1.5)

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }

        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);

        int patched = 0;
        for (MethodNode mn : cn.methods) {
            if (!TARGET_METHOD.equals(mn.name)) {
                continue;
            }
            for (AbstractInsnNode insn : mn.instructions.toArray()) {
                if (insn instanceof LdcInsnNode) {
                    LdcInsnNode ldc = (LdcInsnNode) insn;
                    if (ldc.cst instanceof Float && ((Float) ldc.cst) == FROM) {
                        ldc.cst = Float.valueOf(TO);
                        patched++;
                    }
                }
            }
        }

        if (patched > 0) {
            System.out.println("[TechgunsExpanded Core] Shield-block spread patched in "
                    + TARGET_METHOD + ": " + FROM + " -> " + TO + " (replacements=" + patched + ")");
        } else {
            System.out.println("[TechgunsExpanded Core] WARNING: did not find the "
                    + FROM + "f spread constant in " + TARGET_CLASS + "." + TARGET_METHOD
                    + " — Techguns may have changed; shield-spread patch was NOT applied.");
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
