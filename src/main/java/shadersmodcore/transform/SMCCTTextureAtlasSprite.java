package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTTextureAtlasSprite implements IClassTransformer {
   public byte[] transform(String par1, String par2, byte[] par3) {
      SMCLog.fine("transforming %s %s", par1, par2);
      ClassReader cr = new ClassReader(par3);
      ClassWriter cw = new ClassWriter(cr, 1);
      SMCCTTextureAtlasSprite.CVTransform cv = new SMCCTTextureAtlasSprite.CVTransform(cw);
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
         access = access & -7 | 1;
         return this.cv.visitField(access, name, desc, signature, value);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         access = access & -7 | 1;
         if (Names.textureAtlasSpri_updateAnimation.equalsNameDesc(name, desc)) {
            return new SMCCTTextureAtlasSprite.MVanimation(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else if (name.equals("load") && desc.equals("(" + Names.iResourceManager_.desc + Names.resourceLocation_.desc + ")Z")) {
            return new SMCCTTextureAtlasSprite.MVload(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else if (Names.textureAtlasSpri_loadSprite.equalsNameDesc(name, desc)) {
            return new SMCCTTextureAtlasSprite.MVloadSprite(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else {
            return (MethodVisitor)(name.equals("uploadFrameTexture") && desc.equals("(III)V")
               ? new SMCCTTextureAtlasSprite.MVuploadFrameTexture(this.cv.visitMethod(access, name, desc, signature, exceptions))
               : this.cv.visitMethod(access, name, desc, signature, exceptions));
         }
      }
   }

   protected static class MVanimation extends MethodVisitor {
      public MVanimation(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
         if (Names.textureUtil_uploadTexSub.equals(owner, name, desc)) {
            this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "uploadTexSub", "([[IIIIIZZ)V");
         } else {
            this.mv.visitMethodInsn(opcode, owner, name, desc);
         }
      }
   }

   private static class MVload extends MethodVisitor {
      public MVload(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
         if (Names.iResourceManager_getResource.equals(owner, name, desc)) {
            this.mv
               .visitMethodInsn(
                  184,
                  "shadersmodcore/client/ShadersTex",
                  "loadResource",
                  "(" + Names.iResourceManager_.desc + Names.resourceLocation_.desc + ")" + Names.iResource_.desc
               );
         } else {
            this.mv.visitMethodInsn(opcode, owner, name, desc);
         }
      }
   }

   private static class MVloadSprite extends MethodVisitor {
      public MVloadSprite(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitIntInsn(int opcode, int operand) {
         if (opcode == 188 && operand == 10) {
            this.mv.visitInsn(6);
            this.mv.visitInsn(104);
         }

         this.mv.visitIntInsn(opcode, operand);
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
         if (Names.equals("java/awt/image/BufferedImage", "getRGB", "(IIII[III)[I", owner, name, desc)) {
            this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "loadAtlasSprite", "(Ljava/awt/image/BufferedImage;IIII[III)[I");
         } else if (Names.textureAtlasSpri_getFrameTextureData.equals(owner, name, desc)) {
            this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "getFrameTexData", "([[IIII)[[I");
         } else if (Names.textureAtlasSpri_prepareAF.equals(owner, name, desc)) {
            this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "prepareAF", "(" + Names.textureAtlasSpri_.desc + "[[III)[[I");
         } else if (Names.equals(Names.textureAtlasSpri_.clas, "fixTransparentColor", "([I)V", owner, name, desc)) {
            this.mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "fixTransparentColor", "(" + Names.textureAtlasSpri_.desc + "[I)V");
         } else {
            this.mv.visitMethodInsn(opcode, owner, name, desc);
         }
      }
   }

   private static class MVuploadFrameTexture extends MethodVisitor {
      public MVuploadFrameTexture(MethodVisitor mv) {
         super(262144, (MethodVisitor)null);
         mv.visitCode();
         Label l0 = new Label();
         mv.visitLabel(l0);
         mv.visitVarInsn(25, 0);
         mv.visitVarInsn(21, 1);
         mv.visitVarInsn(21, 2);
         mv.visitVarInsn(21, 3);
         mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTex", "uploadFrameTexture", "(" + Names.textureAtlasSpri_.desc + "III)V");
         mv.visitInsn(177);
         Label l2 = new Label();
         mv.visitLabel(l2);
         mv.visitLocalVariable("this", Names.textureAtlasSpri_.desc, (String)null, l0, l2, 0);
         mv.visitLocalVariable("frameIndex", "I", (String)null, l0, l2, 1);
         mv.visitLocalVariable("xPos", "I", (String)null, l0, l2, 2);
         mv.visitLocalVariable("yPos", "I", (String)null, l0, l2, 3);
         mv.visitMaxs(4, 4);
         mv.visitEnd();
      }
   }
}
