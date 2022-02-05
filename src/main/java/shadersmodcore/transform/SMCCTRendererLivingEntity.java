package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTRendererLivingEntity implements IClassTransformer {
    @Override
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTRendererLivingEntity.CVTransform cv = new SMCCTRendererLivingEntity.CVTransform(cw);
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
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (Names.rendererLivingE_mainModel.name.equals(name) || Names.rendererLivingE_renderPassModel.name.equals(name)) {
                access = access & -8 | 1;
            }

            return this.cv.visitField(access, name, desc, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (Names.rendererLivingE_doRender.equalsNameDesc(name, desc)) {
                return new SMCCTRendererLivingEntity.MVdoRenderLiving(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                return Names.rendererLivingE_renderLabel.equalsNameDesc(name, desc)
                        ? new MVrenderLivingLabel(this.cv.visitMethod(access, name, desc, signature, exceptions))
                        : this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    private static class MVdoRenderLiving extends MethodVisitor {
        private int lastInt = 0;
        private int state = 0;
        private static final int stateEnd = 7;
        Label labelEndVH = null;

        public MVdoRenderLiving(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitCode() {
            this.mv.visitCode();
            this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "useEntityHurtFlash", "Z");
            Label label1 = new Label();
            this.mv.visitJumpInsn(153, label1);
            this.mv.visitVarInsn(25, 1);
            this.mv.visitFieldInsn(180, Names.entityLivingBase_hurtTime.clas, Names.entityLivingBase_hurtTime.name, Names.entityLivingBase_hurtTime.desc);
            Label label2 = new Label();
            this.mv.visitJumpInsn(157, label2);
            this.mv.visitVarInsn(25, 1);
            this.mv.visitFieldInsn(180, Names.entityLivingBase_deathTime.clas, Names.entityLivingBase_deathTime.name, Names.entityLivingBase_deathTime.desc);
            Label label3 = new Label();
            this.mv.visitJumpInsn(158, label3);
            this.mv.visitLabel(label2);
            this.mv.visitFrame(3, 0, null, 0, null);
            this.mv.visitIntInsn(16, 102);
            Label label4 = new Label();
            this.mv.visitJumpInsn(167, label4);
            this.mv.visitLabel(label3);
            this.mv.visitFrame(3, 0, null, 0, null);
            this.mv.visitInsn(3);
            this.mv.visitLabel(label4);
            this.mv.visitFrame(4, 0, null, 1, new Object[]{Opcodes.INTEGER});
            this.mv.visitVarInsn(25, 0);
            this.mv.visitVarInsn(25, 1);
            this.mv.visitVarInsn(25, 1);
            this.mv.visitVarInsn(23, 9);
            this.mv.visitMethodInsn(182, Names.entity_getBrightness.clas, Names.entity_getBrightness.name, Names.entity_getBrightness.desc);
            this.mv.visitVarInsn(23, 9);
            this.mv
                    .visitMethodInsn(
                            182,
                            Names.rendererLivingE_getColorMultiplier.clas,
                            Names.rendererLivingE_getColorMultiplier.name,
                            Names.rendererLivingE_getColorMultiplier.desc
                    );
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "setEntityHurtFlash", "(II)V");
            this.mv.visitLabel(label1);
            this.mv.visitFrame(3, 0, null, 0, null);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            if (opcode == 17) {
                this.lastInt = operand;
            }

            this.mv.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if (cst instanceof Integer) {
                int icst = (Integer) cst;
                if (icst == 32826 && this.labelEndVH != null) {
                    this.mv.visitLabel(this.labelEndVH);
                    this.labelEndVH = null;
                }
            }

            this.mv.visitLdcInsn(cst);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (opcode == 182 && Names.rendererLivingE_renderEquippedItems.equals(owner, name, desc)) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "resetEntityHurtFlash", "()V");
                this.mv.visitMethodInsn(opcode, owner, name, desc);
                this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "useEntityHurtFlash", "Z");
                this.labelEndVH = new Label();
                this.mv.visitJumpInsn(154, this.labelEndVH);
                this.state = 1;
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
                if (opcode == 184) {
                    if (Names.equals("org/lwjgl/opengl/GL11", "glDepthFunc", "(I)V", owner, name, desc)) {
                        if (this.state == 3) {
                            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginLivingDamage", "()V");
                            ++this.state;
                        } else if (this.state == 4) {
                            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endLivingDamage", "()V");
                            ++this.state;
                        }
                    } else if (Names.openGlHelper_setActiveTexture.equals(owner, name, desc)) {
                        if (this.state == 1) {
                            ++this.state;
                        } else if (this.state == 2) {
                            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "disableLightmap", "()V");
                            ++this.state;
                        } else if (this.state == 5) {
                            ++this.state;
                        } else if (this.state == 6) {
                            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "enableLightmap", "()V");
                            ++this.state;
                        }
                    }
                }

            }
        }

        @Override
        public void visitEnd() {
            this.mv.visitEnd();
            if (this.state != 7) {
                SMCLog.severe("state %d expected %d", this.state, 7);
            }

        }
    }

    private static class MVrenderLivingLabel extends MethodVisitor {
        int pushedInt = 0;

        public MVrenderLivingLabel(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            this.mv.visitIntInsn(opcode, operand);
            if (opcode == 17 && operand == 3553) {
                this.pushedInt = 3553;
            }

        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (this.pushedInt == 3553) {
                this.pushedInt = 0;
                if (opcode == 184 && owner.equals("org/lwjgl/opengl/GL11") && desc.equals("(I)V")) {
                    if (name.equals("glDisable")) {
                        owner = "shadersmodcore/client/Shaders";
                        name = "sglDisableT2D";
                    } else if (name.equals("glEnable")) {
                        owner = "shadersmodcore/client/Shaders";
                        name = "sglEnableT2D";
                    }
                }
            }

            this.mv.visitMethodInsn(opcode, owner, name, desc);
        }
    }
}
