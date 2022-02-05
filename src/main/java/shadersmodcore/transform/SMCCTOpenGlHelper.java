package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTOpenGlHelper implements IClassTransformer {
   public byte[] transform(String par1, String par2, byte[] par3) {
      SMCLog.fine("transforming %s %s", par1, par2);
      ClassReader cr = new ClassReader(par3);
      ClassWriter cw = new ClassWriter(cr, 1);
      SMCCTOpenGlHelper.CVTransform cv = new SMCCTOpenGlHelper.CVTransform(cw);
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
         return (MethodVisitor)(Names.openGlHelper_setActiveTexture.equalsNameDesc(name, desc)
            ? new SMCCTOpenGlHelper.MVsetActiveTexture(this.cv.visitMethod(access, name, desc, signature, exceptions))
            : this.cv.visitMethod(access, name, desc, signature, exceptions));
      }
   }

   private static class MVsetActiveTexture extends MethodVisitor {
      public MVsetActiveTexture(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitCode() {
         this.mv.visitCode();
         this.mv.visitVarInsn(21, 0);
         this.mv.visitFieldInsn(179, "shadersmodcore/client/Shaders", "activeTexUnit", "I");
         SMCLog.finest("    set activeTexUnit");
      }
   }
}
