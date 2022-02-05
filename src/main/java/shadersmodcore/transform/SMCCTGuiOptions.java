package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTGuiOptions implements IClassTransformer {
    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTGuiOptions.CVTransform cv = new SMCCTGuiOptions.CVTransform(cw);
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
            if (Names.guiOptions_initGui.equalsNameDesc(name, desc)) {
                return new SMCCTGuiOptions.MVinitGui(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                return Names.guiOptions_actionPerformed.equalsNameDesc(name, desc)
                        ? new MVactionPerformed(this.cv.visitMethod(access, name, desc, signature, exceptions))
                        : this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    private static class MVactionPerformed extends MethodVisitor {
        public MVactionPerformed(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitCode() {
            super.visitCode();
            this.mv.visitVarInsn(25, 1);
            this.mv.visitFieldInsn(180, Names.guiButton_id.clas, Names.guiButton_id.name, Names.guiButton_id.desc);
            this.mv.visitIntInsn(17, 190);
            Label l1 = new Label();
            this.mv.visitJumpInsn(160, l1);
            this.mv.visitVarInsn(25, 0);
            this.mv.visitFieldInsn(180, Names.guiOptions_mc.clas, Names.guiOptions_mc.name, Names.guiOptions_mc.desc);
            this.mv.visitFieldInsn(180, Names.minecraft_gameSettings.clas, Names.minecraft_gameSettings.name, Names.minecraft_gameSettings.desc);
            this.mv.visitMethodInsn(182, Names.gameSettings_saveOptions.clas, Names.gameSettings_saveOptions.name, Names.gameSettings_saveOptions.desc);
            this.mv.visitVarInsn(25, 0);
            this.mv.visitFieldInsn(180, Names.guiOptions_mc.clas, Names.guiOptions_mc.name, Names.guiOptions_mc.desc);
            this.mv.visitTypeInsn(187, "shadersmodcore/client/GuiShaders");
            this.mv.visitInsn(89);
            this.mv.visitVarInsn(25, 0);
            this.mv.visitVarInsn(25, 0);
            this.mv.visitFieldInsn(180, Names.guiOptions_options.clas, Names.guiOptions_options.name, Names.guiOptions_options.desc);
            this.mv.visitMethodInsn(183, "shadersmodcore/client/GuiShaders", "<init>", "(" + Names.guiScreen_.desc + Names.gameSettings_.desc + ")V");
            this.mv.visitMethodInsn(182, Names.minecraft_displayGuiScreen.clas, Names.minecraft_displayGuiScreen.name, Names.minecraft_displayGuiScreen.desc);
            this.mv.visitLabel(l1);
            this.mv.visitFrame(3, 0, null, 0, null);
            SMCLog.finest("    shaders button action");
        }
    }

    private static class MVinitGui extends MethodVisitor {
        int state = 0;

        public MVinitGui(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitInsn(int opcode) {
            super.visitInsn(opcode);
            if (opcode == 87 && this.state == 1) {
                this.state = 2;
                this.mv.visitVarInsn(25, 0);
                this.mv.visitFieldInsn(180, Names.guiOptions_buttonList.clas, Names.guiOptions_buttonList.name, Names.guiOptions_buttonList.desc);
                this.mv.visitTypeInsn(187, Names.guiButton_.clas);
                this.mv.visitInsn(89);
                this.mv.visitIntInsn(17, 190);
                this.mv.visitVarInsn(25, 0);
                this.mv.visitFieldInsn(180, Names.guiOptions_width.clas, Names.guiOptions_width.name, Names.guiOptions_width.desc);
                this.mv.visitInsn(5);
                this.mv.visitInsn(108);
                this.mv.visitIntInsn(17, 155);
                this.mv.visitInsn(100);
                this.mv.visitIntInsn(16, 76);
                this.mv.visitInsn(96);
                this.mv.visitVarInsn(25, 0);
                this.mv.visitFieldInsn(180, Names.guiOptions_height.clas, Names.guiOptions_height.name, Names.guiOptions_height.desc);
                this.mv.visitIntInsn(16, 6);
                this.mv.visitInsn(108);
                this.mv.visitIntInsn(16, 120);
                this.mv.visitInsn(96);
                this.mv.visitIntInsn(16, 6);
                this.mv.visitInsn(100);
                this.mv.visitIntInsn(16, 74);
                this.mv.visitIntInsn(16, 20);
                this.mv.visitLdcInsn("Shaders...");
                this.mv.visitMethodInsn(183, Names.guiButton_.clas, "<init>", "(IIIIILjava/lang/String;)V");
                this.mv.visitMethodInsn(185, "java/util/List", "add", "(Ljava/lang/Object;)Z");
                this.mv.visitInsn(87);
                SMCLog.finest("    add shaders button");
            }

        }

        public void visitLdcInsn(Object cst) {
            if (cst instanceof String && cst.equals("options.language") && this.state == 0) {
                this.state = 1;
                this.mv.visitInsn(87);
                this.mv.visitInsn(87);
                this.mv.visitIntInsn(16, 74);
                this.mv.visitIntInsn(16, 20);
                SMCLog.finest("    decrease language button size");
            }

            super.visitLdcInsn(cst);
        }
    }
}
