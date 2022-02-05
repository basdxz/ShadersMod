package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTEntityRenderer implements IClassTransformer {
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTEntityRenderer.CVTransform cv = new SMCCTEntityRenderer.CVTransform(cw);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    private static class CVTransform extends ClassVisitor {
        String classname;

        public CVTransform(ClassVisitor cv) {
            super(262144, cv);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.classname = name;
            this.cv.visit(version, access, name, signature, superName, interfaces);
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (Names.entityRenderer_disableLightmap.equalsNameDesc(name, desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTEntityRenderer.MVdisableLightmap(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.entityRenderer_enableLightmap.equalsNameDesc(name, desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTEntityRenderer.MVenableLightmap(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.entityRenderer_updateFogColor.equalsNameDesc(name, desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTEntityRenderer.MVupdateFogColor(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.entityRenderer_setFogColorBuffer.equalsNameDesc(name, desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTEntityRenderer.MVsetFogColorBuffer(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.entityRenderer_setupFog.equalsNameDesc(name, desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTEntityRenderer.MVsetupFog(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.entityRenderer_renderCloudsCheck.equalsNameDesc(name, desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTEntityRenderer.MVrenderCloudsCheck(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.entityRenderer_renderHand.equalsNameDesc(name, desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                access = access & -7 | 1;
                return new SMCCTEntityRenderer.MVrenderHand(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.entityRenderer_renderWorld.equalsNameDesc(name, desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTEntityRenderer.MVrenderWorld(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                return this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    private static class MVdisableLightmap extends MethodVisitor {
        public MVdisableLightmap(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitInsn(int opcode) {
            if (opcode == 177) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "disableLightmap", "()V");
            }

            this.mv.visitInsn(opcode);
        }
    }

    private static class MVenableLightmap extends MethodVisitor {
        public MVenableLightmap(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitInsn(int opcode) {
            if (opcode == 177) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "enableLightmap", "()V");
            }

            this.mv.visitInsn(opcode);
        }
    }

    private static class MVrenderCloudsCheck extends MethodVisitor {
        public MVrenderCloudsCheck(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.gameSettings_shouldRenderClouds.equals(owner, name, desc)) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "shouldRenderClouds", "(" + Names.gameSettings_.desc + ")Z");
            } else if (Names.renderGlobal_renderClouds.equals(owner, name, desc)) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginClouds", "()V");
                this.mv.visitMethodInsn(opcode, owner, name, desc);
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endClouds", "()V");
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class MVrenderHand extends MethodVisitor {
        Label la1 = new Label();
        Label la2 = new Label();

        public MVrenderHand(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.equals("org/lwjgl/util/glu/Project", "gluPerspective", "(FFFF)V", owner, name, desc)) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "applyHandDepth", "()V");
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            } else if (Names.equals("org/lwjgl/opengl/GL11", "glPushMatrix", "()V", owner, name, desc)) {
                this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "isHandRendered", "Z");
                this.mv.visitJumpInsn(154, this.la1);
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            } else if (Names.equals("org/lwjgl/opengl/GL11", "glPopMatrix", "()V", owner, name, desc)) {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
                this.mv.visitLabel(this.la1);
                this.mv.visitFrame(3, 0, null, 0, null);
                this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "isCompositeRendered", "Z");
                this.mv.visitJumpInsn(154, this.la2);
                this.mv.visitInsn(177);
                this.mv.visitLabel(this.la2);
                this.mv.visitFrame(3, 0, null, 0, null);
                this.mv.visitVarInsn(25, 0);
                this.mv.visitVarInsn(23, 1);
                this.mv.visitInsn(141);
                this.mv
                        .visitMethodInsn(
                                182, Names.entityRenderer_disableLightmap.clas, Names.entityRenderer_disableLightmap.name, Names.entityRenderer_disableLightmap.desc
                        );
            } else if (Names.itemRenderer_renderItemInFirstPerson.equals(owner, name, desc)) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersRender", "renderItemFP", "(" + Names.itemRenderer_.desc + "F)V");
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class MVrenderWorld extends MethodVisitor {
        private static final int stateEnd = 32;
        int state = 0;
        String section = "";
        Label labelAfterUpdate = null;
        Label labelEndUpdate = null;
        Label labelNoSky = null;
        Label labelEndRender = null;

        public MVrenderWorld(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitCode() {
            this.mv.visitCode();
            this.mv.visitVarInsn(25, 0);
            this.mv.visitFieldInsn(180, Names.entityRenderer_mc.clas, Names.entityRenderer_mc.name, Names.entityRenderer_mc.desc);
            this.mv.visitVarInsn(23, 1);
            this.mv.visitVarInsn(22, 2);
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginRender", "(" + Names.minecraft_.desc + "FJ)V");
        }

        public void visitLdcInsn(Object cst) {
            if (cst instanceof String) {
                String scst = (String) cst;
                this.section = scst;
            }

            this.mv.visitLdcInsn(cst);
        }

        public void visitIntInsn(int opcode, int operand) {
            this.mv.visitIntInsn(opcode, operand);
        }

        public void visitJumpInsn(int opcode, Label label) {
            switch (this.state) {
                case 4:
                    if (opcode == 161) {
                        ++this.state;
                        this.mv.visitInsn(88);
                        this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "isShadowPass", "Z");
                        this.mv.visitJumpInsn(154, label);
                        return;
                    }
                    break;
                case 10:
                    if (opcode == 154) {
                        ++this.state;
                        this.labelAfterUpdate = label;
                        this.labelEndUpdate = new Label();
                        this.mv.visitJumpInsn(opcode, label);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginUpdateChunks", "()V");
                        return;
                    }
                    break;
                case 11:
                    if (label == this.labelAfterUpdate) {
                        this.mv.visitJumpInsn(opcode, this.labelEndUpdate);
                        return;
                    }
                    break;
                case 28:
                    if (opcode == 154) {
                        ++this.state;
                        this.mv.visitJumpInsn(opcode, label);
                        this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "isShadowPass", "Z");
                        this.mv.visitJumpInsn(154, label);
                        this.mv.visitVarInsn(25, 0);
                        this.mv.visitVarInsn(23, 1);
                        this.mv.visitVarInsn(21, 13);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersRender", "renderHand1", "(" + Names.entityRenderer_.desc + "FI)V");
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "renderCompositeFinal", "()V");
                        return;
                    }
            }

            this.mv.visitJumpInsn(opcode, label);
        }

        public void visitLabel(Label label) {
            switch (this.state) {
                case 11:
                    if (label == this.labelAfterUpdate) {
                        ++this.state;
                        this.mv.visitLabel(this.labelEndUpdate);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endUpdateChunks", "()V");
                        this.mv.visitLabel(label);
                        this.labelAfterUpdate = this.labelEndUpdate = null;
                        return;
                    }
                default:
                    this.mv.visitLabel(label);
            }
        }

        public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
            switch (this.state) {
                case 7:
                    this.state = 8;
                    this.mv.visitLabel(this.labelNoSky);
                    this.labelNoSky = null;
                    this.mv.visitFrame(type, nLocal, local, nStack, stack);
                    return;
                case 22:
                    ++this.state;
                    this.mv.visitFrame(type, nLocal, local, nStack, stack);
                    return;
                case 31:
                    ++this.state;
                    this.mv.visitFrame(type, nLocal, local, nStack, stack);
                    this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endRender", "()V");
                    return;
                default:
                    this.mv.visitFrame(type, nLocal, local, nStack, stack);
            }
        }

        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            switch (this.state) {
                case 3:
                    if (Names.gameSettings_renderDistance.equals(owner, name)) {
                        ++this.state;
                    }
                    break;
                case 23:
                    if (Names.entityRenderer_cameraZoom.equals(owner, name)) {
                        this.state = 28;
                        this.mv.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
            }

            this.mv.visitFieldInsn(opcode, owner, name, desc);
        }

        public void visitVarInsn(int opcode, int var) {
            switch (this.state) {
                case 29:
                    if (opcode == 25) {
                        ++this.state;
                        this.mv.visitVarInsn(opcode, var);
                        return;
                    }
                default:
                    this.mv.visitVarInsn(opcode, var);
            }
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            switch (this.state) {
                case 0:
                    if (Names.equals("org/lwjgl/opengl/GL11", "glViewport", "(IIII)V", owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "setViewport", "(IIII)V");
                        return;
                    }
                    break;
                case 1:
                    if (Names.equals("org/lwjgl/opengl/GL11", "glClear", "(I)V", owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "clearRenderBuffer", "()V");
                        return;
                    }
                    break;
                case 2:
                    if (Names.entityRenderer_setupCameraTransform.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitVarInsn(23, 1);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "setCamera", "(F)V");
                        return;
                    }
                    break;
                case 3:
                    if (Names.equals("Config", "isSkyEnabled", "()Z", owner, name, desc)) {
                        this.state = 6;
                        this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "isShadowPass", "Z");
                        this.labelNoSky = new Label();
                        this.mv.visitJumpInsn(154, this.labelNoSky);
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        return;
                    }
                case 4:
                case 7:
                case 10:
                case 11:
                case 15:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                default:
                    break;
                case 5:
                    if (Names.renderGlobal_renderSky.equals(owner, name, desc)) {
                        this.state = 8;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginSky", "()V");
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endSky", "()V");
                        return;
                    }
                    break;
                case 6:
                    if (Names.renderGlobal_renderSky.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginSky", "()V");
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endSky", "()V");
                        return;
                    }
                    break;
                case 8:
                    if (Names.iCamera_setPosition.equals(owner, name, desc) || Names.frustrum_setPosition.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersRender", "setFrustrumPosition", "(" + Names.frustrum_.desc + "DDD)V");
                        return;
                    }
                    break;
                case 9:
                    if (Names.renderGlobal_clipRenderersByFrustum.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv
                                .visitMethodInsn(
                                        184, "shadersmodcore/client/ShadersRender", "clipRenderersByFrustrum", "(" + Names.renderGlobal_.desc + Names.frustrum_.desc + "F)V"
                                );
                        return;
                    }
                    break;
                case 12:
                    if (Names.renderGlobal_sortAndRender.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginTerrain", "()V");
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endTerrain", "()V");
                        return;
                    }
                    break;
                case 13:
                    if (Names.effectRenderer_renderLitParticles.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginLitParticles", "()V");
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        return;
                    }
                    break;
                case 14:
                    if (Names.effectRenderer_renderParticles.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginParticles", "()V");
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endParticles", "()V");
                        this.state = 16;
                        return;
                    }
                    break;
                case 16:
                    if (Names.entityRenderer_renderRainSnow.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginWeather", "()V");
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endWeather", "()V");
                        return;
                    }
                    break;
                case 17:
                    if (Names.equals("org/lwjgl/opengl/GL11", "glDepthMask", "(Z)V", owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitVarInsn(25, 0);
                        this.mv.visitVarInsn(23, 1);
                        this.mv.visitVarInsn(21, 13);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersRender", "renderHand0", "(" + Names.entityRenderer_.desc + "FI)V");
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "preWater", "()V");
                        return;
                    }
                case 18:
                case 19:
                case 20:
                    if (Names.renderGlobal_sortAndRender.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginWater", "()V");
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endWater", "()V");
                        return;
                    }

                    if (Names.equals(Names.renderGlobal_.clas, "renderAllSortedRenderers", "(ID)I", owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginWater", "()V");
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endWater", "()V");
                        return;
                    }
                    break;
                case 21:
                    if (Names.entityRenderer_renderCloudsCheck.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        return;
                    }
                    break;
                case 30:
                    if (Names.entityRenderer_renderHand.equals(owner, name, desc)) {
                        ++this.state;
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersRender", "renderFPOverlay", "(" + Names.entityRenderer_.desc + "FI)V");
                        return;
                    }
            }

            this.mv.visitMethodInsn(opcode, owner, name, desc);
        }

        public void visitEnd() {
            if (this.state != 32) {
                SMCLog.severe("  state %d expect %d", this.state, 32);
            }

            this.mv.visitEnd();
        }
    }

    private static class MVsetFogColorBuffer extends MethodVisitor {
        public MVsetFogColorBuffer(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitCode() {
            this.mv.visitCode();
            this.mv.visitVarInsn(23, 1);
            this.mv.visitVarInsn(23, 2);
            this.mv.visitVarInsn(23, 3);
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "setFogColor", "(FFF)V");
        }
    }

    private static class MVsetupFog extends MethodVisitor {
        public MVsetupFog(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.equals("org/lwjgl/opengl/GL11", "glFogi", "(II)V", owner, name, desc)) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "sglFogi", "(II)V");
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class MVupdateFogColor extends MethodVisitor {
        public MVupdateFogColor(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.equals("org/lwjgl/opengl/GL11", "glClearColor", "(FFFF)V", owner, name, desc)) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "setClearColor", "(FFFF)V");
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }
}
