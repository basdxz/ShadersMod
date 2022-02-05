package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SMCCTItemRenderer implements IClassTransformer {
   public byte[] transform(String par1, String par2, byte[] par3) {
      SMCLog.fine("transforming %s %s", par1, par2);
      ClassReader cr = new ClassReader(par3);
      ClassWriter cw = new ClassWriter(cr, 1);
      SMCCTItemRenderer.CVTransform cv = new SMCCTItemRenderer.CVTransform(cw);
      cr.accept(cv, 0);
      return cw.toByteArray();
   }

   private static class CVTransform extends ClassVisitor {
      String classname;
      Names.Meth renderItemIrt = new Names.Meth(
         Names.itemRenderer_,
         "renderItem",
         "(" + Names.entityLivingBase_.desc + Names.itemStack_.desc + "ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V"
      );

      public CVTransform(ClassVisitor cv) {
         super(262144, cv);
      }

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.classname = name;
         this.cv.visit(version, access, name, signature, superName, interfaces);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         if (Names.itemRenderer_updateEquipped.equalsNameDesc(name, desc)) {
            SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
            return new SMCCTItemRenderer.MVupdate(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else if (Names.itemRenderer_renderItem.equalsNameDesc(name, desc)) {
            SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
            return new SMCCTItemRenderer.MVrenderItem(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else if (this.renderItemIrt.equalsNameDesc(name, desc)) {
            SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
            return new SMCCTItemRenderer.MVrenderItem(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else {
            return this.cv.visitMethod(access, name, desc, signature, exceptions);
         }
      }
   }

   private static class MVrenderItem extends MethodVisitor {
      int state = 0;

      public MVrenderItem(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
         if (this.state == 0 && Names.equals("org/lwjgl/opengl/GL11", "glDepthMask", "(Z)V", owner, name, desc)) {
            ++this.state;
            this.mv.visitInsn(87);
            this.mv.visitFieldInsn(178, "shadersmodcore/client/Shaders", "renderItemPass1DepthMask", "Z");
            this.mv.visitMethodInsn(opcode, owner, name, desc);
         } else {
            this.mv.visitMethodInsn(opcode, owner, name, desc);
         }
      }
   }

   private static class MVupdate extends MethodVisitor {
      public MVupdate(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitFieldInsn(int opcode, String owner, String name, String desc) {
         if (opcode == 181 && Names.itemRenderer_itemToRender.equals(owner, name, desc)) {
            this.mv.visitInsn(89);
            this.mv.visitFieldInsn(179, "shadersmodcore/client/Shaders", "itemToRender", Names.itemStack_.desc);
         }

         this.mv.visitFieldInsn(opcode, owner, name, desc);
      }
   }
}
