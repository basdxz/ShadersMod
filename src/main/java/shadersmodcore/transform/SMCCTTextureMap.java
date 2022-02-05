package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTTextureMap implements IClassTransformer {
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTTextureMap.CVTransform cv = new SMCCTTextureMap.CVTransform(cw);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    private static class CVTransform extends ClassVisitor {
        String classname;
        boolean endFields = false;

        public CVTransform(ClassVisitor cv) {
            super(262144, cv);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.classname = name;
            this.cv.visit(version, access, name, signature, superName, interfaces);
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (name.equals("atlasWidth")) {
                return null;
            } else if (name.equals("atlasHeight")) {
                return null;
            } else {
                if (Names.textureMap_anisotropic.name.equals(name)) {
                    access = access & -7 | 1;
                }

                return super.visitField(access, name, desc, signature, value);
            }
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (!this.endFields) {
                this.endFields = true;
                FieldVisitor fv = this.cv.visitField(1, "atlasWidth", "I", null, null);
                fv.visitEnd();
                fv = this.cv.visitField(1, "atlasHeight", "I", null, null);
                fv.visitEnd();
            }

            if (Names.textureMap_getIconResLoc.equalsNameDesc(name, desc)) {
                access = access & -7 | 1;
            } else {
                if (Names.textureMap_loadTexture.equalsNameDesc(name, desc)) {
                    return new SMCCTTextureMap.MVloadTexture(this.cv.visitMethod(access, name, desc, signature, exceptions));
                }

                if (Names.textureMap_loadTextureAtlas.equalsNameDesc(name, desc)) {
                    return new SMCCTTextureMap.MVloadAtlas(this.cv.visitMethod(access, name, desc, signature, exceptions));
                }

                if (Names.textureMap_updateAnimations.equalsNameDesc(name, desc)) {
                    return new SMCCTTextureMap.MVanimation(this.cv.visitMethod(access, name, desc, signature, exceptions));
                }
            }

            return this.cv.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    private static class MVanimation extends MethodVisitor {
        public MVanimation(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitCode() {
            this.mv.visitCode();
            this.mv.visitVarInsn(25, 0);
            this.mv.visitMethodInsn(182, Names.textureMap_.clas, "getMultiTexID", "()Lshadersmodcore/client/MultiTexID;");
            this.mv.visitFieldInsn(179, "shadersmodcore/client/ShadersTex", "updatingTex", "Lshadersmodcore/client/MultiTexID;");
        }

        public void visitInsn(int opcode) {
            if (opcode == 177) {
                this.mv.visitInsn(1);
                this.mv.visitFieldInsn(179, "shadersmodcore/client/ShadersTex", "updatingTex", "Lshadersmodcore/client/MultiTexID;");
            }

            this.mv.visitInsn(opcode);
        }
    }

    private static class MVloadAtlas extends MethodVisitor {
        int varStitcher = 0;
        boolean isStitcher = false;
        int state = 0;

        public MVloadAtlas(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitCode() {
            super.visitCode();
            this.mv.visitVarInsn(25, 1);
            this.mv.visitFieldInsn(179, "shadersmodcore/client/ShadersTex", "resManager", Names.iResourceManager_.desc);
        }

        public void visitIntInsn(int opcode, int operand) {
            if (opcode == 188 && operand == 10) {
                this.mv.visitInsn(6);
                this.mv.visitInsn(104);
            }

            this.mv.visitIntInsn(opcode, operand);
        }

        public void visitVarInsn(int opcode, int var) {
            if (opcode == 58 && this.isStitcher) {
                this.isStitcher = false;
                this.varStitcher = var;
            }

            this.mv.visitVarInsn(opcode, var);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.iResourceManager_getResource.equals(owner, name, desc)) {
                SMCLog.finest("    %s", "loadRes");
                this.mv
                        .visitMethodInsn(
                                184,
                                "shadersmodcore/client/ShadersTex",
                                "loadResource",
                                "(" + Names.iResourceManager_.desc + Names.resourceLocation_.desc + ")" + Names.iResource_.desc
                        );
            } else {
                if (opcode == 183 && Names.equals(Names.stitcher_.clas, "<init>", "(IIZII)V", owner, name, desc)) {
                    this.isStitcher = true;
                } else {
                    if (Names.textureUtil_allocateTextureMipmapAniso.equals(owner, name, desc)) {
                        SMCLog.finest("    %s", "allocateTextureMap");
                        this.mv.visitVarInsn(25, this.varStitcher);
                        this.mv.visitVarInsn(25, 0);
                        this.mv
                                .visitMethodInsn(
                                        184, "shadersmodcore/client/ShadersTex", "allocateTextureMap", "(IIIIF" + Names.stitcher_.desc + Names.textureMap_.desc + ")V"
                                );
                        this.state = 1;
                        return;
                    }

                    if (this.state == 1 && Names.textureAtlasSpri_getIconName.equals(owner, name, desc)) {
                        SMCLog.finest("    %s", "setSprite setIconName");
                        this.mv
                                .visitMethodInsn(
                                        184, "shadersmodcore/client/ShadersTex", "setSprite", "(" + Names.textureAtlasSpri_.desc + ")" + Names.textureAtlasSpri_.desc
                                );
                        this.mv.visitMethodInsn(opcode, owner, name, desc);
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "setIconName", "(Ljava/lang/String;)Ljava/lang/String;");
                        this.state = 0;
                        return;
                    }

                    if (Names.textureUtil_uploadTexSub.equals(owner, name, desc)) {
                        SMCLog.finest("    %s", "uploadTexSubForLoadAtlas");
                        this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "uploadTexSubForLoadAtlas", "([[IIIIIZZ)V");
                        return;
                    }
                }

                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class MVloadTexture extends MethodVisitor {
        public MVloadTexture(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitCode() {
            super.visitCode();
            this.mv.visitVarInsn(25, 1);
            this.mv.visitFieldInsn(179, "shadersmodcore/client/ShadersTex", "resManager", Names.iResourceManager_.desc);
        }
    }
}
