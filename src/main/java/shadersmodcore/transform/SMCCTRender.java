package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTRender implements IClassTransformer {
   public byte[] transform(String par1, String par2, byte[] par3) {
      SMCLog.fine("transforming %s %s", par1, par2);
      ClassReader cr = new ClassReader(par3);
      ClassWriter cw = new ClassWriter(cr, 1);
      SMCCTRender.CVTransform cv = new SMCCTRender.CVTransform(cw);
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
         return (MethodVisitor)(Names.render_renderShadow.equalsNameDesc(name, desc)
            ? new SMCCTRender.MVrenderShadow(this.cv.visitMethod(access, name, desc, signature, exceptions))
            : this.cv.visitMethod(access, name, desc, signature, exceptions));
      }
   }

   private static class MVrenderShadow extends MethodVisitor {
      public MVrenderShadow(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitCode() {
         this.mv.visitCode();
         this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "shouldSkipDefaultShadow", "Z");
         Label l1 = new Label();
         this.mv.visitJumpInsn(153, l1);
         this.mv.visitInsn(177);
         this.mv.visitLabel(l1);
         SMCLog.finer("    conditionally skip default shadow");
      }
   }
}
