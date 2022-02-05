package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTTextureDynamic implements IClassTransformer {
    @Override
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTTextureDynamic.CVTransform cv = new SMCCTTextureDynamic.CVTransform(cw);
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
            if (name.equals("<init>") && desc.equals("(II)V")) {
                return new SMCCTTextureDynamic.MVinit(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                return Names.dynamicTexture_updateDynamicTexture.equalsNameDesc(name, desc)
                        ? new MVupdate(this.cv.visitMethod(access, name, desc, signature, exceptions))
                        : this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    private static class MVinit extends MethodVisitor {
        public MVinit(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            if (opcode == 188 && operand == 10) {
                this.mv.visitInsn(6);
                this.mv.visitInsn(104);
            }

            this.mv.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.textureUtil_allocateTexture.equals(owner, name, desc)) {
                this.mv.visitVarInsn(25, 0);
                this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "initDynamicTexture", "(III" + Names.dynamicTexture_.desc + ")V");
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class MVupdate extends MethodVisitor {
        public MVupdate(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.textureUtil_uploadTexture.equals(owner, name, desc)) {
                this.mv.visitVarInsn(25, 0);
                this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "updateDynamicTexture", "(I[III" + Names.dynamicTexture_.desc + ")V");
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }
}
