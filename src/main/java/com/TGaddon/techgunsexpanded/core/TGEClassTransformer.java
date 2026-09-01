package com.TGaddon.techgunsexpanded.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Makes the "spread while blocking with a shield" penalty for Techguns guns
 * configurable (and 0 by default).
 *
 * In GenericGun.shootGunPrimary the penalty is:
 *     if (mainHand && player.isActiveItemStackBlocking())
 *         spreadPenalty = (handType == ONE_HANDED) ? 4.0 : 8.0;
 *
 * Those 4.0 / 8.0 are hard-coded constants with no API/config. Instead of baking
 * in a fixed number, we REDIRECT each constant to one of our static getters, so
 * the value is read live from our JSON config (ShieldSpreadConfig):
 *     4.0 (one-handed)          -> ShieldSpreadConfig.oneHanded()
 *     8.0 (1.5-/two-handed)     -> ShieldSpreadConfig.other()
 *
 * The 4.0 literal is NOT unique in the method (it is also a sound range), so we
 * only start replacing after the GunHandType.ONE_HANDED comparison that begins
 * the penalty branch, and replace one of each.
 */
public class TGEClassTransformer implements IClassTransformer {

    private static final String TARGET_CLASS  = "techguns.items.guns.GenericGun";
    private static final String TARGET_METHOD = "shootGunPrimary";
    private static final String CONFIG = "com/TGaddon/techgunsexpanded/core/ShieldSpreadConfig";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }

        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);

        int oneDone = 0, otherDone = 0;
        for (MethodNode mn : cn.methods) {
            if (!TARGET_METHOD.equals(mn.name)) {
                continue;
            }
            boolean afterHandTypeCheck = false;
            for (AbstractInsnNode insn : mn.instructions.toArray()) {
                if (insn instanceof FieldInsnNode) {
                    FieldInsnNode fi = (FieldInsnNode) insn;
                    if (fi.getOpcode() == Opcodes.GETSTATIC
                            && fi.owner.endsWith("GunHandType")
                            && "ONE_HANDED".equals(fi.name)) {
                        afterHandTypeCheck = true;
                    }
                }
                if (afterHandTypeCheck && insn instanceof LdcInsnNode) {
                    LdcInsnNode ldc = (LdcInsnNode) insn;
                    if (ldc.cst instanceof Float) {
                        float v = (Float) ldc.cst;
                        if (v == 4.0f && oneDone == 0) {
                            mn.instructions.set(ldc, new MethodInsnNode(
                                    Opcodes.INVOKESTATIC, CONFIG, "oneHanded", "()F", false));
                            oneDone++;
                        } else if (v == 8.0f && otherDone == 0) {
                            mn.instructions.set(ldc, new MethodInsnNode(
                                    Opcodes.INVOKESTATIC, CONFIG, "other", "()F", false));
                            otherDone++;
                        }
                    }
                }
            }
        }

        if (oneDone == 1 && otherDone == 1) {
            System.out.println("[TechgunsExpanded Core] Shield-block spread is now config-driven "
                    + "(one-handed + 1.5/two-handed penalties redirected to JSON).");
        } else {
            System.out.println("[TechgunsExpanded Core] WARNING: shield-spread patch incomplete "
                    + "(oneHanded=" + oneDone + ", other=" + otherDone + "). Techguns may have changed; "
                    + "penalty left as-is.");
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
