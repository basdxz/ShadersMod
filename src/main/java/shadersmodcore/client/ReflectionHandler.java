package shadersmodcore.client;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public final class ReflectionHandler {
    private ReflectionHandler() {
    }

    public static MultiTexID getMultiTexID(AbstractTexture texture) {
        return null;
    }

    public static MultiTexID getMultiTexID(ITextureObject texture) {
        return null;
    }

    public static void setMultiTex(AbstractTexture texture, MultiTexID multiTexID) {
    }

    public static void resetDisplayList(ModelRenderer modelRenderer) {
    }

    public static ShadersTess getShadersTess(Tessellator tessellator) {
        return null;
    }

    public static ByteBuffer getTessellatorByteBuffer() {
        return null;
    }

    public static IntBuffer getTessellatorIntBuffer() {
        return null;
    }

    public static FloatBuffer getTessellatorFloatBuffer() {
        return null;
    }

    public static ShortBuffer getTessellatorShortBuffer() {
        return null;
    }

    public static void reset(Tessellator tessellator) {
    }

    public static void renderHand(EntityRenderer er, float par1, int par2) {
    }

    public static int getAtlasWidth(TextureMap textureMap) {
        return 0;
    }

    public static void setAtlasWidth(TextureMap textureMap, int atlasWidth) {
    }

    public static int getAtlasHeight(TextureMap textureMap) {
        return 0;
    }

    public static void setAtlasHeight(TextureMap textureMap, int atlasHeight) {
    }

    public static ResourceLocation completeResourceLocation(TextureMap textureMap, ResourceLocation resourceLocation,
                                                            int mipMapLevel) {
        return null;
    }
}
