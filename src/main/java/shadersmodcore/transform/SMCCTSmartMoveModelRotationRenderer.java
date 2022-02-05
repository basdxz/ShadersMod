package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTSmartMoveModelRotationRenderer implements IClassTransformer {
    @Override
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTSmartMoveModelRotationRenderer.CVTransform cv = new SMCCTSmartMoveModelRotationRenderer.CVTransform(cw);
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
            return "resetDisplayList".equals(name) && "()V".equals(desc) ? null : this.cv.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            MethodVisitor mv = this.cv.visitMethod(1, "resetDisplayList", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(183, Names.modelRenderer_.clas, "resetDisplayList", "()V");
            mv.visitVarInsn(25, 0);
            mv.visitInsn(3);
            mv.visitFieldInsn(181, "net/smart/render/ModelRotationRenderer", Names.modelRenderer_compiled.name, "Z");
            mv.visitVarInsn(25, 0);
            mv.visitInsn(3);
            mv.visitFieldInsn(181, "net/smart/render/ModelRotationRenderer", Names.modelRenderer_displayList.name, "I");
            mv.visitInsn(177);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
            super.visitEnd();
        }
    }
}
