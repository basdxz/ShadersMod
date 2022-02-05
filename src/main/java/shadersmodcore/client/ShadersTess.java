package shadersmodcore.client;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shadersmodcore.transform.SMCLog;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class ShadersTess {
    public static final int vertexStride = 18;
    public float midTextureU;
    public float midTextureV;
    public float normalX;
    public float normalY;
    public float normalZ;
    public float v0x;
    public float v0y;
    public float v0z;
    public float v0u;
    public float v0v;
    public float v1x;
    public float v1y;
    public float v1z;
    public float v1u;
    public float v1v;
    public float v2x;
    public float v2y;
    public float v2z;
    public float v2u;
    public float v2v;
    public float v3x;
    public float v3y;
    public float v3z;
    public float v3u;
    public float v3v;

    public static int draw(Tessellator tess) {
        if (!tess.isDrawing) {
            throw new IllegalStateException("Not tesselating!");
        } else {
            tess.isDrawing = false;
            if (tess.drawMode == 7 && tess.vertexCount % 4 != 0) {
                SMCLog.warning("%s", "bad vertexCount");
            }

            int voffset = 0;
            int realDrawMode = tess.drawMode;

            while (voffset < tess.vertexCount) {
                int vcount = Math.min(tess.vertexCount - voffset, Tessellator.byteBuffer.capacity() / 72);
                if (realDrawMode == 7) {
                    vcount = vcount / 4 * 4;
                }

                Tessellator.floatBuffer.clear();
                Tessellator.shortBuffer.clear();
                Tessellator.intBuffer.clear();
                Tessellator.intBuffer.put(tess.rawBuffer, voffset * 18, vcount * 18);
                Tessellator.byteBuffer.position(0);
                Tessellator.byteBuffer.limit(vcount * 72);
                voffset += vcount;
                if (tess.hasTexture) {
                    Tessellator.floatBuffer.position(3);
                    GL11.glTexCoordPointer(2, 72, Tessellator.floatBuffer);
                    GL11.glEnableClientState(32888);
                }

                if (tess.hasBrightness) {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    Tessellator.shortBuffer.position(12);
                    GL11.glTexCoordPointer(2, 72, Tessellator.shortBuffer);
                    GL11.glEnableClientState(32888);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (tess.hasColor) {
                    Tessellator.byteBuffer.position(20);
                    GL11.glColorPointer(4, true, 72, Tessellator.byteBuffer);
                    GL11.glEnableClientState(32886);
                }

                if (tess.hasNormals) {
                    Tessellator.floatBuffer.position(9);
                    GL11.glNormalPointer(72, Tessellator.floatBuffer);
                    GL11.glEnableClientState(32885);
                }

                Tessellator.floatBuffer.position(0);
                GL11.glVertexPointer(3, 72, Tessellator.floatBuffer);
                preDrawArray(tess);
                GL11.glEnableClientState(32884);
                GL11.glDrawArrays(realDrawMode, 0, vcount);
            }

            GL11.glDisableClientState(32884);
            postDrawArray(tess);
            if (tess.hasTexture) {
                GL11.glDisableClientState(32888);
            }

            if (tess.hasBrightness) {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisableClientState(32888);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            if (tess.hasColor) {
                GL11.glDisableClientState(32886);
            }

            if (tess.hasNormals) {
                GL11.glDisableClientState(32885);
            }

            int n = tess.rawBufferIndex * 4;
            tess.reset();
            return n;
        }
    }

    public static void preDrawArray(Tessellator tess) {
        if (Shaders.useMultiTexCoord3Attrib && tess.hasTexture) {
            GL13.glClientActiveTexture(33987);
            GL11.glTexCoordPointer(2, 72, (FloatBuffer) Tessellator.floatBuffer.position(16));
            GL11.glEnableClientState(32888);
            GL13.glClientActiveTexture(33984);
        }

        if (Shaders.useMidTexCoordAttrib && tess.hasTexture) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.midTexCoordAttrib, 2, false, 72, (FloatBuffer) Tessellator.floatBuffer.position(16));
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
        }

        if (Shaders.useTangentAttrib && tess.hasTexture) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.tangentAttrib, 4, false, 72, (FloatBuffer) Tessellator.floatBuffer.position(12));
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.tangentAttrib);
        }

        if (Shaders.useEntityAttrib) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.entityAttrib, 3, false, false, 72, (ShortBuffer) Tessellator.shortBuffer.position(14));
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.entityAttrib);
        }

    }

    public static void preDrawArrayVBO(Tessellator tess) {
        if (Shaders.useMultiTexCoord3Attrib && tess.hasTexture) {
            GL13.glClientActiveTexture(33987);
            GL11.glTexCoordPointer(2, 5126, 72, 64L);
            GL11.glEnableClientState(32888);
            GL13.glClientActiveTexture(33984);
        }

        if (Shaders.useMidTexCoordAttrib && tess.hasTexture) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.midTexCoordAttrib, 2, 5126, false, 72, 64L);
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
        }

        if (Shaders.useTangentAttrib && tess.hasTexture) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.tangentAttrib, 4, 5126, false, 72, 48L);
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.tangentAttrib);
        }

        if (Shaders.useEntityAttrib) {
            ARBVertexShader.glVertexAttribPointerARB(Shaders.entityAttrib, 3, 5122, false, 72, 28L);
            ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.entityAttrib);
        }

    }

    public static void postDrawArray(Tessellator tess) {
        if (Shaders.useEntityAttrib) {
            ARBVertexShader.glDisableVertexAttribArrayARB(Shaders.entityAttrib);
        }

        if (Shaders.useMidTexCoordAttrib && tess.hasTexture) {
            ARBVertexShader.glDisableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
        }

        if (Shaders.useTangentAttrib && tess.hasTexture) {
            ARBVertexShader.glDisableVertexAttribArrayARB(Shaders.tangentAttrib);
        }

        if (Shaders.useMultiTexCoord3Attrib && tess.hasTexture) {
            GL13.glClientActiveTexture(33987);
            GL11.glDisableClientState(32888);
            GL13.glClientActiveTexture(33984);
        }

    }

    public static void addVertex(Tessellator tess, double parx, double pary, double parz) {
        ShadersTess stess = tess.shadersTess;
        int[] rawBuffer = tess.rawBuffer;
        int rbi = tess.rawBufferIndex;
        float fx = (float) (parx + tess.xOffset);
        float fy = (float) (pary + tess.yOffset);
        float fz = (float) (parz + tess.zOffset);
        if (rbi >= tess.bufferSize - 72) {
            if (tess.bufferSize >= 16777216) {
                if (tess.addedVertices % 4 == 0) {
                    tess.draw();
                    tess.isDrawing = true;
                }
            } else if (tess.bufferSize > 0) {
                tess.bufferSize *= 2;
                tess.rawBuffer = rawBuffer = Arrays.copyOf(tess.rawBuffer, tess.bufferSize);
                SMCLog.info("Expand tesselator buffer %d", tess.bufferSize);
            } else {
                tess.bufferSize = 65536;
                tess.rawBuffer = rawBuffer = new int[tess.bufferSize];
            }
        }

        if (tess.drawMode == 7) {
            int i = tess.addedVertices % 4;
            switch (i) {
                case 0:
                    stess.v0x = fx;
                    stess.v0y = fy;
                    stess.v0z = fz;
                    stess.v0u = (float) tess.textureU;
                    stess.v0v = (float) tess.textureV;
                    break;
                case 1:
                    stess.v1x = fx;
                    stess.v1y = fy;
                    stess.v1z = fz;
                    stess.v1u = (float) tess.textureU;
                    stess.v1v = (float) tess.textureV;
                    break;
                case 2:
                    stess.v2x = fx;
                    stess.v2y = fy;
                    stess.v2z = fz;
                    stess.v2u = (float) tess.textureU;
                    stess.v2v = (float) tess.textureV;
                    break;
                case 3:
                    stess.v3x = fx;
                    stess.v3y = fy;
                    stess.v3z = fz;
                    stess.v3u = (float) tess.textureU;
                    stess.v3v = (float) tess.textureV;
                    float x1 = stess.v2x - stess.v0x;
                    float y1 = stess.v2y - stess.v0y;
                    float z1 = stess.v2z - stess.v0z;
                    float x2 = stess.v3x - stess.v1x;
                    float y2 = stess.v3y - stess.v1y;
                    float z2 = stess.v3z - stess.v1z;
                    float vnx = y1 * z2 - y2 * z1;
                    float vny = z1 * x2 - z2 * x1;
                    float vnz = x1 * y2 - x2 * y1;
                    float lensq = vnx * vnx + vny * vny + vnz * vnz;
                    float mult = (double) lensq != 0.0 ? (float) (1.0 / Math.sqrt(lensq)) : 1.0F;
                    vnx *= mult;
                    vny *= mult;
                    vnz *= mult;
                    tess.hasNormals = true;
                    x1 = stess.v1x - stess.v0x;
                    y1 = stess.v1y - stess.v0y;
                    z1 = stess.v1z - stess.v0z;
                    float u1 = stess.v1u - stess.v0u;
                    float v1 = stess.v1v - stess.v0v;
                    x2 = stess.v2x - stess.v0x;
                    y2 = stess.v2y - stess.v0y;
                    z2 = stess.v2z - stess.v0z;
                    float u2 = stess.v2u - stess.v0u;
                    float v2 = stess.v2v - stess.v0v;
                    float d = u1 * v2 - u2 * v1;
                    float r = d != 0.0F ? 1.0F / d : 1.0F;
                    float tan1x = (v2 * x1 - v1 * x2) * r;
                    float tan1y = (v2 * y1 - v1 * y2) * r;
                    float tan1z = (v2 * z1 - v1 * z2) * r;
                    float tan2x = (u1 * x2 - u2 * x1) * r;
                    float tan2y = (u1 * y2 - u2 * y1) * r;
                    float tan2z = (u1 * z2 - u2 * z1) * r;
                    lensq = tan1x * tan1x + tan1y * tan1y + tan1z * tan1z;
                    mult = (double) lensq != 0.0 ? (float) (1.0 / Math.sqrt(lensq)) : 1.0F;
                    tan1x *= mult;
                    tan1y *= mult;
                    tan1z *= mult;
                    lensq = tan2x * tan2x + tan2y * tan2y + tan2z * tan2z;
                    mult = (double) lensq != 0.0 ? (float) (1.0 / Math.sqrt(lensq)) : 1.0F;
                    tan2x *= mult;
                    tan2y *= mult;
                    tan2z *= mult;
                    float tan3x = vnz * tan1y - vny * tan1z;
                    float tan3y = vnx * tan1z - vnz * tan1x;
                    float tan3z = vny * tan1x - vnx * tan1y;
                    float tan1w = tan2x * tan3x + tan2y * tan3y + tan2z * tan3z < 0.0F ? -1.0F : 1.0F;
                    rawBuffer[rbi + -45] = rawBuffer[rbi + -27] = rawBuffer[rbi + -9] = rawBuffer[rbi + 9] = Float.floatToRawIntBits(stess.normalX = vnx);
                    rawBuffer[rbi + -44] = rawBuffer[rbi + -26] = rawBuffer[rbi + -8] = rawBuffer[rbi + 10] = Float.floatToRawIntBits(stess.normalY = vny);
                    rawBuffer[rbi + -43] = rawBuffer[rbi + -25] = rawBuffer[rbi + -7] = rawBuffer[rbi + 11] = Float.floatToRawIntBits(stess.normalZ = vnz);
                    rawBuffer[rbi + -42] = rawBuffer[rbi + -24] = rawBuffer[rbi + -6] = rawBuffer[rbi + 12] = Float.floatToRawIntBits(tan1x);
                    rawBuffer[rbi + -41] = rawBuffer[rbi + -23] = rawBuffer[rbi + -5] = rawBuffer[rbi + 13] = Float.floatToRawIntBits(tan1y);
                    rawBuffer[rbi + -40] = rawBuffer[rbi + -22] = rawBuffer[rbi + -4] = rawBuffer[rbi + 14] = Float.floatToRawIntBits(tan1z);
                    rawBuffer[rbi + -39] = rawBuffer[rbi + -21] = rawBuffer[rbi + -3] = rawBuffer[rbi + 15] = Float.floatToRawIntBits(tan1w);
                    stess.midTextureU = (
                            Float.intBitsToFloat(rawBuffer[rbi + -51])
                                    + Float.intBitsToFloat(rawBuffer[rbi + -33])
                                    + Float.intBitsToFloat(rawBuffer[rbi + -15])
                                    + (float) tess.textureU
                    )
                            / 4.0F;
                    stess.midTextureV = (
                            Float.intBitsToFloat(rawBuffer[rbi + -50])
                                    + Float.intBitsToFloat(rawBuffer[rbi + -32])
                                    + Float.intBitsToFloat(rawBuffer[rbi + -14])
                                    + (float) tess.textureV
                    )
                            / 4.0F;
                    rawBuffer[rbi + -38] = rawBuffer[rbi + -20] = rawBuffer[rbi + -2] = rawBuffer[rbi + 16] = Float.floatToRawIntBits(stess.midTextureU);
                    rawBuffer[rbi + -37] = rawBuffer[rbi + -19] = rawBuffer[rbi + -1] = rawBuffer[rbi + 17] = Float.floatToRawIntBits(stess.midTextureV);
            }
        } else if (tess.drawMode == 4) {
            int i = tess.addedVertices % 3;
            switch (i) {
                case 0:
                    stess.v0x = fx;
                    stess.v0y = fy;
                    stess.v0z = fz;
                    stess.v0u = (float) tess.textureU;
                    stess.v0v = (float) tess.textureV;
                    break;
                case 1:
                    stess.v1x = fx;
                    stess.v1y = fy;
                    stess.v1z = fz;
                    stess.v1u = (float) tess.textureU;
                    stess.v1v = (float) tess.textureV;
                    break;
                case 2:
                    stess.v2x = fx;
                    stess.v2y = fy;
                    stess.v2z = fz;
                    stess.v2u = (float) tess.textureU;
                    stess.v2v = (float) tess.textureV;
                    float x1 = stess.v1x - stess.v0x;
                    float y1 = stess.v1y - stess.v0y;
                    float z1 = stess.v1z - stess.v0z;
                    float x2 = stess.v2x - stess.v1x;
                    float y2 = stess.v2y - stess.v1y;
                    float z2 = stess.v2z - stess.v1z;
                    float vnx = y1 * z2 - y2 * z1;
                    float vny = z1 * x2 - z2 * x1;
                    float vnz = x1 * y2 - x2 * y1;
                    float lensq = vnx * vnx + vny * vny + vnz * vnz;
                    float mult = (double) lensq != 0.0 ? (float) (1.0 / Math.sqrt(lensq)) : 1.0F;
                    vnx *= mult;
                    vny *= mult;
                    vnz *= mult;
                    tess.hasNormals = true;
                    x1 = stess.v1x - stess.v0x;
                    y1 = stess.v1y - stess.v0y;
                    z1 = stess.v1z - stess.v0z;
                    float u1 = stess.v1u - stess.v0u;
                    float v1 = stess.v1v - stess.v0v;
                    x2 = stess.v2x - stess.v0x;
                    y2 = stess.v2y - stess.v0y;
                    z2 = stess.v2z - stess.v0z;
                    float u2 = stess.v2u - stess.v0u;
                    float v2 = stess.v2v - stess.v0v;
                    float d = u1 * v2 - u2 * v1;
                    float r = d != 0.0F ? 1.0F / d : 1.0F;
                    float tan1x = (v2 * x1 - v1 * x2) * r;
                    float tan1y = (v2 * y1 - v1 * y2) * r;
                    float tan1z = (v2 * z1 - v1 * z2) * r;
                    float tan2x = (u1 * x2 - u2 * x1) * r;
                    float tan2y = (u1 * y2 - u2 * y1) * r;
                    float tan2z = (u1 * z2 - u2 * z1) * r;
                    lensq = tan1x * tan1x + tan1y * tan1y + tan1z * tan1z;
                    mult = (double) lensq != 0.0 ? (float) (1.0 / Math.sqrt(lensq)) : 1.0F;
                    tan1x *= mult;
                    tan1y *= mult;
                    tan1z *= mult;
                    lensq = tan2x * tan2x + tan2y * tan2y + tan2z * tan2z;
                    mult = (double) lensq != 0.0 ? (float) (1.0 / Math.sqrt(lensq)) : 1.0F;
                    tan2x *= mult;
                    tan2y *= mult;
                    tan2z *= mult;
                    float tan3x = vnz * tan1y - vny * tan1z;
                    float tan3y = vnx * tan1z - vnz * tan1x;
                    float tan3z = vny * tan1x - vnx * tan1y;
                    float tan1w = tan2x * tan3x + tan2y * tan3y + tan2z * tan3z < 0.0F ? -1.0F : 1.0F;
                    rawBuffer[rbi + -27] = rawBuffer[rbi + -9] = rawBuffer[rbi + 9] = Float.floatToRawIntBits(stess.normalX = vnx);
                    rawBuffer[rbi + -26] = rawBuffer[rbi + -8] = rawBuffer[rbi + 10] = Float.floatToRawIntBits(stess.normalY = vny);
                    rawBuffer[rbi + -25] = rawBuffer[rbi + -7] = rawBuffer[rbi + 11] = Float.floatToRawIntBits(stess.normalZ = vnz);
                    rawBuffer[rbi + -24] = rawBuffer[rbi + -6] = rawBuffer[rbi + 12] = Float.floatToRawIntBits(tan1x);
                    rawBuffer[rbi + -23] = rawBuffer[rbi + -5] = rawBuffer[rbi + 13] = Float.floatToRawIntBits(tan1y);
                    rawBuffer[rbi + -22] = rawBuffer[rbi + -4] = rawBuffer[rbi + 14] = Float.floatToRawIntBits(tan1z);
                    rawBuffer[rbi + -21] = rawBuffer[rbi + -3] = rawBuffer[rbi + 15] = Float.floatToRawIntBits(tan1w);
                    stess.midTextureU = (Float.intBitsToFloat(rawBuffer[rbi + -33]) + Float.intBitsToFloat(rawBuffer[rbi + -15]) + (float) tess.textureU) / 3.0F;
                    stess.midTextureV = (Float.intBitsToFloat(rawBuffer[rbi + -32]) + Float.intBitsToFloat(rawBuffer[rbi + -14]) + (float) tess.textureV) / 3.0F;
                    rawBuffer[rbi + -20] = rawBuffer[rbi + -2] = rawBuffer[rbi + 16] = Float.floatToRawIntBits(stess.midTextureU);
                    rawBuffer[rbi + -19] = rawBuffer[rbi + -1] = rawBuffer[rbi + 17] = Float.floatToRawIntBits(stess.midTextureV);
            }
        }

        ++tess.addedVertices;
        rawBuffer[rbi + 0] = Float.floatToRawIntBits(fx);
        rawBuffer[rbi + 1] = Float.floatToRawIntBits(fy);
        rawBuffer[rbi + 2] = Float.floatToRawIntBits(fz);
        rawBuffer[rbi + 3] = Float.floatToRawIntBits((float) tess.textureU);
        rawBuffer[rbi + 4] = Float.floatToRawIntBits((float) tess.textureV);
        rawBuffer[rbi + 5] = tess.color;
        rawBuffer[rbi + 6] = tess.brightness;
        rawBuffer[rbi + 7] = Shaders.getEntityData();
        rawBuffer[rbi + 8] = Shaders.getEntityData2();
        rawBuffer[rbi + 9] = Float.floatToRawIntBits(stess.normalX);
        rawBuffer[rbi + 10] = Float.floatToRawIntBits(stess.normalY);
        rawBuffer[rbi + 11] = Float.floatToRawIntBits(stess.normalZ);
        rbi += 18;
        tess.rawBufferIndex = rbi;
        ++tess.vertexCount;
    }
}
