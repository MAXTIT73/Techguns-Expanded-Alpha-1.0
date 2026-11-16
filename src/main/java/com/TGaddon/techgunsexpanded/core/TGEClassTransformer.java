package com.TGaddon.techgunsexpanded.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Bytecode patches for Techguns classes that expose no API for what we change.
 *
 * 1) techguns.items.guns.GenericGun.shootGunPrimary
 *    Makes the "spread while blocking with a shield" penalty configurable.
 *    The penalty is:
 *        if (mainHand && player.isActiveItemStackBlocking())
 *            spreadPenalty = (handType == ONE_HANDED) ? 4.0 : 8.0;
 *    Each constant is redirected to a static getter reading our JSON config:
 *        4.0 (one-handed)       -> ShieldSpreadConfig.oneHanded()
 *        8.0 (1.5-/two-handed)  -> ShieldSpreadConfig.other()
 *    The 4.0 literal is NOT unique in the method (it is also a sound range), so we
 *    only start replacing after the GunHandType.ONE_HANDED comparison that begins
 *    the penalty branch, and replace one of each.
 *
 * 2) techguns.events.TGGuiEvents.onRenderGameOverlyEvent
 *    Shows the back-slot charge HUD line for our jetpacks too. The original check
 *        item == JETPACK || item == JUMPPACK || item == ANTI_GRAV_PACK
 *    ends with
 *        getstatic TGItems.ANTI_GRAV_PACK ; if_acmpne SKIP
 *    which becomes
 *        invokestatic JetpackHudHooks.showsChargeHud(Item)Z ; ifeq SKIP
 *    (the item is already on the stack). See JetpackHudHooks.
 */
public class TGEClassTransformer implements IClassTransformer {

    private static final String GUN_CLASS  = "techguns.items.guns.GenericGun";
    private static final String GUN_METHOD = "shootGunPrimary";
    private static final String CONFIG = "com/TGaddon/techgunsexpanded/core/ShieldSpreadConfig";

    private static final String HUD_CLASS  = "techguns.events.TGGuiEvents";
    private static final String HUD_METHOD = "onRenderGameOverlyEvent";
    private static final String HUD_HOOK   = "com/TGaddon/techgunsexpanded/equipment/JetpackHudHooks";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return basicClass;
        }
        if (GUN_CLASS.equals(transformedName)) {
            return patchShieldSpread(basicClass);
        }
        if (HUD_CLASS.equals(transformedName)) {
            return patchJetpackHud(basicClass);
        }
        return basicClass;
    }

    // ------------------------------------------------------------------ patch 1

    private static byte[] patchShieldSpread(byte[] basicClass) {
        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);

        int oneDone = 0, otherDone = 0;
        for (MethodNode mn : cn.methods) {
            if (!GUN_METHOD.equals(mn.name)) {
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

    // ------------------------------------------------------------------ patch 2

    private static byte[] patchJetpackHud(byte[] basicClass) {
        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);

        int done = 0;
        for (MethodNode mn : cn.methods) {
            if (!HUD_METHOD.equals(mn.name)) {
                continue;
            }
            for (AbstractInsnNode insn : mn.instructions.toArray()) {
                if (!(insn instanceof FieldInsnNode)) {
                    continue;
                }
                FieldInsnNode fi = (FieldInsnNode) insn;
                if (fi.getOpcode() != Opcodes.GETSTATIC
                        || !"techguns/TGItems".equals(fi.owner)
                        || !"ANTI_GRAV_PACK".equals(fi.name)) {
                    continue;
                }
                AbstractInsnNode next = fi.getNext();
                while (next != null && next.getOpcode() < 0) { // skip labels/line numbers/frames
                    next = next.getNext();
                }
                if (!(next instanceof JumpInsnNode) || next.getOpcode() != Opcodes.IF_ACMPNE) {
                    continue;
                }
                JumpInsnNode jump = (JumpInsnNode) next;

                mn.instructions.set(fi, new MethodInsnNode(Opcodes.INVOKESTATIC, HUD_HOOK,
                        "showsChargeHud", "(Lnet/minecraft/item/Item;)Z", false));
                mn.instructions.set(jump, new JumpInsnNode(Opcodes.IFEQ, jump.label));
                done++;
            }
        }

        if (done == 1) {
            System.out.println("[TechgunsExpanded Core] Back-slot charge HUD now also covers "
                    + "Techguns Expanded jetpacks.");
        } else {
            System.out.println("[TechgunsExpanded Core] WARNING: jetpack HUD patch applied "
                    + done + " times (expected 1). Techguns may have changed.");
        }

        // Stack depth only shrinks (item + static -> item), so COMPUTE_MAXS is enough;
        // branch targets are unchanged, so existing stack map frames stay valid.
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
