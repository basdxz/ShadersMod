package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTTextureObject implements IClassTransformer {
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 0);
        SMCCTTextureObject.CVTransform cv = new SMCCTTextureObject.CVTransform(cw);
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
            return name.equals("getMultiTexID") && desc.equals("()Lshadersmodcore/client/MultiTexID;")
                    ? null
                    : this.cv.visitMethod(access, name, desc, signature, exceptions);
        }

        public void visitEnd() {
            MethodVisitor mv = this.cv.visitMethod(1025, "getMultiTexID", "()Lshadersmodcore/client/MultiTexID;", null, null);
            mv.visitEnd();
            this.cv.visitEnd();
        }
    }
}
