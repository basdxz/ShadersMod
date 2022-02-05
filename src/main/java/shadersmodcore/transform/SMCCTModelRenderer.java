package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTModelRenderer implements IClassTransformer {
   public byte[] transform(String par1, String par2, byte[] par3) {
      SMCLog.fine("transforming %s %s", par1, par2);
      ClassReader cr = new ClassReader(par3);
      ClassWriter cw = new ClassWriter(cr, 1);
      SMCCTModelRenderer.CVTransform cv = new SMCCTModelRenderer.CVTransform(cw);
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
         return (!"resetDisplayList".equals(name) || !"()V".equals(desc))
               && (!"getCompiled".equals(name) || !"()Z".equals(desc))
               && (!"getDisplayList".equals(name) || !"()I".equals(desc))
            ? this.cv.visitMethod(access, name, desc, signature, exceptions)
            : null;
      }

      public void visitEnd() {
         MethodVisitor mv = this.cv.visitMethod(1, "getCompiled", "()Z", (String)null, (String[])null);
         mv.visitCode();
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, Names.modelRenderer_compiled.clas, Names.modelRenderer_compiled.name, Names.modelRenderer_compiled.desc);
         mv.visitInsn(172);
         mv.visitMaxs(1, 1);
         mv.visitEnd();
         mv = this.cv.visitMethod(1, "getDisplayList", "()I", (String)null, (String[])null);
         mv.visitCode();
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, Names.modelRenderer_displayList.clas, Names.modelRenderer_displayList.name, Names.modelRenderer_displayList.desc);
         mv.visitInsn(172);
         mv.visitMaxs(1, 1);
         mv.visitEnd();
         mv = this.cv.visitMethod(1, "resetDisplayList", "()V", (String)null, (String[])null);
         mv.visitCode();
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, Names.modelRenderer_compiled.clas, Names.modelRenderer_compiled.name, Names.modelRenderer_compiled.desc);
         Label l1 = new Label();
         mv.visitJumpInsn(153, l1);
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(180, Names.modelRenderer_displayList.clas, Names.modelRenderer_displayList.name, Names.modelRenderer_displayList.desc);
         mv.visitMethodInsn(
            184, Names.glAllocation_deleteDisplayLists.clas, Names.glAllocation_deleteDisplayLists.name, Names.glAllocation_deleteDisplayLists.desc
         );
         mv.visitVarInsn(25, 0);
         mv.visitInsn(3);
         mv.visitFieldInsn(181, Names.modelRenderer_displayList.clas, Names.modelRenderer_displayList.name, Names.modelRenderer_displayList.desc);
         mv.visitVarInsn(25, 0);
         mv.visitInsn(3);
         mv.visitFieldInsn(181, Names.modelRenderer_compiled.clas, Names.modelRenderer_compiled.name, Names.modelRenderer_compiled.desc);
         mv.visitLabel(l1);
         mv.visitFrame(3, 0, (Object[])null, 0, (Object[])null);
         mv.visitInsn(177);
         mv.visitMaxs(2, 1);
         mv.visitEnd();
         super.visitEnd();
      }
   }
}
