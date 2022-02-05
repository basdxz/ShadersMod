package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTTextureDownload implements IClassTransformer {
   public byte[] transform(String par1, String par2, byte[] par3) {
      SMCLog.fine("transforming %s %s", par1, par2);
      ClassReader cr = new ClassReader(par3);
      ClassWriter cw = new ClassWriter(cr, 1);
      SMCCTTextureDownload.CVTransform cv = new SMCCTTextureDownload.CVTransform(cw);
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

      public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
         return super.visitField(access, name, desc, signature, value);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         return name.equals("getMultiTexID") ? null : this.cv.visitMethod(access, name, desc, signature, exceptions);
      }

      public void visitEnd() {
         MethodVisitor mv = this.cv.visitMethod(1, "getMultiTexID", "()Lshadersmodcore/client/MultiTexID;", (String)null, (String[])null);
         mv.visitCode();
         Label l0 = new Label();
         mv.visitLabel(l0);
         mv.visitVarInsn(25, 0);
         mv.visitFieldInsn(
            180, Names.textureDownload_textureUploaded.clas, Names.textureDownload_textureUploaded.name, Names.textureDownload_textureUploaded.desc
         );
         Label l1 = new Label();
         mv.visitJumpInsn(154, l1);
         mv.visitVarInsn(25, 0);
         mv.visitMethodInsn(182, Names.textureDownload_.clas, Names.iTextureObject_getGlTextureId.name, Names.iTextureObject_getGlTextureId.desc);
         mv.visitInsn(87);
         mv.visitLabel(l1);
         mv.visitFrame(3, 0, (Object[])null, 0, (Object[])null);
         mv.visitVarInsn(25, 0);
         mv.visitMethodInsn(183, Names.abstractTexture_.clas, "getMultiTexID", "()Lshadersmodcore/client/MultiTexID;");
         mv.visitInsn(176);
         Label l3 = new Label();
         mv.visitLabel(l3);
         mv.visitLocalVariable("this", Names.textureDownload_.desc, (String)null, l0, l3, 0);
         mv.visitMaxs(1, 1);
         mv.visitEnd();
         this.cv.visitEnd();
      }
   }
}
