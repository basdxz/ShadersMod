package shadersmodcore.client;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public final class ReflectionHandler {
    private ReflectionHandler() {
    }

    // region Vanilla
    public static ByteBuffer getTessellatorByteBuffer() {
        return ReflectionHelper.getPrivateValue(Tessellator.class, null, "byteBuffer", "field_78394_d");
    }

    public static IntBuffer getTessellatorIntBuffer() {
        return ReflectionHelper.getPrivateValue(Tessellator.class, null, "intBuffer", "field_147568_c");
    }

    public static FloatBuffer getTessellatorFloatBuffer() {
        return ReflectionHelper.getPrivateValue(Tessellator.class, null, "floatBuffer", "field_147566_d");
    }

    public static ShortBuffer getTessellatorShortBuffer() {
        return ReflectionHelper.getPrivateValue(Tessellator.class, null, "shortBuffer", "field_147567_e");
    }

    public static void reset(Tessellator tessellator) {
        Method method = ReflectionHelper.findMethod(Tessellator.class, tessellator, new String[]{"reset", "func_78379_d"});
        try {
            method.invoke(tessellator);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void renderHand(EntityRenderer er, float par1, int par2) {
        Method method = ReflectionHelper.findMethod(EntityRenderer.class, er, new String[]{"renderHand", "func_78476_b"}, int.class, double.class);
        try {
            method.invoke(er, par1, par2);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResourceLocation completeResourceLocation(TextureMap textureMap, ResourceLocation resourceLocation, int mipMapLevel) {
        Method method = ReflectionHelper.findMethod(TextureMap.class, textureMap, new String[]{"completeResourceLocation", "func_147634_a"}, ResourceLocation.class, int.class);
        try {
            return (ResourceLocation) method.invoke(textureMap, resourceLocation, mipMapLevel);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    // endregion

    // region Transformed
    public static MultiTexID getMultiTexID(ITextureObject texture) {
        Method method = ReflectionHelper.findMethod(ITextureObject.class, texture, new String[]{"getMultiTexID"});
        try {
            return (MultiTexID) method.invoke(texture);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setMultiTex(AbstractTexture texture, MultiTexID multiTexID) {
        ReflectionHelper.setPrivateValue(AbstractTexture.class, texture, multiTexID, "multiTexID");
    }

    public static void resetDisplayList(ModelRenderer modelRenderer) {
        Method method = ReflectionHelper.findMethod(ModelRenderer.class, modelRenderer, new String[]{"resetDisplayList"});
        try {
            method.invoke(modelRenderer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static ShadersTess getShadersTess(Tessellator tessellator) {
        return ReflectionHelper.getPrivateValue(Tessellator.class, tessellator, "shaderTess");
    }

    public static int getAtlasWidth(TextureMap textureMap) {
        return ReflectionHelper.getPrivateValue(TextureMap.class, textureMap, "atlasWidth");
    }

    public static void setAtlasWidth(TextureMap textureMap, int atlasWidth) {
        ReflectionHelper.setPrivateValue(TextureMap.class, textureMap, atlasWidth, "atlasWidth");
    }

    public static int getAtlasHeight(TextureMap textureMap) {
        return ReflectionHelper.getPrivateValue(TextureMap.class, textureMap, "atlasHeight");
    }

    public static void setAtlasHeight(TextureMap textureMap, int atlasHeight) {
        ReflectionHelper.setPrivateValue(TextureMap.class, textureMap, atlasHeight, "atlasHeight");
    }
    // endregion
}
