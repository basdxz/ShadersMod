package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class SMCCTRenderBlocks implements IClassTransformer {
    static final String[] fieldsBlockLightLevel = new String[]{null, "blockLightLevel05", "blockLightLevel06", "blockLightLevel08"};

    @Override
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTRenderBlocks.CVTransform cv = new SMCCTRenderBlocks.CVTransform(cw);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    private static class CVTransform extends ClassVisitor {
        String classname;

        public CVTransform(ClassVisitor cv) {
            super(262144, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.classname = name;
            this.cv.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (Names.renderBlocks_renderBlockByRenderType.equalsNameDesc(name, desc)) {
                return new SMCCTRenderBlocks.MVrenBlkByRenType(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.renderBlocks_renderBlockFlowerPot.equalsNameDesc(name, desc)) {
                return new SMCCTRenderBlocks.MVrenBlkFlowerPot(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (!Names.renderBlocks_renderStdBlockWithAOP.equalsNameDesc(name, desc)
                    && !Names.renderBlocks_renderStdBlockWithAO.equalsNameDesc(name, desc)) {
                if (!Names.renderBlocks_renderStdBlockWithCM.equalsNameDesc(name, desc)
                        && !Names.renderBlocks_renderBlockCactusImpl.equalsNameDesc(name, desc)
                        && !Names.renderBlocks_renderBlockBed.equalsNameDesc(name, desc)
                        && !Names.renderBlocks_renderBlockFluids.equalsNameDesc(name, desc)
                        && !Names.renderBlocks_renderBlockDoor.equalsNameDesc(name, desc)
                        && !Names.renderBlocks_renderBlockSandFalling.equalsNameDesc(name, desc)) {
                    return Names.renderBlocks_renderPistonExtension.equalsNameDesc(name, desc)
                            ? new MVrenBlkPistonExt(this.cv.visitMethod(access, name, desc, signature, exceptions))
                            : this.cv.visitMethod(access, name, desc, signature, exceptions);
                } else {
                    return new SMCCTRenderBlocks.MVrenBlkFVar(this.cv.visitMethod(access, name, desc, signature, exceptions));
                }
            } else {
                return new SMCCTRenderBlocks.MVrenBlkWithAO(
                        access, name, desc, signature, exceptions, this.cv.visitMethod(access, name, desc, signature, exceptions)
                );
            }
        }
    }

    private static class MVrenBlkByRenType extends MethodVisitor {
        int nPatch = 0;

        public MVrenBlkByRenType(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitCode() {
            this.mv.visitCode();
            this.mv.visitVarInsn(25, 0);
            this.mv.visitVarInsn(25, 1);
            this.mv.visitVarInsn(21, 2);
            this.mv.visitVarInsn(21, 3);
            this.mv.visitVarInsn(21, 4);
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "pushEntity", "(" + Names.renderBlocks_.desc + Names.block_.desc + "III)V");
            ++this.nPatch;
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == 172) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "popEntity", "()V");
                ++this.nPatch;
            }

            this.mv.visitInsn(opcode);
        }

        @Override
        public void visitEnd() {
            this.mv.visitEnd();
        }
    }

    private static class MVrenBlkFVar extends MethodVisitor {
        int nPatch = 0;
        int state = 0;

        public MVrenBlkFVar(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            int match1 = 0;
            if (cst instanceof Float) {
                float fcst = (Float) cst;
                if (fcst == 0.5F) {
                    match1 = 1;
                } else if (fcst == 0.6F) {
                    match1 = 2;
                } else if (fcst == 0.8F) {
                    match1 = 3;
                }
            }

            if (match1 != 0 && this.state < 3) {
                ++this.state;
                String fieldName = SMCCTRenderBlocks.fieldsBlockLightLevel[match1];
                this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", fieldName, "F");
                ++this.nPatch;
            } else {
                this.mv.visitLdcInsn(cst);
            }
        }

        @Override
        public void visitEnd() {
            this.mv.visitEnd();
        }
    }

    private static class MVrenBlkFlowerPot extends MethodVisitor {
        int nPatch = 0;
        int state = 0;

        public MVrenBlkFlowerPot(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            super.visitVarInsn(opcode, var);
            if (this.state == 1) {
                ++this.state;
                this.mv.visitVarInsn(25, var);
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "pushEntity", "(" + Names.block_.desc + ")V");
            }

        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            super.visitMethodInsn(opcode, owner, name, desc);
            switch (this.state) {
                case 0:
                    if (Names.block_getBlockFromItem.equals(owner, name, desc)) {
                        ++this.state;
                    }
                case 1:
                default:
                    break;
                case 2:
                    if (Names.tessellator_addTranslation.equals(owner, name, desc)) {
                        ++this.state;
                    }
                    break;
                case 3:
                    if (Names.tessellator_addTranslation.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "popEntity", "()V");
                    }
            }

        }

        @Override
        public void visitCode() {
            this.mv.visitCode();
            this.mv.visitVarInsn(25, 0);
            this.mv.visitVarInsn(25, 1);
            this.mv.visitVarInsn(21, 2);
            this.mv.visitVarInsn(21, 3);
            this.mv.visitVarInsn(21, 4);
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "pushEntity", "(" + Names.renderBlocks_.desc + Names.block_.desc + "III)V");
            ++this.nPatch;
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == 172) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "popEntity", "()V");
                ++this.nPatch;
            }

            this.mv.visitInsn(opcode);
        }

        @Override
        public void visitEnd() {
            this.mv.visitEnd();
        }
    }

    private static class MVrenBlkPistonExt extends MethodVisitor {
        int nPatch = 0;
        int state = 0;

        public MVrenBlkPistonExt(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            this.state = 1;
            this.mv.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            int match1 = 0;
            if (cst instanceof Float) {
                float fcst = (Float) cst;
                if (fcst == 0.5F) {
                    match1 = 1;
                } else if (fcst == 0.6F) {
                    match1 = 2;
                } else if (fcst == 0.8F) {
                    match1 = 3;
                }
            }

            if (match1 != 0 && this.state == 1) {
                String fieldName = SMCCTRenderBlocks.fieldsBlockLightLevel[match1];
                this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", fieldName, "F");
                ++this.nPatch;
            } else {
                this.mv.visitLdcInsn(cst);
            }
        }

        @Override
        public void visitEnd() {
            this.mv.visitEnd();
        }
    }

    private static class MVrenBlkWithAO extends MethodVisitor {
        MethodVisitor mv1;
        MethodNode mn;
        int nPatch = 0;

        public MVrenBlkWithAO(int access, String name, String desc, String signature, String[] exceptions, MethodVisitor mv) {
            super(262144);
            super.mv = this.mn = new MethodNode(access, name, desc, signature, exceptions);
            this.mv1 = mv;
        }

        @Override
        public void visitEnd() {
            this.mn.visitEnd();
            this.mn.accept(this.mv1);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            int match1 = 0;
            if (cst instanceof Float) {
                float fcst = (Float) cst;
                if (fcst == 0.5F) {
                    match1 = 1;
                } else if (fcst == 0.6F) {
                    match1 = 2;
                } else if (fcst == 0.8F) {
                    match1 = 3;
                }
            }

            if (match1 != 0) {
                int match2 = 0;
                InsnList insns = this.mn.instructions;
                AbstractInsnNode insn = insns.getLast();
                if (insn != null && insn.getOpcode() == 23) {
                    insn = insn.getPrevious();
                    if (insn != null && insn.getOpcode() == 25) {
                        insn = insn.getPrevious();
                        if (insn != null && insn.getOpcode() == 25) {
                            insn = insn.getPrevious();
                            if (insn != null && insn.getOpcode() == 25) {
                                insn = insn.getPrevious();
                                if (insn != null && insn.getOpcode() == 25) {
                                    match2 = match1;
                                }
                            }
                        }
                    }
                }

                if (insn != null && insn.getOpcode() == 25) {
                    insn = insn.getPrevious();
                    if (insn != null && insn.getOpcode() == 25) {
                        insn = insn.getPrevious();
                        if (insn != null && insn.getOpcode() == 25) {
                            insn = insn.getPrevious();
                            if (insn != null && insn.getOpcode() == 25) {
                                match2 = match1;
                            }
                        }
                    }
                }

                if (match2 != 0) {
                    String fieldName = SMCCTRenderBlocks.fieldsBlockLightLevel[match2];
                    this.mn.visitFieldInsn(178, "shadersmodcore/client/Shaders", fieldName, "F");
                    ++this.nPatch;
                    return;
                }
            }

            this.mn.visitLdcInsn(cst);
        }
    }
}
