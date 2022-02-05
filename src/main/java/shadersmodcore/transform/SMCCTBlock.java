package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTBlock implements IClassTransformer {
   public byte[] transform(String par1, String par2, byte[] par3) {
      SMCLog.fine("transforming %s %s", par1, par2);
      ClassReader cr = new ClassReader(par3);
      ClassWriter cw = new ClassWriter(cr, 0);
      SMCCTBlock.CVTransform cv = new SMCCTBlock.CVTransform(cw);
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
         super.visit(version, access, name, signature, superName, interfaces);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         return (MethodVisitor)(Names.block_getAoLight.equalsNameDesc(name, desc)
            ? new SMCCTBlock.MVgetAoLight(this.cv.visitMethod(access, name, desc, signature, exceptions))
            : this.cv.visitMethod(access, name, desc, signature, exceptions));
      }
   }

   private static class MVgetAoLight extends MethodVisitor {
      public MVgetAoLight(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitLdcInsn(Object cst) {
         if (cst instanceof Float && (Float)cst == 0.2F) {
            this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "blockAoLight", "F");
            SMCLog.info("   blockAoLight");
         } else {
            this.mv.visitLdcInsn(cst);
         }
      }
   }
}
