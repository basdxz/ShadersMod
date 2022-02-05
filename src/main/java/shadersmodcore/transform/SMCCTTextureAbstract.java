package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTTextureAbstract implements IClassTransformer {
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTTextureAbstract.CVTransform cv = new SMCCTTextureAbstract.CVTransform(cw);
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
            if (name.equals("multiTex")) {
                return null;
            } else {
                access = access & -7 | 1;
                return this.cv.visitField(access, name, desc, signature, value);
            }
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (!this.endFields) {
                this.endFields = true;
                FieldVisitor fv = this.cv.visitField(1, "multiTex", "Lshadersmodcore/client/MultiTexID;", null, null);
                fv.visitEnd();
            }

            if (Names.abstractTexture_deleteGlTexture.equalsNameDesc(name, desc)) {
                return new SMCCTTextureAbstract.MVdeleteGlTexture(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                return name.equals("getMultiTexID") && desc.equals("()Lshadersmodcore/client/MultiTexID;")
                        ? null
                        : this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }

        public void visitEnd() {
            MethodVisitor mv = this.cv.visitMethod(1, "getMultiTexID", "()Lshadersmodcore/client/MultiTexID;", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(
                    184, "shadersmodcore/client/ShadersTex", "getMultiTexID", "(" + Names.abstractTexture_.desc + ")Lshadersmodcore/client/MultiTexID;"
            );
            mv.visitInsn(176);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
            this.cv.visitEnd();
        }
    }

    private static class MVdeleteGlTexture extends MethodVisitor {
        public MVdeleteGlTexture(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitCode() {
            this.mv.visitCode();
            this.mv.visitVarInsn(25, 0);
            this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "deleteTextures", "(" + Names.abstractTexture_.desc + ")V");
        }
    }
}
