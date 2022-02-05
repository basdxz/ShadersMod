package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTRenderItemFrame implements IClassTransformer {
    @Override
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTRenderItemFrame.CVTransform cv = new SMCCTRenderItemFrame.CVTransform(cw);
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
            return Names.renderItemFrame_renderItemInFrame.equalsNameDesc(name, desc)
                    ? new MVrenderItem(this.cv.visitMethod(access, name, desc, signature, exceptions))
                    : this.cv.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    private static class MVrenderItem extends MethodVisitor {
        int state = 0;

        public MVrenderItem(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.textureManager_bindTexture.equals(owner, name, desc)) {
                if (this.state == 1) {
                    this.mv
                            .visitMethodInsn(
                                    184,
                                    "shadersmodcore/client/ShadersTex",
                                    "bindTextureMapForUpdateAndRender",
                                    "(" + Names.textureManager_.desc + Names.resourceLocation_.desc + ")V"
                            );
                    return;
                }

                ++this.state;
            }

            this.mv.visitMethodInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == 177) {
                this.mv.visitInsn(1);
                this.mv.visitFieldInsn(179, "shadersmodcore/client/ShadersTex", "updatingTex", "Lshadersmodcore/client/MultiTexID;");
            }

            this.mv.visitInsn(opcode);
        }
    }
}
