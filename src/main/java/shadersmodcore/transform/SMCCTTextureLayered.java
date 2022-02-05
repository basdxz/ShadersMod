package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTTextureLayered implements IClassTransformer {
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTTextureLayered.CVTransform cv = new SMCCTTextureLayered.CVTransform(cw);
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
            return Names.iTextureObject_loadTexture.equalsNameDesc(name, desc)
                    ? new MVloadTexture(this.cv.visitMethod(access, name, desc, signature, exceptions))
                    : this.cv.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    private static class MVloadTexture extends MethodVisitor {
        public MVloadTexture(MethodVisitor mv) {
            super(262144);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 1);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(
                    180, Names.layeredTexture_layeredTextureNames.clas, Names.layeredTexture_layeredTextureNames.name, Names.layeredTexture_layeredTextureNames.desc
            );
            mv.visitMethodInsn(
                    184,
                    "shadersmodcore/client/ShadersTex",
                    "loadLayeredTexture",
                    "(" + Names.layeredTexture_.desc + Names.iResourceManager_.desc + "Ljava/util/List;)V"
            );
            mv.visitInsn(177);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", Names.layeredTexture_.desc, null, l0, l2, 0);
            mv.visitLocalVariable("manager", Names.iResourceManager_.desc, null, l0, l2, 1);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
    }
}
