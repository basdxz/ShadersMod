package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTRenderSpider implements IClassTransformer {
    @Override
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTRenderSpider.CVTransform cv = new SMCCTRenderSpider.CVTransform(cw);
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
            if (Names.renderDragon_shouldRenderPass.equalsNameDesc(name, desc)) {
                return new SMCCTRenderSpider.MVshouldRenderPass(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.renderEnderman_shouldRenderPass.equalsNameDesc(name, desc)) {
                return new SMCCTRenderSpider.MVshouldRenderPass(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                return Names.renderSpider_shouldRenderPass.equalsNameDesc(name, desc)
                        ? new MVshouldRenderPass(this.cv.visitMethod(access, name, desc, signature, exceptions))
                        : this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    private static class MVshouldRenderPass extends MethodVisitor {
        public MVshouldRenderPass(MethodVisitor mv) {
            super(262144, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            this.mv.visitMethodInsn(opcode, owner, name, desc);
            if (Names.equals("org/lwjgl/opengl/GL11", "glColor4f", "(FFFF)V", owner, name, desc)) {
                this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginSpiderEyes", "()V");
            }

        }
    }
}
