package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTRenderGlobal implements IClassTransformer {
   public byte[] transform(String par1, String par2, byte[] par3) {
      SMCLog.fine("transforming %s %s", par1, par2);
      ClassReader cr = new ClassReader(par3);
      ClassWriter cw = new ClassWriter(cr, 1);
      SMCCTRenderGlobal.CVTransform cv = new SMCCTRenderGlobal.CVTransform(cw);
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
         if (Names.renderGlobal_worldRenderers.name.equals(name)) {
            access = access & -7 | 1;
         }

         return this.cv.visitField(access, name, desc, signature, value);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         if (Names.renderGlobal_renderEntities.equalsNameDesc(name, desc)) {
            SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
            return new SMCCTRenderGlobal.MVrenderEntities(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else if (Names.renderGlobal_sortAndRender.equalsNameDesc(name, desc)) {
            SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
            return new SMCCTRenderGlobal.MVendisTexFog(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else if (Names.renderGlobal_renderSky.equalsNameDesc(name, desc)) {
            SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
            return new SMCCTRenderGlobal.MVrenderSky(this.cv.visitMethod(access, name, desc, signature, exceptions));
         } else if (!Names.renderGlobal_drawBlockDamageTexture.equalsNameDesc(name, desc) && !name.equals("drawBlockDamageTexture")) {
            if (Names.renderGlobal_drawSelectionBox.equalsNameDesc(name, desc)) {
               SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
               return new SMCCTRenderGlobal.MVendisTexFog(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
               return this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
         } else {
            SMCLog.finer("  patch method %s.%s%s", this.classname, name, desc);
            return new SMCCTRenderGlobal.MVdrawBlockDamageTexture(this.cv.visitMethod(access, name, desc, signature, exceptions));
         }
      }
   }

   private static class MVdrawBlockDamageTexture extends MethodVisitor {
      int state = 0;

      public MVdrawBlockDamageTexture(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitIntInsn(int opcode, int operand) {
         switch(this.state) {
         case 0:
            if (opcode == 17 && operand == 3008) {
               ++this.state;
            }
            break;
         case 2:
            if (opcode == 17 && operand == 3008) {
               ++this.state;
               this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endBlockDestroyProgress", "()V");
            }
         }

         this.mv.visitIntInsn(opcode, operand);
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
         switch(this.state) {
         case 1:
            if (owner.equals("org/lwjgl/opengl/GL11") && name.equals("glEnable")) {
               ++this.state;
               this.mv.visitMethodInsn(opcode, owner, name, desc);
               this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginBlockDestroyProgress", "()V");
               return;
            }
         default:
            this.mv.visitMethodInsn(opcode, owner, name, desc);
         }
      }
   }

   private static class MVendisTexFog extends MethodVisitor {
      int lastInt = 0;

      public MVendisTexFog(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitIntInsn(int opcode, int operand) {
         this.mv.visitIntInsn(opcode, operand);
         if (opcode != 17 || operand != 3553 && operand != 2912) {
            this.lastInt = 0;
         } else {
            this.lastInt = operand;
         }

      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
         this.mv.visitMethodInsn(opcode, owner, name, desc);
         if (owner.equals("org/lwjgl/opengl/GL11")) {
            if (name.equals("glEnable")) {
               if (this.lastInt == 3553) {
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "enableTexture2D", "()V");
               } else if (this.lastInt == 2912) {
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "enableFog", "()V");
               }
            } else if (name.equals("glDisable")) {
               if (this.lastInt == 3553) {
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "disableTexture2D", "()V");
               } else if (this.lastInt == 2912) {
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "disableFog", "()V");
               }
            }
         }

         this.lastInt = 0;
      }
   }

   private static class MVrenderEntities extends MethodVisitor {
      int state = 0;

      public MVrenderEntities(MethodVisitor mv) {
         super(262144, mv);
      }

      public void visitLdcInsn(Object cst) {
         if (cst instanceof String) {
            String scst = (String)cst;
            if (scst.equals("entities")) {
               this.state = 1;
            } else if (scst.equals("blockentities")) {
               this.state = 4;
            }
         }

         this.mv.visitLdcInsn(cst);
      }

      public void visitFieldInsn(int opcode, String owner, String name, String desc) {
         if (this.state == 2 && Names.renderManager_instance.equals(owner, name, desc)) {
            this.state = 3;
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "nextEntity", "()V");
         }

         this.mv.visitFieldInsn(opcode, owner, name, desc);
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
         this.mv.visitMethodInsn(opcode, owner, name, desc);
         if (this.state == 1) {
            this.state = 2;
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginEntities", "()V");
         } else if (this.state == 4) {
            this.state = 5;
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endEntities", "()V");
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "beginBlockEntities", "()V");
         } else if (this.state == 5 && Names.entityRenderer_disableLightmap.equals(owner, name, desc)) {
            this.state = 6;
            this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "endBlockEntities", "()V");
         }

      }
   }

   private static class MVrenderSky extends SMCCTRenderGlobal.MVendisTexFog {
      int state = 0;
      boolean detectedOptifine = false;
      int lastInt = 0;
      int lastVar = 0;

      public MVrenderSky(MethodVisitor mv) {
         super(mv);
      }

      @Override
      public void visitIntInsn(int opcode, int operand) {
         this.mv.visitIntInsn(opcode, operand);
         if (opcode == 17) {
            this.lastInt = operand;
         } else {
            this.lastInt = 0;
         }

      }

      public void visitVarInsn(int opcode, int var) {
         this.mv.visitVarInsn(opcode, var);
         if (opcode == 25) {
            this.lastVar = var;
         } else {
            this.lastVar = 0;
         }

      }

      public void visitFieldInsn(int opcode, String owner, String name, String desc) {
         switch(this.state) {
         case 0:
            if (Names.vec3_xCoord.equals(owner, name)) {
               ++this.state;
               this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "setSkyColor", "(" + Names.vec3_.desc + ")V");
               this.mv.visitVarInsn(25, this.lastVar);
            }
            break;
         case 1:
            if (Names.renderGlobal_glSkyList.equals(owner, name)) {
               ++this.state;
               this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "preSkyList", "()V");
            }
         }

         this.mv.visitFieldInsn(opcode, owner, name, desc);
      }

      @Override
      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
         switch(this.state) {
         case 0:
            if (owner.equals("Config") && name.equals("isSkyEnabled")) {
               this.detectedOptifine = true;
            }
            break;
         case 2:
            if (Names.worldClient_getRainStrength.equals(owner, name, desc)) {
               ++this.state;
            }
         }

         this.mv.visitMethodInsn(opcode, owner, name, desc);
         if (owner.equals("org/lwjgl/opengl/GL11")) {
            if (name.equals("glEnable")) {
               if (this.lastInt == 3553) {
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "enableTexture2D", "()V");
               } else if (this.lastInt == 2912) {
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "enableFog", "()V");
               }
            } else if (name.equals("glDisable")) {
               if (this.lastInt == 3553) {
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "disableTexture2D", "()V");
               } else if (this.lastInt == 2912) {
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "disableFog", "()V");
               }
            } else if (name.equals("glRotatef")) {
               if (this.state == 3) {
                  ++this.state;
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "preCelestialRotate", "()V");
               } else if (this.state == 4) {
                  ++this.state;
                  this.mv.visitMethodInsn(184, "shadersmodcore/client/Shaders", "postCelestialRotate", "()V");
               }
            }
         }

      }
   }
}
