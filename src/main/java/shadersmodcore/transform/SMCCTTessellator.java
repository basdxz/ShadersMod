package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SMCCTTessellator implements IClassTransformer {
    private static boolean inputHasStaticBuffer = false;

    public byte[] transform(String par1, String par2, byte[] par3) {
        SMCLog.fine("transforming %s %s", par1, par2);
        ClassReader cr = new ClassReader(par3);
        ClassWriter cw = new ClassWriter(cr, 1);
        SMCCTTessellator.CVTransform cv = new SMCCTTessellator.CVTransform(cw);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    private static class CVTransform extends ClassVisitor {
        String classname;
        boolean endFields = false;

        public CVTransform(ClassVisitor cv) {
            super(262144, cv);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.classname = name;
            this.cv.visit(version, access, name, signature, superName, interfaces);
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (name.equals("shadersTess")) {
                return null;
            } else {
                if ((access & 8) == 0
                        || !Names.tessellator_byteBuffer.name.equals(name)
                        && !Names.tessellator_intBuffer.name.equals(name)
                        && !Names.tessellator_floatBuffer.name.equals(name)
                        && !Names.tessellator_shortBuffer.name.equals(name)
                        && !Names.tessellator_vertexCount.name.equals(name)) {
                    access = access & -7 | 1;
                } else {
                    SMCCTTessellator.inputHasStaticBuffer = true;
                    access = access & -15 | 1;
                }

                return this.cv.visitField(access, name, desc, signature, value);
            }
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (!this.endFields) {
                this.endFields = true;
                FieldVisitor fv = this.cv.visitField(1, "shadersTess", "Lshadersmodcore/client/ShadersTess;", null, null);
                fv.visitEnd();
            }

            if (name.equals("<clinit>")) {
                return new SMCCTTessellator.MVclinit(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (name.equals("<init>") && desc.equals("()V")) {
                return new SMCCTTessellator.MVinit(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (name.equals("<init>") && desc.equals("(I)V")) {
                return new SMCCTTessellator.MVinitI(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.tessellator_draw.equalsNameDesc(name, desc)) {
                return new SMCCTTessellator.MVdraw(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.tessellator_reset.equalsNameDesc(name, desc)) {
                access = access & -7 | 1;
                return new SMCCTTessellator.MVreset(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.tessellator_addVertex.equalsNameDesc(name, desc)) {
                return new SMCCTTessellator.MVaddVertex(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.tessellator_setNormal.equalsNameDesc(name, desc)) {
                return new SMCCTTessellator.MVsetNormal(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else if (Names.tessellator_sortQuad.equalsNameDesc(name, desc)) {
                return new SMCCTTessellator.MVsortQuad(this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                access = access & -7 | 1;
                return this.cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    private static class MVaddVertex extends MethodVisitor {
        public MVaddVertex(MethodVisitor mv) {
            super(262144, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(466, l0);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(24, 1);
            mv.visitVarInsn(24, 3);
            mv.visitVarInsn(24, 5);
            mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTess", "addVertex", "(" + Names.tessellator_.desc + "DDD)V");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(467, l1);
            mv.visitInsn(177);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", Names.tessellator_.desc, null, l0, l2, 0);
            mv.visitLocalVariable("par1", "D", null, l0, l2, 1);
            mv.visitLocalVariable("par3", "D", null, l0, l2, 3);
            mv.visitLocalVariable("par5", "D", null, l0, l2, 5);
            mv.visitMaxs(7, 7);
            mv.visitEnd();
        }
    }

    private static class MVclinit extends MethodVisitor {
        public MVclinit(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (opcode != 179
                    || !Names.tessellator_byteBuffer.equals(owner, name)
                    && !Names.tessellator_intBuffer.equals(owner, name)
                    && !Names.tessellator_floatBuffer.equals(owner, name)
                    && !Names.tessellator_shortBuffer.equals(owner, name)
                    && !Names.tessellator_vertexCount.equals(owner, name)) {
                if (opcode == 178 && Names.tessellator_byteBuffer.equals(owner, name)) {
                    this.mv.visitInsn(1);
                } else {
                    this.mv.visitFieldInsn(opcode, owner, name, desc);
                }
            } else {
                this.mv.visitInsn(87);
            }
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (Names.glAllocation_createDirectByteBuffer.equals(owner, name, desc)) {
                this.mv.visitInsn(87);
                this.mv.visitInsn(1);
            } else if (Names.glAllocation_createDirectIntBuffer.equals(owner, name, desc)) {
                this.mv.visitInsn(87);
                this.mv.visitInsn(1);
            } else if (Names.equals("java/nio/ByteBuffer", "asIntBuffer", "()Ljava/nio/IntBuffer;", owner, name, desc)) {
                this.mv.visitInsn(87);
                this.mv.visitInsn(1);
            } else if (Names.equals("java/nio/ByteBuffer", "asFloatBuffer", "()Ljava/nio/FloatBuffer;", owner, name, desc)) {
                this.mv.visitInsn(87);
                this.mv.visitInsn(1);
            } else if (Names.equals("java/nio/ByteBuffer", "asShortBuffer", "()Ljava/nio/ShortBuffer;", owner, name, desc)) {
                this.mv.visitInsn(87);
                this.mv.visitInsn(1);
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class MVdraw extends MethodVisitor {
        public MVdraw(MethodVisitor mv) {
            super(262144, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(185, l0);
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(184, "shadersmodcore/client/ShadersTess", "draw", "(" + Names.tessellator_.desc + ")I");
            mv.visitInsn(172);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", Names.tessellator_.desc, null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
    }

    private static class MVinit extends MethodVisitor {
        public MVinit(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (opcode == 183 && Names.equals("java/lang/Object", "<init>", "()V", owner, name, desc)) {
                this.mv.visitLdcInsn(new Integer(65536));
                this.mv.visitMethodInsn(183, Names.tessellator_.clas, "<init>", "(I)V");
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class MVinitI extends MethodVisitor {
        public MVinitI(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitInsn(int opcode) {
            if (opcode == 177) {
                if (SMCCTTessellator.inputHasStaticBuffer) {
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitVarInsn(21, 1);
                    this.mv.visitInsn(7);
                    this.mv.visitInsn(104);
                    this.mv
                            .visitMethodInsn(
                                    184,
                                    Names.glAllocation_createDirectByteBuffer.clas,
                                    Names.glAllocation_createDirectByteBuffer.name,
                                    Names.glAllocation_createDirectByteBuffer.desc
                            );
                    this.mv.visitFieldInsn(181, Names.tessellator_byteBuffer.clas, Names.tessellator_byteBuffer.name, Names.tessellator_byteBuffer.desc);
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitFieldInsn(180, Names.tessellator_byteBuffer.clas, Names.tessellator_byteBuffer.name, Names.tessellator_byteBuffer.desc);
                    this.mv.visitMethodInsn(182, "java/nio/ByteBuffer", "asIntBuffer", "()Ljava/nio/IntBuffer;");
                    this.mv.visitFieldInsn(181, Names.tessellator_intBuffer.clas, Names.tessellator_intBuffer.name, Names.tessellator_intBuffer.desc);
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitFieldInsn(180, Names.tessellator_byteBuffer.clas, Names.tessellator_byteBuffer.name, Names.tessellator_byteBuffer.desc);
                    this.mv.visitMethodInsn(182, "java/nio/ByteBuffer", "asFloatBuffer", "()Ljava/nio/FloatBuffer;");
                    this.mv.visitFieldInsn(181, Names.tessellator_floatBuffer.clas, Names.tessellator_floatBuffer.name, Names.tessellator_floatBuffer.desc);
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitFieldInsn(180, Names.tessellator_byteBuffer.clas, Names.tessellator_byteBuffer.name, Names.tessellator_byteBuffer.desc);
                    this.mv.visitMethodInsn(182, "java/nio/ByteBuffer", "asShortBuffer", "()Ljava/nio/ShortBuffer;");
                    this.mv.visitFieldInsn(181, Names.tessellator_shortBuffer.clas, Names.tessellator_shortBuffer.name, Names.tessellator_shortBuffer.desc);
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitVarInsn(21, 1);
                    this.mv.visitIntInsn(188, 10);
                    this.mv.visitFieldInsn(181, Names.tessellator_rawBuffer.clas, Names.tessellator_rawBuffer.name, Names.tessellator_rawBuffer.desc);
                    this.mv.visitVarInsn(25, 0);
                    this.mv.visitInsn(3);
                    this.mv.visitFieldInsn(181, Names.tessellator_vertexCount.clas, Names.tessellator_vertexCount.name, Names.tessellator_vertexCount.desc);
                }

                this.mv.visitVarInsn(25, 0);
                this.mv.visitTypeInsn(187, "shadersmodcore/client/ShadersTess");
                this.mv.visitInsn(89);
                this.mv.visitMethodInsn(183, "shadersmodcore/client/ShadersTess", "<init>", "()V");
                this.mv.visitFieldInsn(181, Names.tessellator_.clas, "shadersTess", "Lshadersmodcore/client/ShadersTess;");
            }

            this.mv.visitInsn(opcode);
        }
    }

    private static class MVreset extends MethodVisitor {
        public MVreset(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (opcode == 178 && Names.tessellator_byteBuffer.equals(owner, name)) {
                this.mv.visitVarInsn(25, 0);
                this.mv.visitFieldInsn(180, owner, name, desc);
            } else {
                this.mv.visitFieldInsn(opcode, owner, name, desc);
            }
        }
    }

    private static class MVsetNormal extends MethodVisitor {
        public MVsetNormal(MethodVisitor mv) {
            super(262144, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, Names.tessellator_.clas, "shadersTess", "Lshadersmodcore/client/ShadersTess;");
            mv.visitVarInsn(58, 4);
            mv.visitVarInsn(25, 0);
            mv.visitInsn(4);
            mv.visitFieldInsn(181, Names.tessellator_hasNormals.clas, Names.tessellator_hasNormals.name, Names.tessellator_hasNormals.desc);
            mv.visitVarInsn(25, 4);
            mv.visitVarInsn(23, 1);
            mv.visitFieldInsn(181, "shadersmodcore/client/ShadersTess", "normalX", "F");
            mv.visitVarInsn(25, 4);
            mv.visitVarInsn(23, 2);
            mv.visitFieldInsn(181, "shadersmodcore/client/ShadersTess", "normalY", "F");
            mv.visitVarInsn(25, 4);
            mv.visitVarInsn(23, 3);
            mv.visitFieldInsn(181, "shadersmodcore/client/ShadersTess", "normalZ", "F");
            mv.visitInsn(177);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitLocalVariable("this", Names.tessellator_.desc, null, l0, l5, 0);
            mv.visitLocalVariable("par1", "F", null, l0, l5, 1);
            mv.visitLocalVariable("par2", "F", null, l0, l5, 2);
            mv.visitLocalVariable("par3", "F", null, l0, l5, 3);
            mv.visitLocalVariable("stess", "Lshadersmodcore/client/ShadersTess;", null, l0, l5, 4);
            mv.visitMaxs(2, 5);
            mv.visitEnd();
        }
    }

    private static class MVsortQuad extends MethodVisitor {
        public MVsortQuad(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitIntInsn(int opcode, int operand) {
            if (opcode == 16 && operand == 32) {
                operand = 72;
            }

            super.visitIntInsn(opcode, operand);
        }
    }
}
