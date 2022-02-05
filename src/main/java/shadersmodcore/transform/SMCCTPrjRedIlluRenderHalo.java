package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTPrjRedIlluRenderHalo implements IClassTransformer {
    @Override
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 0);
        SMCCTPrjRedIlluRenderHalo.CVTransform cv = new SMCCTPrjRedIlluRenderHalo.CVTransform(cw);
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
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ("prepareRenderState".equals(name) && "()V".equals(desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTPrjRedIlluRenderHalo.MVprepare(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if ("restoreRenderState".equals(name) && "()V".equals(desc)) {
                SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
                return new SMCCTPrjRedIlluRenderHalo.MVrestore(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                return this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    private static class MVprepare extends MethodVisitor {
        public MVprepare(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.equals("codechicken/lib/render/CCRenderState", "reset", "()V", owner, name, desc)) {
                SMCLog.info("   beginHalo");
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginProjectRedHalo", "()V");
            }

            super.visitMethodInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitInsn(int opcode) {
            super.visitInsn(opcode);
        }
    }

    private static class MVrestore extends MethodVisitor {
        public MVrestore(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            super.visitMethodInsn(opcode, owner, name, desc);
            if (Names.equals("org/lwjgl/opengl/GL11", "glDisable", "(I)V", owner, name, desc)) {
                SMCLog.info("   endHalo");
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endProjectRedHalo", "()V");
            }

        }
    }
}
